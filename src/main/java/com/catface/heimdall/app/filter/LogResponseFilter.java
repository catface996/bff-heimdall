package com.catface.heimdall.app.filter;

import com.catface.heimdall.app.util.HttpUtil;
import com.catface.heimdall.app.util.LogUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * 路由过滤器
 *
 */
@Slf4j
@Component
public class LogResponseFilter extends ZuulFilter {

  @Override
  public String filterType() {
    return FilterConstants.POST_TYPE;
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {

    HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
    // 排除健康检查日志
    if (!HttpUtil.isActuator(request)) {
      // 记录日志
      LogUtil.logResponse(RequestContext.getCurrentContext());
    }
    return null;
  }


}
