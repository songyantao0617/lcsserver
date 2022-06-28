package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class ActionWithFeedbackRespWrapper extends ResponseWrapper<LcsProtos.ActionWithFeedbackResp> {
    public static final String TypeUrl = "type.googleapis.com/ActionWithFeedbackResp";
    private final boolean allDispatched;

    public ActionWithFeedbackRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.ActionWithFeedbackResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.ActionWithFeedbackResp.class);
        this.allDispatched = v.getAllDispatched();
    }

    public ActionWithFeedbackRespWrapper(ResponseParam param, boolean allDispatched) {
        super(param);
        this.allDispatched = allDispatched;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.ActionWithFeedbackResp
                .newBuilder()
                .setAllDispatched(this.allDispatched)
                .build()));
        return builder;
    }

    public boolean isAllDispatched() {
        return allDispatched;
    }

}
