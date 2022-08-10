package com.catface.heimdall.app.filter;

import brave.Tracer;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @date 2019-03-18
 */
@Slf4j
@Component
public class TraceFilter extends ZuulFilter {

  private final static String TRACE_ID = "traceId";

  @Autowired
  private Tracer tracer;

  @Value("${spring.profiles.active}")
  private String env;

  @Override
  public String filterType() {
    return FilterConstants.POST_TYPE;
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext requestContext = RequestContext.getCurrentContext();
    requestContext.getResponse()
        .setHeader(TRACE_ID, tracer.currentSpan().context().traceIdString());
    return null;
  }
}
