package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetSimpleEventWrapper;

public class CabinetSimpleEvent extends FwBaseEvent {
    CabinetSimpleEventWrapper message;
    int cabinetId;

    public int getCabinetId() {
        return cabinetId;
    }

    public CabinetSimpleEvent(Object source, CabinetSimpleEventWrapper message) {
        super(source);
        this.message = message;
    }

    public CabinetSimpleEventWrapper getMessage() {
        return this.message;
    }

}
