package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.UUID;

public class Dali2LightDetailModel extends LightDetailModelBase implements IPbModel<LcsProtos.LightsDetailUpload.Dali2LightDetail> {

    public Dali2LightDetailModel(
            UUID uuid,
            String lightName,
            String description,
            int axis_x,
            int axis_y,
            int axis_z,
            int shortAddress, int terminalIndex, boolean isBlinking
    ) {
        super(uuid, lightName, description, axis_x, axis_y, axis_z, shortAddress, terminalIndex, isBlinking);

    }


    public Dali2LightDetailModel(LcsProtos.LightsDetailUpload.Dali2LightDetail pb) {
        super(pb.getCommon());
    }

    public LcsProtos.LightsDetailUpload.Dali2LightDetail getPb() {
        return LcsProtos.LightsDetailUpload.Dali2LightDetail.newBuilder()
                .setCommon(super.getCommonPb())
                .build();
    }
}
