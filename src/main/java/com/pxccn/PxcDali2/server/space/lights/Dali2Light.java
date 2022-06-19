package com.pxccn.PxcDali2.server.space.lights;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightDetailModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.UUID;

@FwComponentAnnotation
@Slf4j
public class Dali2Light extends LightBase {
    FwProperty<Double> subscribe_level;
    FwProperty<String> subscribe_fault;
    FwProperty<String> lightType;

    @Autowired
    CabinetRequestService cabinetRequestService;

    @PostConstruct
    public void post() {
        super.post();
        lightType = addProperty("DALI2", "lightType");
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

    public void onSetNewAddress(int newAddress) {
        log.debug("执行灯具<{}>短地址从{}修改到{}", this.lightName.get(), this.shortAddress.get(), newAddress);
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(this.cabinetId.get()), ActionWithFeedbackRequestWrapper.SetShortAddress(Util.NewCommonHeaderForClient(), new UUID[]{this.lightUuid}, newAddress)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.info("执行灯具<{}>短地址从{}修改到{},指令下发成功",lightName.get(), shortAddress.get(), newAddress);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("执行灯具<{}>短地址从{}修改到{},指令下发失败:{}",lightName.get(), shortAddress.get(), newAddress,t.getMessage());
            }
        }, MoreExecutors.directExecutor());
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
    }
}
