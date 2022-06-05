package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;
import org.slf4j.helpers.MessageFormatter;

import java.util.UUID;

public class LightRealtimeStatusModel implements IPbModel<LcsProtos.LightRealtimeStatus> {
    public final UUID lightId;
    public final int terminalIndex;
    public final int shortAddress;
    public final long timestamp;

    String exceptionMessage;

    public final int rawStatusResponse;
    public final int rawActualLevelResponse;
    public final boolean frameError;

    public LightRealtimeStatusModel(
            UUID lightId,
            int terminalIndex,
            int shortAddress,
            long timestamp,
            int rawStatusResponse,
            int rawActualLevelResponse,
            boolean frameError,
            String exceptionMessage){
        this.lightId = lightId;
        this.terminalIndex = terminalIndex;
        this.shortAddress = shortAddress;
        this.timestamp = timestamp;
        this.rawStatusResponse = rawStatusResponse;
        this.rawActualLevelResponse= rawActualLevelResponse;
        this.frameError = frameError;
        this.exceptionMessage = exceptionMessage;
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
        return exceptionMessage != null || controlGearError || lampError;
    }

    public String getErrorMessage() {
        if (exceptionMessage != null) {
            return exceptionMessage;
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
            return MessageFormatter.arrayFormat("Light<{}>-Error:{}", new Object[]{this.lightId, errMsg}).getMessage();
        } else {
            return MessageFormatter.arrayFormat("Light<{}>-Lux:{}", new Object[]{this.lightId, this.brightness}).getMessage();

        }

    }

    public LcsProtos.LightRealtimeStatus getPb() {
        return LcsProtos.LightRealtimeStatus.newBuilder()
                .setLightId(Util.ToPbUuid(this.lightId))
                .setTerminalIndex(this.terminalIndex)
                .setShortAddress(this.shortAddress)
                .setTimestamp(this.timestamp)
                .setRawStatusResponse(this.rawStatusResponse)
                .setRawActualLevelResponse(this.rawActualLevelResponse)
                .setFrameError(this.frameError)
                .setExceptionMessage(this.exceptionMessage)
                .build();

    }

    public LightRealtimeStatusModel(LcsProtos.LightRealtimeStatus pb) {
        this.lightId = Util.ToUuid(pb.getLightId());
        this.terminalIndex = pb.getTerminalIndex();
        this.shortAddress = pb.getShortAddress();
        this.timestamp = pb.getTimestamp();
        this.rawStatusResponse = pb.getRawStatusResponse();
        this.rawActualLevelResponse = pb.getRawActualLevelResponse();
        this.frameError = pb.getFrameError();


        if (!pb.getExceptionMessage().isEmpty()) {
            exceptionMessage = pb.getExceptionMessage();
        } else if (this.rawStatusResponse == -1 || this.rawActualLevelResponse == -1) {
            exceptionMessage = "lost collection";
        } else if (pb.getFrameError()) {
            exceptionMessage = "frame error";
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
