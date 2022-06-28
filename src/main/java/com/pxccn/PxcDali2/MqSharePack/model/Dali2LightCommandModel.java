package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class Dali2LightCommandModel implements IPbModel<LcsProtos.Dali2LightCommand> {

    private final Instructions instruction;
    private final int parameter;

    public Dali2LightCommandModel(
            Instructions instruction,
            int parameter
    ) {
        this.instruction = instruction;
        this.parameter = parameter;
    }

    public Dali2LightCommandModel(LcsProtos.Dali2LightCommand pb) {
        this.parameter = pb.getParameter();
        Instructions instructions;
        try {
            instructions = Instructions.valueOf(pb.getInstruction());
        } catch (IllegalArgumentException ignore) {
            instructions = Instructions.Unknown;
        }
        this.instruction = instructions;
    }

    public Instructions getInstruction() {
        return instruction;
    }

    public int getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return "Dali2LightCommandModel{" +
                "instruction=" + instruction +
                ", parameter=" + parameter +
                '}';
    }

    public LcsProtos.Dali2LightCommand getPb() {
        return LcsProtos.Dali2LightCommand.newBuilder()
                .setParameter(this.parameter)
                .setInstruction(this.instruction.name())
                .build();
    }


    public enum Instructions {
        Unknown,
        None,
        DirectPwr_percent,
        DirectPwr,
        Off,
        Up,
        Down,
        StepUp,
        StepDown,
        MaxLevel,
        MinLevel,
        StepDownAndOff,
        OnAndStepUp,
        GotoScene,
        ContinuousUp_V2Only,
        ContinuousDown_V2Only
    }
}
