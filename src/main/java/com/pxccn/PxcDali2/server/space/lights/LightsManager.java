package com.pxccn.PxcDali2.server.space.lights;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.events.*;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * 灯具管理器
 */
@Component
@Slf4j
public class LightsManager extends FwUaComponent<LightsManager.LCS_GlobalLightsManagerNode> {

    @Autowired
    CabinetRequestService cabinetRequestService;
    @Autowired
    UaAlarmEventService uaAlarmEventService;

    @Autowired
    CabinetsManager cabinetsManager;
    ExecutorService executorService = LcsExecutors.newWorkStealingPool(8, getClass());

    FwProperty<Integer> count;

    @PostConstruct
    public void init(){
        this.count = addProperty(0,"count");
    }

    public void updateCount(){
        this.count.set(this.getPropertyCount());
    }

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        executorService.execute(()->{
            var message = event.getMessage();
            var uid = message.getUuid();
            var cabinetId = message.getCabinetId();
            String logString = "";
            switch (message.getEvent()) {
                case LightInfoChange:
                    logString = logStr("Light<{}> LightInfoChange", uid);
                    log.debug(logString);
                    uaAlarmEventService.debugEvent(this, logString);
                    this.AskToUpdateLightsInfo(Collections.singletonList(uid), cabinetId);
                    break;
                case LightAdded:
                    logString = logStr("Light<{}> LightAdded", uid);
                    log.debug(logString);
                    uaAlarmEventService.debugEvent(this, logString);
                    this.AskToUpdateLightsInfo(Collections.singletonList(uid), cabinetId);
                    break;
                case LightRemoved:
                    logString = logStr("Light<{}> LightRemoved", uid);
                    log.debug(logString);
                    uaAlarmEventService.debugEvent(this, logString);
                    this.removeProperty(uid.toString());
                    break;
            }
        });
    }

    public boolean checkHasLight(UUID lightUuid) {
        return this.getProperty(lightUuid.toString()) != null;
    }

    @EventListener
    public void onDaliLightsRealtimeStatusModelEvent(DaliLightsRealtimeStatusModelEvent event) {
        executorService.submit(() -> {
            event.getModelList().forEach(m -> {
                var light = (Dali2Light) this.GetOrCreateLight(m.id, event.getCabinetId(), LightType.DALI2);
                light.onNewStatus(m);
            });
        });
    }

    @EventListener
    public void onDoLightsRealtimeStatusModelEvent(DoLightsRealtimeStatusModelEvent event) {
        executorService.execute(()->{
            event.getModelList().forEach(m -> {
                var light = (DoLight) this.GetOrCreateLight(m.id, event.getCabinetId(), LightType.DO);
                light.onNewStatus(m);
            });
        });
    }

    @EventListener
    public void onLightDetailUploadEvent(LightsDetailUploadEvent event) {
        executorService.execute(()->{
            int cabinetId = event.getMessage().getCabinetId();
            event.getMessage().getDali2LightDetailModels().forEach(model -> {
                var light = (Dali2Light) this.GetOrCreateLight(model.uuid, cabinetId, LightType.DALI2);
                light.onDetailUpload(model);
            });
            event.getMessage().getDoLightDetailModels().forEach(model -> {
                var light = (DoLight) this.GetOrCreateLight(model.uuid, cabinetId, LightType.DO);
                light.onDetailUpload(model);
            });
        });
    }

