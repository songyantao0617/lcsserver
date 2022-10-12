package com.pxccn.PxcDali2.server.service.log;

import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.server.events.CabinetLogsEvent;
import com.pxccn.PxcDali2.server.events.RoomActionLogEvent;
import com.pxccn.PxcDali2.server.service.db.DatabaseService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 日志服务
 * <p>
 * 本服务用于持久化控制器上传的运行日志，包括
 * <p>--运行时日志
 * <p>--房间动作日志
 *
 * TODO: CRUD
 */
@Component
@ConditionalOnProperty(prefix = "LcsServer", name = "logService", matchIfMissing = false)
@Slf4j
public class LogService {

    @Autowired
    DatabaseService databaseService;

    ExecutorService executorService;
    final LinkedBlockingDeque<RoomAction> actionQueue = new LinkedBlockingDeque<>();

    static final int BatchSize = 30;

    @PostConstruct
    public void init() {
        this.executorService = LcsExecutors.newWorkStealingPool(5, getClass());
        initWriteThread();
    }

    private void initWriteThread() {
        this.executorService.execute(() -> {
            log.warn("roomAction writer thread running");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var roomAction = actionQueue.poll(2, TimeUnit.SECONDS);
                    if (roomAction != null) {
                        Thread.sleep(2000);
                        List<RoomAction> buf = new ArrayList<>();
                        buf.add(roomAction);
                        actionQueue.drainTo(buf);
                        databaseService.saveRoomActionLogSync(buf);
                    }
                } catch (Exception e) {
                    log.error("error in roomAction writer thread", e);
                } finally {

                }
            }
            log.warn("roomAction writer thread stopped");
        });
    }

    @EventListener
    public void onCabinetLog(CabinetLogsEvent event) {
        log.trace("onCabinetLog: cabinetId={},logPackSize={}", event.getCabinetId(), event.getModelList().size());
        executorService.execute(() -> {
            event.getModelList().forEach(i -> i.setTimestamp(i.getTimestamp() / 1000));
            var li = event.getModelList();
            int cnt = li.size();
            while (cnt > BatchSize) {
                databaseService.saveCabinetLogSync(event.getCabinetId(), li.subList(0, BatchSize));
                li = li.subList(BatchSize, cnt);
                cnt = li.size();
            }
            if (cnt > 0) {
                databaseService.saveCabinetLogSync(event.getCabinetId(), li);
            }
        });
    }



    @EventListener
    public void onRoomActionLog(RoomActionLogEvent event) {
        log.info("onRoomActionLog={}", event.getWrapper());
        this.actionQueue.add(RoomAction.builder()
                .cabinet_ID(event.getCabinetId())
                .timestamp(event.getTimestamp() / 1000)
                .roomUuid(event.getWrapper().getRoomUuid().toString())
                .source(event.getWrapper().getSource())
                .cmd103_tag(event.getWrapper().getDali2LightCommandModel().getInstruction().name())
                .cmd103_value(event.getWrapper().getDali2LightCommandModel().getParameter())
                .dt8_tag(event.getWrapper().getDt8CommandModel().getInstruction().name())
                .dt8_value1(event.getWrapper().getDt8CommandModel().getDt8ActionParam())
                .dt8_value2(event.getWrapper().getDt8CommandModel().getDt8ActionParam2())
                .build());
    }


    /**
     * 每天凌晨，删除七天前的日志记录
     * <p>
     * TODO: 当前使用简单方式处理，考虑未来用 quartz,同时考虑分布式锁
     */
    @Scheduled(cron = "@daily") // “At 00:00”
    public void clearDb() {
        log.info("Clear Database history");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -7); // 七天前
        databaseService.clearCabinetLogAndRoomActionLogBefore(c.getTimeInMillis());
    }


    @Data
    @Builder
    public static class RoomAction {
        int cabinet_ID;
        long timestamp;
        String roomUuid;
        String source;
        String cmd103_tag;
        int cmd103_value;
        String dt8_tag;
        int dt8_value1;
        int dt8_value2;
    }


}
