package com.campus.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手交易平台启动类
 * 
 * @author 开发团队
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.campus.trade.mapper")
public class CampusTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTradeApplication.class, args);
        System.out.println("校园二手交易平台启动成功！");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui/");
    }
}
