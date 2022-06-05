package com.pxccn.PxcDali2.MqSharePack.message;


import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PingRequestWrapper;
import com.pxccn.PxcDali2.Util;


public abstract class ProtoToPlcQueueMsg<T extends com.google.protobuf.GeneratedMessageV3> implements QueueMsg {

    private final ProtoHeaders headers;
    protected final T payload;
    private final long timestamp;


    public ProtoToPlcQueueMsg(ProtoHeaders headers, T payload) {
        this.headers = headers;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }


    @Override
    public ProtoHeaders getHeaders() {
        return this.headers;
    }
    @Override
    public long getTimestamp(){
        return this.timestamp;
    }

    @Override
    public byte[] getData() {
        return LcsProtos.ToPlcMessage
                .newBuilder()
                .setHeaders(this.headers.getInternalData())
                .setTimestamp(System.currentTimeMillis())
                .setPayload(Any.pack(this.payload))
                .build()
                .toByteArray();
    }

    public static ProtoToPlcQueueMsg FromData(byte[] data) throws InvalidProtocolBufferException {
        LcsProtos.ToPlcMessage msg = LcsProtos.ToPlcMessage.parseFrom(data);
        ProtoHeaders headers = new ProtoHeaders(msg.getHeaders());
        Any pb_payload = msg.getPayload();

        switch (pb_payload.getTypeUrl()){
            case PingRequestWrapper.TypeUrl:
                return new PingRequestWrapper(headers,pb_payload.unpack(LcsProtos.PingRequest.class));
            default:
                return null;
        }
    }

}