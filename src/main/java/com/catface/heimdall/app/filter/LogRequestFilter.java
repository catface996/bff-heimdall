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
public class LogRequestFilter extends ZuulFilter {

  @Override
  public String filterType() {
    return FilterConstants.PRE_TYPE;
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

    // 设置接收到请求的开始时间
    RequestContext.getCurrentContext().set(HeimdallConst.START_TIME, System.currentTimeMillis());

    HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
    // 忽略健康检查
    if (!HttpUtil.isActuator(request)) {
      // 打印请求信息
      LogUtil.logRequest(request);
    }
    return null;
  }


}
