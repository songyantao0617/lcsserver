package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class Dali2LightCommandModel implements IPbModel<LcsProtos.Dali2LightCommand> {

    public enum Instructions {
        Unknown,
        DAPC_100,
        DAPC,
        OFF,
        UP,
        DOWN,
        STEP_UP,
        STEP_DOWN,
        MAX_LEVEL,
        MIN_LEVEL,
        STEP_DOWN_AND_OFF,
        ON_AND_STEP_UP,
        CONTINUOUS_UP,
        CONTINUOUS_DOWN,
        GOTO_SCENE
    }

    private final Instructions instruction;
    private final int parameter;


    public Dali2LightCommandModel(
            Instructions instruction,
            int parameter
    ) {
        this.instruction = instruction;
        this.parameter = parameter;
    }


    public LcsProtos.Dali2LightCommand getPb() {
        return LcsProtos.Dali2LightCommand.newBuilder()
                .setParameter(this.parameter)
                .setInstruction(this.instruction.name())
                .build();
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
}
