package com.pxccn.PxcDali2.server.space.cabinets;

import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.server.events.CabinetStatusMqEvent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@FwComponentAnnotation
@Component
@Slf4j
public class CabinetsManager extends FwUaComponent<CabinetsManager.CabinetsManagerNode> {

    @EventListener
    public void onCabinetStatusMqEvent(CabinetStatusMqEvent event) {
        var message = event.getMessage();
        var cabinet = this.GetOrCreateCabinet(message.getCabinetId());
        cabinet.updateStatus(message);
    }


    public Cabinet GetOrCreateCabinet(int cabinetId) {
        String cabinetPropName = String.valueOf(cabinetId);
        var p = this.getProperty(cabinetPropName);
        if (p == null) {
            var c = context.getBean(Cabinet.class);
            c.setCid(cabinetId);
            p = addProperty(c, cabinetPropName);
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
            cabinets;
        }
    }
}
