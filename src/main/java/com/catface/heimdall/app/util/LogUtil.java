package com.catface.heimdall.app.util;

import com.alibaba.fastjson.JSONObject;
import com.catface.heimdall.app.filter.HeimdallConst;
import com.netflix.zuul.context.RequestContext;
import java.util.Enumeration;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {

  /**
   * 打印请求信息
   *
   * @param request 请求对象
   */
  public static void logRequest(HttpServletRequest request) {

    JSONObject logJsonObject = new JSONObject();

    try {

      // 构造调用端信息
      buildRemote(request, logJsonObject);

      // 构造URI
      buildUri(request, logJsonObject);

      // 构造headers
      buildHeaders(request, logJsonObject);

      // 构造请求参数
      buildParams(request, logJsonObject);


    } finally {
      log.info("{}", logJsonObject);
    }
  }


  /**
   * 打印响应结果
   */
  public static void logResponse(RequestContext requestContext) {
    JSONObject logJsonObject = new JSONObject();
    try {
      // 构造响应数据
      buildResponse(logJsonObject, requestContext);
    } finally {
      log.info("{}", logJsonObject);
    }
  }

  /**
   * 构造响应数据
   */
  private static void buildResponse(JSONObject jsonObject, RequestContext requestContext) {

    // 获取请求路径
    String url = requestContext.getRequest().getRequestURI();
    // 获取请求状态
    int status = requestContext.getResponseStatusCode();

    // 计算耗时
    Long startTime = Long.valueOf(requestContext.get(HeimdallConst.START_TIME).toString());
    Long duration = System.currentTimeMillis() - startTime;

    // 构造响应状态和耗时
    jsonObject.fluentPut("apiStatus", status).fluentPut("apiDuration", duration).fluentPut("managerUrl", url);
  }

  /**
   * 构造parameters
   */
  private static void buildParams(HttpServletRequest request, JSONObject jsonObject) {
    // 构造parameters
    JSONObject paramsObject = new JSONObject();
    jsonObject.put("parameters", paramsObject);
    StringBuilder params = new StringBuilder();
    Enumeration<String> names = request.getParameterNames();
    if (names != null) {
      while (names.hasMoreElements()) {
        String name = names.nextElement();
        params.append(name);
        params.append("=");
        params.append(request.getParameter(name));
        params.append("&");
        paramsObject.put(name, request.getParameter(name));
      }
      jsonObject.put("queryString", params);
    }

    try {
      // 读取body中的参数
      final RequestContext ctx = RequestContext.getCurrentContext();
      if (!ctx.isChunkedRequestBody()) {
        ServletInputStream inp;
        inp = ctx.getRequest().getInputStream();
        if (inp != null) {
          JSONObject jsonBody = HttpUtil.getBodyDataToJson(ctx.getRequest());
          if (jsonBody != null) {
            jsonObject.put("body", jsonBody);
            // 从body移出url中的参数
            for (String key : paramsObject.keySet()) {
              jsonBody.remove(key);
            }
            // 将body中的参数加入到parameters
            paramsObject.putAll(jsonBody);
          }
        }
      }
    } catch (Exception e) {
      log.error("读取request body中的数据异常!{}", e);
    }
  }


  /**
   * 构造URI
   */
  private static void buildUri(HttpServletRequest request, JSONObject jsonObject) {
    // 构造URI
    JSONObject requestUriObject = new JSONObject();
    requestUriObject.fluentPut("method", request.getMethod())
        .fluentPut("url", request.getRequestURI())
        .fluentPut("protocol", request.getProtocol());
    jsonObject.put("requestUri", requestUriObject);
  }

  /**
   * 构造调用方信息
   */
  private static void buildRemote(HttpServletRequest request, JSONObject jsonObject) {
    // 构造调用端信息
    JSONObject remoteObject = new JSONObject();
    remoteObject.fluentPut("scheme", request.getScheme())
        .fluentPut("remoteAddr", request.getRemoteAddr())
        .fluentPut("remotePort", request.getRemotePort());
    jsonObject.put("remote", remoteObject);
  }

  /**
   * 构造headers
   */
  private static void buildHeaders(HttpServletRequest request, JSONObject jsonObject) {
    // 构造headers
    Enumeration<String> headers = request.getHeaderNames();
    JSONObject headerJson = new JSONObject();
    while (headers.hasMoreElements()) {
      String name = headers.nextElement();
      String value = request.getHeader(name);
      headerJson.put(name, value);
    }
    jsonObject.put("headers", headerJson);
  }

}
