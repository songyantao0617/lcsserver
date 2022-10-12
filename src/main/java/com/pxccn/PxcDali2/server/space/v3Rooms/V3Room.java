package com.pxccn.PxcDali2.server.space.v3Rooms;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.db.DatabaseService;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.lights.LightBase;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@FwComponentAnnotation
@Slf4j
public class V3Room extends FwUaComponent<V3Room.V3RoomNode> {

    @Autowired
    ConfigurableApplicationContext context;
    @Autowired
    DatabaseService databaseService;
    @Autowired
    V3RoomsManager v3RoomsManager;
    @Autowired
    CabinetsManager cabinetsManager;
    @Autowired
    CabinetRequestService cabinetRequestService;
    @Autowired
    UaAlarmEventService uaAlarmEventService;
    @Autowired
    LightsManager lightsManager;
    FwProperty<String> roomName;
    FwProperty<String> description;
    FwProperty<Integer> axis_x;
    FwProperty<Integer> axis_y;
    FwProperty<Integer> axis_z;
    FwProperty<Double> brightnessAverage;
    FwProperty<String> brightnessSummary;
    FwProperty<String> CorrelateCabinets;
    UUID roomUuid;
    Map<UUID, LightBase> memberLights = new ConcurrentHashMap<>();
    List<UUID> missingLights = new ArrayList<>();

    @PostConstruct
    public void init() {
        roomName = addProperty("", "roomName");
        description = addProperty("description", "description");
        axis_x = addProperty(0, "axis_x");
        axis_y = addProperty(0, "axis_y");
        axis_z = addProperty(0, "axis_z");
        brightnessAverage = addProperty(0.0, "brightnessAverage");
        brightnessSummary = addProperty("", "brightnessSummary");
        CorrelateCabinets = addProperty("", "correlateCabinets");
    }

    public UUID getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(UUID roomUuid) {
        this.roomUuid = roomUuid;
    }

    public V3RoomsManager getRoomsManager() {
        return (V3RoomsManager) this.getParentComponent();
    }

    @Override
    public UaNode getParentNode() {
        return this.getRoomsManager().getRoomsFolderNode();
    }

    public void updateBasicInfo() {
        var id = this.getRoomUuid();
    }

    private void onMemberLightChanged(@Nullable LightBase light) {
        int size = memberLights.size();
        if (size > 0) {
            List<Double> valid = new ArrayList<>();
            List<LightBase> hasError = new ArrayList<>();
            for (var entry : this.memberLights.entrySet()) {
                var l = entry.getValue();
                if (l.getErrorMsg().isEmpty()) {
                    valid.add(l.getBrightness());
                } else {
                    hasError.add(l);
                }
            }
            this.brightnessAverage.set(valid.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            this.brightnessSummary.set("有效灯具" + valid.size() + "盏;异常灯具" + hasError.size() + "盏;未上线灯具" + missingLights.size() + "盏");
        } else {
            this.brightnessAverage.set(-1.0);
            this.brightnessSummary.set("目前没有可以监视的有效灯具（房间无灯，或者控制柜未上线）");
        }

    }

    /**
     * 订阅灯具亮度变化 ,变化时回调 onMemberLightChanged
     *
     * @param valid 参与订阅的灯具
     * @param missing 无法命中的灯具UUID
     */
    private void handleLightsSubscribe(List<LightBase> valid, List<UUID> missing) {
        if (log.isTraceEnabled()) {
            log.trace(logStr("handleLightsSubscribe : valid={},missing={}", valid, missing));
        }
        var remain = new HashMap<>(memberLights);
        valid.forEach(i -> {
            if (memberLights.containsKey(i.getLightUuid())) {
                remain.remove(i.getLightUuid());
            } else {
                i.addListener(this::onMemberLightChanged);
                memberLights.put(i.getLightUuid(), i);

                this.getLightsFolderNode().addComponent(i.getNode());

            }
        });
        remain.forEach((k, v) -> {
            memberLights.remove(k);
            v.deleteObserver(this::onMemberLightChanged);

            this.getLightsFolderNode().removeComponent(v.getNode());

        });
        this.missingLights = missing;
        onMemberLightChanged(null);
    }

    public void stopped() {
        super.stopped();
        memberLights.forEach((k, v) -> {
            v.deleteObserver(this::onMemberLightChanged);
        });
    }

    /**
     * 从数据库读取关联控制柜
     * 填入 CorrelateCabinets 后，调用消费函数
     */
    public void getCabinetsFromDb(@Nullable Consumer<List<Integer>> then) {
        Futures.addCallback(databaseService.queryCorrelateCabinetIdFromV3RoomId(this.getRoomUuid()), new FutureCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> result) {
                CorrelateCabinets.set(StringUtils.collectionToCommaDelimitedString(result));
                if (then != null) {
                    then.accept(result);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("V3房间<{}>无法读取关联控制柜ID", roomName.get(), t);
            }
        }, this.getRoomsManager().getExecutor());
    }

