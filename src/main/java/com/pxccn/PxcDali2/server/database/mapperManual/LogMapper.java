package com.pxccn.PxcDali2.server.database.mapperManual;

import com.pxccn.PxcDali2.MqSharePack.model.CabinetLogEventModel;
import com.pxccn.PxcDali2.server.service.log.LogService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.sql.Timestamp;
import java.util.List;

public interface LogMapper {
    @Insert("<script>  " +
            "INSERT INTO dbo.cabinet_logs (cabinet_ID,ts,level,className,methodName,msg,errorStack) VALUES " +
            "<foreach collection=\"list\" item=\"item\"  separator=\",\"> " +
            "(#{cabinetId},DATEADD(s,#{item.timestamp},'1970-01-01 08:00:00'),#{item.logLevel},#{item.clsName},#{item.methodName},#{item.message},#{item.error}) " +
            "</foreach> " +
            "</script>")
    int insertCabinetLog(int cabinetId, List<CabinetLogEventModel> list);


    @Insert("<script>  " +
            "INSERT INTO dbo.room_action_log (cabinet_ID,roomUuid,time,source,cmd103_tag,cmd103_value,dt8_tag,dt8_value1,dt8_value2) VALUES " +
            "<foreach collection=\"list\" item=\"item\"  separator=\",\"> " +
            "(#{item.cabinet_ID},#{item.roomUuid},DATEADD(s,#{item.timestamp},'1970-01-01 08:00:00'),#{item.source},#{item.cmd103_tag},#{item.cmd103_value},#{item.dt8_tag},#{item.dt8_value1},#{item.dt8_value2}) " +
            "</foreach> " +
            "</script>")
    int insertRoomAction(List<LogService.RoomAction> list);

    @Delete("DELETE FROM dbo.room_action_log WHERE time <= #{time}")
    int deleteRoomActionBefore(Timestamp time);

    @Delete("DELETE FROM dbo.cabinet_logs WHERE ts <= #{time}")
    int deleteCabinetLogBefore(Timestamp time);
}


