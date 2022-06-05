package com.pxccn.PxcDali2.server.opcua;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "prosys")
public class ProsysConfig {
    private String ApplicationName;
    private String ApplicationUri;
    private String ProductUri;
    private String ApplicationCertificateStore;
    private String ApplicationIssuerCertificateStore;
    private String UserCertificateStore;
    private String UserIssuerCertificateStore;
    private int Port;
    private String Version;
//    private String LcsNodeManagerNS;
}
