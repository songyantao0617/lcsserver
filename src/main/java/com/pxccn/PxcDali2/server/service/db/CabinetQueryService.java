package com.pxccn.PxcDali2.server.service.db;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.database.mapper.CabinetV2Mapper;
import com.pxccn.PxcDali2.server.database.mapper.RoomLightMapV3Mapper;
import com.pxccn.PxcDali2.server.database.mapper.RoomUnitV3Mapper;
import com.pxccn.PxcDali2.server.database.mapperManual.FuncMapper;
import com.pxccn.PxcDali2.server.database.model.RoomLightMapV3;
import com.pxccn.PxcDali2.server.database.model.RoomLightMapV3Example;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3;
import com.pxccn.PxcDali2.server.database.model.RoomUnitV3Example;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CabinetQueryService {

    ListeningExecutorService listeningExecutorService;
    @Autowired
    CabinetV2Mapper cabinetV2Mapper;
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
     * 查询与指定房间相关联的CabinetId
     *
     * @param roomUuid
     */
    public ListenableFuture<List<Integer>> queryCorrelateCabinetIdFromV3RoomId(UUID roomUuid) {
        return this.listeningExecutorService.submit(() -> this.funcMapper.FUNC_queryCorrelateCabinetIdFromRoomId(roomUuid.toString())
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

}
