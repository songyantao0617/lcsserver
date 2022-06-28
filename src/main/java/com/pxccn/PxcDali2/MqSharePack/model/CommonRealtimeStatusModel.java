package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public abstract class CommonRealtimeStatusModel {
    public final UUID id;
    public final int terminalIndex;
    public final int shortAddress;
    public final long timestamp;
    public final String exceptionMessage;
    public CommonRealtimeStatusModel(UUID id, int terminalIndex, int shortAddress, long timestamp, String exceptionMessage) {
        this.id = id;
        this.terminalIndex = terminalIndex;
        this.shortAddress = shortAddress;
        this.timestamp = timestamp;
        this.exceptionMessage = exceptionMessage;
    }
    public CommonRealtimeStatusModel(LcsProtos.CommonRealtimeStatus pb) {
        this.id = Util.ToUuid(pb.getId());
        this.terminalIndex = pb.getTerminalIndex();
        this.shortAddress = pb.getShortAddress();
        this.timestamp = pb.getTimestamp();
        this.exceptionMessage = pb.getExceptionMessage();
    }

    protected LcsProtos.CommonRealtimeStatus getCommonPb() {
        return LcsProtos.CommonRealtimeStatus.newBuilder()
                .setId(Util.ToPbUuid(this.id))
                .setTerminalIndex(this.terminalIndex)
                .setShortAddress(this.shortAddress)
                .setTimestamp(this.timestamp)
                .setExceptionMessage(this.exceptionMessage)
                .build();
    }

}
