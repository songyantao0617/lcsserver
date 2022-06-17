package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class RoomDetailModel implements IPbModel<LcsProtos.RoomsDetailUpload.RoomDetail> {
    @Override
    public String toString() {
        return "RoomDetailModel{" +
                "uuid=" + uuid +
                ", lightName='" + roomName + '\'' +
                ", description='" + description + '\'' +
                ", axis_x=" + axis_x +
                ", axis_y=" + axis_y +
                ", axis_z=" + axis_z +
                '}';
    }

    public final UUID uuid;
    public final String roomName;
    public final String description;
    public final int axis_x;
    public final int axis_y;
    public final int axis_z;

    public RoomDetailModel(
            UUID uuid,
            String roomName,
            String description,
            int axis_x,
            int axis_y,
            int axis_z
    ) {
        this.uuid = uuid;
        this.roomName = roomName;
        this.description = description;
        this.axis_x = axis_x;
        this.axis_y = axis_y;
        this.axis_z = axis_z;
    }


    public LcsProtos.RoomsDetailUpload.RoomDetail getPb() {
        return LcsProtos.RoomsDetailUpload.RoomDetail.newBuilder()
                .setUuid(Util.ToPbUuid(this.uuid))
                .setRoomName(this.roomName)
                .setDescription(this.description)
                .setAxisX(this.axis_x)
                .setAxisY(this.axis_y)
                .setAxisZ(this.axis_z)
                .build();
    }

    public RoomDetailModel(LcsProtos.RoomsDetailUpload.RoomDetail pb) {
        this.uuid = Util.ToUuid(pb.getUuid());
        this.roomName = pb.getRoomName();
        this.description = pb.getDescription();
        this.axis_x = pb.getAxisX();
        this.axis_y = pb.getAxisY();
        this.axis_z = pb.getAxisZ();
    }
}
