package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetDetailUploadWrapper;

public class CabinetDetailUploadEvent extends FwBaseEvent {
    CabinetDetailUploadWrapper message;

    public CabinetDetailUploadEvent(Object source, CabinetDetailUploadWrapper message) {
        super(source);
        this.message = message;
    }

    public CabinetDetailUploadWrapper getMessage() {
        return this.message;
    }

}