    /**
     * 从数据库读取关联灯具信息
     */
    public void getLightsFromDb(@Nullable Consumer<Void> then) {
        log.trace(logStr("getLightsFromDb"));
        var f = databaseService.queryLightsUuidOfV3Room(this.getRoomUuid());
        Futures.addCallback(f, new FutureCallback<List<UUID>>() {
            @Override
            public void onSuccess(List<UUID> result) {
                List<LightBase> validLights = new ArrayList<>();
                List<UUID> missingLights = new ArrayList<>();
                result.forEach(lid -> {
                    if (lightsManager.checkHasLight(lid)) {
                        var light = lightsManager.GetOrCreateLight(lid);
                        validLights.add(light);
                    } else {
                        missingLights.add(lid);
                    }
                });
                handleLightsSubscribe(validLights, missingLights);
                if (then != null) {
                    then.accept(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("V3房间<{}>获取灯具列表失败", roomName.get(), t);
            }
        }, getRoomsManager().getExecutor());
    }


    public void setBasicInfo(RoomUnitV3 model) {
        this.roomName.set(Util.Safe(model.getName()));
        this.description.set(Util.Safe(model.getDescription()));
        this.axis_x.set(Util.Safe(model.getAxisX()));
        this.axis_y.set(Util.Safe(model.getAxisY()));
        this.axis_z.set(Util.Safe(model.getAxisZ()));
    }


    @Override
    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == this.roomName) {
            this.getNode().setDisplayName(new LocalizedText(this.roomName.get()));
            this.getNode().setBrowseName(new QualifiedName(this.getNode().getNamespaceIndex(),this.roomName.get()));
        }
    }

    /**
     * 刷新房间配置
     *
     * @param result 模型
     */
    public void refresh(@Nullable RoomUnitV3 result) {
        log.trace("refresh: result={}", result);
        if (result != null) {
            setBasicInfo(result);
            getLightsFromDb((Void) -> {
                getCabinetsFromDb(null);
            });

        } else {
            getBasicInfoFromDb((roomUnitV3) -> {
                setBasicInfo(roomUnitV3);
                getLightsFromDb((Void) -> {
                    getCabinetsFromDb(null);
                });
            });
        }
    }

