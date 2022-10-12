package com.pxccn.PxcDali2.server.database.mapperManual;

import org.apache.ibatis.annotations.Update;

public interface UpdateAuditTerminalLightInfoMapper {

    @Update("UPDATE dbo.cabinet_module_V2 SET audit_light_count=#{audit_light_count},audit_error_light_count=#{audit_error_light_count},audit_time=GETDATE() WHERE cabinet_ID=#{cabinetId} and terminal_Index=#{terminalIndex}")
    Integer updateLightCountAndErrorCount(int cabinetId,int terminalIndex,int audit_light_count,int audit_error_light_count);

}
