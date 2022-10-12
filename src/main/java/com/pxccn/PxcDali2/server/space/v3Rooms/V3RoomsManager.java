package com.pxccn.PxcDali2.server.space.v3Rooms;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.db.DatabaseService;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 数据库房间管理器
 */
@Component
@Slf4j
public class V3RoomsManager extends FwUaComponent<V3RoomsManager.V3RoomsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;
    @Autowired
    DatabaseService databaseService;
    @Autowired
    UaAlarmEventService uaAlarmEventService;
    @Autowired
    CabinetsManager cabinetsManager;
    FwProperty<Integer> count;

    @Value("${LcsServer.autoDiscoverChangedRoom:false}")
    boolean autoDiscoverChangedRoom;

    private ExecutorService executor;

    public ExecutorService getExecutor() {
        return executor;
    }

    @PostConstruct
    public void init() {
        executor = LcsExecutors.newWorkStealingPool(32, getClass());
        this.count = addProperty(0,"count");
    }

    public void updateCount(){
        this.count.set(this.getPropertyCount());
    }

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
        executor.execute(()->{
            var message = event.getMessage();
            var uid = message.getUuid();
            var cabinetId = message.getCabinetId();
            switch (message.getEvent()) {
//            case RoomAdded:
//                log.debug("房间<{}>创建", uid);
//                this.AskToUpdateRoomsInfo(Collections.singletonList(uid), cabinetId);
//                break;
//            case RoomRemoved:
//                log.debug("房间<{}>被移除", uid);
//                this.removeProperty(uid.toString());
//                break;
//            case RoomInfoChange:
//                log.debug("房间<{}>信息变更", uid);
//                this.AskToUpdateRoomsInfo(Collections.singletonList(uid), cabinetId);
//                break;
            }
        });
    }


