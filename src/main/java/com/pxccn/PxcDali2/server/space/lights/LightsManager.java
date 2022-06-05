package com.pxccn.PxcDali2.server.space.lights;

import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.server.events.ToComponent.LightStatusMessageMqCompEvent;
import com.pxccn.PxcDali2.server.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@FwComponentAnnotation
@Component
public class LightsManager extends FwUaComponent<LightsManager.LCS_GlobalLightsManagerNode> {
//    FwProperty<LightBase> l1 = addProperty(new LightBase(), "light1");
//    FwProperty<LightBase> l2 = addProperty(new LightBase(), "light2");

    @Override
    public void onEvent(Object event) {
        if (event instanceof LightStatusMessageMqCompEvent) {
            var msg = ((LightStatusMessageMqCompEvent) event).getMessage();
            var light = this.GetOrCreateLight(msg.lightId);
            light.onNewStatus(msg);
        }
    }

    public void started() {
        super.started();
//        addProperty(new LightBase(), "light3");

    }

    public LightBase GetOrCreateLight(UUID lightUuid) {
        String lightPropName = lightUuid.toString();
        var p = this.getProperty(lightPropName);
        if (p == null) {
            var c = context.getBean(LightBase.class);
            c.setUuid(lightUuid);
            p = addProperty(c, lightPropName);
        }
        return (LightBase) p.get();
    }


    UaNode getLightsFolderNode() {
        return this.getNode().getDeclaredNode(LCS_GlobalLightsManagerNode.folders.lights);
    }

    @Override
    protected LCS_GlobalLightsManagerNode createUaNode() {
        return new LCS_GlobalLightsManagerNode(this, this.getName());
    }

    protected static class LCS_GlobalLightsManagerNode extends LCS_ComponentFastObjectNode {

        protected LCS_GlobalLightsManagerNode(FwUaComponent uaComponent, String qname) {
            super(uaComponent, qname);
            addAdditionalDeclares(folders.values());
        }

        private enum folders implements UaHelperUtil.UaFolderDeclare {
            lights;
        }
    }

}
