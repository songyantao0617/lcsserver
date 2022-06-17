package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.LightsDetailUploadWrapper;

public class LightsDetailUploadEvent extends FwBaseEvent {
    LightsDetailUploadWrapper message;
    int cabinetId;

    public int getCabinetId() {
        return cabinetId;
    }

    public LightsDetailUploadEvent(Object source, LightsDetailUploadWrapper message) {
        super(source);
        this.message = message;
    }

    public LightsDetailUploadWrapper getMessage() {
        return this.message;
    }

}
