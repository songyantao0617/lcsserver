package com.pxccn.PxcDali2.server.service.db;

import com.google.common.util.concurrent.*;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomLightInfoModel;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomTriggerInfoModel;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.database.mapper.CabinetV2Mapper;
import com.pxccn.PxcDali2.server.database.mapper.RoomLightMapV3Mapper;
import com.pxccn.PxcDali2.server.database.mapper.RoomTriggerV3Mapper;
import com.pxccn.PxcDali2.server.database.mapper.RoomUnitV3Mapper;
import com.pxccn.PxcDali2.server.database.mapperManual.FuncMapper;
import com.pxccn.PxcDali2.server.database.model.*;
import com.pxccn.PxcDali2.server.database.modelManual.FUNC_getV3RoomListFromCabinetID;
import com.pxccn.PxcDali2.server.database.modelManual.TargetLight;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CabinetQueryService {

    ListeningExecutorService listeningExecutorService;
    @Autowired
    CabinetV2Mapper cabinetV2Mapper;
    @Autowired
    RoomTriggerV3Mapper roomTriggerV3Mapper;
    @Autowired
    RoomUnitV3Mapper roomUnitV3Mapper;
    @Autowired
    RoomLightMapV3Mapper roomLightMapV3Mapper;
    @Autowired
    FuncMapper funcMapper;
    @Value("${LcsServer.dataBaseMaxPoolSize}")
    private int poolSize;

    @PostConstruct
    public void init() {
        this.listeningExecutorService = MoreExecutors.listeningDecorator(LcsExecutors.newWorkStealingPool(this.poolSize, getClass()));
    }

    /**
     * 查询所有房间
     *
     * @return
     */
    public ListenableFuture<List<RoomUnitV3>> getAllV3Room() {
        return this.listeningExecutorService.submit(() -> {
            var exp = new RoomUnitV3Example();
            exp.createCriteria();
            return this.roomUnitV3Mapper.selectByExample(exp);
        });
    }

    /**
     * 查询指定房间
     *
     * @param roomUuid
     * @return
     */
    public ListenableFuture<RoomUnitV3> getV3RoomBasicInfo(UUID roomUuid) {
        return this.listeningExecutorService.submit(() -> this.roomUnitV3Mapper.selectByPrimaryKey(roomUuid.toString()));
    }

    /**
     * 查询需要更新的房间
     *
     * @return
     */
    public ListenableFuture<List<RoomUnitV3>> getUpdatedV3Room() {
        var exp = new RoomUnitV3Example();
        var c = exp.createCriteria();
        c.andStatusGreaterThan(0);
        return this.listeningExecutorService.submit(() -> this.roomUnitV3Mapper.selectByExample(exp));
    }

    public void removeV3RoomSync(UUID uuid){
            var u = uuid.toString();
            var exp1 = new RoomLightMapV3Example();
            exp1.createCriteria().andRoomuuidEqualTo(u);
            this.roomLightMapV3Mapper.deleteByExample(exp1);
            var exp2 = new RoomTriggerV3Example();
            exp2.createCriteria().andRoomuuidEqualTo(u);
            this.roomTriggerV3Mapper.deleteByExample(exp2);
            this.roomUnitV3Mapper.deleteByPrimaryKey(u);
    }

    public void clearV3RoomUpdateFlag(UUID uuid){
        var r = new RoomUnitV3();
        r.setRoomuuid(uuid.toString());
        r.setStatus(0);
        this.roomUnitV3Mapper.updateByPrimaryKeySelective(r);
    }

    /**
     * 查询与指定房间相关联的CabinetId
     *
     * @param roomUuid
     */
    public ListenableFuture<List<Integer>> queryCorrelateCabinetIdFromV3RoomId(UUID roomUuid) {
        return this.listeningExecutorService.submit(() -> this.funcMapper.FUNC_queryCorrelateCabinetIdFromRoomId(roomUuid.toString())
        );
    }

    /**
     * 查询与指定控制柜ID相关联的房间
     *
     * @param cabinetId
     * @return
     */
    public ListenableFuture<List<RoomUnitV3>> queryCorrelateV3RoomsOfCabinetId(int cabinetId) {
        return this.listeningExecutorService.submit(() ->
                this.funcMapper.FUNC_getV3RoomListFromCabinetID(cabinetId)
                        .stream()
                        .map(FUNC_getV3RoomListFromCabinetID::getRoomUUID)
//                        .collect(Collectors.toList())
//                        .stream()
                        .map(a -> this.roomUnitV3Mapper.selectByPrimaryKey(a))
                        .collect(Collectors.toList())
        );
    }

    @Deprecated
    public ListenableFuture<List<UUID>> queryCorrelateV3RoomsUuidOfCabinetId(int cabinetId){
        return this.listeningExecutorService.submit(()->
            this.funcMapper.FUNC_getV3RoomListFromCabinetID(cabinetId)
                    .stream()
                    .map(i->UUID.fromString(i.getRoomUUID()))
                    .collect(Collectors.toList())
        );
    }

    /**
     * 查询V3房间下所有的灯具
     *
     * @param room
     * @return
     */
    public ListenableFuture<List<UUID>> queryLightsUuidOfV3Room(UUID room) {
        return this.listeningExecutorService.submit(() -> {
            var exp = new RoomLightMapV3Example();
            exp.createCriteria().andRoomuuidEqualTo(room.toString());
            return this.roomLightMapV3Mapper.selectByExample(exp)
                    .stream()
                    .map(RoomLightMapV3::getLightuuid)
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 获取指定V3房间的目标灯具
     *
     * @param room
     * @return
     */
    public ListenableFuture<List<TargetLight>> queryTargetsOfV3Room(UUID room) {
        return this.listeningExecutorService.submit(() -> this.funcMapper.FUNC_getV3LightsListFromRoomUuid(room.toString()));
    }

    public ListenableFuture<List<RoomTriggerV3>> queryTriggerOfV3RoomWithCabinetId(UUID room, int cabinetId) {
        log.trace("queryTriggerOfV3RoomWithCabinetId: room={},cabinetId={}", room, cabinetId);
        RoomTriggerV3Example exp = new RoomTriggerV3Example();
        var c =exp.createCriteria();
        c.andRoomuuidEqualTo(room.toString()).andCabinetIdEqualTo(cabinetId);
        var c1 = exp.or();
        c1.andRoomuuidEqualTo(room.toString()).andCabinetIdIsNull();
        return this.listeningExecutorService.submit(()->roomTriggerV3Mapper.selectByExample(exp));
    }

    /**
     * 获取指定控制柜的V3房间客户端同步信息
     *
     * @param cabinetId
     * @param executor
     * @return
     */
    public ListenableFuture<List<V3RoomLightInfoModel.Room>> getV3RoomModelFromCabinetId(int cabinetId, Executor executor) {
        log.trace("getV3RoomModelFromCabinetId: cabinetId={}", cabinetId);
        SettableFuture<List<V3RoomLightInfoModel.Room>> future = SettableFuture.create();
        Futures.addCallback(queryCorrelateV3RoomsOfCabinetId(cabinetId), new FutureCallback<List<RoomUnitV3>>() {
            @Override
            public void onSuccess(@Nullable List<RoomUnitV3> roomUnits) {
                assert roomUnits != null;
                List<ListenableFuture<List<TargetLight>>> targetsFuture = roomUnits.stream().map(room -> queryTargetsOfV3Room(UUID.fromString(room.getRoomuuid()))).collect(Collectors.toList());
                Futures.addCallback(Futures.successfulAsList(targetsFuture), new FutureCallback<List<List<TargetLight>>>() {
                    @Override
                    public void onSuccess(@Nullable List<List<TargetLight>> result) {
                        assert result != null;
                        int length = roomUnits.size();
                        List<V3RoomLightInfoModel.Room> roomsModelList = new ArrayList<>();
                        for (int index = 0; index < length; index++) {
                            RoomUnitV3 roomUnit = roomUnits.get(index);
                            List<TargetLight> targetLights = result.get(index);
                            if (targetLights == null) {
                                log.error("can not get target lights of v3 room {}/{}", roomUnit.getName(), roomUnit.getRoomuuid());
                                continue;
                            }
                            V3RoomLightInfoModel.Room roomModel = new V3RoomLightInfoModel.Room(
                                    UUID.fromString(roomUnit.getRoomuuid()),
                                    roomUnit.getName(),
                                    roomUnit.getDescription(),
                                    targetLights.stream().map(t ->
                                            new V3RoomLightInfoModel.TargetLight(UUID.fromString(t.getLightUUID()), t.getCabinet_ID(), t.getIp_address())
                                    ).collect(Collectors.toList())
                            );
                            roomsModelList.add(roomModel);
                        }
                        future.set(roomsModelList);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        future.setException(t);
                    }
                }, MoreExecutors.directExecutor());

            }

            @Override
            public void onFailure(Throwable t) {
                log.error("can not query correlate v3 rooms of {}", cabinetId, t);
                future.setException(t);
            }
        }, executor);
        return future;
    }
    /**
     * 获取指定控制柜的V3房间客户端触发器同步信息
     *
     * @param cabinetId
     * @param executor
     * @return
     */
    public ListenableFuture<List<V3RoomTriggerInfoModel.Trigger>> getV3RoomTriggerModelFromCabinetId(int cabinetId, Executor executor){
        log.trace("sendV3RoomTriggerUpdate");
        SettableFuture<List<V3RoomTriggerInfoModel.Trigger>> future = SettableFuture.create();
        Futures.addCallback(this.queryCorrelateV3RoomsOfCabinetId(cabinetId), new FutureCallback<List<RoomUnitV3>>() {
            @Override
            public void onSuccess(@Nullable List<RoomUnitV3> v3Rooms) {
                assert v3Rooms != null;
                List<ListenableFuture<List<RoomTriggerV3>>> ft = v3Rooms.stream()
                        .map(i->queryTriggerOfV3RoomWithCabinetId(UUID.fromString(i.getRoomuuid()),cabinetId))
                        .collect(Collectors.toList());
                Futures.addCallback(Futures.successfulAsList(ft), new FutureCallback<List<List<RoomTriggerV3>>>() {
                    @Override
                    public void onSuccess(@Nullable List<List<RoomTriggerV3>> result) {
                        assert result != null;
                        int size = v3Rooms.size();
                        List<V3RoomTriggerInfoModel.Trigger> finalTriggerList = new ArrayList<>();
                        for(int index=0;index<size;index++){
                            var v3Room = v3Rooms.get(index);
                            var triggers = result.get(index);
                            if(triggers == null){
                                log.error("can not query triggers of room<{}>",v3Room);
                                continue;
                            }

                            triggers.forEach(t->{
                                var trigger = new V3RoomTriggerInfoModel.Trigger(UUID.fromString(t.getTriggeruuid()),
                                        t.getTriggertype(),
                                        UUID.fromString(t.getRoomuuid()),
                                        t.getTriggerconfigure()
                                );
                                finalTriggerList.add(trigger);
                            });
                        }
                        future.set(finalTriggerList);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        future.setException(t);
                    }
                },MoreExecutors.directExecutor());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        },executor);
        return future;
    }

    public void updateTriggerFeedback2(UUID triggerUuid,String fb){
        var m = new RoomTriggerV3();
        m.setTriggeruuid(triggerUuid.toString());
        m.setFb2(fb);
//        var s = this.roomTriggerV3Mapper.selectByPrimaryKey(triggerUuid.toString());
//        s.setFb2(fb);
//        this.roomTriggerV3Mapper.updateByPrimaryKeyWithBLOBs(s);
        this.roomTriggerV3Mapper.updateByPrimaryKeySelective(m);
    }
}
