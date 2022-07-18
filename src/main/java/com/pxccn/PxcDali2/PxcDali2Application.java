package com.pxccn.PxcDali2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan({"com.pxccn.PxcDali2.server.database.mapper", "com.pxccn.PxcDali2.server.database.mapperManual"})
public class PxcDali2Application {
    public static void main(String[] args) {
        SpringApplication.run(PxcDali2Application.class, args);
    }
}