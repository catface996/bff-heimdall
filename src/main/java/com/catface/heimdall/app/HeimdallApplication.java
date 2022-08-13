package com.catface.heimdall.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients(basePackages = {"com.catface.eden.api"})
public class HeimdallApplication {

  public static void main(String[] args) {
    SpringApplication.run(HeimdallApplication.class, args);
    log.info("Heimdall Application start success!");
    log.info("http://localhost:8001");
  }

}

