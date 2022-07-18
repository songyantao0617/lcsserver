package com.pxccn.PxcDali2.server.space.cabinets;

import com.prosysopc.ua.nodes.UaNode;
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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

//@FwComponentAnnotation
@Component
@Slf4j
public class CabinetsManager extends FwUaComponent<CabinetsManager.CabinetsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;
    ExecutorService executorService = LcsExecutors.newWorkStealingPool(16, getClass());


    FwProperty<Integer> count_AllCabinets;
    FwProperty<Integer> count_AllOnlineCabinets;
    FwProperty<Integer> count_AllOfflineCabinets;

    @PostConstruct
    public void init(){
        this.count_AllCabinets = addProperty(0,"count_AllCabinets");
        this.count_AllOnlineCabinets = addProperty(0,"count_AllOnlineCabinets");
        this.count_AllOfflineCabinets = addProperty(0,"count_AllOfflineCabinets");
    }

    @Scheduled(fixedDelay = 1000*5) // 每五秒钟检查存活
    private void scheduleCheckCabinetAlive() {
        log.trace(logStr("scheduleCheckCabinetAlive"));
        Arrays.stream(this.getAllCabinet()).forEach(Cabinet::checkIsNotAlive);
        updateSummary();
    }

    @Scheduled(fixedDelay = 1000*60*30) // 每半小时执行一次灯具房间信息更新
    private void scheduleUpdateAllOnlineCabinetLightsAndRoomsInfo() {
        log.trace(logStr("scheduleUpdateAllOnlineCabinetLightsAndRoomsInfo"));
        updateAllOnlineCabinetLightsAndRoomsInfo();
    }

    @Scheduled(fixedDelay = 1000*60*20) // 每半个小时清空一次实时状态缓存
    private void schedulePurgeStatusCache(){
        log.trace(logStr("schedulePurgeStatusCache"));
        Arrays.stream(this.getAllOnlineCabinet()).forEach(Cabinet::purgeCacheForUpdateAllRealtimeStatus);
    }

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        var message = event.getMessage();
        var cabinetId = message.getCabinetId();
        switch (message.getEvent()) {
            case CabinetInfoChange:
                log.debug("Cabinet<{}> Info Changed", cabinetId);
                this.GetOrCreateCabinet(cabinetId).AskToUpdateCabinetInfo();
                break;
        }
    }

    public void updateSummary(){
        int all = this.getAllCabinet().length;
        int online = this.getAllOnlineCabinet().length;
        int offline = this.getAllOfflineCabinet().length;
        this.count_AllCabinets.set(all);
        this.count_AllOfflineCabinets.set(offline);
        this.count_AllOnlineCabinets.set(online);
    }

    @EventListener
    public void onCabinetDetailUploadEvent(CabinetDetailUploadEvent event) {
        var cabinet = this.GetOrCreateCabinet(event.getMessage().getCabinetId());
        cabinet.onDetailUpload(event.getMessage());
    }

    @EventListener
    public void onCabinetStatusMqEvent(CabinetStatusMqEvent event) {
        var message = event.getMessage();
        var cabinet = this.GetOrCreateCabinet(message.getCabinetId());
        cabinet.onUpdateStatus(message);
    }

    /**
     * 是否存在某控制柜
     * @param cabinetId
     * @return
     */
    public boolean checkHasCabinet(int cabinetId) {
        return this.getProperty(String.valueOf(cabinetId)) != null;
    }

    /**
     * 指定控制柜是否在线
     * @param cabinetId
     * @return
     */
    public boolean checkIsAlive(int cabinetId){
        var cabinet = this.getProperty(String.valueOf(cabinetId));
        if(cabinet == null){
            return false;
        }
        return ((Cabinet)cabinet.get()).isAlive();
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
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            cabinets
        }
    }
}
