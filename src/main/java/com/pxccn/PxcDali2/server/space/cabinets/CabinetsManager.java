package com.pxccn.PxcDali2.server.space.cabinets;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.events.CabinetDetailUploadEvent;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.events.CabinetStatusMqEvent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

//@FwComponentAnnotation
@Component
@Slf4j
public class CabinetsManager extends FwUaComponent<CabinetsManager.CabinetsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        var message = event.getMessage();
        var cabinetId = message.getCabinetId();
        switch (message.getEvent()) {
            case CabinetInfoChange:
                log.debug("控制柜<{}>信息变更", cabinetId);
                this.GetOrCreateCabinet(cabinetId).AskToUpdateCabinetInfo();
                break;
        }
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
        cabinet.updateStatus(message);
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

    public void doCheckCabinetAlive() {
        Arrays.stream(this.getAllCabinet()).forEach(Cabinet::checkIsNotAlive);
    }

    public Cabinet[] getAllCabinet() {
        return Arrays.stream(this.getAllProperty(Cabinet.class)).map(FwProperty::get).toArray(Cabinet[]::new);
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
            addAdditionalDeclares(folders.values());
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            cabinets
        }
    }
}
