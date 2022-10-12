package com.pxccn.PxcDali2;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.server.service.db.DatabaseService;
import com.pxccn.PxcDali2.server.service.log.LogService;
import com.pxccn.PxcDali2.server.service.ota.OtaService;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_log;

@SpringBootTest
@RabbitListener
class PxcDali2ApplicationTests {

    @Autowired
    CabinetRequestService cabinetRequestService;

    @Autowired
    OtaService otaService;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    LogService logService;

    @Test
    void contextLoads() {
//        cabinetRequestService.writePropertyValueAsync(RpcTarget.ToCabinet(14598041),"station:|slot:/LightControlSystem/description","testDesc");
        cabinetRequestService.writePropertyValueAsync(RpcTarget.ToCabinet(14598041),"station:|slot:/LightControlSystem/globalSettings/ntp_enable","false");
    }
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    void test2() throws IOException {
        File file = new File("C:\\OpenManage\\support\\OMCleanup\\OMCleanup.exe");
        byte[] bytes = new FileInputStream(file).readAllBytes();
        System.out.println(bytes.length);
    }

    @Test
    void testOtc(){
        otaService.tranFileToCabinet(9950662,"F:\\BMW_Lib\\v1.000\\PxcDali2-rt.jar","/home/sysmik/niagara/modules",true);
    }

    @Test
    void testUpload() throws InterruptedException {
        otaService.AskFileUpload(9950662,"/home/sysmik/niagara_user_home/stations/IL_500_26/config.bog","D:\\test\\PxcDali2\\foobar");
        Thread.sleep(20000);
    }

    @Test
    void testC() throws InterruptedException {
        logService.clearDb();
        Thread.sleep(30);
    }

}
