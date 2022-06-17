package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;

public class CabinetDetailUploadWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetDetailUpload> {
    public static final String TypeUrl = "type.googleapis.com/CabinetDetailUpload";

    public final String cabinetName;
    public final String description;
    public final int axis_x;
    public final int axis_y;
    public final int axis_z;
    public final boolean isMaintenance;

    public CabinetDetailUploadWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetDetailUpload v = pb.getPayload().unpack(LcsProtos.CabinetDetailUpload.class);
        this.cabinetName = v.getCabinetName();
        this.description = v.getDescription();
        this.axis_x = v.getAxisX();
        this.axis_y = v.getAxisY();
        this.axis_z = v.getAxisZ();
        this.isMaintenance = v.getIsMaintenance();
    }

    public CabinetDetailUploadWrapper(ToServerMsgParam param, String cabinetName, String description, int x, int y, int z,boolean isMaintenance) {
        super(param);
        this.cabinetName = cabinetName;
        this.description = description;
        this.axis_x = x;
        this.axis_y = y;
        this.axis_z = z;
        this.isMaintenance = isMaintenance;
    }

    @Override
    protected LcsProtos.CabinetDetailUpload.Builder internal_get_payload() {
        return LcsProtos.CabinetDetailUpload.newBuilder()
                .setCabinetName(this.cabinetName)
                .setDescription(this.description)
                .setAxisX(this.axis_x)
                .setAxisY(this.axis_y)
                .setAxisZ(this.axis_z)
                .setIsMaintenance(this.isMaintenance)
                ;

    }
}
