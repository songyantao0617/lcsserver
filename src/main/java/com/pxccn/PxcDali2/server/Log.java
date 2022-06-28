package com.pxccn.PxcDali2.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
//@Order(Integer.MIN_VALUE)
@Slf4j
public class Log {
    @PostConstruct
//    @Order(Integer.MIN_VALUE)
    public void init() {
        log.trace("testTrace");
        log.warn("testWarn");
        log.info("testInfo");
        log.error("testError");
    }
}
