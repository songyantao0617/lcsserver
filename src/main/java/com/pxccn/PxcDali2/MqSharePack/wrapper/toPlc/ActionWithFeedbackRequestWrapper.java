package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActionWithFeedbackRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.ActionWithFeedbackRequest> {
    public static final String TypeUrl = "type.googleapis.com/ActionWithFeedbackRequest";

    /***************************************************************************/
    public static ActionWithFeedbackRequestWrapper Blink(ProtoHeaders headers, UUID[] uuid, boolean enable) {
        return new ActionWithFeedbackRequestWrapper(headers, new Blink(uuid, enable));
    }

    public static ActionWithFeedbackRequestWrapper SetShortAddress(ProtoHeaders headers, UUID[] uuid, int newShortAddress) {
        return new ActionWithFeedbackRequestWrapper(headers, new SetShortAddress(uuid, newShortAddress));
    }


    public static ActionWithFeedbackRequestWrapper SendLevelInstruction(ProtoHeaders headers, UUID[] uuid, Dali2LightCommandModel command) {
        return new ActionWithFeedbackRequestWrapper(headers, new SendLevelInstruction(uuid, command));
    }

    /***************************************************************************/


    public static abstract class Action<T extends com.google.protobuf.GeneratedMessageV3> {
        abstract T getPb();
    }

    //闪烁命令
    public static class Blink extends Action<LcsProtos.ActionWithFeedbackRequest.Blink> {
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

        final UUID[] targetUuids;
        final boolean enable;

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

        final UUID[] targetUuids;

        final int newShortAddress;

        @Override
        LcsProtos.ActionWithFeedbackRequest.SetShortAddress getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SetShortAddress.newBuilder()
                    .setNewShortAddress(this.newShortAddress)
                    .addAllTargetUuids(Arrays.stream(this.targetUuids).map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    public static class SendLevelInstruction extends Action<LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction> {
        public SendLevelInstruction(UUID[] targetUuids, Dali2LightCommandModel command) {
            this.command = command;
            this.targetUuids = targetUuids;
        }

        public SendLevelInstruction(LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction pb) {
            this.command = new Dali2LightCommandModel(pb.getCommand());
            this.targetUuids = pb.getTargetUuidsList().stream().map(Util::ToUuid).toArray(UUID[]::new);

        }

        public Dali2LightCommandModel getCommand() {
            return command;
        }

        public UUID[] getTargetUuids() {
            return targetUuids;
        }

        final UUID[] targetUuids;
        final Dali2LightCommandModel command;

        @Override
        LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction getPb() {
            return LcsProtos.ActionWithFeedbackRequest.SendLevelInstruction.newBuilder()
                    .setCommand(command.getPb())
                    .addAllTargetUuids(Arrays.stream(this.targetUuids).map(Util::ToPbUuid).collect(Collectors.toList()))
                    .build();
        }
    }

    public Action getAction() {
        return this.action;
    }

    private Action action;


    public UUID getRequestId() {
        return requestId;
    }

    private UUID requestId;

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
        }

        return builder;
    }

}
