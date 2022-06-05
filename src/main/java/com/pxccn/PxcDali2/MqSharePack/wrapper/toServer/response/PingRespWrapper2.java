package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper2;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class PingRespWrapper2 extends ResponseWrapper2<LcsProtos.PingResp> {
    public static final String TypeUrl = "type.googleapis.com/PingResp";


    public PingRespWrapper2(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response payload) throws InvalidProtocolBufferException {
        super(timestamp, cabinetId, headers, payload);
    }

    public PingRespWrapper2(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response.Status status, String exceptionMessage, PlcResponse response) {
        super(timestamp, cabinetId, headers, status, exceptionMessage, response);
    }

    public PingRespWrapper2(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response.Status status, String exceptionMessage) {
        super(timestamp, cabinetId, headers, status, exceptionMessage);
    }
}
