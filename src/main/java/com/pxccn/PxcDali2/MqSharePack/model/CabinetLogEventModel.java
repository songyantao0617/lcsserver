package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;

public class CabinetLogEventModel implements IPbModel<LcsProtos.CabinetLogEvents.Event> {

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long ts) {
        this.timestamp = ts;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getClsName() {
        return clsName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public CabinetLogEventModel(long timestamp, String logLevel, String clsName, String methodName, String message, String error) {
        this.timestamp = timestamp;
        this.logLevel = logLevel;
        this.clsName = clsName;
        this.methodName = methodName;
        this.message = message;
        this.error = error;
    }

    @Override
    public String toString() {
        return "CabinetLogEventModel{" +
                "timestamp=" + timestamp +
                ", logLevel='" + logLevel + '\'' +
                ", clsName='" + clsName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    long timestamp;
    final String logLevel;
    final String clsName;
    final String methodName;
    final String message;
    final String error;

    public CabinetLogEventModel(LcsProtos.CabinetLogEvents.Event pb) {
        this.timestamp = pb.getTimestamp();
        this.logLevel = pb.getLogLevel();
        this.clsName = pb.getClsName();
        this.methodName = pb.getMethodName();
        this.message = pb.getMessage();
        this.error = pb.getError();
    }

    public LcsProtos.CabinetLogEvents.Event getPb() {
        return LcsProtos.CabinetLogEvents.Event.newBuilder()
                .setTimestamp(this.getTimestamp())
                .setLogLevel(this.getLogLevel())
                .setClsName(this.getClsName())
                .setMethodName(this.getMethodName())
                .setMessage(this.getMessage())
                .setError(this.getError())
                .build();
    }
}
