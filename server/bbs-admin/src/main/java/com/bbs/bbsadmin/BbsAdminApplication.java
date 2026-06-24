package com.bbs.bbsadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.bbs.bbsadmin.mapper")
public class BbsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbsAdminApplication.class, args);
    }

}
