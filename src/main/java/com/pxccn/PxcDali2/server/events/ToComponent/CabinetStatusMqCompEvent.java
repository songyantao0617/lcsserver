package com.pxccn.PxcDali2.server.events.ToComponent;

import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;

public class CabinetStatusMqCompEvent extends ToCabinetsManagerCompEvent {
    CabinetStatusWrapper message;

    public CabinetStatusMqCompEvent(Object source, CabinetStatusWrapper message) {
        super(source);
        this.message = message;
    }

    public CabinetStatusWrapper getMessage() {
        return this.message;
    }

}
