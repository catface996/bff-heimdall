package com.catface.heimdall.app.config.feign;

import feign.Logger;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignConfigure {


  @Bean
  Logger.Level feignLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  FeignLoggerFactory infoFeignLoggerFactory() {
    return new InfoFeignLoggerFactory();
  }


}
