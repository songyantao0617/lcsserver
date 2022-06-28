package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public abstract class LightDetailModelBase {
    public final UUID uuid;
    public final String lightName;
    public final String description;
    public final int axis_x;
    public final int axis_y;
    public final int axis_z;
    public final int shortAddress;
    public final int terminalIndex;
    public final boolean isBlinking;

    public LightDetailModelBase(
            UUID uuid,
            String lightName,
            String description,
            int axis_x,
            int axis_y,
            int axis_z,
            int shortAddress,
            int terminalIndex,
            boolean isBlinking
    ) {
        this.uuid = uuid;
        this.lightName = lightName;
        this.description = description;
        this.axis_x = axis_x;
        this.axis_y = axis_y;
        this.axis_z = axis_z;
        this.shortAddress = shortAddress;
        this.terminalIndex = terminalIndex;
        this.isBlinking = isBlinking;
    }


    public LightDetailModelBase(LcsProtos.LightsDetailUpload.LightCommonDetail pb) {
        this.uuid = Util.ToUuid(pb.getUuid());
        this.lightName = pb.getLightName();
        this.description = pb.getDescription();
        this.axis_x = pb.getAxisX();
        this.axis_y = pb.getAxisY();
        this.axis_z = pb.getAxisZ();
        this.shortAddress = pb.getShortAddress();
        this.terminalIndex = pb.getTerminalIndex();
        this.isBlinking = pb.getIsBlinking();
    }

    public LcsProtos.LightsDetailUpload.LightCommonDetail getCommonPb() {
        return LcsProtos.LightsDetailUpload.LightCommonDetail.newBuilder()
                .setUuid(Util.ToPbUuid(this.uuid))
                .setLightName(this.lightName)
                .setDescription(this.description)
                .setAxisX(this.axis_x)
                .setAxisY(this.axis_y)
                .setAxisZ(this.axis_z)
                .setShortAddress(this.shortAddress)
                .setTerminalIndex(this.terminalIndex)
                .setIsBlinking(this.isBlinking)
                .build();
    }
}
