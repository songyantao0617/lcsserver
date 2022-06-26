package com.pxccn.PxcDali2.server.modelManual;


import java.io.Serializable;
import java.util.UUID;

public class FUNC_getV3RoomListFromCabinetID implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getRoomUUID() {
        return roomUUID;
    }

    public void setRoomUUID(String roomUUID) {
        this.roomUUID = roomUUID;
    }

    @Override
    public String toString() {
        return "FUNC_getV3RoomListFromCabinetID{" +
                "roomUUID=" + roomUUID +
                '}';
    }

    String roomUUID;
}
