package com.hqs.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author  huangqingshi
 * @date 2018.04.11
 * 启动入口类
 */
//@SpringBootApplication
//@ComponentScan("com.hqs.springboot")

@SpringBootApplication
@ServletComponentScan
//@EnableTransactionManagement
@EnableAsync
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
//@EnableRetry
@ComponentScan(basePackages = {"com.hqs.springboot"})
public class SpringbootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDemoApplication.class, args);
	}
}
