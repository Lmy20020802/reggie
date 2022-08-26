package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName ReggieApplication
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 08:42
 **/
@SuppressWarnings({"all"})
@Slf4j
@SpringBootApplication
@ServletComponentScan//扫描WebFilter的一些注解
@EnableTransactionManagement//开启事务注解支持
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class);
        log.info("项目启动了");
    }
}
