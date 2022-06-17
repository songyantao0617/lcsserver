package com.pxccn.PxcDali2.server.space.lights;


import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightDetailModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
@FwComponentAnnotation
@Slf4j
public class Dali2Light extends LightBase{
    FwProperty<Double> subscribe_level;
    FwProperty<String> subscribe_fault;
    FwProperty<String> lightType;

    @PostConstruct
    public void post() {
        super.post();
        lightType = addProperty("DALI2","lightType");
        subscribe_level = addProperty(0.0, "subscribe_level");
        subscribe_fault = addProperty("", "subscribe_fault");
    }
    public void onNewStatus(Dali2LightRealtimeStatusModel lrs) {
        super.onNewStatus(lrs);
        this.subscribe_level.set(lrs.brightness);
        this.subscribe_fault.set(lrs.getErrorMessage() != null ? lrs.getErrorMessage() : "");
    }
    public void onDetailUpload(Dali2LightDetailModel model) {
        super.onDetailUpload(model);
    }

    protected LCS_LightBaseNode createUaNode() {
        return new LCS_Dali2LightNode(this, this.getName(), this.getName());
    }

    protected static class LCS_Dali2LightNode extends LCS_LightBaseNode {
        Dali2Light comp;
        protected LCS_Dali2LightNode(Dali2Light comp, String qualifiedName, String objLocalizedText) {
            super(comp, qualifiedName, objLocalizedText);
            this.comp = comp;
            addProperty(comp.subscribe_level);
            addProperty(comp.subscribe_fault);
            addProperty(comp.lightType);
        }

    }
}
