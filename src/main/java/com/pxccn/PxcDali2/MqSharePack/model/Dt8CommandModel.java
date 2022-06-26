package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class Dt8CommandModel implements IPbModel<LcsProtos.Dt8Command> {

    public static Dt8CommandModel NONE = new Dt8CommandModel(Instructions.None,0,0);

    public enum Instructions {
        Unknown,
        None,
        Tc_ByValue,
        Tc_StepWarmer,
        Tc_StepCooler
    }

    public Instructions getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "Dt8CommandModel{" +
                "instruction=" + instruction +
                ", dt8ActionParam=" + dt8ActionParam +
                ", dt8ActionParam2=" + dt8ActionParam2 +
                '}';
    }

    private final Instructions instruction;

    public int getDt8ActionParam() {
        return dt8ActionParam;
    }

    public int getDt8ActionParam2() {
        return dt8ActionParam2;
    }

    private final int dt8ActionParam;
    private final int dt8ActionParam2;


    public Dt8CommandModel(
            Instructions instruction,
            int dt8ActionParam,int dt8ActionParam2
    ) {
        this.instruction = instruction;
        this.dt8ActionParam = dt8ActionParam;
        this.dt8ActionParam2 = dt8ActionParam2;
    }


    public LcsProtos.Dt8Command getPb() {
        return LcsProtos.Dt8Command.newBuilder()
                .setDt8ActionParam(this.dt8ActionParam)
                .setDt8ActionParam2(this.dt8ActionParam2)
                .setInstruction(this.instruction.name())
                .build();
    }


    public Dt8CommandModel(LcsProtos.Dt8Command pb) {
        this.dt8ActionParam = pb.getDt8ActionParam();
        this.dt8ActionParam2 = pb.getDt8ActionParam2();
        Instructions instructions;
        try {
            instructions = Instructions.valueOf(pb.getInstruction());
        } catch (IllegalArgumentException ignore) {
            instructions = Instructions.Unknown;
        }
        this.instruction = instructions;
    }
}
