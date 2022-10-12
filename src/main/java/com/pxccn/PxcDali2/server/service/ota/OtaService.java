package com.pxccn.PxcDali2.server.service.ota;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.MqSharePack.model.FilePackModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.OtaPackageRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.OtaPackageResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

@Component
@Slf4j
public class OtaService {
    ExecutorService executorService;
    @Autowired
    CabinetRequestService cabinetRequestService;

    @Autowired
    CabinetsManager cabinetsManager;

    /**
     * 保存到控制器上的路径地址
     */
    @Value("${LcsServer.otaTargetPath:/home/sysmik/niagara/modules}")
    String otaTargetPath;

    /**
     * 升级包的本地获取路径
     */
    @Value("${LcsServer.otaSourcePath:}")
    String otaSourcePath;

    /**
     * 简单内存型文件缓存
     */
    static class FileCache {
        final File file;
        final byte[] content;
        final long lastFileModified;
        final long crc;

        FileCache(File file, long lastFileModified, byte[] content) {
            this.file = file;
            this.content = content;
            this.lastFileModified = lastFileModified;
            CRC32 crc32 = new CRC32();
            crc32.update(content);
            this.crc = crc32.getValue();
        }
    }

    @PostConstruct
    public void init() {
        executorService = LcsExecutors.newWorkStealingPool(3, this.getClass());
    }

    private static FileCache lastFile;

    /**
     * 获取升级包文件
     *
     * @param fileName 本地路径
     * @return FileCache
     * @throws IOException
     */
    static synchronized FileCache GetFileContent(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            lastFile = null;
            throw new FileNotFoundException();
        }
        if (lastFile != null && (!lastFile.file.equals(f) || lastFile.lastFileModified != f.lastModified())) {
            lastFile = null;
        }
        if (lastFile == null) {
            try (FileInputStream fis = new FileInputStream(f)) {
                byte[] bytes = fis.readAllBytes();
                fis.close();
                lastFile = new FileCache(f, f.lastModified(), bytes);
            }
        }
        return lastFile;
    }

    /**
     * 执行 OTA 升级
     *
     * @param oldVersion      需要命中的旧版本号
     * @param selectedCabinet 命中区间,如果为空代表从全场控制器中进行命中
     * @param restart         成功后是否执行控制器重启
     * @param test            测试，不进行实际下发
     * @return 命中数量
     */
    public int OTA_Update(String oldVersion, Set<Integer> selectedCabinet, boolean restart, boolean test) {
        log.trace("OTA_Update: oldVersion={},selectedCabinet={},restart={}", oldVersion, selectedCabinet, restart);
        try {
            GetFileContent(otaSourcePath);
        } catch (IOException e) {
            log.error("can not load ota file on this server with path {}", otaSourcePath, e);
            throw new IllegalStateException(e);
        }
        Set<Cabinet> targetCabinets;
        if (selectedCabinet == null || selectedCabinet.isEmpty()) {
            targetCabinets = Arrays.stream(cabinetsManager.getAllOnlineCabinet())
                    .filter(c -> Objects.equals(c.getVersion(), oldVersion))
                    .collect(Collectors.toSet());
        } else {
            targetCabinets = Arrays.stream(cabinetsManager.getAllOnlineCabinet())
                    .filter(c -> selectedCabinet.contains(c.getCabinetId()))
                    .filter(c -> Objects.equals(c.getVersion(), oldVersion))
                    .collect(Collectors.toSet());
        }
        log.info("select {} cabinets prepare to update from version {}", targetCabinets.size(), oldVersion);
        if (!test) {
            targetCabinets.forEach(c -> {
                tranFileToCabinet(c.getCabinetId(), otaSourcePath, otaTargetPath, restart);
            });
        }
        return targetCabinets.size();
    }

    /**
     * 向控制器传送文件
     *
     * @param cabinetId       控制柜ID
     * @param sourceDir       本地路径
     * @param targetDir       目标路径
     * @param restartRequired 完成后是否执行重启
     */
    public void tranFileToCabinet(int cabinetId, String sourceDir, String targetDir, boolean restartRequired) {
        log.info("tranFileToCabinet: cabinetId={},sourceDir={},targetDir={}", cabinetId, sourceDir, targetDir);
        FileCache f;
        try {
            f = GetFileContent(sourceDir);
            var future = cabinetRequestService.asyncSend(RpcTarget.ToCabinet(cabinetId), new OtaPackageRequestWrapper(Util.NewCommonHeaderForClient(), targetDir, restartRequired,
                    new FilePackModel(f.file.getName(), f.crc, ByteString.copyFrom(f.content))));
            Futures.addCallback(future, new FutureCallback<ResponseWrapper>() {
                @Override
                public void onSuccess(@Nullable ResponseWrapper result) {
                    var resp = (OtaPackageResponseWrapper) result;
                    log.info(resp.toString());
                }

                @Override
                public void onFailure(Throwable t) {
                    log.error("Cabinet<{}> fail to execute OTA", cabinetId, t);
                }
            }, MoreExecutors.directExecutor());
        } catch (IOException e) {
            log.error("OTA:can not get file", e);
            return;
        }

    }

    /**
     * 从控制柜获取文件
     *
     * @param cabinetId      控制柜ID
     * @param remoteFilePath 控制柜文件路径
     * @param saveTo         保存到本地路径
     */
    public void AskFileUpload(int cabinetId, String remoteFilePath, String saveTo) {
        var future = cabinetRequestService.asyncSendWithAsyncFeedback(RpcTarget.ToCabinet(cabinetId), ActionWithFeedbackRequestWrapper.FileUpload(Util.NewCommonHeaderForClient(), remoteFilePath), (responseWrapper) -> {
            log.debug("Cabinet<{}> received AskFileUpload command", cabinetId);
        }, 60000);
        Futures.addCallback(future, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null || !(result.getFeedback() instanceof AsyncActionFeedbackWrapper.FileUpload)) {
                    log.error("Internal Error");
                    return;
                }

                var r = (AsyncActionFeedbackWrapper.FileUpload) result.getFeedback();
                if (r.isSuccess()) {
                    try {
                        r.getFileModel().TransTo(saveTo);
                        log.info("receive the file '{}' from cabinet<{}>, saved to '{}'", r.getFileModel().getFileName(), cabinetId, saveTo);
                    } catch (IOException e) {
                        log.error("fail to save file from cabinet to server", e);
                    }
                } else {
                    log.error("fail to get file from cabinet <{}> : {}", cabinetId, r.getErrorMsg());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("Fail to request file", t);
            }
        }, executorService);
    }


}