//    /**
//     * 同步修改灯具名称
//     * @param lightUUID
//     * @param newName
//     */
//    public ChangeLightNameResult changeLightNameSync(UUID lightUUID,String newName){
//        log.trace(logStr("changeLightName: lightUUID={},newName={}",lightUUID,newName));
//        var light = this.getLightByUUID(lightUUID);
////        if(light == null){
////            this.
////        }
//    }
//    public static class ChangeLightNameResult{
//        public final boolean success;
//        public final String reason;
//        ChangeLightNameResult(){
//            this.success = true;
//            this.reason = "";
//        }
//        ChangeLightNameResult(String reason){
//            this.success = false;
//            this.reason = reason;
//        }
//    }

    public void AskToUpdateLightsInfo(List<UUID> lightsUuid, int cabinetId) {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(cabinetId), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), false, lightsUuid)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                var msg = logStr("execute AskToUpdateLightsInfo command success from cabinet {}", cabinetId);
                log.info(msg);
                uaAlarmEventService.successEvent(LightsManager.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("Can not execute AskToUpdateLightsInfo command from cabinet{} ", cabinetId, t);
                log.error(msg);
                uaAlarmEventService.failureEvent(LightsManager.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    public void AskToBlinkLight(List<UUID> lightsUuid, boolean enable, int cabinetId) {
        var msg = logStr("AskToBlinkLight:lightsUuid={},enable={},cabinetId={}",lightsUuid,enable,cabinetId);
        log.trace(msg);
        uaAlarmEventService.debugEvent(this, msg);

        Futures.addCallback(cabinetRequestService.asyncSendWithAsyncFeedback(RpcTarget.ToCabinet(cabinetId),
                ActionWithFeedbackRequestWrapper.Blink(Util.NewCommonHeaderForClient(), lightsUuid.toArray(new UUID[0]), enable), (ResponseWrapper) -> {
                    log.debug("Cabinet<{}> received AskToBlinkLight command", cabinetId);
                }, 20000), new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if ((result == null || result.getFeedback() == null) && result.getFeedback() instanceof AsyncActionFeedbackWrapper.Blink) {
                    log.error("Internal Error");
                    uaAlarmEventService.failureEvent(LightsManager.this, "Internal Error");
                    return;
                }
                var msg2 = "execute AskToBlinkLight command success -- " + msg;
                log.info(msg2);
                uaAlarmEventService.successEvent(LightsManager.this, msg2);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to execute AskToBlinkLight command", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(LightsManager.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    public void AskToUpdateLightsInfo(List<UUID> lightsUuid) {
        Map<Integer, List<UUID>> tmp = new HashMap<>();
        lightsUuid.stream().map(this::getLightByUUID).filter(Objects::nonNull).forEach(l -> {
            tmp.computeIfAbsent(l.getCabinetId(), (i) -> new ArrayList<>()).add(l.lightUuid);
        });

        tmp.forEach((cabinetId, lightUuid) -> {
            this.AskToUpdateLightsInfo(lightsUuid, cabinetId);
        });
    }


    //        @Cacheable(value = "getLightByUUIDCache", condition = "#result != null")
    public LightBase getLightByUUID(UUID lightUUID) {
        var p = Arrays.stream(this.getAllProperty()).filter(i -> i.getName().equals(lightUUID.toString())).findFirst().orElse(null);
        if (p != null) {
            return (LightBase) p.get();
        } else {
            return null;
        }
    }

//    @Override
//    public void onEvent(Object event) {
//        if (event instanceof LightStatusMessageMqCompEvent) {
//            var msg = ((LightStatusMessageMqCompEvent) event).getMessage();
//            var light = this.GetOrCreateLight(msg.lightId);
//            light.onNewStatus(msg);
//        }
//    }

    public void started() {
        super.started();
//        addProperty(new LightBase(), "light3");

    }



    public LightBase[] findAllLightsWithCabinetId(int cabinetId) {
        return Arrays.stream(this.getAllProperty(LightBase.class, false))
                .map(i -> (LightBase) i.get())
                .filter(i -> i.getCabinetId() == cabinetId)
                .toArray(LightBase[]::new);
    }

    @EventListener
    public void onCabinetAliveChangedEvent(CabinetAliveChangedEvent event) {
        executorService.execute(()->{
            var cabinet = ((Cabinet) event.getSource());
            for (var l : this.findAllLightsWithCabinetId(cabinet.getCabinetId())) {
                l.cabinetStatusChanged(event.isOnLine());
            }
        });
    }

    public LightBase GetOrCreateLight(UUID lightUuid) {
        return this.GetOrCreateLight(lightUuid, null, null);
    }

    public LightBase GetOrCreateLight(UUID lightUuid, Integer cabinetId, LightType type) {
        String lightPropName = lightUuid.toString();
        FwProperty p;
        synchronized (this) {
            p = this.getProperty(lightPropName);
            if (p == null) {
                Assert.notNull(type, "Internal Error, the light has not been registered");
                LightBase light = null;
                if (type == LightType.DALI2)
                    light = context.getBean(Dali2Light.class);
                else if (type == LightType.DO)
                    light = context.getBean(DoLight.class);
                Assert.notNull(light, "unknown light type");
                light.setUuid(lightUuid);
                if (cabinetId != null)
                    light.setCabinetId(cabinetId);
                p = addProperty(light, lightPropName);
            }
        }
        var light = (LightBase) p.get();
        if (cabinetId != null && cabinetId.intValue() != light.getCabinetId()) {
            light.setCabinetId(cabinetId.intValue());
        }
        updateCount();
        return light;
    }

    UaNode getLightsFolderNode() {
        return this.getNode().getDeclaredNode(LCS_GlobalLightsManagerNode.folders.lights);
    }

    @Override
    protected LCS_GlobalLightsManagerNode createUaNode() {
        return new LCS_GlobalLightsManagerNode(this, this.getName());
    }

    public enum LightType {
        DALI2, DO
    }

    protected static class LCS_GlobalLightsManagerNode extends LCS_ComponentFastObjectNode {
        LightsManager comp;

        protected LCS_GlobalLightsManagerNode(FwUaComponent uaComponent, String qname) {
            super(uaComponent, qname);
            comp = (LightsManager) uaComponent;
            addProperty(comp.count);
            addAdditionalDeclares(folders.values()
                    , methods.values()
            );
        }

        private enum folders implements UaHelperUtil.UaFolderDeclare {
            lights
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            fetchAllLightsDetail();

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
            if (declared == methods.fetchAllLightsDetail) {
                comp.cabinetsManager.updateAllOnlineCabinetLightsAndRoomsInfo();
                return null;
            }
            return super.onMethodCall(declared, input);
        }
    }

}
