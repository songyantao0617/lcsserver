package com.pxccn.PxcDali2.server.space.cabinets;

import com.google.common.util.concurrent.*;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomLightInfoModel;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomTriggerInfoModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PollManagerSettingRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetDetailUploadWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.database.model.RoomTriggerV3;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.events.CabinetAliveChangedEvent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.db.CabinetQueryService;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@FwComponentAnnotation
@Slf4j
public class Cabinet extends FwUaComponent<Cabinet.CabinetNode> {

    @Value("${LcsServer.offlineThreshold:30000}")
    int offlineThreshold;

    @Value("${LcsServer.timeServer:false}")
    boolean isTimeServer;
    @Autowired
    UaAlarmEventService uaAlarmEventService;
    @Autowired
    CabinetRequestService cabinetRequestService;

    @Autowired
    CabinetQueryService cabinetQueryService;

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    LightsManager lightsManager;

    FwProperty<String> Name;
    FwProperty<String> Description;
    FwProperty<String> Version;
    FwProperty<String> Ip0;
    FwProperty<String> Ip1;
    FwProperty<Boolean> IsMaintenance;
    FwProperty<Integer> Axis_x;
    FwProperty<Integer> Axis_y;
    FwProperty<Integer> Axis_z;
    FwProperty<Integer> CabinetId;
    FwProperty<Props> Props;
    FwProperty<Boolean> IsAlive;
    FwProperty<DateTime> LastConnectionLostTimeStamp;
    FwProperty<DateTime> LastConnectionEstablishTimeStamp;
    FwProperty<DateTime> LastInfosUploadedTimestamp;
    //    FwProperty<DateTime> LastInfosUploadedTimestamp2;
    private long _lastMsgTimestamp = System.currentTimeMillis();

    public boolean isAlive() {
        return this.IsAlive.get();
    }

    @Override
    public String toString() {
        try {
            return logStr("");
        } catch (Exception e) {
            return super.toString();
        }
    }

    public int getCabinetId() {
        return this.CabinetId.get();
    }

    @PostConstruct
    public void post() {
        Name = addProperty("", "cabinetName");
        Description = addProperty("", "description");
        CabinetId = addProperty(0, "cabinetId");
        Props = addProperty(context.getBean(Props.class), "realtimeStatus");
        IsAlive = addProperty(true, "isAlive");
        LastConnectionLostTimeStamp = addProperty(DateTime.MIN_VALUE, "lastConnectionLostTimeStamp");
        LastConnectionEstablishTimeStamp = addProperty(DateTime.MIN_VALUE, "lastConnectionEstablishTimeStamp");
        LastInfosUploadedTimestamp = addProperty(DateTime.MIN_VALUE, "lastInfosUploadedTimestamp");
        Axis_x = addProperty(0, "axis_x");
        Axis_y = addProperty(0, "axis_y");
        Axis_z = addProperty(0, "axis_z");
        Version = addProperty("", "version");
        IsMaintenance = addProperty(true, "isMaintenance");
        Ip0 = addProperty("", "Ip0");
        Ip1 = addProperty("", "Ip1");
    }

    public void checkIsNotAlive() {
        if (System.currentTimeMillis() - this._lastMsgTimestamp > this.offlineThreshold) {
            this.IsAlive.set(false);
        }
    }

    public void onDetailUpload(CabinetDetailUploadWrapper detail) {
        this.LastInfosUploadedTimestamp.set(DateTime.currentTime());
        this.Name.set(detail.cabinetName);
        this.Description.set(detail.description);
        this.Axis_x.set(detail.axis_x);
        this.Axis_y.set(detail.axis_y);
        this.Axis_z.set(detail.axis_z);
        this.Version.set(detail.getHeaders().get("ver"));
        this.IsMaintenance.set(detail.isMaintenance);
    }

    //本函数同样代表控制柜在本次server生命期的首次上线
    public void started() {
        super.started();
        log.info(logStr("started"));


        this.LastConnectionEstablishTimeStamp.set(DateTime.currentTime());
        this.context.publishEvent(new CabinetAliveChangedEvent(this, true, System.currentTimeMillis()));
        this.cabinetDataInit();
    }

