package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.UUID;

public class DoLightDetailModel extends LightDetailModelBase implements IPbModel<LcsProtos.LightsDetailUpload.DoLightDetail> {

    public DoLightDetailModel(
            UUID uuid,
            String lightName,
            String description,
            int axis_x,
            int axis_y,
            int axis_z,
            int shortAddress, int terminalIndex,boolean isBlinking
    ) {
        super(uuid, lightName, description, axis_x, axis_y, axis_z, shortAddress, terminalIndex,isBlinking);

    }


    public LcsProtos.LightsDetailUpload.DoLightDetail getPb() {
        return LcsProtos.LightsDetailUpload.DoLightDetail.newBuilder()
                .setCommon(super.getCommonPb())
                .build();
    }

    public DoLightDetailModel(LcsProtos.LightsDetailUpload.DoLightDetail pb) {
        super(pb.getCommon());
    }
}
