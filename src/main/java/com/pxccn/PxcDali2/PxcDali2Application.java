package com.pxccn.PxcDali2;

import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
@SpringBootApplication
@EnableCaching
@MapperScan({"com.pxccn.PxcDali2.server.database.mapper", "com.pxccn.PxcDali2.server.database.mapperManual"})
public class PxcDali2Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(PxcDali2Application.class, args);

//        LcsProtos.File.newBuilder().

//        byte[] data = new byte[]{0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02,0x01, 0x03, 0x01,  0x00, 0x02};
//
//        CRC32 crc32 = new CRC32();
//
//        crc32.update(data);
//
//        System.out.println(crc32.getValue());

//        Calendar c = Calendar.getInstance();
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        //过去七天
//        c.setTime(new Date());
//        c.add(Calendar.DATE, - 7);
//        System.out.println(format.format(c.getTime()));


    }
}
