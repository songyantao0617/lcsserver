package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.FilePackModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            case LcsProtos.AsyncActionFeedback.V3ROOMUPDATE_FIELD_NUMBER:
                feedBack = new V3RoomUpdate(v.getV3RoomUpdate());
                break;
            case LcsProtos.AsyncActionFeedback.V3ROOMTRIGGERUPDATE_FIELD_NUMBER:
                feedBack = new V3RoomTriggerUpdate(v.getV3RoomTriggerUpdate());
                break;
            case LcsProtos.AsyncActionFeedback.FILEUPLOAD_FIELD_NUMBER:
                feedBack = new FileUpload(v.getFileUpload());
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
        } else if(this.feedBack instanceof V3RoomUpdate){
            builder.setV3RoomUpdate(((V3RoomUpdate) this.feedBack).getPb());
        }else if(this.feedBack instanceof V3RoomTriggerUpdate){
            builder.setV3RoomTriggerUpdate((((V3RoomTriggerUpdate)this.feedBack).getPb()));
        }else if(this.feedBack instanceof FileUpload){
            builder.setFileUpload(((FileUpload)this.feedBack).getPb());
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

    public static class V3RoomUpdate extends ActionFeedBack<LcsProtos.AsyncActionFeedback.V3RoomUpdate> {

        public V3RoomUpdate(List<UUID> updatedRooms, List<UUID> removedRooms, List<UUID> unChangedRooms) {
            this.updatedRooms = updatedRooms;
            this.removedRooms = removedRooms;
            this.unChangedRooms = unChangedRooms;
        }

        public List<UUID> getUpdatedRooms() {
            return updatedRooms;
        }

        public List<UUID> getRemovedRooms() {
            return removedRooms;
        }

        public List<UUID> getUnChangedRooms() {
            return unChangedRooms;
        }

        @Override
        public String toString() {
            return "V3RoomUpdate{" +
                    "updatedRooms=" + updatedRooms +
                    ", removedRooms=" + removedRooms +
                    ", unChangedRooms=" + unChangedRooms +
                    '}';
        }

        final List<UUID> updatedRooms;
        final List<UUID> removedRooms;
        final List<UUID> unChangedRooms;


        public V3RoomUpdate(LcsProtos.AsyncActionFeedback.V3RoomUpdate pb) {
            this.updatedRooms = pb.getUpdatedRoomsList().stream().map(Util::ToUuid).collect(Collectors.toList());
            this.removedRooms = pb.getRemovedRoomsList().stream().map(Util::ToUuid).collect(Collectors.toList());
            this.unChangedRooms = pb.getUnChangedRoomsList().stream().map(Util::ToUuid).collect(Collectors.toList());
        }


        @Override
        LcsProtos.AsyncActionFeedback.V3RoomUpdate getPb() {
            return LcsProtos.AsyncActionFeedback.V3RoomUpdate.newBuilder()
                    .addAllUpdatedRooms(this.updatedRooms.stream().map(Util::ToPbUuid).collect(Collectors.toList()))
                    .addAllRemovedRooms(this.removedRooms.stream().map(Util::ToPbUuid).collect(Collectors.toList()))
                    .addAllUnChangedRooms(this.unChangedRooms.stream().map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    public static class V3RoomTriggerUpdate extends ActionFeedBack<LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate> {

        public static class Feedback{
            public UUID getTriggerUuid() {
                return triggerUuid;
            }

            public String getMsg() {
                return msg;
            }

            public Feedback(UUID triggerUuid, String msg) {
                this.triggerUuid = triggerUuid;
                this.msg = msg;
            }

            public Feedback(LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate.Feedback pb) {
                this.triggerUuid = Util.ToUuid(pb.getTriggerUuid());
                this.msg = pb.getMsg();
            }

            public LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate.Feedback getPb(){
                return LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate.Feedback.newBuilder()
                        .setTriggerUuid(Util.ToPbUuid(this.triggerUuid))
                        .setMsg(this.msg)
                        .build();
            }

            @Override
            public String toString() {
                return "Feedback{" +
                        "triggerUuid=" + triggerUuid +
                        ", msg='" + msg + '\'' +
                        '}';
            }

            UUID triggerUuid;
            String msg;
        }

        public V3RoomTriggerUpdate(List<Feedback> feedbacks) {
            this.feedbacks = feedbacks;
        }

        public List<Feedback> getFeedbacks() {
            return feedbacks;
        }


        @Override
        public String toString() {
            return "V3RoomTriggerUpdate{" +
                    "feedbacks=" + feedbacks +
                    '}';
        }

        final List<Feedback> feedbacks;


        public V3RoomTriggerUpdate(LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate pb) {
            this.feedbacks = pb.getFeedbackList().stream().map(Feedback::new).collect(Collectors.toList());
        }


        @Override
        LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate getPb() {
            return LcsProtos.AsyncActionFeedback.V3RoomTriggerUpdate.newBuilder()
                    .addAllFeedback(this.feedbacks.stream().map(Feedback::getPb).collect(Collectors.toList()))
                    .build();
        }
    }

    public static class FileUpload extends ActionFeedBack<LcsProtos.AsyncActionFeedback.FileUpload>{

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public FilePackModel getFileModel() {
            return fileModel;
        }

        final boolean success ;
        final String errorMsg;
        final FilePackModel fileModel;

        public FileUpload(boolean success,String errorMsg,FilePackModel filePackModel){
            this.success = success;
            this.errorMsg = errorMsg;
            this.fileModel = filePackModel;
        }

        public FileUpload(LcsProtos.AsyncActionFeedback.FileUpload pb){
            this.success = pb.getSuccess();
            this.errorMsg = pb.getErrMsg();
            this.fileModel = new FilePackModel(pb.getFile());
        }

        @Override
        LcsProtos.AsyncActionFeedback.FileUpload getPb() {
            return LcsProtos.AsyncActionFeedback.FileUpload.newBuilder()
                    .setSuccess(this.success)
                    .setErrMsg(this.errorMsg)
                    .setFile(this.fileModel.getPb())
                    .build();
        }
    }

}
