package com.pxccn.PxcDali2.MqSharePack.model;

import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class NiagaraOperateRequestModel implements IPbModel<LcsProtos.perOperateRequest> {

    public LcsProtos.perOperateRequest.Operate getOperateCode() {
        return operateCode;
    }

    public Locate getLocate() {
        return locate;
    }

    public String getTargetValue() {
        return targetValue;
    }

    LcsProtos.perOperateRequest.Operate operateCode;
    Locate locate;
    String targetValue;
    MethodParameter methodParameter;

    public MethodParameter getMethodParameter() {
        return methodParameter;
    }

    public NiagaraOperateRequestModel(LcsProtos.perOperateRequest pb) {
        this.operateCode = pb.getOperate();
        this.locate = new Locate(pb);
        this.targetValue = pb.hasNewValueToSet() ? pb.getNewValueToSet() : pb.getMethodName();
        if (this.operateCode == LcsProtos.perOperateRequest.Operate.INVOKE_METHOD && !pb.hasMethodName()) {
            throw new IllegalStateException("参数不合理");
        }
        if (this.operateCode == LcsProtos.perOperateRequest.Operate.INVOKE_METHOD) {
            this.methodParameter = new MethodParameter(pb);
        }
    }

    private NiagaraOperateRequestModel() {
    }

    public static NiagaraOperateRequestModel READ_PROPERTY(String propertyOrd) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.READ_PROPERTY;
        model.locate = new Locate(propertyOrd);
        model.targetValue = "";
        return model;
    }

    public static NiagaraOperateRequestModel READ_PROPERTY(UUID lightOrRoomUuid, String slotPath) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.READ_PROPERTY;
        model.locate = new Locate(lightOrRoomUuid, slotPath);
        model.targetValue = "";
        return model;
    }

    public static NiagaraOperateRequestModel WRITE_PROPERTY(String propertyOrd, String newValue) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.WRITE_PROPERTY;
        model.locate = new Locate(propertyOrd);
        model.targetValue = newValue;
        return model;
    }

    public static NiagaraOperateRequestModel WRITE_PROPERTY(UUID lightOrRoomUuid, String slotPath, String newValue) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.WRITE_PROPERTY;
        model.locate = new Locate(lightOrRoomUuid, slotPath);
        model.targetValue = newValue;
        return model;
    }

    public static NiagaraOperateRequestModel INVOKE_METHOD(String componentOrd, String methodName) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.INVOKE_METHOD;
        model.locate = new Locate(componentOrd);
        model.targetValue = methodName;
        model.methodParameter = new MethodParameter();
        return model;
    }

    public static NiagaraOperateRequestModel INVOKE_METHOD(UUID lightOrRoomUuid, String slotPath, String methodName) {
        NiagaraOperateRequestModel model = new NiagaraOperateRequestModel();
        model.operateCode = LcsProtos.perOperateRequest.Operate.INVOKE_METHOD;
        model.locate = new Locate(lightOrRoomUuid, slotPath);
        model.targetValue = methodName;
        model.methodParameter = new MethodParameter();
        return model;
    }


    public LcsProtos.perOperateRequest getPb() {
        LcsProtos.perOperateRequest.Builder builder = LcsProtos.perOperateRequest.newBuilder();
        builder.setOperate(this.operateCode);
        this.locate.fillPb(builder);
        if (this.operateCode == LcsProtos.perOperateRequest.Operate.INVOKE_METHOD) {
            builder.setMethodName(this.targetValue);
            this.methodParameter.fillPb(builder);
        } else {
            builder.setNewValueToSet(this.targetValue);
        }
        return builder.build();
    }

    public static class Locate {
        String content;

        int strategy = 0; //0: 通过BORD，1：通过 UUID ### SlotOrd

        public Locate(String bord) {
            this.content = bord;
            this.strategy = 0;
        }

        public Locate(UUID lightOrRoomUuid, String slotPath) {
            this.content = lightOrRoomUuid.toString() + "###" + slotPath;
            this.strategy = 1;
        }

        public Locate(LcsProtos.perOperateRequest pb) {
            switch (pb.getHowToLocateCase().getNumber()) {
                case LcsProtos.perOperateRequest.TARGETSLOTORD_FIELD_NUMBER:
                    this.strategy = 0;
                    this.content = pb.getTargetSlotOrd();
                    break;
                case LcsProtos.perOperateRequest.LIGHTORROOMUUID_FIELD_NUMBER:
                    this.strategy = 1;
                    this.content = pb.getLightOrRoomUuid();
                    break;
            }

        }

        public void fillPb(LcsProtos.perOperateRequest.Builder pb) {
            if (this.strategy == 0) {
                pb.setTargetSlotOrd(this.content);
            } else if (this.strategy == 1) {
                pb.setLightOrRoomUuid(this.content);
            } else {
                throw new IllegalStateException("");
            }
        }

        public int getStrategy() {
            return this.strategy;
        }

        public String getBord() {
            if (this.strategy != 0) {
                throw new IllegalStateException("方式错误");
            }
            return this.content;
        }

        public String getUuidPart() {
            if (this.strategy != 1) {
                throw new IllegalStateException("方式错误");
            }
            return this.content.split("###")[0];
        }

        public String getSlotPathPart() {
            if (this.strategy != 1) {
                throw new IllegalStateException("方式错误");
            }
            return this.content.split("###")[1];
        }
    }

    private static class PV implements Serializable {
        private static final long serialVersionUID = -1l;
        Class<?> cls;
        Object val;
    }

    public static class MethodParameter {
        List<PV> paramList = new ArrayList<>();

        public MethodParameter(LcsProtos.perOperateRequest pb) {
            paramList = pb.getMethodParametersList().stream().map(i -> {
                try {
                    ObjectInputStream oits = new ObjectInputStream(new ByteArrayInputStream(i.toByteArray()));
                    return (PV) oits.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }

        public MethodParameter() {
        }

        public void fillPb(LcsProtos.perOperateRequest.Builder pb) {
            List<ByteString> bs = new ArrayList<>();
            this.paramList.forEach(i -> {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
                    new ObjectOutputStream(baos).writeObject(i);
                    bs.add(ByteString.copyFrom(baos.toByteArray()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pb.addAllMethodParameters(bs);
        }

        public <T> void add(Class<T> cls, T value) {
            PV pv = new PV();
            pv.cls = cls;
            pv.val = value;
            this.paramList.add(pv);
        }

        public Class<?>[] getTypeSig() {
            return this.paramList.stream().map(i -> i.cls).toArray(Class[]::new);
        }

        public Object[] getParams() {
            return this.paramList.stream().map((i -> i.val)).toArray();
        }

    }

}
