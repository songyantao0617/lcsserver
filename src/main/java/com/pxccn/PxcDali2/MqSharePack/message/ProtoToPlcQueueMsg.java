package com.pxccn.PxcDali2.MqSharePack.message;


import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.*;
import com.pxccn.PxcDali2.Proto.LcsProtos;


public abstract class ProtoToPlcQueueMsg<T extends com.google.protobuf.GeneratedMessageV3> implements QueueMsg {

    private final ProtoHeaders headers;
    private final long timestamp;


    public ProtoToPlcQueueMsg(ProtoHeaders headers) {
        this.headers = headers;
        this.timestamp = System.currentTimeMillis();
    }

    public ProtoToPlcQueueMsg(LcsProtos.ToPlcMessage pb) {
        this.headers = new ProtoHeaders(pb.getHeaders());
        this.timestamp = pb.getTimestamp();
    }

    protected abstract T.Builder internal_get_payload();

    @Override
    public ProtoHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public byte[] getData() {
        return LcsProtos.ToPlcMessage
                .newBuilder()
                .setHeaders(this.headers.getInternalData())
                .setTimestamp(System.currentTimeMillis())
                .setPayload(Any.pack(this.internal_get_payload().build()))
                .build()
                .toByteArray();
    }

    public static ProtoToPlcQueueMsg FromData(byte[] data) throws InvalidProtocolBufferException {
        LcsProtos.ToPlcMessage msg = LcsProtos.ToPlcMessage.parseFrom(data);
        Any pb_payload = msg.getPayload();

        switch (pb_payload.getTypeUrl()) {
            case PingRequestWrapper.TypeUrl:
                return new PingRequestWrapper(msg);
            case NiagaraOperateRequestWrapper.TypeUrl:
                return new NiagaraOperateRequestWrapper(msg);
            case DetailInfoRequestWrapper.TypeUrl:
                return new DetailInfoRequestWrapper(msg);
            case PollManagerSettingRequestWrapper.TypeUrl:
                return new PollManagerSettingRequestWrapper(msg);
//            case CabinetSimpleActionRequestWrapper.TypeUrl:
//                return new CabinetSimpleActionRequestWrapper(msg);
            case ActionWithFeedbackRequestWrapper.TypeUrl:
                return new ActionWithFeedbackRequestWrapper(msg);
            default:
                return null;
        }
    }

}