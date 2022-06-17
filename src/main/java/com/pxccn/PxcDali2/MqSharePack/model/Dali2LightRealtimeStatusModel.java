package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;
import org.slf4j.helpers.MessageFormatter;

import java.util.UUID;

public class Dali2LightRealtimeStatusModel extends CommonRealtimeStatusModel implements IPbModel<LcsProtos.Dali2LightRealtimeStatus> {
    public final int rawStatusResponse;
    public final int rawActualLevelResponse;
    public final boolean frameError;

    public Dali2LightRealtimeStatusModel(
            UUID lightId,
            int terminalIndex,
            int shortAddress,
            long timestamp,
            String exceptionMessage,
            int rawStatusResponse,
            int rawActualLevelResponse,
            boolean frameError
    ) {
        super(lightId, terminalIndex, shortAddress, timestamp, exceptionMessage);

        this.frameError = frameError;
        this.rawStatusResponse = rawStatusResponse;
        this.rawActualLevelResponse = rawActualLevelResponse;
    }

    public double brightness;
    public boolean controlGearError;
    public boolean lampError;
    public boolean lampOn;
    public boolean limitError;
    public boolean fadeRunning;
    public boolean resetState;
    public boolean powerCycleSeen;

    public boolean HasError() {
        return exec != null || controlGearError || lampError;
    }

    public String getErrorMessage() {
        if (exec != null) {
            return exec;
        }
        if (controlGearError) {
            return "controlGearError";
        }
        if (lampError) {
            return "lampError";
        }
        return null;
    }

    public String toString() {
        String errMsg = getErrorMessage();
        if (errMsg != null) {
            return MessageFormatter.arrayFormat("Light<{}>-Error:{}", new Object[]{this.id, errMsg}).getMessage();
        } else {
            return MessageFormatter.arrayFormat("Light<{}>-Lux:{}", new Object[]{this.id, this.brightness}).getMessage();

        }

    }

    public LcsProtos.Dali2LightRealtimeStatus getPb() {
        return LcsProtos.Dali2LightRealtimeStatus.newBuilder()
                .setCommon(this.getCommonPb())

                .setRawStatusResponse(this.rawStatusResponse)
                .setRawActualLevelResponse(this.rawActualLevelResponse)
                .setFrameError(this.frameError)
                .build();

    }
    private String exec = null;
    public Dali2LightRealtimeStatusModel(LcsProtos.Dali2LightRealtimeStatus pb) {
        super(pb.getCommon());
        this.rawStatusResponse = pb.getRawStatusResponse();
        this.rawActualLevelResponse = pb.getRawActualLevelResponse();
        this.frameError = pb.getFrameError();

        if (!this.exceptionMessage.isEmpty()) {
            //如果已经携带异常信息了，就用这个信息
            this.exec = exceptionMessage;
        } else if (this.rawStatusResponse == -1 || this.rawActualLevelResponse == -1) {
            this.exec = "lost collection";
        } else if (pb.getFrameError()) {
            this.exec = "frame error";
        } else {
            controlGearError = Util.CheckBit(this.rawStatusResponse, 0);
            lampError = Util.CheckBit(this.rawStatusResponse, 1);
            lampOn = Util.CheckBit(this.rawStatusResponse, 2);
            limitError = Util.CheckBit(this.rawStatusResponse, 3);
            fadeRunning = Util.CheckBit(this.rawStatusResponse, 4);
            resetState = Util.CheckBit(this.rawStatusResponse, 5);
            powerCycleSeen = Util.CheckBit(this.rawStatusResponse, 7);
            brightness = Double.parseDouble(String.format("%.2f", ((this.rawActualLevelResponse & 255) / 254.0D * 100.0D)));
        }
    }
}
