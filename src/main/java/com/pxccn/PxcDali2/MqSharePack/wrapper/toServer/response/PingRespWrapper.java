package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class PingRespWrapper extends ResponseWrapper<LcsProtos.PingResp> {
    public static final String TypeUrl = "type.googleapis.com/PingResp";
    private final int foo;
    private final int bar;
    String cabinetName;

    public PingRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.PingResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.PingResp.class);
        this.foo = v.getFoo();
        this.bar = v.getBar();
        this.cabinetName = v.getCabinetName();
    }
    public PingRespWrapper(ResponseParam param, int foo, int bar, String cabinetName) {
        super(param);
        this.foo = foo;
        this.bar = bar;
        this.cabinetName = cabinetName;
    }

    public int getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.PingResp
                .newBuilder()
                .setFoo(this.foo)
                .setBar(this.bar)
                .setCabinetName(this.cabinetName)
                .build()));
        return builder;
    }

}
