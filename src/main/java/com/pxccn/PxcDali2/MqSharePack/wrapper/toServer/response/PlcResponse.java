package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.pxccn.PxcDali2.MqSharePack.model.IPbModel;

public abstract class PlcResponse<T extends com.google.protobuf.GeneratedMessageV3> implements IPbModel<T> {

    protected T payload;

    public PlcResponse(T payload) {
        this.payload = payload;
    }

    public T getPb() {
        return this.payload;
    }

}
