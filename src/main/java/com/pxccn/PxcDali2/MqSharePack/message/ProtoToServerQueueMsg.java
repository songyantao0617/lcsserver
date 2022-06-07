package com.pxccn.PxcDali2.MqSharePack.message;


import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RealtimeStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public abstract class ProtoToServerQueueMsg<T extends com.google.protobuf.GeneratedMessageV3> implements QueueMsg {

    private final long timestamp;
    private final ProtoHeaders headers;
    private final int cabinetId;

    public int getCabinetId() {
        return this.cabinetId;
    }

    public static class ToServerMsgParam {
        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public ProtoHeaders getHeaders() {
            return headers;
        }

        public void setHeaders(ProtoHeaders headers) {
            this.headers = headers;
        }

        public int getCabinetId() {
            return cabinetId;
        }

        public void setCabinetId(int cabinetId) {
            this.cabinetId = cabinetId;
        }

        long timestamp;
        ProtoHeaders headers;
        int cabinetId;

        public ToServerMsgParam(long timestamp, ProtoHeaders headers, int cabinetId) {
            this.timestamp = timestamp;
            this.headers = headers;
            this.cabinetId = cabinetId;
        }
    }

    public ProtoToServerQueueMsg(ToServerMsgParam param) {
        this(param.timestamp, param.cabinetId, param.headers);
    }

    public ProtoToServerQueueMsg(long timestamp, int cabinetId, ProtoHeaders headers) {
        this.timestamp = timestamp;
        this.cabinetId = cabinetId;
        this.headers = headers;
    }

    public ProtoToServerQueueMsg(LcsProtos.ToServerMessage pb) {
        this.timestamp = pb.getTimestamp();
        this.cabinetId = pb.getCabinetId();
        this.headers = new ProtoHeaders(pb.getHeaders());
    }


    protected abstract T.Builder internal_get_payload();


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
                .setPayload(Any.pack(this.internal_get_payload().build()))
                .build()
                .toByteArray();
    }

    public static ProtoToServerQueueMsg FromData(byte[] data) throws InvalidProtocolBufferException {
        LcsProtos.ToServerMessage msg = LcsProtos.ToServerMessage.parseFrom(data);
        Any pb_payload = msg.getPayload();

        switch (pb_payload.getTypeUrl()) {
            case ResponseWrapper.TypeUrl:
                return ResponseWrapper.MAKE(msg);
            case RealtimeStatusWrapper.TypeUrl:
                return new RealtimeStatusWrapper(msg);
            case CabinetStatusWrapper.TypeUrl:
                return new CabinetStatusWrapper(msg);
            default:
                return null;
        }
    }
}