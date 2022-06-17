package com.pxccn.PxcDali2.server.space.lights;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.events.LightsDetailUploadEvent;
import com.pxccn.PxcDali2.server.events.DoLightsRealtimeStatusModelEvent;
import com.pxccn.PxcDali2.server.events.DaliLightsRealtimeStatusModelEvent;
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
import org.springframework.util.Assert;

import java.util.*;

//@FwComponentAnnotation
@Component
@Slf4j
public class LightsManager extends FwUaComponent<LightsManager.LCS_GlobalLightsManagerNode> {

    @Autowired
    CabinetRequestService cabinetRequestService;

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        var message = event.getMessage();
        var uid = message.getUuid();
        var cabinetId = message.getCabinetId();
        switch (message.getEvent()) {
            case LightInfoChange:
                log.debug("灯具<{}>信息变更", uid);
                this.AskToUpdateLightsInfo(Collections.singletonList(uid), cabinetId);
                break;
            case LightAdded:
                log.debug("灯具<{}>创建", uid);
                this.AskToUpdateLightsInfo(Collections.singletonList(uid), cabinetId);
                break;
            case LightRemoved:
                log.debug("灯具<{}>被移除", uid);
                this.removeProperty(uid.toString());
                break;
        }

    }

    @EventListener
    public void onDaliLightsRealtimeStatusModelEvent(DaliLightsRealtimeStatusModelEvent event) {
        event.getModelList().forEach(m -> {
            var light = (Dali2Light)this.GetOrCreateLight(m.id, event.getCabinetId(), LightType.DALI2);
            light.onNewStatus(m);
        });
    }

    @EventListener
    public void onDoLightsRealtimeStatusModelEvent(DoLightsRealtimeStatusModelEvent event) {
        event.getModelList().forEach(m -> {
            var light = (DoLight)this.GetOrCreateLight(m.id, event.getCabinetId(), LightType.DO);
            light.onNewStatus(m);
        });
    }

    @EventListener
    public void onLightDetailUploadEvent(LightsDetailUploadEvent event) {
        event.getMessage().getDali2LightDetailModels().forEach(model -> {
            var light = (Dali2Light)this.GetOrCreateLight(model.uuid, event.getCabinetId(), LightType.DALI2);
            light.onDetailUpload(model);
        });
        event.getMessage().getDoLightDetailModels().forEach(model->{
            var light = (DoLight)this.GetOrCreateLight(model.uuid, event.getCabinetId(), LightType.DO);
            light.onDetailUpload(model);
        });
    }


    public void AskToUpdateLightsInfo(List<UUID> lightsUuid, int cabinetId) {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(cabinetId), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), false, lightsUuid)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.info("从控制柜<{}>获取灯具详细信息成功", cabinetId);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("无法从控制柜<{}>获取灯具详细信息:{}", cabinetId, t.getMessage());
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

    public LightBase GetOrCreateLight(UUID lightUuid) {
        return this.GetOrCreateLight(lightUuid, null, null);
    }

    public enum LightType{
        DALI2,DO
    }

    public LightBase GetOrCreateLight(UUID lightUuid, Integer cabinetId, LightType type) {
        String lightPropName = lightUuid.toString();
        FwProperty p;
        synchronized (this) {
            p = this.getProperty(lightPropName);
            if (p == null) {
                Assert.notNull(type, "内部错误,灯具没有被注册");
                LightBase light = null;
                if (type == LightType.DALI2)
                    light = context.getBean(Dali2Light.class);
                else if (type == LightType.DO)
                    light = context.getBean(DoLight.class);
                Assert.notNull(light, "未知灯具类型");
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
        return light;
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
            lights
        }
    }

}
