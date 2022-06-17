package com.pxccn.PxcDali2.server.events.ToComponent;

import com.pxccn.PxcDali2.MqSharePack.model.CommonRealtimeStatusModel;

public class LightStatusMessageMqCompEvent extends ToLightsManagerCompEvent {
    CommonRealtimeStatusModel message;

    public LightStatusMessageMqCompEvent(Object source, CommonRealtimeStatusModel msg) {
        super(source);
        this.message = msg;
    }

    public CommonRealtimeStatusModel getMessage() {
        return this.message;
    }
}
