package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomLightInfoModel;
import com.pxccn.PxcDali2.MqSharePack.model.V3RoomTriggerInfoModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActionWithFeedbackRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.ActionWithFeedbackRequest> {
    public static final String TypeUrl = "type.googleapis.com/ActionWithFeedbackRequest";
    private final Action action;
    private final UUID requestId;


    //反序列化使用
    public ActionWithFeedbackRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.ActionWithFeedbackRequest v = pb.getPayload().unpack(LcsProtos.ActionWithFeedbackRequest.class);
        this.requestId = Util.ToUuid(v.getRequestId());
        switch (v.getActionCase().getNumber()) {
            case LcsProtos.ActionWithFeedbackRequest.BLINK_FIELD_NUMBER:
                this.action = new Blink(v.getBlink());
                break;
            case LcsProtos.ActionWithFeedbackRequest.SETSHORTADDRESS_FIELD_NUMBER:
                this.action = new SetShortAddress(v.getSetShortAddress());
                break;
            case LcsProtos.ActionWithFeedbackRequest.SENDLEVELINSTRUCTION_FIELD_NUMBER:
                this.action = new SendLevelInstruction(v.getSendLevelInstruction());
                break;
            case LcsProtos.ActionWithFeedbackRequest.SENDBROADCASTLEVELINSTRUCTION_FIELD_NUMBER:
                this.action = new SendBroadcastLevelInstruction(v.getSendBroadcastLevelInstruction());
                break;
            case LcsProtos.ActionWithFeedbackRequest.SAVESTATION_FIELD_NUMBER:
                this.action = new SaveStation(v.getSaveStation());
                break;
            case LcsProtos.ActionWithFeedbackRequest.SETSYSTIME_FIELD_NUMBER:
                this.action = new SetSysTime(v.getSetSysTime());
                break;
            case LcsProtos.ActionWithFeedbackRequest.DBSYNC_FIELD_NUMBER:
                this.action = new DbSync(v.getDbSync());
                break;
            case LcsProtos.ActionWithFeedbackRequest.V3ROOMUPDATE_FIELD_NUMBER:
                this.action = new V3RoomUpdate(v.getV3RoomUpdate());
                break;
            case LcsProtos.ActionWithFeedbackRequest.V3ROOMTRIGGERUPDATE_FIELD_NUMBER:
                this.action = new V3RoomTriggerUpdate(v.getV3RoomTriggerUpdate());
                break;
            case LcsProtos.ActionWithFeedbackRequest.FILEUPLOAD_FIELD_NUMBER:
                this.action = new FileUpload(v.getFileUpload());
                break;
            default:
                this.action = null;
                break;
        }
    }

    public ActionWithFeedbackRequestWrapper(ProtoHeaders headers, Action action) {
        super(headers);
        this.action = action;
        this.requestId = UUID.randomUUID();
    }

    /***************************************************************************/
    public static ActionWithFeedbackRequestWrapper Blink(ProtoHeaders headers, UUID[] uuid, boolean enable) {
        return new ActionWithFeedbackRequestWrapper(headers, new Blink(uuid, enable));
    }

    public static ActionWithFeedbackRequestWrapper SetShortAddress(ProtoHeaders headers, UUID[] uuid, int newShortAddress) {
        return new ActionWithFeedbackRequestWrapper(headers, new SetShortAddress(uuid, newShortAddress));
    }

    public static ActionWithFeedbackRequestWrapper SendLevelInstruction(ProtoHeaders headers, UUID[] uuid, Dali2LightCommandModel command, Dt8CommandModel dt8Command) {
        return new ActionWithFeedbackRequestWrapper(headers, new SendLevelInstruction(uuid, command, dt8Command));
    }

    public static ActionWithFeedbackRequestWrapper SendBroadcastLevelInstruction(ProtoHeaders headers, int terminalIndex, Dali2LightCommandModel command, Dt8CommandModel dt8Command) {
        return new ActionWithFeedbackRequestWrapper(headers, new SendBroadcastLevelInstruction(terminalIndex, command, dt8Command));
    }

    public static ActionWithFeedbackRequestWrapper FileUpload(ProtoHeaders headers,String filePath){
        return new ActionWithFeedbackRequestWrapper(headers,new FileUpload(filePath));
    }
    public Action getAction() {
        return this.action;
    }

    public UUID getRequestId() {
        return requestId;
    }

    @Override
    protected LcsProtos.ActionWithFeedbackRequest.Builder internal_get_payload() {
        LcsProtos.ActionWithFeedbackRequest.Builder builder = LcsProtos.ActionWithFeedbackRequest.newBuilder();
        builder.setRequestId(Util.ToPbUuid(this.requestId));
        if (this.action instanceof Blink) {
            builder.setBlink(((Blink) this.action).getPb());
        } else if (this.action instanceof SetShortAddress) {
            builder.setSetShortAddress(((SetShortAddress) this.action).getPb());
        } else if (this.action instanceof SendLevelInstruction) {
            builder.setSendLevelInstruction(((SendLevelInstruction) this.action).getPb());
        } else if (this.action instanceof SendBroadcastLevelInstruction) {
            builder.setSendBroadcastLevelInstruction(((SendBroadcastLevelInstruction) this.action).getPb());
        } else if (this.action instanceof SaveStation) {
            builder.setSaveStation(((SaveStation) this.action).getPb());
        } else if (this.action instanceof SetSysTime) {
            builder.setSetSysTime(((SetSysTime) this.action).getPb());
        } else if (this.action instanceof DbSync) {
            builder.setDbSync(((DbSync) this.action).getPb());
        }else if(this.action instanceof V3RoomUpdate){
            builder.setV3RoomUpdate(((V3RoomUpdate)this.action).getPb());
        }else if(this.action instanceof V3RoomTriggerUpdate){
            builder.setV3RoomTriggerUpdate(((V3RoomTriggerUpdate)action).getPb());
        }else if(this.action instanceof FileUpload){
            builder.setFileUpload(((FileUpload)action).getPb());
        }
        return builder;
    }

    /***************************************************************************/


    public static abstract class Action<T extends com.google.protobuf.GeneratedMessageV3> {
        abstract T getPb();
    }

    //闪烁命令
    public static class Blink extends Action<LcsProtos.ActionWithFeedbackRequest.Blink> {
        final UUID[] targetUuids;
        final boolean enable;

        Blink(LcsProtos.ActionWithFeedbackRequest.Blink pb) {
            this.enable = pb.getEnable();
            this.targetUuids = pb.getTargetUuidsList().stream().map(Util::ToUuid).toArray(UUID[]::new);
        }

        public Blink(UUID[] targets, boolean enable) {
            this.enable = enable;
            this.targetUuids = targets;
        }

        public boolean isEnable() {
            return enable;
        }

        public UUID[] getTargetUuids() {
            return targetUuids;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.Blink getPb() {
            return LcsProtos.ActionWithFeedbackRequest.Blink.newBuilder()
                    .setEnable(this.enable)
                    .addAllTargetUuids(Arrays.stream(this.targetUuids).map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    //设置新短地址
    public static class SetShortAddress extends Action<LcsProtos.ActionWithFeedbackRequest.SetShortAddress> {
        final UUID[] targetUuids;
        final int newShortAddress;

        SetShortAddress(LcsProtos.ActionWithFeedbackRequest.SetShortAddress pb) {
            this.newShortAddress = pb.getNewShortAddress();
            this.targetUuids = pb.getTargetUuidsList().stream().map(Util::ToUuid).toArray(UUID[]::new);
        }

        public SetShortAddress(UUID[] targetUuids, int newShortAddress) {
            this.newShortAddress = newShortAddress;
            this.targetUuids = targetUuids;
        }

        public int getNewShortAddress() {
            return newShortAddress;
        }

        public UUID[] getTargetUuids() {
            return targetUuids;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.SetShortAddress getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SetShortAddress.newBuilder()
                    .setNewShortAddress(this.newShortAddress)
                    .addAllTargetUuids(Arrays.stream(this.targetUuids).map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    //发送灯具控制命令
    public static class SendLevelInstruction extends Action<LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction> {
        final UUID[] targetUuids;
        final Dali2LightCommandModel command;
        final Dt8CommandModel dt8Command;

        public SendLevelInstruction(UUID[] targetUuids, Dali2LightCommandModel command, Dt8CommandModel dt8Command) {
            this.command = command;
            this.targetUuids = targetUuids;
            this.dt8Command = dt8Command;
        }

        public SendLevelInstruction(LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction pb) {
            this.command = new Dali2LightCommandModel(pb.getCommand());
            this.targetUuids = pb.getTargetUuidsList().stream().map(Util::ToUuid).toArray(UUID[]::new);
            this.dt8Command = new Dt8CommandModel(pb.getDt8Command());
        }

        public Dali2LightCommandModel getCommand() {
            return command;
        }

        public UUID[] getTargetUuids() {
            return targetUuids;
        }

        public Dt8CommandModel getDt8Command() {
            return dt8Command;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction.newBuilder()
                    .setCommand(command.getPb())
                    .setDt8Command(dt8Command.getPb())
                    .addAllTargetUuids(Arrays.stream(this.targetUuids).map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    public static class SendBroadcastLevelInstruction extends Action<LcsProtos.ActionWithFeedbackRequest.SendBroadcastLevelInstruction> {
        final Dali2LightCommandModel command;
        final Dt8CommandModel dt8Command;
        final int terminalIndex;


        public SendBroadcastLevelInstruction(int terminalIndex, Dali2LightCommandModel command, Dt8CommandModel dt8Command) {
            this.command = command;
            this.terminalIndex = terminalIndex;
            this.dt8Command = dt8Command;
        }


        public SendBroadcastLevelInstruction(LcsProtos.ActionWithFeedbackRequest.SendBroadcastLevelInstruction pb) {
            this.command = new Dali2LightCommandModel(pb.getCommand());
            this.dt8Command = new Dt8CommandModel(pb.getDt8Command());
            this.terminalIndex = pb.getTerminalIndex();
        }

        public Dali2LightCommandModel getCommand() {
            return command;
        }

        public Dt8CommandModel getDt8Command() {
            return dt8Command;
        }

        public int getTerminalIndex() {
            return terminalIndex;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.SendBroadcastLevelInstruction getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SendBroadcastLevelInstruction.newBuilder()
                    .setCommand(command.getPb())
                    .setDt8Command(dt8Command.getPb())
                    .setTerminalIndex(this.terminalIndex)
                    .build();
        }
    }

    public static class SaveStation extends Action<LcsProtos.ActionWithFeedbackRequest.SaveStation> {
        public SaveStation() {

        }

        public SaveStation(LcsProtos.ActionWithFeedbackRequest.SaveStation pb) {

        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.SaveStation getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SaveStation.newBuilder()
                    .build();
        }
    }

    public static class SetSysTime extends Action<LcsProtos.ActionWithFeedbackRequest.SetSysTime> {
        final long timestamp;
        final boolean forceNtp;
        final String newNtpAddress;
        final boolean setEnableNtp;
        final boolean enableNtp;

        public SetSysTime(long timestamp) {
            this.timestamp = timestamp;
            this.forceNtp = false;
            this.newNtpAddress = "";
            this.setEnableNtp = false;
            this.enableNtp = false;
        }

        //强制NTP刷新
        public SetSysTime() {
            this.timestamp = 0;
            this.forceNtp = true;
            this.newNtpAddress = "";
            this.setEnableNtp = false;
            this.enableNtp = false;
        }

        //设置NTP地址并刷新
        public SetSysTime(String newNtpAddress, boolean forceSync, boolean enable) {
            this.timestamp = 0;
            this.forceNtp = forceSync;
            this.newNtpAddress = newNtpAddress;
            this.setEnableNtp = true;
            this.enableNtp = enable;
        }

        public SetSysTime(LcsProtos.ActionWithFeedbackRequest.SetSysTime pb) {
            this.timestamp = pb.getTimestamp();
            this.forceNtp = pb.getForceNtp();
            this.newNtpAddress = pb.getNewNtpAddress();
            this.setEnableNtp = pb.getSetEnableNtp();
            this.enableNtp = pb.getEnableNtp();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isForceNtp() {
            return forceNtp;
        }

        public String getNewNtpAddress() {
            return newNtpAddress;
        }

        public boolean isSetEnableNtp() {
            return setEnableNtp;
        }

        public boolean isEnableNtp() {
            return enableNtp;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.SetSysTime getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SetSysTime.newBuilder()
                    .setTimestamp(timestamp)
                    .setForceNtp(forceNtp)
                    .setNewNtpAddress(newNtpAddress)
                    .setSetEnableNtp(setEnableNtp)
                    .setEnableNtp(enableNtp)
                    .build();
        }
    }

    public static class DbSync extends Action<LcsProtos.ActionWithFeedbackRequest.DbSync> {
        final int flag;

        public DbSync(int flag) {
            this.flag = flag;
        }

        public DbSync(LcsProtos.ActionWithFeedbackRequest.DbSync pb) {
            this.flag = pb.getFlag();
        }

        public int getFlag() {
            return flag;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.DbSync getPb() {
            return LcsProtos.ActionWithFeedbackRequest.DbSync.newBuilder()
                    .setFlag(this.flag)
                    .build();
        }
    }

    public static class V3RoomUpdate extends Action<LcsProtos.ActionWithFeedbackRequest.V3RoomUpdate> {
        final V3RoomLightInfoModel v3RoomLightInfoModel;

        public V3RoomUpdate(V3RoomLightInfoModel v3RoomLightInfoModel) {
            this.v3RoomLightInfoModel = v3RoomLightInfoModel;
        }

        public V3RoomUpdate(LcsProtos.ActionWithFeedbackRequest.V3RoomUpdate pb) {
            this.v3RoomLightInfoModel = new V3RoomLightInfoModel(pb.getV3RoomLightInfo());
        }

        public V3RoomLightInfoModel getModel() {
            return this.v3RoomLightInfoModel;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.V3RoomUpdate getPb() {
            return LcsProtos.ActionWithFeedbackRequest.V3RoomUpdate.newBuilder()
                    .setV3RoomLightInfo(this.v3RoomLightInfoModel.getPb())
                    .build();
        }
    }

    public static class V3RoomTriggerUpdate extends Action<LcsProtos.ActionWithFeedbackRequest.V3RoomTriggerUpdate> {
        final V3RoomTriggerInfoModel v3RoomTriggerInfoModel;

        public V3RoomTriggerUpdate(V3RoomTriggerInfoModel v3RoomLightInfoModel) {
            this.v3RoomTriggerInfoModel = v3RoomLightInfoModel;
        }

        public V3RoomTriggerUpdate(LcsProtos.ActionWithFeedbackRequest.V3RoomTriggerUpdate pb) {
            this.v3RoomTriggerInfoModel = new V3RoomTriggerInfoModel(pb.getV3RoomTriggerInfo());
        }

        public V3RoomTriggerInfoModel getModel() {
            return this.v3RoomTriggerInfoModel;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.V3RoomTriggerUpdate getPb() {
            return LcsProtos.ActionWithFeedbackRequest.V3RoomTriggerUpdate.newBuilder()
                    .setV3RoomTriggerInfo(this.v3RoomTriggerInfoModel.getPb())
                    .build();
        }
    }

    public static class FileUpload extends Action<LcsProtos.ActionWithFeedbackRequest.FileUpload>{
        final String filePath;
        public FileUpload(String filePath){
            this.filePath = filePath;
        }
        public FileUpload(LcsProtos.ActionWithFeedbackRequest.FileUpload pb){
            this.filePath = pb.getFilePath();
        }

        public String getFilePath(){
            return this.filePath;
        }

        @Override
        LcsProtos.ActionWithFeedbackRequest.FileUpload getPb() {
            return LcsProtos.ActionWithFeedbackRequest.FileUpload.newBuilder().setFilePath(this.filePath).build();
        }
    }



}