    public void cabinetDataInit() {
        log.info(logStr("cabinetDataInit"));
        if (this.isTimeServer) {
            this.setCabinetSystemTime(System.currentTimeMillis());//同步系统时间
        }
        this.AskToUpdateLightsAndRoomInfo();//要求控制器上传灯具和房间的具体信息
        this.purgeCacheForUpdateAllRealtimeStatus();//要求控制器更新所有灯具亮度
        this.disableEnergyMeasuring();//关闭控制器的能源检测功能

    }

    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == IsAlive) {
            if (IsAlive.get()) {
                String msg = logStr("online");
                log.info(msg);
                uaAlarmEventService.sendBasicUaEvent(this, msg, 1);
                this.LastConnectionEstablishTimeStamp.set(DateTime.currentTime());
                cabinetDataInit();
            } else {
                String msg = logStr("offline");
                log.error(msg);
                uaAlarmEventService.sendBasicUaEvent(this, msg, 1);
                this.LastConnectionLostTimeStamp.set(DateTime.currentTime());
            }
            this.context.publishEvent(new CabinetAliveChangedEvent(this, this.IsAlive.get(), System.currentTimeMillis()));
        } else if (property == this.Name) {
            this.getNode().setDisplayName(new LocalizedText(this.Name.get()));
            this.getNode().setBrowseName(new QualifiedName(this.getNode().getNamespaceIndex(),this.Name.get()));
        }
    }

    public void onSendGlobalCtlCommand(Dali2LightCommandModel dali2LightCommandModel) {
        log.trace("onSendGlobalCtlCommand({}): dali2LightCommandModel={}", this.Name.get(), dali2LightCommandModel);
        Futures.addCallback(this.sendBroadcastCommand(-1, dali2LightCommandModel), new FutureCallback<AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction>() {
            @Override
            public void onSuccess(AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction result) {
//                log.info("Cab<{}>执行广播命令（{}）成功,灯具数量：{}", Name.get(), dali2LightCommandModel, result.getCountOfLights());
            }

            @Override
            public void onFailure(Throwable t) {
//                log.error("控制柜<{}>无法执行广播命令：{}", Name.get(), t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * 同步V3房间数据触发器
     * @return
     */
    public ListenableFuture<AsyncActionFeedbackWrapper.V3RoomTriggerUpdate> sendV3RoomTriggerUpdate(){
        log.trace(logStr("sendV3RoomTriggerUpdate"));
        if(!this.isAlive()){
            var m = logStr("can not sendV3RoomTriggerUpdate : cabinet is not alive");
            log.error(m);
            return Futures.immediateFailedFuture(new IllegalStateException(m));
        }
        SettableFuture<AsyncActionFeedbackWrapper.V3RoomTriggerUpdate> future = SettableFuture.create();
        Futures.addCallback(cabinetQueryService.getV3RoomTriggerModelFromCabinetId(this.getCabinetId(), this.getCabinetManager().executorService), new FutureCallback<List<V3RoomTriggerInfoModel.Trigger>>() {
            @Override
            public void onSuccess(@Nullable List<V3RoomTriggerInfoModel.Trigger> result) {
                assert result!=null;
                if(result.size()==0){
                    log.debug(logStr("no trigger need update for this cabinet"));
                    AsyncActionFeedbackWrapper.V3RoomTriggerUpdate r = new AsyncActionFeedbackWrapper.V3RoomTriggerUpdate(Collections.EMPTY_LIST);
                    future.set(r);
                    return;
                }
                var f = cabinetRequestService.asyncSendWithAsyncFeedback(
                        RpcTarget.ToCabinet(getCabinetId()),
                        new ActionWithFeedbackRequestWrapper(Util.NewCommonHeaderForClient(),
                                new ActionWithFeedbackRequestWrapper.V3RoomTriggerUpdate(new V3RoomTriggerInfoModel(result))
                        ),(resp)->{
                            var msg = logStr("receive the sendV3RoomTriggerUpdate command success ");
                            log.info(msg);
                            uaAlarmEventService.successEvent(Cabinet.this, msg);
                        },60000*5);
                Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
                    @Override
                    public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                        if (result == null || result.getFeedback() == null) {
                            log.error("Internal Error! no valid response");
                            return;
                        }
                        if(result.getFeedback() instanceof AsyncActionFeedbackWrapper.V3RoomTriggerUpdate){
                            var msg = logStr("execute the sendV3RoomTriggerUpdate command success :{}", result.getFeedback());

                            var fbList = ((AsyncActionFeedbackWrapper.V3RoomTriggerUpdate) result.getFeedback()).getFeedbacks();

                            //TODO
                            fbList.forEach(fb->{
                                cabinetQueryService.updateTriggerFeedback2(fb.getTriggerUuid(),fb.getMsg());
                            });

                            log.info(msg);
                            uaAlarmEventService.successEvent(Cabinet.this, msg);
                            future.set((AsyncActionFeedbackWrapper.V3RoomTriggerUpdate) result.getFeedback());
                        }else {
                            future.setException(new IllegalStateException("not valid response"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        var msg = logStr("fail to sendV3RoomTriggerUpdate", t);
                        log.error(msg);
                        uaAlarmEventService.failureEvent(Cabinet.this, msg);
                        future.setException(t);
                    }
                },MoreExecutors.directExecutor());
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to query triggers from db", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                future.setException(t);
            }
        },MoreExecutors.directExecutor());

        return future;
    }

    /**
     * 同步V3房间基本信息
     * @return
     */
    public ListenableFuture<AsyncActionFeedbackWrapper.V3RoomUpdate> sendV3RoomUpdate(){
        log.trace(logStr("sendV3RoomUpdate"));
        if(!this.isAlive()){
            var m = logStr("can not sendV3RoomUpdate : cabinet is not alive");
            log.error(m);
            return Futures.immediateFailedFuture(new IllegalStateException(m));
        }
        SettableFuture<AsyncActionFeedbackWrapper.V3RoomUpdate> future = SettableFuture.create();
        Futures.addCallback(cabinetQueryService.getV3RoomModelFromCabinetId(getCabinetId(),this.getCabinetManager().executorService), new FutureCallback<List<V3RoomLightInfoModel.Room>>() {
            @Override
            public void onSuccess(@Nullable List<V3RoomLightInfoModel.Room> result) {
                var f = cabinetRequestService.asyncSendWithAsyncFeedback(
                        RpcTarget.ToCabinet(getCabinetId()),
                        new ActionWithFeedbackRequestWrapper(Util.NewCommonHeaderForClient(),
                                new ActionWithFeedbackRequestWrapper.V3RoomUpdate(new V3RoomLightInfoModel(result))
                        ),(resp)->{
                            var msg = logStr("receive the sendV3RoomUpdate command success ");
                            log.info(msg);
                            uaAlarmEventService.successEvent(Cabinet.this, msg);
                        },60000*5);
                Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
                    @Override
                    public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                        if (result == null || result.getFeedback() == null) {
                            log.error("Internal Error! no valid response");
                            return;
                        }
                        if(result.getFeedback() instanceof AsyncActionFeedbackWrapper.V3RoomUpdate){
                            var msg = logStr("execute the sendV3RoomUpdate command success :{}", result.getFeedback());
                            log.info(msg);
                            uaAlarmEventService.successEvent(Cabinet.this, msg);
                            future.set((AsyncActionFeedbackWrapper.V3RoomUpdate) result.getFeedback());
                        }else {
                            future.setException(new IllegalStateException("not valid response"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        var msg = logStr("fail to sendV3RoomUpdate", t);
                        log.error(msg);
                        uaAlarmEventService.failureEvent(Cabinet.this, msg);
                        future.setException(t);
                    }
                },MoreExecutors.directExecutor());
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to query v3 info from database", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                future.setException(t);
            }
        },MoreExecutors.directExecutor());
        return future;
    }

    public void executeV3Sync(){
        log.info(logStr("executeV3Sync"));
        Futures.addCallback(this.sendV3RoomUpdate(), new FutureCallback<AsyncActionFeedbackWrapper.V3RoomUpdate>() {
            @Override
            public void onSuccess(AsyncActionFeedbackWrapper.@Nullable V3RoomUpdate result) {
                Futures.addCallback(sendV3RoomTriggerUpdate(), new FutureCallback<AsyncActionFeedbackWrapper.V3RoomTriggerUpdate>() {
                    @Override
                    public void onSuccess(AsyncActionFeedbackWrapper.@Nullable V3RoomTriggerUpdate result) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                },MoreExecutors.directExecutor());
            }

            @Override
            public void onFailure(Throwable t) {
                //如果此步骤失败，则使用传统方式
                var msg = logStr("fail to executeV3Sync, use sendDbSync instead", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                sendDbSync(2);
            }
        },MoreExecutors.directExecutor());
    }

    public ListenableFuture<AsyncActionFeedbackWrapper.DbSync> sendDbSync(int flag) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("sendDbSync({}): flag={}", this.Name.get(), flag));
        }

        SettableFuture<AsyncActionFeedbackWrapper.DbSync> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                new ActionWithFeedbackRequestWrapper(Util.NewCommonHeaderForClient(),
                        new ActionWithFeedbackRequestWrapper.DbSync(flag)), (resp) -> {
                    var msg = logStr("receive the sendDbSync command success ");
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                },
                1000 * 60 * 10);
        Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null) {
                    log.error("Internal Error! no valid response");
                    return;
                }
                if (result.getFeedback() instanceof AsyncActionFeedbackWrapper.DbSync) {
                    var msg = logStr("execute the sendDbSync command success :{}", ((AsyncActionFeedbackWrapper.DbSync) result.getFeedback()).getMsg());
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                    future.set((AsyncActionFeedbackWrapper.DbSync) result.getFeedback());
                } else {
                    future.setException(new IllegalStateException("not valid response"));
                }

            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to sendDbSync", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                future.setException(t);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }


    public ListenableFuture<AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction> sendBroadcastCommand(int terminalIndex, Dali2LightCommandModel dali2LightCommandModel) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("sendBroadcastCommand({}): terminalIndex={},dali2LightCommandModel={}", this.Name.get(), terminalIndex, dali2LightCommandModel));
        }
        SettableFuture<AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                ActionWithFeedbackRequestWrapper.SendBroadcastLevelInstruction(
                        Util.NewCommonHeaderForClient(),
                        terminalIndex,
                        dali2LightCommandModel,
                        new Dt8CommandModel(Dt8CommandModel.Instructions.None, 0, 0)),
                (ResponseWrapper) -> {
                    var msg = logStr("receive the sendBroadcastCommand command success ->{}", dali2LightCommandModel);
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null) {
                    log.error("Internal Error! no valid response");
                    return;
                }
                if (result.getFeedback() instanceof AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) {
                    var cnt = ((AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) result.getFeedback()).getCountOfLights();
                    var msg = logStr("execute the sendBroadcastCommand command success, cnt :{}", cnt);
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                    future.set((AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) result.getFeedback());
                } else {
                    future.setException(new IllegalStateException("no valid response"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to sendBroadcastCommand", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                future.setException(t);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }

    public ListenableFuture<AsyncActionFeedbackWrapper.SetSysTime> setCabinetSystemTime(long timestamp) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("setSystemTime"));
        }
        SettableFuture<AsyncActionFeedbackWrapper.SetSysTime> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                new ActionWithFeedbackRequestWrapper(
                        Util.NewCommonHeaderForClient(),
                        new ActionWithFeedbackRequestWrapper.SetSysTime(timestamp)
                ), (Resp) -> {
                    var msg = logStr("receive the setSystemTime command success");
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(AsyncActionFeedbackWrapper result) {
                var msg = logStr("execute the setSystemTime command success, current cabinet time: {}", DateTime.fromMillis(result.getTimestamp()));
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to setSystemTime", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }


    public ListenableFuture<AsyncActionFeedbackWrapper.SaveStation> saveStation() {
        if (log.isTraceEnabled()) {
            log.trace(logStr("saveStation"));
        }
        SettableFuture<AsyncActionFeedbackWrapper.SaveStation> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                new ActionWithFeedbackRequestWrapper(
                        Util.NewCommonHeaderForClient(),
                        new ActionWithFeedbackRequestWrapper.SaveStation()
                ), (Resp) -> {
                    var msg = logStr("receive the saveStation command success");
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                var msg = logStr("execute the saveStation command success");
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to saveStation", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }

    public ListenableFuture<AsyncActionFeedbackWrapper.SendLevelInstruction> sendCtlCommand(
            UUID[] resource,
            Dali2LightCommandModel dali2LightCommandModel
    ) {
        return this.sendCtlCommand(resource, dali2LightCommandModel, Dt8CommandModel.NONE);
    }

    public void onSendCtlCommand(UUID[] resource,
                                 Dali2LightCommandModel dali2LightCommandModel,
                                 Dt8CommandModel dt8CommandModel) {
        Futures.addCallback(this.sendCtlCommand(resource, dali2LightCommandModel, dt8CommandModel), new FutureCallback<AsyncActionFeedbackWrapper.SendLevelInstruction>() {
            @Override
            public void onSuccess(AsyncActionFeedbackWrapper.@Nullable SendLevelInstruction result) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, MoreExecutors.directExecutor());

    }

    public ListenableFuture<AsyncActionFeedbackWrapper.SendLevelInstruction> sendCtlCommand(
            UUID[] resource,
            Dali2LightCommandModel dali2LightCommandModel,
            Dt8CommandModel dt8CommandModel

    ) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("sendCtlCommand: resource={},dali2LightCommandModel={},dt8CommandModel={}", resource, dali2LightCommandModel, dt8CommandModel));
        }
        SettableFuture<AsyncActionFeedbackWrapper.SendLevelInstruction> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                ActionWithFeedbackRequestWrapper.SendLevelInstruction(
                        Util.NewCommonHeaderForClient(),
                        resource,
                        dali2LightCommandModel, dt8CommandModel),
                (ResponseWrapper) -> {
                    var msg = logStr("receive the sendCtlCommand success");
                    log.info(msg);
                    uaAlarmEventService.successEvent(Cabinet.this, msg);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null) {
                    log.error("Internal Error,invalid response");
                    return;
                }
                if (result.getFeedback() instanceof AsyncActionFeedbackWrapper.SendLevelInstruction) {
                    var dali2Light = ((AsyncActionFeedbackWrapper.SendLevelInstruction) result.getFeedback()).getCountOfDali2();
                    var doLight = ((AsyncActionFeedbackWrapper.SendLevelInstruction) result.getFeedback()).getCountOfDo();
                    var room = ((AsyncActionFeedbackWrapper.SendLevelInstruction) result.getFeedback()).getCountOfRoom();
                    if (dali2Light > 0 || doLight > 0 || room > 0) {
                        var msg = logStr("execute the sendCtlCommand success,dali2Light={},doLight={},room={}", dali2Light, doLight, room);
                        log.info(msg);
                        uaAlarmEventService.successEvent(Cabinet.this, msg);
                    } else {
                        var msg = logStr("fail to match any resource");
                        log.error(msg);
                        uaAlarmEventService.failureEvent(Cabinet.this, msg);
                    }
                    future.set(((AsyncActionFeedbackWrapper.SendLevelInstruction) result.getFeedback()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("execute the setPollManager command failure", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
                future.setException(t);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }

    /**
     * 清除控制器实时状态缓存
     */
    public void purgeCacheForUpdateAllRealtimeStatus() {
        var request = new PollManagerSettingRequestWrapper.PollManagerParam();
        request.purgeCache = true;
        this.setPollManager(request);
    }

    public void setPollManager(PollManagerSettingRequestWrapper.PollManagerParam param) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("setPollManager"));
        }
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new PollManagerSettingRequestWrapper(Util.NewCommonHeaderForClient(), param)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                var msg = logStr("execute the setPollManager command success");
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("execute the setPollManager command failure", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    //关闭控制器能源监测模块
    public void disableEnergyMeasuring() {
        if (log.isTraceEnabled()) {
            log.trace(logStr("disableEnergyMeasuring"));
        }
        Futures.addCallback(this.cabinetRequestService.writePropertyValueAsync(RpcTarget.ToCabinet(CabinetId.get()), "station:|slot:/LightControlSystem/energyMeasuring/enable", "false"), new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                var msg = logStr("execute the disableEnergyMeasuring command success");
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("execute the disableEnergyMeasuring command failure", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    public void onUpdateStatus(CabinetStatusWrapper status) {
        this.Props.get().accept(status.getPropsMap());
        this._lastMsgTimestamp = System.currentTimeMillis();
        this.IsAlive.set(true);
        this.Ip0.set(status.getHeaders().get("ip0"));
        this.Ip1.set(status.getHeaders().get("ip1"));
    }


    protected String getLogLocate() {
        return Name.get() + "(" + CabinetId.get() + ")";
    }

    /**
     * 请求控制器更新所有灯具和房间详细信息
     */
    public void AskToUpdateLightsAndRoomInfo() {
        if (log.isTraceEnabled()) {
            log.trace(logStr("AskToUpdateLightsAndRoomInfo"));
        }
        uaAlarmEventService.sendBasicUaEvent(this, logStr("ask to update lights and rooms info"), 0);
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), true, null)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                var msg = logStr("execute the AskToUpdateLightsAndRoomInfo command success");
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("execute the AskToUpdateLightsAndRoomInfo command failure", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * 请求控制器更新控制柜基本信息
     */
    public void AskToUpdateCabinetInfo() {
        if (log.isTraceEnabled()) {
            log.trace(logStr("AskToUpdateCabinetInfo"));
        }
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(CabinetId.get()), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), false, Collections.singletonList(new UUID(0, 0)))), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                var msg = logStr("execute the AskToUpdateCabinetInfo command success");
                log.info(msg);
                uaAlarmEventService.successEvent(Cabinet.this, msg);
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("execute the AskToUpdateCabinetInfo command failure", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(Cabinet.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    public void setCid(int cid) {
        this.CabinetId.set(cid);
    }

    public void restart() {
        if (log.isTraceEnabled()) {
            log.trace(logStr("restart"));
        }
        Futures.addCallback(this.cabinetRequestService.asyncSend(RpcTarget.ToCabinet(this.CabinetId.get()), new ActionRequestWrapper(Util.NewCommonHeaderForClient(), ActionRequestWrapper.Instruction.Reboot)), new FutureCallback<ResponseWrapper>() {
                    @Override
                    public void onSuccess(@Nullable ResponseWrapper result) {
                        var msg = logStr("execute the restart command success");
                        log.info(msg);
                        uaAlarmEventService.successEvent(Cabinet.this, msg);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        var msg = logStr("execute the restart command failure", t);
                        log.error(msg);
                        uaAlarmEventService.failureEvent(Cabinet.this, msg);
                    }
                }, MoreExecutors.directExecutor()
        );
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
            addProperty(uaComponent.Ip0);
            addProperty(uaComponent.Ip1);
            addAdditionalDeclares(methods.values());
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
            } else if (declared == methods.sendGlobalCtlCommand) {
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[0].intValue()), input[1].intValue());
                this.comp.onSendGlobalCtlCommand(Cmd103);
                return null;
            } else if (declared == methods.syncTime) {
                this.comp.setCabinetSystemTime(System.currentTimeMillis());
                return null;
            } else if (declared == methods.saveStation) {
                this.comp.saveStation();
                return null;
            } else if (declared == methods.dbSync) {
                this.comp.sendDbSync(input[0].intValue());
                return null;
            } else if (declared == methods.restart) {
                this.comp.restart();
                return null;
            } else if (declared == methods.sendCtlCommand) {
                var targets = Util.ResolveUUIDArrayFromString(input[0].toString());
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[1].intValue()), input[2].intValue());
                var CmdDt8 = new Dt8CommandModel(UaHelperUtil.getEnum(Dt8CommandModel.Instructions.class, input[3].intValue()), input[4].intValue(), input[5].intValue());
                this.comp.onSendCtlCommand(targets, Cmd103, CmdDt8);
                return null;
            }else if(declared == methods.executeV3Sync){
                this.comp.executeV3Sync();
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            executeV3Sync(),
            syncTime(),
            restart(),
            saveStation(),
            fetchCabinetDetailsNow(),
            dbSync(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("flag", Identifiers.Int32),
            }, null),
            fetchRoomAndLightDetailsNow(),
            sendGlobalCtlCommand(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
            }, null),
            sendCtlCommand(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("uuid", Identifiers.String),
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8Action", Dt8CommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("dt8ActionParam", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8ActionParam2", Identifiers.Int32),
            }, null),
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
    }
}
