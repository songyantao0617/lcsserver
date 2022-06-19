package com.pxccn.PxcDali2.server.space.lights;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.CommonRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.LightDetailModelBase;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@FwComponentAnnotation
@Slf4j
public abstract class LightBase extends FwUaComponent<LightBase.LCS_LightBaseNode> {

    @Autowired
    UaAlarmEventService uaAlarmEventService;

    FwProperty<String> lightName;
    FwProperty<String> description;
    FwProperty<Integer> axis_x;
    FwProperty<Integer> terminalIndex;
    FwProperty<Integer> axis_y;
    FwProperty<Integer> axis_z;

    FwProperty<Integer> shortAddress;
    FwProperty<Long> lastInfosUploadedTimestamp;
    FwProperty<Long> lastRealtimeStatusUploadedTimestamp;
    FwProperty<Integer> cabinetId;
    FwProperty<Boolean> isBlinking;
    UUID lightUuid;

    public int getCabinetId() {
        return cabinetId.get();
    }

    public void setCabinetId(int cabinetId) {
        this.cabinetId.set(cabinetId);
    }

    public void onNewStatus(CommonRealtimeStatusModel lrs) {
        this.lastRealtimeStatusUploadedTimestamp.set(System.currentTimeMillis());
    }

    public void onDetailUpload(LightDetailModelBase model) {
        this.lightName.set(model.lightName);
        this.description.set(model.description);
        this.axis_x.set(model.axis_x);
        this.axis_y.set(model.axis_y);
        this.axis_z.set(model.axis_z);
        this.terminalIndex.set(model.terminalIndex);
        this.lastInfosUploadedTimestamp.set(System.currentTimeMillis());
        this.isBlinking.set(model.isBlinking);
        this.shortAddress.set(model.shortAddress);
    }


    public void setUuid(UUID id) {
        this.lightUuid = id;
    }

    @PostConstruct
    public void post() {
        lightName = addProperty("lightName", "lightName");
        description = addProperty("description", "description");
        axis_x = addProperty(0, "axis_x");
        axis_y = addProperty(0, "axis_y");
        axis_z = addProperty(0, "axis_z");
        lastInfosUploadedTimestamp = addProperty(0L, "lastInfosUploadedTimestamp");
        lastRealtimeStatusUploadedTimestamp = addProperty(0L, "lastRealtimeStatusUploadedTimestamp");
        cabinetId = addProperty(-1, "cabinetId");
        terminalIndex = addProperty(-1, "terminalIndex");
        isBlinking = addProperty(false, "isBlinking");
        shortAddress = addProperty(-1,"shortAddress");

    }

    public LightsManager getLightsManager() {
        return (LightsManager) this.getParentComponent();
    }

    @Override
    public UaNode getParentNode() {
        return this.getLightsManager().getLightsFolderNode();
    }

    public void onFetchDetailsNow() {
        log.info("灯具<{}>数据上传执行", this.lightName.get());
        this.getLightsManager().AskToUpdateLightsInfo(Collections.singletonList(this.lightUuid), this.cabinetId.get());
    }

    public void onBlink(boolean enable) {
        log.info("灯具<{}>闪烁执行", this.lightName.get());
        this.getLightsManager().AskToBlinkLight(Collections.singletonList(this.lightUuid), enable, this.cabinetId.get());
    }

    @Override
    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == this.lightName) {
            this.getNode().setDisplayName(new LocalizedText(this.lightName.get()));
        }
    }

    @Override
    protected LCS_LightBaseNode createUaNode() {
        return new LCS_LightBaseNode(this, this.getName(), this.getName());
    }

    protected static class LCS_LightBaseNode extends LCS_ComponentFastObjectNode {
        LightBase comp;

        protected LCS_LightBaseNode(LightBase comp, String qualifiedName, String objLocalizedText) {
            super(comp, qualifiedName, objLocalizedText);
            this.comp = comp;
            addProperty(comp.lightName);
            addProperty(comp.description);
            addProperty(comp.axis_x);
            addProperty(comp.axis_y);
            addProperty(comp.axis_z);
            addProperty(comp.lastInfosUploadedTimestamp);
            addProperty(comp.lastRealtimeStatusUploadedTimestamp);
            addProperty(comp.cabinetId);
            addProperty(comp.terminalIndex);
            addProperty(comp.isBlinking);
            addProperty(comp.shortAddress);
            addAdditionalDeclares(methods.values());
        }

        public void ownerChanged(FwProperty property) {
            super.ownerChanged(property);
        }


        private enum methods implements UaHelperUtil.UaMethodDeclare {
            blink(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("enable", Identifiers.Boolean)}, null),
            fetchDetailsNow();
//            removeThisLight();


            private UaHelperUtil.MethodArgument[] in = null;
            private UaHelperUtil.MethodArgument[] out = null;

            methods(UaHelperUtil.MethodArgument[] inputArguments, UaHelperUtil.MethodArgument[] outputArguments) {
                this();
                this.in = inputArguments;
                this.out = outputArguments;
            }

            methods() {
            }

            @Override
            public UaHelperUtil.MethodArgument[] getInputArguments() {
                return this.in;
            }

            @Override
            public UaHelperUtil.MethodArgument[] getOutputArguments() {
                return this.out;
            }
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.blink) {
                comp.onBlink(input[0].booleanValue());
                return null;
            } else if (declared == methods.fetchDetailsNow) {
                this.comp.onFetchDetailsNow();
                return null;
            }
            return super.onMethodCall(declared, input);
        }
    }
}
