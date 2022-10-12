package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.*;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class ResponseWrapper<T extends com.google.protobuf.GeneratedMessageV3> extends ProtoToServerQueueMsg<LcsProtos.Response> {
    public static final String TypeUrl = "type.googleapis.com/Response";
    private final LcsProtos.Response.Status status;
    private final String exceptionMessage;

    public ResponseWrapper(ResponseParam param) {
        super(param.tsmp);
        this.status = param.status;
        this.exceptionMessage = param.exceptionMessage;
    }

    public ResponseWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.Response v = pb.getPayload().unpack(LcsProtos.Response.class);
        this.status = v.getStatus();
        this.exceptionMessage = v.getExceptionMessage();

    }

    public static ResponseWrapper MAKE(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        switch (pb.getPayload().unpack(LcsProtos.Response.class).getPayload().getTypeUrl()) {
            case PingRespWrapper.TypeUrl:
                return new PingRespWrapper(pb);
            case NiagaraOperateRespWrapper.TypeUrl:
                return new NiagaraOperateRespWrapper(pb);
            case DetailInfoRespWrapper.TypeUrl:
                return new DetailInfoRespWrapper(pb);
            case PollManagerSettingRespWrapper.TypeUrl:
                return new PollManagerSettingRespWrapper(pb);
//            case CabinetSimpleActionRespWrapper.TypeUrl:
//                return new CabinetSimpleActionRespWrapper(pb);
            case ActionWithFeedbackRespWrapper.TypeUrl:
                return new ActionWithFeedbackRespWrapper(pb);
            case ActionRespWrapper.TypeUrl:
                return new ActionRespWrapper(pb);
            case OtaPackageResponseWrapper.TypeUrl:
                return new OtaPackageResponseWrapper(pb);
        }
        return new ResponseWrapper(pb);//异常情况
    }

    public LcsProtos.Response.Status getStatus() {
        return status;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        return LcsProtos.Response.newBuilder()
                .setStatus(this.status)
                .setExceptionMessage(this.exceptionMessage);
    }

    public static class ResponseParam {
        ToServerMsgParam tsmp;
        LcsProtos.Response.Status status;
        String exceptionMessage;

        public ResponseParam(ToServerMsgParam tsmp, LcsProtos.Response.Status status, String exceptionMessage) {
            this.tsmp = tsmp;
            this.status = status;
            this.exceptionMessage = exceptionMessage;
        }

        public ToServerMsgParam getTsmp() {
            return tsmp;
        }

        public void setTsmp(ToServerMsgParam tsmp) {
            this.tsmp = tsmp;
        }

        public LcsProtos.Response.Status getStatus() {
            return status;
        }

        public void setStatus(LcsProtos.Response.Status status) {
            this.status = status;
        }

        public String getExceptionMessage() {
            return exceptionMessage;
        }

        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }
    }

}