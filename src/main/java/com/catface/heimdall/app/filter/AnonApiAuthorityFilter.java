package com.catface.heimdall.app.filter;

import com.catface.heimdall.app.util.HttpUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

/**
 * 路由过滤器
 *
 */
@Component
@Slf4j
public class AnonApiAuthorityFilter extends ZuulFilter {

    private boolean filterSwitch;

    @Value("${heimdall.anonApi}")
    private String anonApi;

    @Override
    public String filterType() {
        // 可以在请求被路由之前调用
        return "pre";
    }

    @Override
    public int filterOrder() {
        // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
        return 11;
    }

    @Override
    public boolean shouldFilter() {
        if (filterSwitch) {
            RequestContext ctx = getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            return request.getRequestURI().contains(anonApi);
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 对匿名的访问,如果死options请求,直接返回
        if (HttpUtil.isOption(request)) {
            HttpUtil.returnSuccess(ctx);
        }
        return null;

    }

    public boolean isFilterSwitch() {
        return filterSwitch;
    }

    public void setFilterSwitch(boolean filterSwitch) {
        this.filterSwitch = filterSwitch;
    }

    public String getAnonApi() {
        return anonApi;
    }

    public void setAnonApi(String anonApi) {
        this.anonApi = anonApi;
    }
}
