package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.RoomDetailModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoomsDetailUploadWrapper extends ProtoToServerQueueMsg<LcsProtos.RoomsDetailUpload> {
    public static final String TypeUrl = "type.googleapis.com/RoomsDetailUpload";
    private List<RoomDetailModel> roomDetailModels = Collections.EMPTY_LIST;

    public RoomsDetailUploadWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.RoomsDetailUpload v = pb.getPayload().unpack(LcsProtos.RoomsDetailUpload.class);
        if (v.getRoomDetailList().size() > 0) {
            this.roomDetailModels = v.getRoomDetailList().stream().map(RoomDetailModel::new).collect(Collectors.toList());
        }
    }

    public RoomsDetailUploadWrapper(ToServerMsgParam param, List<RoomDetailModel> roomDetailModels) {
        super(param);
        this.roomDetailModels = roomDetailModels;
    }

    public List<RoomDetailModel> getRoomDetailModels() {
        return roomDetailModels;
    }

    @Override
    protected LcsProtos.RoomsDetailUpload.Builder internal_get_payload() {
        return LcsProtos.RoomsDetailUpload.newBuilder()
                .addAllRoomDetail(this.roomDetailModels.stream().map(RoomDetailModel::getPb).collect(Collectors.toList()));
    }
}
