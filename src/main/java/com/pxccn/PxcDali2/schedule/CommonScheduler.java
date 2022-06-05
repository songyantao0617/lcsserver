package com.pxccn.PxcDali2.schedule;

import com.pxccn.PxcDali2.server.mq.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.mq.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.space.TopSpace;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class CommonScheduler {

    @Autowired
    TopSpace topSpace;

    @Autowired
    CabinetRequestService cabinetRequestService;

    @Scheduled(cron = "0/2 * * * * ?")
    private void cabtest() {
        if (topSpace.isReady()) {
            cabinetRequestService.sendPing(RpcTarget.CommonToAllCabinet, (resp) -> {
                log.info("控制器在线 {}，{}", resp.getFoo(), resp.getBar());
            }, (ex) -> {
                log.error("控制器没有应答Ping指令", ex);
            });
        }
    }


    @Scheduled(cron = "0/5 * * * * ?")
    private void checkCabinetAlive() {
        if (topSpace.isReady()) {
            topSpace.getCabinetsManager().doCheckCabinetAlive();
        }
    }
}
