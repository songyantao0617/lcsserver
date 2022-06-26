package com.pxccn.PxcDali2.server.space.v3Rooms;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.DetailInfoRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.events.CabinetSimpleEvent;
import com.pxccn.PxcDali2.server.events.RoomsDetailUploadEvent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.service.db.CabinetQueryService;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
@Slf4j
public class V3RoomsManager extends FwUaComponent<V3RoomsManager.V3RoomsManagerNode> {
    @Autowired
    CabinetRequestService cabinetRequestService;
    @Autowired
    CabinetQueryService cabinetQueryService;

    @Autowired
    CabinetsManager cabinetsManager;

    public ExecutorService getExecutor() {
        return executor;
    }

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = LcsExecutors.newWorkStealingPool(20, getClass());
    }

    @EventListener
    public void onCabinetSimpleEvent(CabinetSimpleEvent event) {
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
        return (V3Room) p.get();
    }

    public void started() {
        super.started();
//        GetOrCreateRoom(UUID.randomUUID(), 1213);
        Refresh();
    }

    @Scheduled(fixedDelay = 600 * 1000) // 每十分钟同步一次
    public void Refresh() {
        if(!this.isRunning()){
            return;
        }
        log.trace("Refresh");
        Futures.addCallback(cabinetQueryService.getAllV3Room(), new FutureCallback<List<RoomUnitV3>>() {
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
                    log.info("房间<{}>已经不存在于数据库中", r);
                    removeProperty(r.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("获取所有V3房间出错!", t);
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
            addAdditionalDeclares(folders.values(), methods.values());
        }

        enum folders implements UaHelperUtil.UaFolderDeclare {
            rooms
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {

            refreshV3RoomsFromDb();


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
            if (declared == methods.refreshV3RoomsFromDb) {
                this.comp.Refresh();
                return null;
            }
            return super.onMethodCall(declared, input);
        }
    }
}
