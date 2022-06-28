package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class DetailInfoRespWrapper extends ResponseWrapper<LcsProtos.DetailInfoResp> {
    public static final String TypeUrl = "type.googleapis.com/DetailInfoResp";
    private final int uploadedLightsCount;
    private final int uploadedRoomsCount;

    public DetailInfoRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.DetailInfoResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.DetailInfoResp.class);
        this.uploadedLightsCount = v.getUploadedLightsCount();
        this.uploadedRoomsCount = v.getUploadedRoomsCount();
    }
    public DetailInfoRespWrapper(ResponseParam param, int lightsCount, int roomCount) {
        super(param);
        this.uploadedLightsCount = lightsCount;
        this.uploadedRoomsCount = roomCount;
    }

    public int getUploadedLightsCount() {
        return uploadedLightsCount;
    }

    public int getUploadedRoomsCount() {
        return uploadedRoomsCount;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.DetailInfoResp
                .newBuilder()
                .setUploadedLightsCount(this.uploadedLightsCount)
                .setUploadedRoomsCount(this.uploadedRoomsCount)
                .build()));
        return builder;
    }

}
