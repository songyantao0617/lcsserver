package com.pxccn.PxcDali2.server.space.lights;

import com.pxccn.PxcDali2.MqSharePack.model.DoLightDetailModel;
import com.pxccn.PxcDali2.MqSharePack.model.DoLightRealtimeStatusModel;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@FwComponentAnnotation
@Slf4j
public class DoLight extends LightBase {

    FwProperty<Double> subscribe_level;
    FwProperty<String> subscribe_fault;
    FwProperty<String> lightType;

    @Override
    void cabinetStatusChanged(boolean isOnline) {
        if (!isOnline) {
            this.subscribe_level.set(-1.0);
            this.subscribe_fault.set("Cabinet Offline");
        }
    }

    @PostConstruct
    public void post() {
        super.post();
        lightType = addProperty("DO", "lightType");
        subscribe_level = addProperty(0.0, "subscribe_level");
        subscribe_fault = addProperty("", "subscribe_fault");
    }

    @Override
    public double getBrightness() {
        return this.subscribe_level.get();
    }

    @Override
    public String getErrorMsg() {
        return this.subscribe_fault.get();
    }

    protected LCS_LightBaseNode createUaNode() {
        return new LCS_DoLightNode(this, this.getName(), this.getName());
    }

    public void onNewStatus(DoLightRealtimeStatusModel lrs) {
        super.onNewStatus(lrs);
        this.subscribe_level.set(lrs.currentValue ? 100.0 : 0);
        this.subscribe_fault.set(lrs.exceptionMessage);
    }

    public void onDetailUpload(DoLightDetailModel model) {
        super.onDetailUpload(model);
    }


    protected static class LCS_DoLightNode extends LCS_LightBaseNode {
        DoLight comp;

        protected LCS_DoLightNode(DoLight comp, String qualifiedName, String objLocalizedText) {
            super(comp, qualifiedName, objLocalizedText);
            this.comp = comp;
            addProperty(comp.subscribe_level);
            addProperty(comp.subscribe_fault);
            addProperty(comp.lightType);
        }

    }
}
