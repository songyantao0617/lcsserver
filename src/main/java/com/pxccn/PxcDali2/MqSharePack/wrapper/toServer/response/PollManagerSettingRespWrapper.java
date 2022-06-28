package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class PollManagerSettingRespWrapper extends ResponseWrapper<LcsProtos.PollManagerSettingResp> {
    public static final String TypeUrl = "type.googleapis.com/PollManagerSettingResp";


    private final int managerCount;

    public PollManagerSettingRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.PollManagerSettingResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.PollManagerSettingResp.class);
        this.managerCount = v.getManagerCount();
    }

    public PollManagerSettingRespWrapper(ResponseParam param, int managerCount) {
        super(param);
        this.managerCount = managerCount;
    }

    public int getManagerCount() {
        return managerCount;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.PollManagerSettingResp
                .newBuilder()
                .setManagerCount(this.managerCount)
                .build()));
        return builder;
    }

}
