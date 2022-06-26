package com.pxccn.PxcDali2;

import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
public class Util {

    public static String Safe(String value) {
        return value == null ? "" : value;
    }

    public static int Safe(Integer value) {
        return value == null ? -1 : value;
    }

    public static boolean CheckBit(int value, int offset) {
        return (value & (1 << offset)) == (1 << offset);
    }

    public static UUID[] ResolveUUIDArrayFromString(String input) {
        return ResolveUUIDArrayFromString(input, ",");
    }

    public static UUID[] ResolveUUIDArrayFromString(String input, String split) {
        UUID[] targets;
        if (input.indexOf(',') != -1) {
            targets = Arrays.stream(input.split(split))
                    .map(String::strip)
                    .map(UUID::fromString)
                    .toArray(UUID[]::new);
        } else {
            targets = new UUID[1];
            targets[0] = UUID.fromString(input);
        }
        return targets;
    }


    public static LcsProtos.Uuid ToPbUuid(UUID uuid) {
        return LcsProtos
                .Uuid
                .newBuilder()
                .setMSB(uuid.getMostSignificantBits())
                .setLSB(uuid.getLeastSignificantBits())
                .build();
    }

    public static UUID ToUuid(LcsProtos.Uuid pbUuid) {
        return new UUID(pbUuid.getMSB(), pbUuid.getLSB());
    }

    public static String serverName;
    public static String serverVer;


    public Util(
            @Value("${LcsServer.name}") String name,
            @Value("${LcsServer.ver}") String ver
    ) {
        Util.serverName = name;
        Util.serverVer = ver;
    }

    public static ProtoHeaders NewCommonHeaderForClient() {
        ProtoHeaders headers = new ProtoHeaders();
        headers.put("serverName", serverName);
        headers.put("serverVer", serverVer);
        return headers;
    }
}
