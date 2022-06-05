package com.pxccn.PxcDali2.server.events.ToComponent;

import com.pxccn.PxcDali2.MqSharePack.model.DeviceRealtimeStatusModel;

public class DeviceStatusMessageMqCompEvent extends ToLightsManagerCompEvent {
    DeviceRealtimeStatusModel message;

    public DeviceStatusMessageMqCompEvent(Object source, DeviceRealtimeStatusModel msg) {
        super(source);
        this.message = msg;
    }

    public DeviceRealtimeStatusModel getMessage() {
        return this.message;
    }
}
