package com.pxccn.PxcDali2.server.space.lights;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightDetailModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwPropFlag;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@FwComponentAnnotation
@Slf4j
public class Dali2Light extends LightBase {
    FwProperty<Double> subscribe_level;
    FwProperty<String> subscribe_fault;
    FwProperty<String> lightType;


    @Override
    void cabinetStatusChanged(boolean isOnline) {
        var msg = logStr("Cabinet state change: isOnline={}",isOnline);
        if (!isOnline) {
            log.warn(msg);
            uaAlarmEventService.failureEvent(this,msg);
            this.subscribe_level.set(-1.0);
            this.subscribe_fault.set("cabinet offline");
        }else{
            log.debug(msg);
        }
    }

    @Override
    public double getBrightness() {
        return this.subscribe_level.get();
    }

    @Override
    public String getErrorMsg() {
        return this.subscribe_fault.get();
    }

    @PostConstruct
    public void init() {
        super.init();
        lightType = addProperty("DALI2", "lightType");
        subscribe_level = addProperty(0.0, "subscribe_level", FwPropFlag.READ_WRITE);
        subscribe_fault = addProperty("", "subscribe_fault");

    }

    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (context == FwContext.BY_OPCUA && property == this.subscribe_level) {
            var m = logStr("brightness change to {} by ua node",this.subscribe_level.get());
            log.debug(m);
            uaAlarmEventService.debugEvent(this,m);
            var value = this.subscribe_level.get();
            if (value < 0 || value > 100) {
                throw new RuntimeException("", new StatusException(StatusCodes.Bad_OutOfRange));
            }
            this.sendCtlCommand(new Dali2LightCommandModel(Dali2LightCommandModel.Instructions.DirectPwr_percent, value.intValue()));
        }
        else if(property == this.subscribe_fault){
            checkAlarm();
        }
    }


    public void onNewStatus(Dali2LightRealtimeStatusModel lrs) {
        log.trace(logStr("onNewStatus: lrs={}",lrs));
        super.onNewStatus(lrs);
        this.subscribe_level.set(lrs.brightness);
        this.subscribe_fault.set(lrs.getErrorMessage() != null ? lrs.getErrorMessage() : "");
    }

    public void onDetailUpload(Dali2LightDetailModel model) {
        super.onDetailUpload(model);
    }

    public void onSetNewAddress(int newAddress) {
        log.trace(logStr("onSetNewAddress: newAddress={}", newAddress));
        this.actionFeedback.set("");
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.cabinetId.get()),
                ActionWithFeedbackRequestWrapper.SetShortAddress(
                        Util.NewCommonHeaderForClient(),
                        Collections.singleton(this.lightUuid).toArray(UUID[]::new),
                        newAddress),
                (ResponseWrapper) -> {
                    var m = logStr("receive the SetNewAddress command");
                    log.info(m);
                    uaAlarmEventService.successEvent(Dali2Light.this,m);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null) {
                    var m = logStr("Internal Error");
                    log.error(m);
                    uaAlarmEventService.failureEvent(Dali2Light.this,m);
                    return;
                }
                if (result.getFeedback() instanceof AsyncActionFeedbackWrapper.SetShortAddress) {
                    var m = logStr("success to execute SetNewAddress");
                    log.info(m);
                    uaAlarmEventService.successEvent(Dali2Light.this,m);
                    actionFeedback.set("修改短地址成功,现在为" + newAddress);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                var m = logStr("fail to execute SetNewAddress command",t);
                log.error(m);
                actionFeedback.set("修改短地址失败:" + t.getMessage());

            }
        }, MoreExecutors.directExecutor());
    }

    private void checkAlarm(){
        var alarmNode = this.getLightErrorAlarmNode();
        if(alarmNode == null){
            return;
        }
        if(!this.subscribe_fault.get().isEmpty()){
            if (this.subscribe_fault.get().contains("Timed out waiting for response")){
                // 通信异常，忽略
            }else{
                alarmNode.activateAlarm(this.lightName.get(),this.description.get(),this.subscribe_fault.get(),500);
            }
        }else{
            alarmNode.inactivateAlarm(false);
        }
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
            addProperty(comp.shortAddress);
            addProperty(comp.lightType);
            addAdditionalDeclares(methods.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.setShortAddress) {
                int newAddress = input[0].intValue();
                if (!(newAddress >= 0 && newAddress < 64))
                    throw new StatusException(StatusCodes.Bad_OutOfRange);
                comp.onSetNewAddress(newAddress);
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            setShortAddress(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("newAddress", Identifiers.Int32)}, null);


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
