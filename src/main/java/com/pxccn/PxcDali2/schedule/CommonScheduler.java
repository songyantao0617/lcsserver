package com.pxccn.PxcDali2.schedule;

import com.pxccn.PxcDali2.learn.Learnmmm;
import com.pxccn.PxcDali2.server.service.rpc.impl.CabinetRequestServiceImpl;
import com.pxccn.PxcDali2.server.space.TopSpace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class CommonScheduler {

    @Autowired
    TopSpace topSpace;

    @Autowired
    CabinetRequestServiceImpl cabinetRequestService;

    @Autowired
    Learnmmm le;
    @Scheduled(cron = "0/2 * * * * ?")
    private void cabtest() {
        if (topSpace.isReady()) {

            le.readDescription();

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


    @Scheduled(cron = "0/5 * * * * ?")
    private void checkCabinetAlive() {
        if (topSpace.isReady()) {
            topSpace.getCabinetsManager().doCheckCabinetAlive();
        }
    }
}
