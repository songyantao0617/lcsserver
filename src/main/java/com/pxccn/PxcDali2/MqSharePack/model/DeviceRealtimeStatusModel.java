package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class DeviceRealtimeStatusModel implements IPbModel<LcsProtos.Dali2DeviceRealtimeStatus> {


    public DeviceRealtimeStatusModel(LcsProtos.Dali2DeviceRealtimeStatus pb) {

    }

    public LcsProtos.Dali2DeviceRealtimeStatus getPb() {
        return LcsProtos.Dali2DeviceRealtimeStatus.newBuilder()
                .build();
    }
}
