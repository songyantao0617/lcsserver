package com.pxccn.PxcDali2.server.space.lights;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.CommonRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.LightDetailModelBase;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.opcua.type.enums.LCS_AlarmLightErrorTypeNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;

@FwComponentAnnotation
@Slf4j
public abstract class LightBase extends FwUaComponent<LightBase.LCS_LightBaseNode> {
    private final Vector<Consumer<LightBase>> oVector = new Vector<>(2);
    @Autowired
    CabinetsManager cabinetsManager;
    @Autowired
    CabinetRequestService cabinetRequestService;
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
    FwProperty<String> actionFeedback;
    UUID lightUuid;

    LCS_AlarmLightErrorTypeNode alarmNode;

    public LCS_AlarmLightErrorTypeNode getLightErrorAlarmNode() {
        return this.alarmNode;
    }

    public UUID getLightUuid() {
        return lightUuid;
    }

    public String getLightName(){
        return lightName.get();
    }

    abstract void cabinetStatusChanged(boolean isOnline);

    //增加一个观察者，相当于观察者注册
    public void addListener(Consumer<LightBase> observer) {
        if (!this.oVector.contains(observer))
            this.oVector.add(observer);
    }

    public void deleteObserver(Consumer<LightBase> observer) {
        this.oVector.remove(observer);
    }

    public void notifyObserver() {
        for (Consumer<LightBase> observer : this.oVector) {
            try {
                observer.accept(this);
            } catch (Exception e) {
                log.error("notifyObserverError", e);
            }
        }
    }


    public abstract double getBrightness();

    public abstract String getErrorMsg();

    public int getCabinetId() {
        return cabinetId.get();
    }

    public int getTerminalIndex(){
        return this.terminalIndex.get();
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
    public void init() {
        lightName = addProperty("", "lightName");
        description = addProperty("", "description");
        axis_x = addProperty(0, "axis_x");
        axis_y = addProperty(0, "axis_y");
        axis_z = addProperty(0, "axis_z");
        lastInfosUploadedTimestamp = addProperty(0L, "lastInfosUploadedTimestamp");
        lastRealtimeStatusUploadedTimestamp = addProperty(0L, "lastRealtimeStatusUploadedTimestamp");
        cabinetId = addProperty(-1, "cabinetId");
        terminalIndex = addProperty(-1, "terminalIndex");
        isBlinking = addProperty(false, "isBlinking");
        shortAddress = addProperty(-1, "shortAddress");
        actionFeedback = addProperty("", "actionFeedback");

    }

    public void changeLightName(String name){
        var m = logStr("changeLightName: name={}",name);
        log.trace(m);
        uaAlarmEventService.debugEvent(this,m);
        var cabinet = this.getCabinet();
        if (!cabinet.isAlive()){
            log.error("changeLightName failure! cabinet is not alive");
            uaAlarmEventService.failureEvent(this,"changeLightName failure! cabinet is not alive");
        }
        Map<UUID,String> p = new HashMap<>();
        p.put(this.getLightUuid(),name);
        cabinet.changeLightName(p);
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
            this.getNode().setBrowseName(new QualifiedName(getNode().getNodeId().getNamespaceIndex(), this.lightName.get()));
        }

        this.notifyObserver();
    }

    public Cabinet getCabinet() {
        return cabinetsManager.GetOrCreateCabinet(this.cabinetId.get());
    }

    protected String getLogLocate() {
        return this.lightName.get()+"("+this.getLightUuid()+")";
    }

    public void sendCtlCommand(
            Dali2LightCommandModel dali2LightCommandModel
    ) {
        this.sendCtlCommand(dali2LightCommandModel, Dt8CommandModel.NONE);
    }

    public void sendCtlCommand(
            Dali2LightCommandModel dali2LightCommandModel,
            Dt8CommandModel dt8CommandModel
    ) {
        Futures.addCallback(this.getCabinet().sendCtlCommand(Collections.singletonList(this.lightUuid).toArray(UUID[]::new), dali2LightCommandModel, dt8CommandModel), new FutureCallback<>() {
            @Override
            public void onSuccess(AsyncActionFeedbackWrapper.SendLevelInstruction result) {
                if (result.getCountOfDali2() > 0 || result.getCountOfDo() > 0) {
                    log.info("灯具<{}>成功执行命令:{},{}", lightName.get(), dali2LightCommandModel, dt8CommandModel);
                } else {
                    log.error("灯具<{}>未能被命中！", lightName.get());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("灯具<{}>未能成功执行命令:{}", lightName.get(), t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }


    @Override
    protected LCS_LightBaseNode createUaNode() {
        return new LCS_LightBaseNode(this, this.getName(), this.getName());
    }

    @Override
    protected void afterUaNodeCreated() {
        super.afterUaNodeCreated();
        try {
            var node = this.getNode();
            this.alarmNode = LCS_AlarmLightErrorTypeNode.createInstance(opcuaService.getLcsNodeManager(), this.getNode().getNodeId().getValue() + ".lightErrorAlarm", "lightErrorAlarm");
            this.alarmNode.setSource(node);
            node.addComponent(this.alarmNode);
            this.alarmNode.addReference(node, Identifiers.HasNotifier, true);
//        node.addReference(this.getParentNode(), Identifiers.HasNotifier, true);
            this.alarmNode.enable();
        } catch (Exception e) {
            log.error("Fatal error to create light ua node", e);
        }
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
            addProperty(comp.actionFeedback);

            addAdditionalDeclares(methods.values());
        }

        public void ownerChanged(FwProperty property) {
            super.ownerChanged(property);
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.blink) {
                comp.onBlink(input[0].booleanValue());
                return null;
            } else if (declared == methods.fetchDetailsNow) {
                this.comp.onFetchDetailsNow();
                return null;
            } else if (declared == methods.sendCtlCommand) {
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[0].intValue()), input[1].intValue());
                var CmdDt8 = new Dt8CommandModel(UaHelperUtil.getEnum(Dt8CommandModel.Instructions.class, input[2].intValue()), input[3].intValue(), input[4].intValue());
                this.comp.sendCtlCommand(Cmd103, CmdDt8);
                return null;
            }else if(declared == methods._changeLightName){
                var n = input[0].toString();
                this.comp.changeLightName(n);
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            _changeLightName(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("name", Identifiers.String)}, null),
            blink(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("enable", Identifiers.Boolean)}, null),
            fetchDetailsNow(),

            sendCtlCommand(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8Action", Dt8CommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("dt8ActionParam", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8ActionParam2", Identifiers.Int32),

            }, null);

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
    }
}
