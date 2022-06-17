package com.pxccn.PxcDali2.server.space.cabinets;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PollManagerSettingRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetDetailUploadWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.events.CabinetAliveChangedEvent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@FwComponentAnnotation
@Slf4j
public class Cabinet extends FwUaComponent<Cabinet.CabinetNode> {

    @Autowired
    CabinetRequestService cabinetRequestService;
    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    LightsManager lightsManager;

    FwProperty<String> Name;
    FwProperty<String> Description;
    FwProperty<String> Version;
    FwProperty<Boolean> IsMaintenance;
    FwProperty<Integer> Axis_x;
    FwProperty<Integer> Axis_y;
    FwProperty<Integer> Axis_z;
    FwProperty<Integer> CabinetId;
    FwProperty<Props> Props;
    FwProperty<Boolean> IsAlive;
    FwProperty<Long> LastConnectionLostTimeStamp;
    FwProperty<Long> LastConnectionEstablishTimeStamp;
    FwProperty<Long> LastInfosUploadedTimestamp;
    FwProperty<DateTime> LastInfosUploadedTimestamp2;

    @PostConstruct
    public void post() {
        Name = addProperty("name..", "cabinetName");
        Description = addProperty("name..", "description");
        CabinetId = addProperty(0, "cabinetId");
        Props = addProperty(context.getBean(Props.class), "realtimeStatus");
        IsAlive = addProperty(false, "isAlive");
        LastConnectionLostTimeStamp = addProperty(0L, "lastConnectionLostTimeStamp");
        LastConnectionEstablishTimeStamp = addProperty(0L, "lastConnectionEstablishTimeStamp");
        LastInfosUploadedTimestamp = addProperty(0L, "lastInfosUploadedTimestamp");
        Axis_x = addProperty(0, "axis_x");
        Axis_y = addProperty(0, "axis_y");
        Axis_z = addProperty(0, "axis_z");
        Version = addProperty("", "version");
        IsMaintenance = addProperty(true, "isMaintenance");
        LastInfosUploadedTimestamp2 = addProperty(DateTime.MIN_VALUE, "dt");
    }

    private long _lastMsgTimestamp = System.currentTimeMillis();

    public void checkIsNotAlive() {
        if (System.currentTimeMillis() - this._lastMsgTimestamp > 30000) {
            this.IsAlive.set(false);
        }
    }

    public void onDetailUpload(CabinetDetailUploadWrapper detail) {
        this.LastInfosUploadedTimestamp.set(System.currentTimeMillis());
        this.LastInfosUploadedTimestamp2.set(DateTime.currentTime());
        this.Name.set(detail.cabinetName);
        this.Description.set(detail.description);
        this.Axis_x.set(detail.axis_x);
        this.Axis_y.set(detail.axis_y);
        this.Axis_z.set(detail.axis_z);
        this.Version.set(detail.getHeaders().get("ver"));
        this.IsMaintenance.set(detail.isMaintenance);
    }

