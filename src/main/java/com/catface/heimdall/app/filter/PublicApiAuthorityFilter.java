package com.catface.heimdall.app.filter;

import com.alibaba.fastjson.JSONObject;
import com.catface.common.model.JsonResult;
import com.catface.eden.api.auth.AuthApi;
import com.catface.eden.api.auth.request.CheckTokenRequest;
import com.catface.eden.api.auth.vo.TokenInfoVO;
import com.catface.heimdall.app.util.HttpUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 路由过滤器
 */
@Slf4j
@Component
public class PublicApiAuthorityFilter extends ZuulFilter {


    @Value("${heimdall.publicApi}")
    private String publicApi;

    @Autowired
    private AuthApi authApi;


    @Override
    public String filterType() {
        // 可以在请求被路由之前调用
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return request.getRequestURI().contains(publicApi);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            String url = request.getRequestURI();
            // 登录检查
            if (needCheckLogin(url)) {
                // 如果未获取到token,提示重新登录
                String token = HttpUtil.getToken(ctx);
                if (!StringUtils.isBlank(token)) {
                    TokenInfoVO tokenInfo = checkToken(token);
                    //登录成功
                    if (tokenInfo != null) {
                        JSONObject requestBody = HttpUtil.getBodyDataToJson(ctx.getRequest());
                        requestBody.put("ctxUserId", tokenInfo.getCtxUserId());
                        requestBody.put("ctxClientId", tokenInfo.getCtxClientId());
                        HttpUtil.injectCommonParam(ctx, requestBody);
                        // 通过
                        HttpUtil.pass(ctx);
                        return null;
                    }
                }
            }
            // 重新登录
            HttpUtil.reLogin(ctx);
            return null;

        } catch (Exception e) {
            log.error("check public interface error!", e);
            HttpUtil.reTry(ctx);
            return null;
        }
    }


    /**
     * 是否需要登录检查
     *
     * @param url 请求路径
     * @return 是否检查
     */
    private boolean needCheckLogin(String url) {
        return url.contains(publicApi);
    }

    /**
     * 校验token
     *
     * @param token token
     * @return token代表的当前用户和客户
     */
    private TokenInfoVO checkToken(String token) {
        CheckTokenRequest request = new CheckTokenRequest();
        request.setToken(token);
        JsonResult<TokenInfoVO> result = authApi.checkToken(request);
        if (result.getSuccess()) {
            return result.getData();
        }
        return null;
    }

}
