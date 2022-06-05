package com.pxccn.PxcDali2.MqSharePack.message;


import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RealtimeStatusWrapper;

public class ProtoToServerQueueMsg<T extends com.google.protobuf.GeneratedMessageV3> implements QueueMsg {

    private final long timestamp;
    private final ProtoHeaders headers;
    protected final T payload;
    private final int cabinetId;

    public int getCabinetId() {
        return this.cabinetId;
    }

    public ProtoToServerQueueMsg(long timestamp, int cabinetId, ProtoHeaders headers, T payload) {
        this.timestamp = timestamp;
        this.headers = headers;
        this.payload = payload;
        this.cabinetId = cabinetId;
    }



    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public ProtoHeaders getHeaders() {
        return this.headers;
    }


    @Override
    public byte[] getData() {
        return LcsProtos.ToServerMessage
                .newBuilder()
                .setCabinetId(this.cabinetId)
                .setTimestamp(this.timestamp)
                .setHeaders(this.headers.getInternalData())
                .setPayload(Any.pack(this.payload))
                .build()
                .toByteArray();
    }

    public static ProtoToServerQueueMsg FromData(byte[] data) throws InvalidProtocolBufferException {
        LcsProtos.ToServerMessage msg = LcsProtos.ToServerMessage.parseFrom(data);
        long timestamp = msg.getTimestamp();
        int cabinetId = msg.getCabinetId();
        ProtoHeaders headers = new ProtoHeaders(msg.getHeaders());
        Any pb_payload = msg.getPayload();

        switch (pb_payload.getTypeUrl()) {
            case ResponseWrapper.TypeUrl:
                return new ResponseWrapper(timestamp, cabinetId, headers, pb_payload.unpack(LcsProtos.Response.class));
            case RealtimeStatusWrapper.TypeUrl:
                return new RealtimeStatusWrapper(timestamp, cabinetId, headers, pb_payload.unpack(LcsProtos.RealtimeStatus.class));
            case CabinetStatusWrapper.TypeUrl:
                return new CabinetStatusWrapper(timestamp, cabinetId, headers, pb_payload.unpack(LcsProtos.CabinetStatus.class));
            default:
                return null;
        }
    }
}