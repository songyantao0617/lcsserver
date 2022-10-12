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

@Configuration
@EnableScheduling
@Slf4j
public class CommonScheduler {


}
