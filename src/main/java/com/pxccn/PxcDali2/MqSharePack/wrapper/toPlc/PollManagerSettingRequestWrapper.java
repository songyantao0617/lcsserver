package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PollManagerSettingRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.PollManagerSettingRequest> {
    public static final String TypeUrl = "type.googleapis.com/PollManagerSettingRequest";

    public static class PollManagerParam {
        @Override
        public String toString() {
            return "PollManagerParam{" +
                    "normalDelay=" + normalDelay +
                    ", highPriorityDelay=" + highPriorityDelay +
                    ", highPriorityQueueLength=" + highPriorityQueueLength +
                    ", highPriorityBundleSize=" + highPriorityBundleSize +
                    ", normalPriorityBundleSize=" + normalPriorityBundleSize +
                    ", notifyChangeOnly=" + notifyChangeOnly +
                    ", purgeCache=" + purgeCache +
                    '}';
        }

        public Integer normalDelay;
        public Integer highPriorityDelay;
        public Integer highPriorityQueueLength;
        public Integer highPriorityBundleSize;
        public Integer normalPriorityBundleSize;
        public Boolean notifyChangeOnly;
        public Boolean purgeCache;
    }


    public PollManagerParam getParam() {
        return param;
    }

    final PollManagerParam param;

    //反序列化使用
    public PollManagerSettingRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.PollManagerSettingRequest v = pb.getPayload().unpack(LcsProtos.PollManagerSettingRequest.class);
        PollManagerParam p = new PollManagerParam();
        if (v.getXnormalDelay())
            p.normalDelay = v.getNormalDelay();
        if (v.getXhighPriorityDelay())
            p.highPriorityDelay = v.getHighPriorityDelay();
        if (v.getXhighPriorityQueueLength())
            p.highPriorityQueueLength = v.getHighPriorityQueueLength();
        if (v.getXhighPriorityBundleSize())
            p.highPriorityBundleSize = v.getHighPriorityBundleSize();
        if (v.getXnormalPriorityBundleSize())
            p.normalPriorityBundleSize = v.getNormalPriorityBundleSize();
        if (v.getXnotifyChangeOnly())
            p.notifyChangeOnly = v.getNotifyChangeOnly();
        p.purgeCache = v.getPurgeCache();
        this.param = p;
    }

    public PollManagerSettingRequestWrapper(ProtoHeaders headers, PollManagerParam param) {
        super(headers);
        this.param = param;
    }

    @Override
    protected LcsProtos.PollManagerSettingRequest.Builder internal_get_payload() {
        LcsProtos.PollManagerSettingRequest.Builder builder = LcsProtos.PollManagerSettingRequest.newBuilder();
        Optional.ofNullable(this.param.normalDelay).ifPresent(p->{
            builder.setXnormalDelay(true);builder.setNormalDelay(p);
        });
        Optional.ofNullable(this.param.highPriorityDelay).ifPresent(p->{
            builder.setXhighPriorityDelay(true);builder.setHighPriorityDelay(p);
        });
        Optional.ofNullable(this.param.highPriorityQueueLength).ifPresent(p->{
            builder.setXhighPriorityQueueLength(true);builder.setHighPriorityQueueLength(p);
        });
        Optional.ofNullable(this.param.highPriorityBundleSize).ifPresent(p->{
            builder.setXhighPriorityBundleSize(true);builder.setHighPriorityBundleSize(p);
        });
        Optional.ofNullable(this.param.normalPriorityBundleSize).ifPresent(p->{
            builder.setXnormalPriorityBundleSize(true);builder.setNormalPriorityBundleSize(p);
        });
        Optional.ofNullable(this.param.notifyChangeOnly).ifPresent(p->{
            builder.setXnotifyChangeOnly(true);builder.setNotifyChangeOnly(p);
        });
        Optional.ofNullable(this.param.purgeCache).ifPresent(builder::setPurgeCache);
        return builder;
    }

}
