package com.wms.wmsend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.wms.wmsend.mapper")
public class WmsEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsEndApplication.class, args);
    }

}
