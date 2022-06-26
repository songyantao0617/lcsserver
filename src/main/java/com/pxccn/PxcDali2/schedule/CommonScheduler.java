package com.pxccn.PxcDali2.schedule;

import com.pxccn.PxcDali2.server.service.opcua.UaAlarmEventService;
import com.pxccn.PxcDali2.server.service.rpc.impl.CabinetRequestServiceImpl;
import com.pxccn.PxcDali2.server.space.TopSpace;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class CommonScheduler {

    @Autowired
    CabinetsManager cabinetsManager;
    @Autowired
    UaAlarmEventService uaAlarmEventService;
    @Autowired
    TopSpace topSpace;

    @Autowired
    CabinetRequestServiceImpl cabinetRequestService;

    @Value("${LcsServer.timeServer:false}")
    boolean isTimeServer;

    @Scheduled(fixedDelay = 3600 * 1 * 1000)
    private void onPer1Hour() {
        log.trace("onPer1Hour");
        if (isTimeServer) {
            Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(c -> {
                c.setCabinetSystemTime(System.currentTimeMillis());
            });
        }
    }


    @Scheduled(cron = "0/2 * * * * ?")
    private void cabtest() {
        if (topSpace.isReady()) {
            uaAlarmEventService.sendBasicUaEvent(null, "11111", 1);

//            Futures.addCallback(cabinetRequestService.asyncSend(RpcTarget.CommonToAllCabinet, new DetailInfoRequestWrapper(Util.NewCommonHeaderForClient(), true, null)), new FutureCallback<ResponseWrapper>() {
//                @Override
//                public void onSuccess(@Nullable ResponseWrapper result) {
//                    log.info("111");
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    log.info("222");
//
//                }
//            }, MoreExecutors.directExecutor());
//
//            ;
//

//            cabinetRequestService.test();
//            le.getCabinetTime();
//            cabinetRequestService.test2();
//            cabinetRequestService.invokeMethodAsync(RpcTarget.CommonToAllCabinet,
//                    "station:|slot:/LightControlSystem",
//                    "testRpc",
//                    Arrays.asList(
//                            new InvokeParam<>(Integer.TYPE, 123),
//                            new InvokeParam<>(String.class, "dd")),
//                    (rtn) -> {
//                        log.info("调,{}", rtn);
//                    }, (ex) -> {
//                    });
//            cabinetRequestService.sendPing(RpcTarget.CommonToAllCabinet, (resp) -> {
//                log.info("控制器<{},{}>在线", resp.getCabinetId(), resp.getCabinetName());
//            }, (ex) -> {
//                log.error("控制器没有应答Ping指令");
//            });
        }
    }



}
