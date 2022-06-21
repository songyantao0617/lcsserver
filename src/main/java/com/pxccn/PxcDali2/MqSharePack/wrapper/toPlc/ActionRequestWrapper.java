package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class ActionRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.ActionRequest> {
    public static final String TypeUrl = "type.googleapis.com/ActionRequest";

    public enum Instruction {
        Unknown,
        Reboot,
//        SaveBog
    }

    public Instruction getInstruction() {
        return instruction;
    }

    private final Instruction instruction;

    //反向构造
    public ActionRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.ActionRequest v = pb.getPayload().unpack(LcsProtos.ActionRequest.class);
        Instruction instruction;
        try {
            instruction = Instruction.valueOf(v.getInstruction());
        } catch (IllegalArgumentException ignore) {
            instruction = Instruction.Unknown;
        }
        this.instruction = instruction;
    }

    //正向构造
    public ActionRequestWrapper(ProtoHeaders headers, Instruction instruction

    ) {
        super(headers);
        this.instruction = instruction;
    }

    @Override
    protected LcsProtos.ActionRequest.Builder internal_get_payload() {
        return LcsProtos.ActionRequest.newBuilder()
                .setInstruction(this.instruction.name())

                ;
    }

}
