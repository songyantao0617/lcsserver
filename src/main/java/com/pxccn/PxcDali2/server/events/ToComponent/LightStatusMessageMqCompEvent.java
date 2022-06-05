package com.pxccn.PxcDali2.server.events.ToComponent;

import com.pxccn.PxcDali2.MqSharePack.model.LightRealtimeStatusModel;

public class LightStatusMessageMqCompEvent extends ToLightsManagerCompEvent {
    LightRealtimeStatusModel message;

    public LightStatusMessageMqCompEvent(Object source, LightRealtimeStatusModel msg) {
        super(source);
        this.message = msg;
    }

    public LightRealtimeStatusModel getMessage() {
        return this.message;
    }
}
