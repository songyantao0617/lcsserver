package com.pxccn.PxcDali2.server.events;

import org.springframework.context.ApplicationEvent;

public class FwBaseEvent extends ApplicationEvent {
    public FwBaseEvent(Object source) {
        super(source);
    }
}
