package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RoomsDetailUploadWrapper;

public class RoomsDetailUploadEvent extends FwBaseEvent {
    RoomsDetailUploadWrapper message;

    public RoomsDetailUploadEvent(Object source, RoomsDetailUploadWrapper message) {
        super(source);
        this.message = message;
    }

    public RoomsDetailUploadWrapper getMessage() {
        return this.message;
    }

}