    public void started() {
        super.started();
        log.info("注册控制柜<{}>...", this.CabinetId.get());
        this.AskToUpdateLightsAndRoomInfo();//要求控制器上传灯具和房间的具体信息
        this.purgeCacheForUpdateAllRealtimeStatus();//要求控制器更新所有灯具亮度
    }


    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == IsAlive) {
            var tp = System.currentTimeMillis();
            if (IsAlive.get()) {
                log.info("控制柜<{}>上线！", this.CabinetId.get());
                this.LastConnectionEstablishTimeStamp.set(tp);
            } else {
                log.error("控制柜<{}>离线！", this.CabinetId.get());
                this.LastConnectionLostTimeStamp.set(tp);
            }
            this.context.publishEvent(new CabinetAliveChangedEvent(this, this.IsAlive.get(), tp));
        } else if (property == this.Name) {
            this.getNode().setDisplayName(new LocalizedText(this.Name.get()));
        }
    }

    /**
     * 清除控制器实时状态缓存
     */
    public void purgeCacheForUpdateAllRealtimeStatus(){
        var request = new PollManagerSettingRequestWrapper.PollManagerParam();
        request.purgeCache = true;
        this.setPollManager(request);
    }

    public void setPollManager(PollManagerSettingRequestWrapper.PollManagerParam param) {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new PollManagerSettingRequestWrapper(Util.NewCommonHeaderForClient(), param)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.debug("控制柜<{}>完成PollManager设置:{}", Name.get(), param);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("控制柜<{}>无法完成PollManager设置:{} :{}", Name.get(), param, t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void updateStatus(CabinetStatusWrapper status) {
        this.Props.get().accept(status.getPropsMap());
        this._lastMsgTimestamp = System.currentTimeMillis();
        this.IsAlive.set(true);
    }

    /**
     * 请求控制器更新所有灯具和房间详细信息
     */
    public void AskToUpdateLightsAndRoomInfo() {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), true, null)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.info("从控制柜<{}>获取灯具与房间详细信息成功", Name.get());
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("无法从控制柜<{}>获取灯具与房间详细信息:{}", Name.get(), t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * 请求控制器更新控制柜基本信息
     */
    public void AskToUpdateCabinetInfo() {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), false, Collections.singletonList(new UUID(0, 0)))), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.info("控制柜<{}>获取信息成功", Name.get());
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("无法从控制柜<{}>获取详细信息:{}", Name.get(), t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void setCid(int cid) {
        this.CabinetId.set(cid);
    }

    public CabinetsManager getCabinetManager() {
        return (CabinetsManager) this.getParentComponent();
    }

    public UaNode getParentNode() {
        return this.getCabinetManager().getCabinetsFolderNode();
    }

    @Override
    protected CabinetNode createUaNode() {
        return new CabinetNode(this, this.getName());//控制柜节点
    }

    protected static class CabinetNode extends LCS_ComponentFastObjectNode {
        Cabinet comp;

        protected CabinetNode(Cabinet uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addProperty(uaComponent.Name);
            addProperty(uaComponent.CabinetId);
            addProperty(uaComponent.IsAlive);
            addProperty(uaComponent.Description);
            addProperty(uaComponent.LastConnectionLostTimeStamp);
            addProperty(uaComponent.LastConnectionEstablishTimeStamp);
            addProperty(uaComponent.LastInfosUploadedTimestamp);
            addProperty(uaComponent.Axis_x);
            addProperty(uaComponent.Axis_y);
            addProperty(uaComponent.Axis_z);
            addProperty(uaComponent.Version);
            addProperty(uaComponent.IsMaintenance);
            addProperty(uaComponent.LastInfosUploadedTimestamp2);
            addAdditionalDeclares(methods.values());
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            fetchCabinetDetailsNow(),
            fetchRoomAndLightDetailsNow(),
            setPollManager(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("setNormalDelay", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("normalDelay", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("getHighPriorityDelay", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("highPriorityDelay", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("setHighPriorityQueueLength", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("highPriorityQueueLength", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("setHighPriorityBundleSize", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("highPriorityBundleSize", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("setNormalPriorityBundleSize", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("normalPriorityBundleSize", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("setNotifyChangeOnly", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("notifyChangeOnly", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("purgeCache", Identifiers.Boolean),
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

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.fetchCabinetDetailsNow) {
                this.comp.AskToUpdateCabinetInfo();
                return null;
            } else if (declared == methods.setPollManager) {
                var param = new PollManagerSettingRequestWrapper.PollManagerParam();
                if (input[0].booleanValue())
                    param.normalDelay = input[1].intValue();
                if (input[2].booleanValue())
                    param.highPriorityDelay = input[3].intValue();
                if (input[4].booleanValue())
                    param.highPriorityQueueLength = input[5].intValue();
                if (input[6].booleanValue())
                    param.highPriorityBundleSize = input[7].intValue();
                if (input[8].booleanValue())
                    param.normalPriorityBundleSize = input[9].intValue();
                if (input[10].booleanValue())
                    param.notifyChangeOnly = input[11].booleanValue();
                param.purgeCache = input[12].booleanValue();
                this.comp.setPollManager(param);
                return null;
            } else if (declared == methods.fetchRoomAndLightDetailsNow) {
                this.comp.AskToUpdateLightsAndRoomInfo();
                return null;
            }
            return super.onMethodCall(declared, input);
        }
    }

}
