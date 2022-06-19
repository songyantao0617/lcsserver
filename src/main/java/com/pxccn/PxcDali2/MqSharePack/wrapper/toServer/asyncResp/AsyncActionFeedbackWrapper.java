package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class AsyncActionFeedbackWrapper extends ProtoToServerQueueMsg<LcsProtos.AsyncActionFeedback> {
    public static final String TypeUrl = "type.googleapis.com/AsyncActionFeedback";

    public static AsyncActionFeedbackWrapper Blink(ToServerMsgParam param, UUID requestId) {
        return new AsyncActionFeedbackWrapper(param, requestId, new Blink());
    }

    public static AsyncActionFeedbackWrapper Blink(ToServerMsgParam param, UUID requestId, String errorReason) {
        return new AsyncActionFeedbackWrapper(param, requestId, new Blink(errorReason));
    }

    public static AsyncActionFeedbackWrapper SetShortAddressSuccess(ToServerMsgParam param, UUID requestId) {
        return new AsyncActionFeedbackWrapper(param, requestId, new SetShortAddress());
    }

    public static AsyncActionFeedbackWrapper SetShortAddressFailure(ToServerMsgParam param, UUID requestId, String errorReason) {
        return new AsyncActionFeedbackWrapper(param, requestId, new SetShortAddress(errorReason));
    }


    private final UUID requestId;

    public UUID getRequestId() {
        return requestId;
    }

    public ActionFeedBack getFeedback() {
        return this.feedBack;
    }

    final ActionFeedBack feedBack;

    public static abstract class ActionFeedBack<T extends com.google.protobuf.GeneratedMessageV3> {
        abstract T getPb();
    }

    public static class Blink extends ActionFeedBack<LcsProtos.AsyncActionFeedback.Blink> {

        public Blink(LcsProtos.AsyncActionFeedback.Blink pb) {
            this.success = pb.getSuccess();
            this.errorMsg = pb.getFailureReason();
        }

        public Blink() {
            this.success = true;
            this.errorMsg = "";
        }

        public Blink(String errorMsg) {
            this.success = false;
            this.errorMsg = errorMsg;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        final boolean success;
        final String errorMsg;

        @Override
        LcsProtos.AsyncActionFeedback.Blink getPb() {
            return LcsProtos.AsyncActionFeedback.Blink.newBuilder()
                    .setSuccess(this.success)
                    .setFailureReason(this.errorMsg)
                    .build();
        }
    }

    public static class SetShortAddress extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SetShortAddress> {

        public boolean isSuccess() {
            return success;
        }

        public String getFailureReason() {
            return failureReason;
        }

        boolean success;
        String failureReason;

        public SetShortAddress() {
            this.success = true;
            this.failureReason = "";
        }

        public SetShortAddress(String errorMessage) {
            this.success = false;
            this.failureReason = errorMessage;
        }

        public SetShortAddress(LcsProtos.AsyncActionFeedback.SetShortAddress pb) {
            this.success = pb.getSuccess();
            this.failureReason = pb.getFailureReason();
        }

        @Override
        LcsProtos.AsyncActionFeedback.SetShortAddress getPb() {
            return LcsProtos.AsyncActionFeedback.SetShortAddress.newBuilder()
                    .setSuccess(this.success)
                    .setFailureReason(this.failureReason)
                    .build();
        }
    }

    //反向构造
    public AsyncActionFeedbackWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.AsyncActionFeedback v = pb.getPayload().unpack(LcsProtos.AsyncActionFeedback.class);
        //TODO:赋property
        this.requestId = Util.ToUuid(v.getRequestId());
        ActionFeedBack feedBack;
        switch (v.getPayloadCase().getNumber()) {
            case LcsProtos.AsyncActionFeedback.SETSHORTADDRESS_FIELD_NUMBER:
                feedBack = new SetShortAddress(v.getSetShortAddress());
                break;
            case LcsProtos.AsyncActionFeedback.BLINK_FIELD_NUMBER:
                feedBack = new Blink(v.getBlink());
                break;
            default:
                feedBack = null;
                break;
        }
        this.feedBack = feedBack;
    }

    //正向构造
    public AsyncActionFeedbackWrapper(ToServerMsgParam param,
                                      //TODO: 构造参数
                                      UUID requestId,
                                      ActionFeedBack actionFeedBack
    ) {
        super(param);
        //TODO:赋property
        this.requestId = requestId;
        this.feedBack = actionFeedBack;
    }


    @Override
    protected LcsProtos.AsyncActionFeedback.Builder internal_get_payload() {
        LcsProtos.AsyncActionFeedback.Builder builder = LcsProtos.AsyncActionFeedback.newBuilder()
                //TODO:property转pb
                .setRequestId(Util.ToPbUuid(this.requestId));

        if (this.feedBack instanceof Blink) {
            builder.setBlink(((Blink) this.feedBack).getPb());
        } else if (this.feedBack instanceof SetShortAddress) {
            builder.setSetShortAddress(((SetShortAddress) this.feedBack).getPb());
        }


        return builder;
    }
}
