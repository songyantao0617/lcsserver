package com.pxccn.PxcDali2.server.events;

public class CabinetAliveChangedEvent extends FwBaseEvent {
    final boolean IsOnline;
    final long Timestamp;

    public CabinetAliveChangedEvent(Object source, boolean isOnline,long timestamp) {
        super(source);
        this.IsOnline = isOnline;
        this.Timestamp = timestamp;
    }

    public boolean isOnLine() {
        return this.IsOnline;
    }
}
