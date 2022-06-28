package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;


public class ActionRespWrapper extends ResponseWrapper<LcsProtos.ActionResp> {
    public static final String TypeUrl = "type.googleapis.com/ActionResp";

    final boolean success;

    //反向构造
    public ActionRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.ActionResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.ActionResp.class);
        this.success = v.getSuccess();
    }


    //正向构造
    public ActionRespWrapper(ResponseParam param,
                             boolean success
    ) {
        super(param);
        this.success = success;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.ActionResp
                .newBuilder()
                .setSuccess(this.success)

                .build()));
        return builder;
    }

}