    private void getBasicInfoFromDb(@NonNull Consumer<RoomUnitV3> then) {
        Futures.addCallback(databaseService.getV3RoomBasicInfo(this.getRoomUuid()), new FutureCallback<RoomUnitV3>() {
            @Override
            public void onSuccess(@Nullable RoomUnitV3 result) {
                if (result != null) {
                    var msg = logStr("getBasicInfoFromDb : {}",result);
                    log.debug(msg);
                    then.accept(result);
                } else {
                    var msg = logStr("room not exist in db");
                    log.warn(msg);
                    uaAlarmEventService.failureEvent(V3Room.this,msg);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to getBasicInfoFromDb",t);
                log.error(msg);
                uaAlarmEventService.failureEvent(V3Room.this,msg);
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * 获取详细信息
     */
    public void FetchDetailsNow() {
        log.trace(logStr("onFetchDetailsNow"));
        Futures.addCallback(databaseService.getV3RoomBasicInfo(this.getRoomUuid()), new FutureCallback<RoomUnitV3>() {
            @Override
            public void onSuccess(@Nullable RoomUnitV3 result) {
                if (result != null) {
                    var msg = logStr("getV3RoomBasicInfo : {}", result);
                    log.debug(msg);
                    uaAlarmEventService.debugEvent(V3Room.this, msg);
                    refresh(result);
                } else {
                    var msg = logStr("room not exist anymore");
                    log.warn(msg);
                    uaAlarmEventService.failureEvent(V3Room.this, msg);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to getV3RoomBasicInfo",t);
                log.error(msg);
                uaAlarmEventService.failureEvent(V3Room.this, msg);
            }
        }, MoreExecutors.directExecutor());
    }

    private void sendCommandToCabinets(List<Integer> targets, Dali2LightCommandModel dali2LightCommandModel, Dt8CommandModel dt8CommandModel) {
        targets.forEach(cid -> {
            if (cabinetsManager.checkIsAlive(cid)) {
                var cab = cabinetsManager.GetOrCreateCabinet(cid);
                Futures.addCallback(cab.sendCtlCommand(new UUID[]{getRoomUuid()}, dali2LightCommandModel, dt8CommandModel), new FutureCallback<AsyncActionFeedbackWrapper.SendLevelInstruction>() {
                    @Override
                    public void onSuccess(AsyncActionFeedbackWrapper.@Nullable SendLevelInstruction result) {
                        var msg = logStr("sendCommandToCabinets <{}> success", cab);
                        log.info(msg);
                        uaAlarmEventService.successEvent(V3Room.this, msg);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        var msg = logStr("sendCommandToCabinets <{}> failure", cab, t);
                        log.error(msg);
                        uaAlarmEventService.failureEvent(V3Room.this, msg);
                    }
                }, MoreExecutors.directExecutor());
            } else {
                var msg = logStr("sendCommandToCabinets <{}> failure: not alive)", cid);
                log.warn(msg);
                uaAlarmEventService.failureEvent(V3Room.this, msg);
            }
        });
    }

    /**
     * 获取与本房间相关联的控制柜ID,非实时
     *
     * @param cabinetIds
     */
    private void getAccessibleCabinetIds(Consumer<List<Integer>> cabinetIds) {
        if (CorrelateCabinets.get().isEmpty()) {
            this.getCabinetsFromDb(cabinetIds);
        } else {
            var targets = Arrays.stream(StringUtils.commaDelimitedListToStringArray(CorrelateCabinets.get())).map(Integer::valueOf).collect(Collectors.toList());
            cabinetIds.accept(targets);
        }
    }

    public void SendCtlCommand(Dali2LightCommandModel dali2LightCommandModel, Dt8CommandModel dt8CommandModel) {
        var msg = logStr("onSendCtlCommand: dali2LightCommandModel={},dt8CommandModel={}", dali2LightCommandModel, dt8CommandModel);
        log.trace(msg);
        uaAlarmEventService.debugEvent(this, msg);
        this.getAccessibleCabinetIds((targets) -> {
            sendCommandToCabinets(targets, dali2LightCommandModel, dt8CommandModel);
        });
    }

    public void SendGroupAddress() {
        var msg = logStr("onSendGroupAddress");
        log.trace(msg);
        uaAlarmEventService.debugEvent(this, msg);

        this.getAccessibleCabinetIds((targets) -> {
            targets.forEach(id -> {
                var f = cabinetRequestService.invokeMethodAsync(RpcTarget.ToCabinet(id), this.getRoomUuid(), "members", "sendGroupAddress");
                Futures.addCallback(f, new FutureCallback<Object>() {
                    @Override
                    public void onSuccess(@Nullable Object result) {
                        var msg = logStr("Cabinet<{}> execute the sendGroupAddress command");
                        log.info(msg);
                        uaAlarmEventService.successEvent(V3Room.this, msg);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        var msg = logStr("Cabinet<{}> fail to execute the sendGroupAddress command", t);
                        log.error(msg);
                        uaAlarmEventService.failureEvent(V3Room.this, msg);
                    }
                }, MoreExecutors.directExecutor());
            });
        });
    }

    /**
     * 命令本房间相关联的控制柜执行房间同步任务
     */
    public void syncToCabinets() {
        if (log.isTraceEnabled()) {
            var msg = "syncToCabinets";
            log.trace(msg);
            uaAlarmEventService.debugEvent(this, msg);
        }

        getCabinetsFromDb(ids -> {
            ids.forEach(id -> {
                if (cabinetsManager.checkIsAlive(id)) {
                    cabinetsManager.GetOrCreateCabinet(id).executeV3Sync();
                } else {
                    var msg = logStr("cabinet<> not alive", id);
                    log.warn(msg);
                    uaAlarmEventService.failureEvent(V3Room.this, msg);
                }
            });
        });
    }


    protected String getLogLocate() {
        return roomName.get() + "(" + roomUuid + ")";
    }

    UaNode getLightsFolderNode() {
        return this.getNode().getDeclaredNode(V3RoomNode.folders.lights);
    }

    @Override
    protected V3RoomNode createUaNode() {
        return new V3RoomNode(this, this.getName());
    }

    protected static class V3RoomNode extends LCS_ComponentFastObjectNode {
        V3Room comp;

        protected V3RoomNode(V3Room uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addProperty(uaComponent.roomName);
            addProperty(uaComponent.description);
            addProperty(uaComponent.axis_x);
            addProperty(uaComponent.axis_y);
            addProperty(uaComponent.axis_z);
            addProperty(uaComponent.brightnessAverage);
            addProperty(uaComponent.brightnessSummary);
            addProperty(uaComponent.CorrelateCabinets);
            addAdditionalDeclares(methods.values(), folders.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.blink) {
                //TODO
//                comp.blink(BBoolean.make(input[0].booleanValue()));
                return null;
            } else if (declared == methods.fetchDetailsNow) {
                this.comp.FetchDetailsNow();
                return null;
            } else if (declared == methods.sendCtlCommand) {
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[0].intValue()), input[1].intValue());
                var CmdDt8 = new Dt8CommandModel(UaHelperUtil.getEnum(Dt8CommandModel.Instructions.class, input[2].intValue()), input[3].intValue(), input[4].intValue());
                this.comp.SendCtlCommand(Cmd103, CmdDt8);
                return null;
            } else if (declared == methods.sendGroupAddress) {
                this.comp.SendGroupAddress();
                return null;
            } else if (declared == methods.syncToCabinets) {
                this.comp.syncToCabinets();
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            lights
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            syncToCabinets(),
            sendGroupAddress(),
            sendCtlCommand(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8Action", Dt8CommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("dt8ActionParam", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8ActionParam2", Identifiers.Int32),
            }, null),
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
    }
}
