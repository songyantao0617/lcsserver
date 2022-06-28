package com.pxccn.PxcDali2.server.events.ToComponent;


import com.pxccn.PxcDali2.server.events.FwBaseEvent;

public class ToComponentEvent extends FwBaseEvent {
    String[] routingKey;

    public ToComponentEvent(Object source, String[] routingKey) {
        super(source);
        this.routingKey = routingKey;
    }

    public ToComponentEvent(Object source, String routingKey) {
        super(source);
        this.routingKey = routingKey.split("/");
    }

    public String[] getRoutingKey() {
        return this.routingKey;
    }
}
