package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PlcResponse;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PingRespWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class ResponseWrapper extends ProtoToServerQueueMsg<LcsProtos.Response> {
    public static final String TypeUrl = "type.googleapis.com/Response";


    public LcsProtos.Response.Status getStatus() {
        return this.payload.getStatus();
    }

    public String getExceptionMessage() {
        return this.payload.getExceptionMessage();
    }

    public PlcResponse getResponse() throws InvalidProtocolBufferException {
        switch (payload.getPayload().getTypeUrl()) {
            case PingRespWrapper.TypeUrl:
                return new PingRespWrapper(payload.getPayload().unpack(LcsProtos.PingResp.class));

        }
        return null;
    }

    public ResponseWrapper(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response payload) throws InvalidProtocolBufferException {
        super(timestamp, cabinetId, headers, payload);
    }

    public ResponseWrapper(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response.Status status, String exceptionMessage, PlcResponse response) {
        super(timestamp, cabinetId, headers, LcsProtos.Response
                .newBuilder()
                .setStatus(status)
                .setExceptionMessage(exceptionMessage)
                .setPayload(Any.pack(response.getPb()))
                .build());
    }

    public ResponseWrapper(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.Response.Status status, String exceptionMessage) {
        super(timestamp, cabinetId, headers, LcsProtos.Response
                .newBuilder()
                .setStatus(status)
                .setExceptionMessage(exceptionMessage)
                .build());
    }
}