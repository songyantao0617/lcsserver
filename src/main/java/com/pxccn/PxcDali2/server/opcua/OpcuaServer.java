package com.pxccn.PxcDali2.server.opcua;

import com.prosysopc.ua.*;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.server.UaServerException;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.cert.*;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.core.ApplicationType;
import com.prosysopc.ua.stack.core.MessageSecurityMode;
import com.prosysopc.ua.stack.transport.security.Cert;
import com.prosysopc.ua.stack.transport.security.SecurityMode;
import com.prosysopc.ua.stack.transport.security.SecurityPolicy;
import com.prosysopc.ua.stack.utils.CertificateUtils;
import com.prosysopc.ua.stack.utils.EndpointUtil;
import com.pxccn.PxcDali2.server.space.TopSpace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.CertificateParsingException;
import java.util.*;

@Service
@Slf4j
public class OpcuaServer {

    @Autowired
    TopSpace topSpace;

    @Autowired
    ProsysConfig prosysConfig;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @EventListener
    @Order(value = 0)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            this._server.start();
            this.topSpace.onServerReady(getLcsNodeManager());
        } catch (Exception e) {
            log.error("Can not start Opcua Server", e);
        }

    }

    private UaServer _server;

    public UaServer getServer() {
        return this._server;
    }

    DefaultCertificateValidatorListener validationListener = new DefaultCertificateValidatorListener() {
        @Override
        public ValidationResult onValidate(Cert cert, ApplicationDescription applicationDescription, EnumSet<CertificateCheck> enumSet) {
            try {
                log.info(applicationDescription + ", " + CertificateUtils.getApplicationUriOfCertificate(cert));
            } catch (CertificateParsingException ignore) {
            }
            return ValidationResult.AcceptPermanently;
        }
    };

    @PostConstruct
    public boolean initialize() {
        try {
            var server = new UaServer();
            server.setEnableIPv6(false);
            var applicationCertificateStore = new PkiDirectoryCertificateStore(this.prosysConfig.getApplicationCertificateStore());
            var applicationIssuerCertificateStore = new PkiDirectoryCertificateStore(this.prosysConfig.getApplicationIssuerCertificateStore());
            var applicationCertificateValidator = new DefaultCertificateValidator(applicationCertificateStore, applicationIssuerCertificateStore);
            applicationCertificateValidator.setValidationListener(this.validationListener);
            server.setCertificateValidator(applicationCertificateValidator);

            var userCertificateStore = new PkiDirectoryCertificateStore(this.prosysConfig.getUserCertificateStore());
            var userIssuerCertificateStore = new PkiDirectoryCertificateStore("USERS_PKI/CA/issuers");
            var userCertificateValidator = new DefaultCertificateValidator(userCertificateStore, userIssuerCertificateStore);
            userCertificateValidator.setValidationListener(this.validationListener);


            ApplicationDescription appDescription = new ApplicationDescription();
            appDescription.setApplicationName(new LocalizedText(this.prosysConfig.getApplicationName()));
            appDescription.setApplicationUri(this.prosysConfig.getApplicationUri());
            appDescription.setProductUri(this.prosysConfig.getProductUri());
            appDescription.setApplicationType(ApplicationType.Server);

            server.setPort(UaApplication.Protocol.OpcTcp, this.prosysConfig.getPort());
            server.setServerName("OPCUA/" + this.prosysConfig.getApplicationName());
            server.setBindAddresses(EndpointUtil.getInetAddresses(server.isEnableIPv6()));
            log.info("Loading certificates..");
            File privatePath = new File(applicationCertificateStore.getBaseDir(), "private");

            server.setApplicationIdentity(ApplicationIdentity.loadOrCreateCertificate(
                    appDescription,
                    "PXCCN",
                    /* Private Key Password */"opcua",
                    /* Key File Path */privatePath,
                    /* Issuer Certificate & Private Key */null,
                    /* Key Sizes for instance certificates to create */null,
                    /* Enable renewing the certificate */true));

            server.getSecurityModes()
                    .addAll(SecurityMode.combinations(
                            Collections.singleton(MessageSecurityMode.None),
                            Collections.singleton(SecurityPolicy.NONE)
                    ));
            server.addUserTokenPolicy(UserTokenPolicies.ANONYMOUS);
            server.init();
            server.getSessionManager().setMaxSessionCount(500);
            server.getSessionManager().setMaxSessionTimeout(3600000); // one hour
            server.getSubscriptionManager().setMaxSubscriptionCount(50);

            var buildInfo = server.getNodeManagerRoot().getServerData().getServerStatusNode().getBuildInfoNode();
            buildInfo.setProductName("LightControlSystem");
            buildInfo.setManufacturerName("PhoenixContact-SongYantao");
            buildInfo.setSoftwareVersion(this.prosysConfig.getVersion());
            buildInfo.setBuildNumber("1");
            final URL classFile = UaServer.class.getResource("/com/pxccn/PxcDali2/server/opcua/OpcuaServer.class");
            if (classFile != null && classFile.getFile() != null) {
                final File mfFile = new File(classFile.getFile());
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(mfFile.lastModified());
                buildInfo.setBuildDate(new DateTime(c));
            }
            this._server = server;
            createAddressSpace();
            log.info("OpcUa init finish");
            return true;
        } catch (Exception e) {
            log.error("Can not init OpcUa Server", e);
            return false;
        }
    }

    private LcsNodeManager lcsNodeManager;

    public LcsNodeManager getLcsNodeManager() {
        return this.lcsNodeManager;
    }

    private void createAddressSpace() {
        lcsNodeManager = new LcsNodeManager(this._server, LcsNodeManager.Namespace);

    }

}