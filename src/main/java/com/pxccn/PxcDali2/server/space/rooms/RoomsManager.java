package com.pxccn.PxcDali2.server.space.rooms;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.events.RoomsDetailUploadEvent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
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

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 常规房间管理器
 */
@Component
@Slf4j
public class RoomsManager extends FwUaComponent<RoomsManager.RoomsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;

    @Autowired
    UaAlarmEventService uaAlarmEventService;

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
        var message = event.getMessage();
        var uid = message.getUuid();
        var cabinetId = message.getCabinetId();
        String logString = "";
        switch (message.getEvent()) {
            case RoomAdded:
                logString = logStr("Room<{}> RoomAdded", uid);
                log.debug(logString);
                uaAlarmEventService.debugEvent(this, logString);
                this.AskToUpdateRoomsInfo(Collections.singletonList(uid), cabinetId);
                break;
            case RoomRemoved:
                logString = logStr("Room<{}> RoomRemoved", uid);
                log.debug(logString);
                uaAlarmEventService.debugEvent(this, logString);
                this.removeProperty(uid.toString());
                break;
            case RoomInfoChange:
                logString = logStr("Room<{}> RoomInfoChange", uid);
                log.debug(logString);
                uaAlarmEventService.debugEvent(this, logString);
                this.AskToUpdateRoomsInfo(Collections.singletonList(uid), cabinetId);
                break;
        }
    }

    @EventListener
    public void onRoomsDetailUploadEvent(RoomsDetailUploadEvent event) {
        //只关心Local房间
        int cabinetId = event.getMessage().getCabinetId();
        event.getMessage().getRoomDetailModels()
                .stream()
                .filter(i -> !i.isDbBased())//只关心Local房间
                .forEach(model -> {
                    var room = this.GetOrCreateRoom(model.uuid, cabinetId);
                    room.onDetailUpload(model);
                });
    }

    public void AskToUpdateRoomsInfo(List<UUID> roomsUuids, int cabinetId) {
        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(cabinetId), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), false, roomsUuids)), new FutureCallback<ResponseWrapper>() {
            @Override
            public void onSuccess(@Nullable ResponseWrapper result) {
                log.info("从控制柜<{}>获取灯具房间信息成功", cabinetId);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("无法从控制柜<{}>获取房间详细信息:{}", cabinetId, t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }

    public void AskToUpdateRoomsInfo(List<UUID> roomsUuids) {
        Map<Integer, List<UUID>> tmp = new HashMap<>();
        roomsUuids.stream().map(this::getRoomByUUID).filter(Objects::nonNull).forEach(l -> {
            tmp.computeIfAbsent(l.getCabinetId(), (i) -> new ArrayList<UUID>()).add(l.roomUuid);
        });

        tmp.forEach((cabinetId, roomUuid) -> {
            this.AskToUpdateRoomsInfo(roomUuid, cabinetId);
        });
    }

//    public void AskToUpdateLightsInfo(int cabinetId) {
//        Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.ToCabinet(cabinetId), new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), true, null)), new FutureCallback<ResponseWrapper>() {
//            @Override
//            public void onSuccess(@Nullable ResponseWrapper result) {
//                log.info("从控制柜<{}>获取灯具详细信息成功", cabinetId);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                log.error("无法从控制柜<{}>获取灯具详细信息:{}", cabinetId, t.getMessage());
//            }
//        }, MoreExecutors.directExecutor());
//    }

    public Room getRoomByUUID(UUID RoomUUID) {
        var p = Arrays.stream(this.getAllProperty()).filter(i -> i.getName().equals(RoomUUID.toString())).findFirst().orElse(null);
        if (p != null) {
            return (Room) p.get();
        } else {
            return null;
        }
    }


    @Override
    protected RoomsManager.RoomsManagerNode createUaNode() {
        return new RoomsManagerNode(this, this.getName());
    }

    public Room GetOrCreateRoom(UUID roomUuid) {
        return this.GetOrCreateRoom(roomUuid, null);
    }

    public Room GetOrCreateRoom(UUID roomUuid, Integer cabinetId) {
        String roomPropName = roomUuid.toString();
        FwProperty p;
        synchronized (this) {
            p = this.getProperty(roomPropName);
            if (p == null) {
                var c = context.getBean(Room.class);
                c.setRoomUuid(roomUuid);
                if (cabinetId != null)
                    c.setCabinetId(cabinetId);
                p = addProperty(c, roomPropName);
            }
        }
        var room = (Room) p.get();
        if (cabinetId != null && cabinetId.intValue() != room.getCabinetId()) {
            room.setCabinetId(cabinetId.intValue());
        }
        updateCount();
        return room;
    }

    public void started() {
        super.started();
//        GetOrCreateRoom(UUID.randomUUID(), 1213);
    }


    UaNode getRoomsFolderNode() {
        return this.getNode().getDeclaredNode(RoomsManagerNode.folders.rooms);
    }

    protected static class RoomsManagerNode extends LCS_ComponentFastObjectNode {
        RoomsManager comp;

        protected RoomsManagerNode(RoomsManager uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addProperty(this.comp.count);
            addAdditionalDeclares(folders.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {

            return super.onMethodCall(declared, input);
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            rooms
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {

            ;
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