//    private static class SyncStatus{
//
//    }
//    public void syncToCabinets(){
//        if(log.isTraceEnabled()){
//            log.trace(logStr("syncToCabinets"));
//        }
//
//
//
//        getCabinetsFromDb(ids->{
//            Map<Cabinet,SyncStatus> cabinets = new HashMap<>();
//            ids.forEach(id->{
//                if(cabinetsManager.checkIsAlive(id)){
//                    cabinets.put(cabinetsManager.GetOrCreateCabinet(id),new SyncStatus());
//                }
//            });
//
//        });
//    }

    public void updateChangedRooms(boolean withLog) {
        var msg = logStr("updateChangedRooms");
        log.trace(msg);
        uaAlarmEventService.debugEvent(this, msg);

        Futures.addCallback(this.databaseService.getUpdatedV3Room(), new FutureCallback<List<RoomUnitV3>>() {
            @Override
            public void onSuccess(@Nullable List<RoomUnitV3> updatedRooms) {
                assert updatedRooms != null;
                if (updatedRooms.size() == 0) {
                    var msg = logStr("no rooms need to be updated");
                    if (withLog) {
                        log.info(msg);
                        uaAlarmEventService.successEvent(V3RoomsManager.this, msg);
                    } else {
                        log.debug(msg);
                        uaAlarmEventService.debugEvent(V3RoomsManager.this, msg);
                    }
                    return;
                }
                var msg = logStr("found rooms that should be updated: {}", updatedRooms);
                if (withLog) {
                    log.info(msg);
                    uaAlarmEventService.successEvent(V3RoomsManager.this, msg);
                } else {
                    log.debug(msg);
                    uaAlarmEventService.debugEvent(V3RoomsManager.this, msg);
                }
                //需要更新的房间
                List<RoomUnitV3> roomsWithStatus1 = updatedRooms.stream().filter(i -> i.getStatus() == 1).collect(Collectors.toList());
                //需要删除的房间
                List<RoomUnitV3> roomsWithStatus2 = updatedRooms.stream().filter(i -> i.getStatus() == 2).collect(Collectors.toList());

                Set<Cabinet> cabinets = new HashSet<>();

                roomsWithStatus1.forEach(room -> {
                    var id = UUID.fromString(room.getRoomuuid());
                    var r = GetOrCreateRoom(id);
                    r.refresh(room);
                    r.getCabinetsFromDb((cList) -> {
                        for (Integer c : cList) {
                            if (cabinetsManager.checkIsAlive(c)) {
                                var cab = cabinetsManager.GetOrCreateCabinet(c);
                                cabinets.add(cab);
                            } else {
                                var msg2 = logStr("ignore cabinet<{}> : not alive", c);
                                if (withLog) {
                                    log.error(msg2);
                                    uaAlarmEventService.failureEvent(V3RoomsManager.this, msg2);
                                } else {
                                    log.debug(msg2);
                                    uaAlarmEventService.debugEvent(V3RoomsManager.this, msg2);
                                }
                            }

                        }
                    });
                    databaseService.clearV3RoomUpdateFlag(id);
                });

                roomsWithStatus2.forEach(room -> {
                    var id = UUID.fromString(room.getRoomuuid());
                    var roomInServer = getRoomByUUID(id);
                    if (roomInServer == null) {
                        var msg2 = logStr("room<{}> not exist", room.getName());
                        if (withLog) {
                            log.error(msg2);
                            uaAlarmEventService.failureEvent(V3RoomsManager.this, msg2);
                        } else {
                            log.debug(msg2);
                            uaAlarmEventService.debugEvent(V3RoomsManager.this, msg2);
                        }
                        return;
                    }
                    roomInServer.getCabinetsFromDb((cList) -> {
                        for (Integer c : cList) {
                            if (cabinetsManager.checkIsAlive(c)) {
                                var cab = cabinetsManager.GetOrCreateCabinet(c);
                                cabinets.add(cab);
                            } else {
                                var msg2 = logStr("ignore cabinet<{}> : not alive", c);
                                if (withLog) {
                                    log.error(msg2);
                                    uaAlarmEventService.failureEvent(V3RoomsManager.this, msg2);
                                } else {
                                    log.debug(msg2);
                                    uaAlarmEventService.debugEvent(V3RoomsManager.this, msg2);
                                }
                            }
                        }
                    });
                    databaseService.removeV3RoomSync(id);
                });

                var msg2 = logStr("sendSyncToCabinets:{}", cabinets);
                log.info(msg2);
                uaAlarmEventService.successEvent(V3RoomsManager.this, msg2);
                cabinets.forEach(c -> {
                    c.executeV3Sync();
                });

            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to updateChangedRooms",t);
                log.error(msg);
                uaAlarmEventService.failureEvent(V3RoomsManager.this, msg);
            }
        }, executor);
    }


    public V3Room getRoomByUUID(UUID RoomUUID) {
        var p = Arrays.stream(this.getAllProperty()).filter(i -> i.getName().equals(RoomUUID.toString())).findFirst().orElse(null);
        if (p != null) {
            return (V3Room) p.get();
        } else {
            return null;
        }
    }

    public V3Room[] getAllRoom() {
        return Arrays.stream(this.getAllProperty(V3Room.class))
                .map(FwProperty::get)
                .map(i -> (V3Room) i)
                .toArray(V3Room[]::new);
    }

    @Override
    protected V3RoomsManagerNode createUaNode() {
        return new V3RoomsManagerNode(this, this.getName());
    }


    public V3Room GetOrCreateRoom(UUID roomUuid) {
        String roomPropName = roomUuid.toString();
        FwProperty p;
        synchronized (this) {
            p = this.getProperty(roomPropName);
            if (p == null) {
                var c = context.getBean(V3Room.class);
                c.setRoomUuid(roomUuid);
                p = addProperty(c, roomPropName);
            }
        }
        updateCount();
        return (V3Room) p.get();
    }

    public void started() {
        super.started();
//        GetOrCreateRoom(UUID.randomUUID(), 1213);
        RefreshV3RoomsFromDb();
    }

    @Scheduled(fixedDelay = 1000 * 60) // 每1分钟刷新一次
    public void checkDbStatus(){
        if (!this.autoDiscoverChangedRoom || !this.isRunning()) {
            return;
        }
        this.updateChangedRooms(false);
    }

    @Scheduled(fixedDelay = 600 * 1000) // 每十分钟同步一次
    public void RefreshV3RoomsFromDb() {
        if (!this.isRunning()) {
            return;
        }
        log.info(logStr("RefreshV3RoomsFromDb"));

        Futures.addCallback(databaseService.getAllV3Room(), new FutureCallback<List<RoomUnitV3>>() {
            @Override
            public void onSuccess(List<RoomUnitV3> result) {
                var remainRoomsId = Arrays.stream(getAllRoom()).map(V3Room::getRoomUuid).collect(Collectors.toList());
                result.forEach(ru3 -> {
                    var id = UUID.fromString(ru3.getRoomuuid());
                    var r = GetOrCreateRoom(id);
                    r.refresh(ru3);
                    remainRoomsId.remove(id);
                });
                for (var r : remainRoomsId) {
                    log.info("V3Room<{}> not exist in DB anymore!", r);
                    removeProperty(r.toString());
                }
                uaAlarmEventService.successEvent(V3RoomsManager.this, logStr("RefreshV3RoomsFromDb success"));
            }

            @Override
            public void onFailure(Throwable t) {
                var msg = logStr("fail to RefreshV3RoomsFromDb!", t);
                log.error(msg);
                uaAlarmEventService.failureEvent(V3RoomsManager.this, msg);
            }
        }, executor);
    }


    UaNode getRoomsFolderNode() {
        return this.getNode().getDeclaredNode(V3RoomsManagerNode.folders.rooms);
    }

    protected static class V3RoomsManagerNode extends LCS_ComponentFastObjectNode {
        V3RoomsManager comp;

        protected V3RoomsManagerNode(V3RoomsManager uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addProperty(this.comp.count);
            addAdditionalDeclares(folders.values(), methods.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.refreshV3RoomsFromDb) {
                this.comp.RefreshV3RoomsFromDb();
                return null;
            }else if(declared == methods.updateChangedRooms){
                this.comp.updateChangedRooms(true);
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            rooms
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            refreshV3RoomsFromDb(),
            updateChangedRooms();

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
