package com.pxccn.PxcDali2.server.service.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class WritePropertyParameter {
    UUID resourceUUID;
    String slot;
    String newValue;
}
