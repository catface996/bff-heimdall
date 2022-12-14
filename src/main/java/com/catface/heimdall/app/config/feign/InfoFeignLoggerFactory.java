package com.catface.heimdall.app.config.feign;

import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignLoggerFactory;

public class InfoFeignLoggerFactory implements FeignLoggerFactory {

  @Override
  public Logger create(Class<?> type) {
    return new InfoFeignLogger(LoggerFactory.getLogger(type));
  }

}
