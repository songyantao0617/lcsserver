package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.FilePackModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class OtaPackageRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.OtaPackageRequest> {
    public static final String TypeUrl = "type.googleapis.com/OtaPackageRequest";

    public String getTargetPath() {
        return targetPath;
    }

    public boolean isRestartRequired() {
        return restartRequired;
    }

    public FilePackModel getFilePackModel() {
        return filePackModel;
    }

    final String targetPath;
    final boolean restartRequired;
    final FilePackModel filePackModel;


    //反向构造
    public OtaPackageRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.OtaPackageRequest v = pb.getPayload().unpack(LcsProtos.OtaPackageRequest.class);
        this.targetPath = v.getTargetPath();
        this.restartRequired = v.getRestartRequired();
        this.filePackModel = new FilePackModel(v.getFile());
    }

    //正向构造
    public OtaPackageRequestWrapper(ProtoHeaders headers,
                                    String targetPath,
                                    boolean restartRequired,
                                    FilePackModel filePackModel
    ) {
        super(headers);
        this.restartRequired = restartRequired;
        this.targetPath = targetPath;
        this.filePackModel = filePackModel;
    }

    @Override
    protected LcsProtos.OtaPackageRequest.Builder internal_get_payload() {
        return LcsProtos.OtaPackageRequest.newBuilder()
                .setRestartRequired(this.restartRequired)
                .setTargetPath(this.targetPath)
                .setFile(this.filePackModel.getPb())
                ;
    }

}
