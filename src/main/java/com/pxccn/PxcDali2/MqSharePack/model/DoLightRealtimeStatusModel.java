package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class DoLightRealtimeStatusModel extends CommonRealtimeStatusModel implements IPbModel<LcsProtos.DoLightRealtimeStatus> {
    public final boolean currentValue;

    public DoLightRealtimeStatusModel(
            UUID lightId,
            int terminalIndex,
            int shortAddress,
            long timestamp,
            String exceptionMessage,
            boolean currentValue
    ) {
        super(lightId, terminalIndex, shortAddress, timestamp, exceptionMessage);
        this.currentValue = currentValue;
    }

    public LcsProtos.DoLightRealtimeStatus getPb() {
        return LcsProtos.DoLightRealtimeStatus.newBuilder()
                .setCommon(this.getCommonPb())
                .setCurrentValue(this.currentValue)
                .build();

    }

    public DoLightRealtimeStatusModel(LcsProtos.DoLightRealtimeStatus pb) {
        super(pb.getCommon());
        this.currentValue = pb.getCurrentValue();
    }
}
