package com.catface.heimdall.app.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

public class StackTraceConverter extends ClassicConverter {

  @Override
  public String convert(ILoggingEvent event) {
    if (event.getThrowableProxy() == null) {
      return "";
    }
    StackTraceElementProxy[] stackTraceElementProxies = event.getThrowableProxy()
        .getStackTraceElementProxyArray();
    if (stackTraceElementProxies == null || stackTraceElementProxies.length <= 0) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder().append("\n");
    for (int i = 0; i < stackTraceElementProxies.length; i++) {
      StackTraceElementProxy stackTraceElementProxy = stackTraceElementProxies[i];
      if (stackTraceElementProxy.getStackTraceElement().getClassName().startsWith("com.jxmc")) {
        stringBuilder.append(stackTraceElementProxy.toString()).append("\n");
      }
    }
    return stringBuilder.toString();
  }
}
