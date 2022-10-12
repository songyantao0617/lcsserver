package com.pxccn.PxcDali2.server.space.cabinets;

import com.prosysopc.ua.ValueRanks;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.events.CabinetDetailUploadEvent;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.events.CabinetStatusMqEvent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

/**
 * 控制柜管理器
 */
@Component
@Slf4j
public class CabinetsManager extends FwUaComponent<CabinetsManager.CabinetsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;
    ExecutorService executorService = LcsExecutors.newWorkStealingPool(16, getClass());

    @Value("${LcsServer.autoUpdateOnlineCabinetLightsAndRoomsInfo:false}")
    boolean autoUpdateOnlineCabinetLightsAndRoomsInfo;

    @Value("${LcsServer.updateAuditInDatabase:false}")
    boolean updateAuditInDatabase;

    FwProperty<Integer> count_AllCabinets;
    FwProperty<Integer> count_AllOnlineCabinets;
    FwProperty<Integer> count_AllOfflineCabinets;

    @PostConstruct
    public void init() {
        this.count_AllCabinets = addProperty(0, "summary.count.cabinets");
        this.count_AllOnlineCabinets = addProperty(0, "summary.count.online");
        this.count_AllOfflineCabinets = addProperty(0, "summary.count.offline");
    }

    @Value("${LcsServer.timeServer:false}")
    boolean isTimeServer;

    /**
     * 控制器对时任务
     */
    @Scheduled(fixedDelay = 3600 * 1 * 1000)
    private void onPer1Hour() {
        log.trace("onPer1Hour");
        if (isTimeServer) {
            executorService.execute(() -> {
                Arrays.stream(getAllOnlineCabinet()).forEach(c -> {
                    c.setCabinetSystemTime(System.currentTimeMillis());
                });
            });
        }
    }

    @Scheduled(fixedDelay = 1000 * 8) // 每8秒钟检查存活
    private void scheduleCheckCabinetAlive() {
        if (!this.isRunning()) {
            return;
        }
        log.trace(logStr("scheduleCheckCabinetAlive"));
        executorService.execute(() -> {
            Arrays.stream(this.getAllCabinet()).forEach(Cabinet::checkIsNotAlive);
            updateSummary();
            updateOnlineListUaNode();
            updateCpuLoadListUaNode();
            updateUptimeListUaNode();
            updateVersionListUaNode();
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 15) // 15分钟统计一次
    private void scheduleAuditCabinet() {
        if (updateAuditInDatabase || !this.isRunning()) {
            return;
        }
        log.trace(logStr("scheduleUpdateAllOnlineCabinetLightsAndRoomsInfo"));
        executorService.execute(() -> {
            Arrays.stream(this.getAllCabinet()).forEach(Cabinet::auditLightsInfo);
        });
    }


    @Scheduled(fixedDelay = 1000 * 60 * 30) // 每半小时执行一次灯具房间信息更新
    private void scheduleUpdateAllOnlineCabinetLightsAndRoomsInfo() {
        if (!autoUpdateOnlineCabinetLightsAndRoomsInfo || !this.isRunning()) {
            return;
        }
        log.trace(logStr("scheduleUpdateAllOnlineCabinetLightsAndRoomsInfo"));
        executorService.execute(this::updateAllOnlineCabinetLightsAndRoomsInfo);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10) // 每十分钟更新一次控制柜信息
    private void scheduleUpdateCabinetInfo() {
        log.trace(logStr("scheduleUpdateCabinetInfo"));
        executorService.execute(() -> {
            Arrays.stream(this.getAllCabinet()).forEach(Cabinet::AskToUpdateCabinetInfo);
        });
    }


    @Scheduled(fixedDelay = 1000 * 60 * 20) // 每20分钟清空一次实时状态缓存
    private void schedulePurgeStatusCache() {
        if (!autoUpdateOnlineCabinetLightsAndRoomsInfo || !this.isRunning()) {
            return;
        }
        log.trace(logStr("schedulePurgeStatusCache"));
        Arrays.stream(this.getAllOnlineCabinet()).forEach(Cabinet::purgeCacheForUpdateAllRealtimeStatus);
    }

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        executorService.execute(() -> {
            var message = event.getMessage();
            var cabinetId = message.getCabinetId();
            switch (message.getEvent()) {
                case CabinetInfoChange:
                    log.debug("Cabinet<{}> Info Changed", cabinetId);
                    this.GetOrCreateCabinet(cabinetId).AskToUpdateCabinetInfo();
                    break;
            }
        });
    }

    public void updateSummary() {
        int all = this.getAllCabinet().length;
        int online = this.getAllOnlineCabinet().length;
        int offline = this.getAllOfflineCabinet().length;
        this.count_AllCabinets.set(all);
        this.count_AllOfflineCabinets.set(offline);
        this.count_AllOnlineCabinets.set(online);

    }

    @EventListener
    public void onCabinetDetailUploadEvent(CabinetDetailUploadEvent event) {
        executorService.execute(() -> {
            var cabinet = this.GetOrCreateCabinet(event.getMessage().getCabinetId());
            cabinet.onDetailUpload(event.getMessage());
        });
    }

    @EventListener
    public void onCabinetStatusMqEvent(CabinetStatusMqEvent event) {
        executorService.execute(() -> {
            var message = event.getMessage();
            var cabinet = this.GetOrCreateCabinet(message.getCabinetId());
            cabinet.onUpdateStatus(message);
        });

    }

    /**
     * 是否存在某控制柜
     *
     * @param cabinetId
     * @return
     */
    public boolean checkHasCabinet(int cabinetId) {
        return this.getProperty(String.valueOf(cabinetId)) != null;
    }

    /**
     * 指定控制柜是否在线
     *
     * @param cabinetId
     * @return
     */
    public boolean checkIsAlive(int cabinetId) {
        var cabinet = this.getProperty(String.valueOf(cabinetId));
        if (cabinet == null) {
            return false;
        }
        return ((Cabinet) cabinet.get()).isAlive();
    }

    public Cabinet GetOrCreateCabinet(int cabinetId) {
        String cabinetPropName = String.valueOf(cabinetId);
        FwProperty p;
        synchronized (this) {
            p = this.getProperty(cabinetPropName);
            if (p == null) {
                var c = context.getBean(Cabinet.class);
                c.setCid(cabinetId);
                p = addProperty(c, cabinetPropName);
            }
        }
        return (Cabinet) p.get();
    }


    public Cabinet[] getAllCabinet() {
        return Arrays.stream(this.getAllProperty(Cabinet.class)).map(FwProperty::get).toArray(Cabinet[]::new);
    }

    public Cabinet[] getAllOnlineCabinet() {
        return Arrays.stream(getAllCabinet()).filter(Cabinet::isAlive).toArray(Cabinet[]::new);
    }

    public Cabinet[] getAllOfflineCabinet() {
        return Arrays.stream(getAllCabinet()).filter(c -> !c.isAlive()).toArray(Cabinet[]::new);
    }

    public void updateAllOnlineCabinetLightsAndRoomsInfo() {
        log.trace(logStr("updateAllOnlineCabinetLightsAndRoomsInfo"));
        Arrays.stream(getAllOnlineCabinet()).forEach(Cabinet::AskToUpdateLightsAndRoomInfo);
    }

    public void started() {
        super.started();
//        GetOrCreateCabinet(123);
//        GetOrCreateCabinet(1244);
    }

    private static final String onlineListNodeName = "summary.online";
    private static final String cpuLoadListNodeName = "summary.cpu.usage";
    private static final String uptimeListNodeName = "summary.uptime";
    private static final String versionListNodeName = "summary.version";

    private void updateOnlineListUaNode() {
        try {
            var cabinets = this.getAllCabinet();
            var result = new String[cabinets.length][2];
            for (int i = 0; i < cabinets.length; i++) {
                var cabinet = cabinets[i];
                result[i][0] = cabinet.getLogLocate();
                result[i][1] = cabinet.isAlive() ? "online" : "offline @ " + cabinet.getLastConnectionLostTimeStamp().toString();
            }
            this.getNode().getProperty(new QualifiedName(this.getNode().getNamespaceIndex(), onlineListNodeName)).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCpuLoadListUaNode() {
        try {
            var cabinets = this.getAllCabinet();
            var result = new String[cabinets.length][2];
            for (int i = 0; i < cabinets.length; i++) {
                var cabinet = cabinets[i];
                result[i][0] = cabinet.getLogLocate();
                if (!cabinet.isAlive()) {
                    result[i][1] = "offline";
                } else {
                    result[i][1] = cabinet.getProps().getCpuUsage();
                }
            }
            this.getNode().getProperty(new QualifiedName(this.getNode().getNamespaceIndex(), cpuLoadListNodeName)).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUptimeListUaNode() {
        try {
            var cabinets = this.getAllCabinet();
            var result = new String[cabinets.length][2];
            for (int i = 0; i < cabinets.length; i++) {
                var cabinet = cabinets[i];
                result[i][0] = cabinet.getLogLocate();
                if (!cabinet.isAlive()) {
                    result[i][1] = "offline";
                } else {
                    result[i][1] = cabinet.getProps().getUptime();
                }
            }
            this.getNode().getProperty(new QualifiedName(this.getNode().getNamespaceIndex(), uptimeListNodeName)).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVersionListUaNode() {
        try {
            var cabinets = this.getAllCabinet();
            var result = new String[cabinets.length][2];
            for (int i = 0; i < cabinets.length; i++) {
                var cabinet = cabinets[i];
                result[i][0] = cabinet.getLogLocate();
                if (!cabinet.isAlive()) {
                    result[i][1] = "offline";
                } else {
                    result[i][1] = cabinet.getVersion();
                }
            }
            this.getNode().getProperty(new QualifiedName(this.getNode().getNamespaceIndex(), versionListNodeName)).setValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
    }

    UaNode getCabinetsFolderNode() {
        return this.getNode().getDeclaredNode(CabinetsManagerNode.folders.cabinets);
    }


    @Override
    protected CabinetsManagerNode createUaNode() {
        return new CabinetsManagerNode(this, this.getName());//控制柜节点
    }

    protected static class CabinetsManagerNode extends LCS_ComponentFastObjectNode {

        protected CabinetsManagerNode(CabinetsManager uaComponent, String qname) {
            super(uaComponent, qname);
            addProperty(uaComponent.count_AllCabinets);
            addProperty(uaComponent.count_AllOfflineCabinets);
            addProperty(uaComponent.count_AllOnlineCabinets);
            addAdditionalDeclares(folders.values());

            addAdditionalDeclares(new UaHelperUtil.UaVariableDeclare[]{
                    new declareItem(onlineListNodeName, new UaHelperUtil.VariableDeclareParams(false, Identifiers.String, 100, ValueRanks.OneOrMoreDimensions, new UnsignedInteger[]{UnsignedInteger.valueOf(2)})),
                    new declareItem(cpuLoadListNodeName, new UaHelperUtil.VariableDeclareParams(false, Identifiers.String, 100, ValueRanks.OneOrMoreDimensions, new UnsignedInteger[]{UnsignedInteger.valueOf(2)})),
                    new declareItem(uptimeListNodeName, new UaHelperUtil.VariableDeclareParams(false, Identifiers.String, 100, ValueRanks.OneOrMoreDimensions, new UnsignedInteger[]{UnsignedInteger.valueOf(2)})),
                    new declareItem(versionListNodeName, new UaHelperUtil.VariableDeclareParams(false, Identifiers.String, 100, ValueRanks.OneOrMoreDimensions, new UnsignedInteger[]{UnsignedInteger.valueOf(2)})),
            });
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            cabinets
        }
    }
}
