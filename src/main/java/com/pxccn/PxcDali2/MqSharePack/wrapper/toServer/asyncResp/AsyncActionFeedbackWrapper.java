package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class AsyncActionFeedbackWrapper extends ProtoToServerQueueMsg<LcsProtos.AsyncActionFeedback> {
    public static final String TypeUrl = "type.googleapis.com/AsyncActionFeedback";
    final ActionFeedBack feedBack;
    final String exceptionMessage;
    private final UUID requestId;

    //反向构造
    public AsyncActionFeedbackWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.AsyncActionFeedback v = pb.getPayload().unpack(LcsProtos.AsyncActionFeedback.class);
        this.requestId = Util.ToUuid(v.getRequestId());
        ActionFeedBack feedBack;
        switch (v.getPayloadCase().getNumber()) {
            case LcsProtos.AsyncActionFeedback.SETSHORTADDRESS_FIELD_NUMBER:
                feedBack = new SetShortAddress(v.getSetShortAddress());
                break;
            case LcsProtos.AsyncActionFeedback.BLINK_FIELD_NUMBER:
                feedBack = new Blink(v.getBlink());
                break;
            case LcsProtos.AsyncActionFeedback.SENDLEVELINSTRUCTION_FIELD_NUMBER:
                feedBack = new SendLevelInstruction(v.getSendLevelInstruction());
                break;
            case LcsProtos.AsyncActionFeedback.SENDBROADCASTLEVELINSTRUCTION_FIELD_NUMBER:
                feedBack = new SendBroadcastLevelInstruction(v.getSendBroadcastLevelInstruction());
                break;
            case LcsProtos.AsyncActionFeedback.SAVESTATION_FIELD_NUMBER:
                feedBack = new SaveStation(v.getSaveStation());
                break;
            case LcsProtos.AsyncActionFeedback.SETSYSTIME_FIELD_NUMBER:
                feedBack = new SetSysTime(v.getSetSysTime());
                break;
            case LcsProtos.AsyncActionFeedback.DBSYNC_FIELD_NUMBER:
                feedBack = new DbSync(v.getDbSync());
                break;
            default:
                feedBack = null;
                break;
        }
        this.feedBack = feedBack;
        this.exceptionMessage = v.getExceptionMessage();
    }

    //正向构造
    public AsyncActionFeedbackWrapper(ToServerMsgParam param,
                                      UUID requestId,
                                      ActionFeedBack actionFeedBack
    ) {
        super(param);
        this.requestId = requestId;
        this.feedBack = actionFeedBack;
        this.exceptionMessage = "";
    }

    //正向构造（异常）
    public AsyncActionFeedbackWrapper(ToServerMsgParam param,
                                      UUID requestId,
                                      String exceptionMessage
    ) {
        super(param);
        this.requestId = requestId;
        this.feedBack = null;
        this.exceptionMessage = exceptionMessage;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public ActionFeedBack getFeedback() {
        return this.feedBack;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    protected LcsProtos.AsyncActionFeedback.Builder internal_get_payload() {
        LcsProtos.AsyncActionFeedback.Builder builder = LcsProtos.AsyncActionFeedback.newBuilder()
                .setRequestId(Util.ToPbUuid(this.requestId))
                .setExceptionMessage(this.exceptionMessage);

        if (this.feedBack instanceof Blink) {
            builder.setBlink(((Blink) this.feedBack).getPb());
        } else if (this.feedBack instanceof SetShortAddress) {
            builder.setSetShortAddress(((SetShortAddress) this.feedBack).getPb());
        } else if (this.feedBack instanceof SendLevelInstruction) {
            builder.setSendLevelInstruction(((SendLevelInstruction) this.feedBack).getPb());
        } else if (this.feedBack instanceof SendBroadcastLevelInstruction) {
            builder.setSendBroadcastLevelInstruction(((SendBroadcastLevelInstruction) this.feedBack).getPb());
        } else if (this.feedBack instanceof SaveStation) {
            builder.setSaveStation(((SaveStation) this.feedBack).getPb());
        } else if (this.feedBack instanceof SetSysTime) {
            builder.setSetSysTime(((SetSysTime) this.feedBack).getPb());
        } else if (this.feedBack instanceof DbSync) {
            builder.setDbSync(((DbSync) this.feedBack).getPb());
        }
        return builder;
    }

    public static abstract class ActionFeedBack<T extends com.google.protobuf.GeneratedMessageV3> {
        abstract T getPb();
    }

    public static class Blink extends ActionFeedBack<LcsProtos.AsyncActionFeedback.Blink> {

        public Blink(LcsProtos.AsyncActionFeedback.Blink pb) {

        }

        public Blink() {

        }

        public Blink(String errorMsg) {

        }


        @Override
        LcsProtos.AsyncActionFeedback.Blink getPb() {
            return LcsProtos.AsyncActionFeedback.Blink.newBuilder()
                    .build();
        }
    }

    public static class SetShortAddress extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SetShortAddress> {


        public SetShortAddress() {

        }


        public SetShortAddress(LcsProtos.AsyncActionFeedback.SetShortAddress pb) {

        }

        @Override
        LcsProtos.AsyncActionFeedback.SetShortAddress getPb() {
            return LcsProtos.AsyncActionFeedback.SetShortAddress.newBuilder()

                    .build();
        }
    }

    public static class SendLevelInstruction extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SendLevelInstruction> {

        final int countOfRoom;
        final int countOfDo;
        final int countOfDali2;

        public SendLevelInstruction(int countOfRoom, int countOfDo, int countOfDali2) {
            this.countOfDali2 = countOfDali2;
            this.countOfDo = countOfDo;
            this.countOfRoom = countOfRoom;
        }
        public SendLevelInstruction(LcsProtos.AsyncActionFeedback.SendLevelInstruction pb) {
            this.countOfRoom = pb.getCountOfRoom();
            this.countOfDo = pb.getCountOfDo();
            this.countOfDali2 = pb.getCountOfDali2();
        }

        public int getCountOfRoom() {
            return countOfRoom;
        }

        public int getCountOfDo() {
            return countOfDo;
        }

        public int getCountOfDali2() {
            return countOfDali2;
        }

        @Override
        LcsProtos.AsyncActionFeedback.SendLevelInstruction getPb() {
            return LcsProtos.AsyncActionFeedback.SendLevelInstruction.newBuilder()
                    .setCountOfDali2(this.countOfDali2)
                    .setCountOfDo(this.countOfDo)
                    .setCountOfRoom(this.countOfRoom)
                    .build();
        }
    }

    public static class SendBroadcastLevelInstruction extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SendBroadcastLevelInstruction> {


        final int countOfLights;

        public SendBroadcastLevelInstruction(int countOfLights) {
            this.countOfLights = countOfLights;
        }

        public SendBroadcastLevelInstruction(LcsProtos.AsyncActionFeedback.SendBroadcastLevelInstruction pb) {
            this.countOfLights = pb.getCountOfLights();
        }

        public int getCountOfLights() {
            return countOfLights;
        }

        @Override
        LcsProtos.AsyncActionFeedback.SendBroadcastLevelInstruction getPb() {
            return LcsProtos.AsyncActionFeedback.SendBroadcastLevelInstruction.newBuilder()
                    .setCountOfLights(this.countOfLights)
                    .build();
        }
    }

    public static class SaveStation extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SaveStation> {


        public SaveStation() {

        }


        public SaveStation(LcsProtos.AsyncActionFeedback.SaveStation pb) {

        }

        @Override
        LcsProtos.AsyncActionFeedback.SaveStation getPb() {
            return LcsProtos.AsyncActionFeedback.SaveStation.newBuilder()

                    .build();
        }
    }

    public static class SetSysTime extends ActionFeedBack<LcsProtos.AsyncActionFeedback.SetSysTime> {


        final long currentTime;

        public SetSysTime(long currentTime) {
            this.currentTime = currentTime;
        }

        public SetSysTime(LcsProtos.AsyncActionFeedback.SetSysTime pb) {
            this.currentTime = pb.getCurrentTime();
        }

        @Override
        LcsProtos.AsyncActionFeedback.SetSysTime getPb() {
            return LcsProtos.AsyncActionFeedback.SetSysTime.newBuilder()
                    .setCurrentTime(this.currentTime)
                    .build();
        }
    }

    public static class DbSync extends ActionFeedBack<LcsProtos.AsyncActionFeedback.DbSync> {

        final String msg;

        public DbSync(String message) {
            this.msg = message;
        }

        public DbSync(LcsProtos.AsyncActionFeedback.DbSync pb) {
            this.msg = pb.getMessage();
        }

        public String getMsg() {
            return msg;
        }

        @Override
        LcsProtos.AsyncActionFeedback.DbSync getPb() {
            return LcsProtos.AsyncActionFeedback.DbSync.newBuilder()
                    .setMessage(this.msg)
                    .build();
        }
    }
}
