package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;

public class CabinetStatusMqEvent extends FwBaseEvent {
    CabinetStatusWrapper message;

    public CabinetStatusMqEvent(Object source, CabinetStatusWrapper message) {
        super(source);
        this.message = message;
    }

    public CabinetStatusWrapper getMessage(){
        return this.message;
    }

}
