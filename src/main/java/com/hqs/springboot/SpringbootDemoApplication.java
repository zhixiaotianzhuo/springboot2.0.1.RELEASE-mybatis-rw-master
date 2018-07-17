package com.hqs.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author  huangqingshi
 * @date 2018.04.11
 * 启动入口类
 */
@SpringBootApplication
@ComponentScan("com.hqs.springboot")
public class SpringbootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDemoApplication.class, args);
	}
}
