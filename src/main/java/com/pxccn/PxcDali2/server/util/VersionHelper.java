package com.pxccn.PxcDali2.server.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class VersionHelper {
    public static CabinetVersion GetCabinetVersionFromId(String id) {

        if (id == null){
            return CabinetVersion.NONE;
        }

        var a = Arrays.stream(CabinetVersion.values()).filter(p -> p.identifier.equals(id)).collect(Collectors.toList());
        if (a.size() == 0) {
            return CabinetVersion.NONE;
        } else {
            return a.get(0);
        }
    }
}
