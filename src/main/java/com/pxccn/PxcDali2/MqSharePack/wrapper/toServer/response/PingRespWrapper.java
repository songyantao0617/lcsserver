package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class PingRespWrapper extends PlcResponse<LcsProtos.PingResp> {
    public static final String TypeUrl = "type.googleapis.com/PingResp";


    public PingRespWrapper(int foo, int bar) {
        super(LcsProtos.PingResp.newBuilder().setFoo(foo).setBar(bar).build());
    }

    public PingRespWrapper(LcsProtos.PingResp resp) {
        super(resp);
    }

    public int getFoo() {
        return this.payload.getFoo();
    }

    public int getBar() {
        return this.payload.getBar();
    }
}
