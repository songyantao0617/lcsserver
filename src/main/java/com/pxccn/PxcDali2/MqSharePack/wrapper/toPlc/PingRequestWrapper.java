package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Util;

public class PingRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.PingRequest> {
    public static final String TypeUrl = "type.googleapis.com/PingRequest";


    //反序列化使用
    public PingRequestWrapper(ProtoHeaders headers, LcsProtos.PingRequest payload) {
        super(headers, payload);
    }

    public PingRequestWrapper(int foo, int bar) {
        super(Util.NewCommonHeaderForClient(), LcsProtos.PingRequest
                .newBuilder()
                .setFoo(foo)
                .setBar(bar)
                .build());
    }

    public int getFoo() {
        return this.payload.getFoo();
    }

    public int getBar() {
        return this.payload.getBar();
    }


}
