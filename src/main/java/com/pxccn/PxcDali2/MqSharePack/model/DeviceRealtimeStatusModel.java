package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class DeviceRealtimeStatusModel implements IPbModel<LcsProtos.DeviceRealtimeStatus> {

    public String toString() {
        return "";

    }

    public DeviceRealtimeStatusModel(LcsProtos.DeviceRealtimeStatus pb) {

    }

    public LcsProtos.DeviceRealtimeStatus getPb() {
        return LcsProtos.DeviceRealtimeStatus.newBuilder()
                .build();
    }
}
