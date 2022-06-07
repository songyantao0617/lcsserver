package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class PingRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.PingRequest> {
    public static final String TypeUrl = "type.googleapis.com/PingRequest";
    private final int foo;
    private final int bar;


    //反序列化使用
    public PingRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.PingRequest v = pb.getPayload().unpack(LcsProtos.PingRequest.class);
        this.foo = v.getFoo();
        this.bar = v.getBar();
    }

    public PingRequestWrapper(ProtoHeaders headers, int foo, int bar) {
        super(headers);
        this.foo = foo;
        this.bar = bar;
    }

    @Override
    protected LcsProtos.PingRequest.Builder internal_get_payload() {
        return LcsProtos.PingRequest.newBuilder().setFoo(this.foo).setBar(this.bar);
    }

}
