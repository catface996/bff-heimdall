package com.catface.heimdall.app.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.catface.common.enums.common.CommonConst;
import com.catface.common.model.JsonResult;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

@Slf4j
public class HttpUtil {

  private static final String ACTUATOR = "actuator";

  private static final String POST = "POST";

  private static final String OPTIONS = "OPTIONS";

  private static final String APPLICATION_JSON = "application/json";

  private static final String CONTENT_TYPE = "Content-Type";

  private static final String FORM_KEY_WORD = "form";

  public static boolean isOption(HttpServletRequest request) {
    return OPTIONS.equals(request.getMethod());
  }

  public static boolean isPost(HttpServletRequest request) {
    return POST.equals(request.getMethod());
  }

  public static boolean isPostJson(HttpServletRequest request) {
    String contextType = request.getHeader(CONTENT_TYPE);
    if (contextType == null) {
      return false;
    }
    return POST.equals(request.getMethod()) && contextType.contains(HttpUtil.APPLICATION_JSON);
  }

  public static String getToken(RequestContext ctx) {
    return ctx.getRequest().getHeader(CommonConst.TOKEN);
  }

  public static String getUserType(RequestContext ctx) {
    return ctx.getRequest().getHeader("userType");
  }

  public static void reLogin(RequestContext ctx) {
    ctx.getResponse().setCharacterEncoding("UTF-8");
    ctx.getResponse().setHeader("Content-Type", "application/json");
    ctx.setSendZuulResponse(false);
    ctx.setResponseStatusCode(401);
    ctx.set("isSuccess", false);
    ctx.setResponseBody(JSONObject.toJSONString(JsonResult.error("???????????????!")));
  }

  public static void noAuthority(RequestContext ctx, String message) {
    ctx.getResponse().setCharacterEncoding("UTF-8");
    ctx.getResponse().setHeader("Content-Type", "application/json");
    ctx.setSendZuulResponse(false);
    ctx.setResponseStatusCode(403);
    ctx.set("isSuccess", false);
    ctx.setResponseBody(JSONObject.toJSONString(JsonResult.error(message)));
  }


  public static void reTry(RequestContext ctx) {
    ctx.getResponse().setCharacterEncoding("UTF-8");
    ctx.getResponse().setHeader("Content-Type", "application/json");
    ctx.setSendZuulResponse(false);
    ctx.setResponseStatusCode(500);
    ctx.set("isSuccess", false);
    ctx.setResponseBody(JSONObject.toJSONString(JsonResult.error("????????????,???????????????!")));
  }


  public static void pass(RequestContext ctx) {
    ctx.setSendZuulResponse(true);
    ctx.setResponseStatusCode(200);
    ctx.set("isSuccess", true);
  }

  public static void returnSuccess(RequestContext ctx) {
    ctx.setSendZuulResponse(false);
    ctx.setResponseStatusCode(200);
    ctx.set("isSuccess", false);
  }

  public static void methodNotSupport(RequestContext ctx) {
    ctx.setSendZuulResponse(false);
    ctx.setResponseStatusCode(200);
    ctx.set("isSuccess", false);
    ctx.setResponseBody(JSON.toJSONString(JsonResult.error("?????????????????????!")));
  }


  /**
   * ??????????????????????????????(POST)
   */
  public static JSONObject getBodyDataToJson(HttpServletRequest request) {

    //form?????????????????????
    String contentType = request.getHeader(CONTENT_TYPE);
    if (contentType != null && contentType.contains(FORM_KEY_WORD)) {
      JSONObject bodyObject = new JSONObject();
      try {
        HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) request;
        StandardMultipartHttpServletRequest httpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequestWrapper
            .getRequest();
        Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
        for (String name : paramMap.keySet()) {
          bodyObject.put(name, JSONObject.toJSONString(paramMap.get(name)));
        }
        //??????????????????
        StringBuilder fileNames = new StringBuilder();
        Map<String, MultipartFile> fileMap = httpServletRequest.getMultiFileMap()
            .toSingleValueMap();
        for (MultipartFile file : fileMap.values()) {
          fileNames.append(file.getOriginalFilename()).append(";");
        }
        if (StringUtils.isNotBlank(fileNames.toString())) {
          bodyObject.put("files", fileNames.toString());
        }
      } catch (Exception e) {
        log.debug("????????????????????????!");
      }
      return bodyObject;
    }

    //??????????????????
    StringBuffer data = new StringBuffer();
    String line;
    BufferedReader reader;
    try {
      reader = request.getReader();
      while (null != (line = reader.readLine())) {
        data.append(line);
      }
      if (StringUtils.isBlank(data.toString())) {
        return new JSONObject();
      }
      return JSONObject.parseObject(data.toString());
    } catch (JSONException e) {
      log.debug("request body ?????????json??????!body:{}", data);
    } catch (Exception e) {
      log.error("??????????????????!data:{}", data);
    }
    return new JSONObject().fluentPut("body", data);
  }

  public static void injectCommonParam(RequestContext ctx, JSONObject jsonObject) {
    HttpServletRequest request = ctx.getRequest();
    // ????????????
    String method = request.getMethod();
    if (POST.equals(method)) {
      try {
        InputStream in = request.getInputStream();
        String body = StreamUtils.copyToString(in, Charset.forName(StandardCharsets.UTF_8.name()));
        if (StringUtils.isBlank(body)) {
          body = "{}";
        }
        JSONObject bodyJsonObject = JSONObject.parseObject(body);
        // ??????????????????
        bodyJsonObject.putAll(jsonObject);
        String newBody = bodyJsonObject.toString();
        final byte[] reqBodyBytes = newBody.getBytes();
        ctx.setRequest(new HttpServletRequestWrapper(request) {
          @Override
          public ServletInputStream getInputStream() {
            return new ServletInputStreamWrapper(reqBodyBytes);
          }

          @Override
          public int getContentLength() {
            return reqBodyBytes.length;
          }

          @Override
          public long getContentLengthLong() {
            return reqBodyBytes.length;
          }
        });
      } catch (Exception e) {
        log.error("????????????????????????????????????!");
      }
    }
  }

  /**
   * ???????????????????????????,?????????,??????true,??????,false
   *
   * @param request http request
   * @return ???????????????????????????
   */
  public static boolean isActuator(HttpServletRequest request) {
    String url = request.getRequestURI();
    // ??????????????????
    return url.contains(ACTUATOR);
  }


}
