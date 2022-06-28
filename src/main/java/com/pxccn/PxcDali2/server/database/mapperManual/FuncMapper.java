package com.pxccn.PxcDali2.server.database.mapperManual;

import com.pxccn.PxcDali2.server.database.modelManual.FUNC_getV3LightsListFromRoomUuid;
import com.pxccn.PxcDali2.server.database.modelManual.FUNC_getV3RoomListFromCabinetID;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FuncMapper {
    @Select("SELECT * FROM plcInner_getV3RoomListFromCabinetID(#{cabinetId})")
    List<FUNC_getV3RoomListFromCabinetID> FUNC_getV3RoomListFromCabinetID(int cabinetId);

    @Select("SELECT * FROM plcInner_getV3LightsListFromRoomUuid(#{roomUuid})")
    List<FUNC_getV3LightsListFromRoomUuid> FUNC_getV3LightsListFromRoomUuid(String roomUuid);

    @Select("select cabinet_ID from dbo.room_light_map_V3 where roomUUID = #{roomUuid} group by cabinet_ID")
    List<Integer> FUNC_queryCorrelateCabinetIdFromRoomId(String roomUuid);
}