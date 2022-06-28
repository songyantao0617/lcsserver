package com.pxccn.PxcDali2.MqSharePack.model;

import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.io.*;

public class NiagaraOperateRespModel implements IPbModel<LcsProtos.perOperateResp> {

    boolean success;
    String exceptionReason;
    String targetValue;
    RV returnValue;
    public NiagaraOperateRespModel(LcsProtos.perOperateResp pb) {
        this.success = pb.getSuccess();
        this.exceptionReason = pb.getExceptionReason();
        this.targetValue = pb.getTargetValue();
        if (pb.hasField(LcsProtos.perOperateResp.getDescriptor().findFieldByNumber(LcsProtos.perOperateResp.METHODRETURN_FIELD_NUMBER))) {
            try {
                ObjectInputStream oits = new ObjectInputStream(new ByteArrayInputStream(pb.getMethodReturn().toByteArray()));
                this.returnValue = (RV) oits.readObject();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    public NiagaraOperateRespModel(boolean success, String exceptionReason, String targetValue) {
        this.success = success;
        this.exceptionReason = exceptionReason;
        this.targetValue = targetValue;
    }
    public NiagaraOperateRespModel(boolean success, String exceptionReason, Object returnValue) {
        this.success = success;
        this.exceptionReason = exceptionReason;
        this.targetValue = "";
        this.returnValue = new RV();
        this.returnValue.v = returnValue;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public Object getReturnValue() {
        if (this.returnValue == null) {
            return null;
        }
        return this.returnValue.v;
    }

    public LcsProtos.perOperateResp getPb() {
        LcsProtos.perOperateResp.Builder builder = LcsProtos.perOperateResp.newBuilder();
        builder.setSuccess(this.success)
                .setExceptionReason(this.exceptionReason)
                .setTargetValue(this.targetValue);
        if (this.returnValue != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
                new ObjectOutputStream(baos).writeObject(this.returnValue);
                builder.setMethodReturn(ByteString.copyFrom(baos.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }

    private static class RV implements Serializable {
        private static final long serialVersionUID = -1l;
        Object v;
    }


}
