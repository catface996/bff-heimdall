package com.catface.heimdall.app.config.feign;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;
import static feign.Util.valuesOrEmpty;

import com.alibaba.fastjson.JSONObject;
import com.catface.common.model.JsonResult;
import feign.Request;
import feign.Response;
import feign.Util;

import java.io.IOException;

import org.slf4j.Logger;

public class InfoFeignLogger extends feign.Logger {

    private final Logger logger;

    private static final Integer CODE_204 = 204;
    private static final Integer CODE_205 = 205;


    public InfoFeignLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (logger.isInfoEnabled()) {
            logRequestLocal(logLevel, request);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response,
                                              long elapsedTime)
            throws IOException {
        if (logger.isInfoEnabled()) {
            return logAndRebufferResponseLocal(logLevel, response, elapsedTime);
        }
        return response;
    }


    @Override
    protected void log(String configKey, String format, Object... args) {

    }

    protected void logRequestLocal(Level logLevel, Request request) {
        JSONObject requestObject = new JSONObject();
        requestObject.put("method", request.method());
        if (request.body() != null && request.body().length > 0) {
            requestObject.put("body", JSONObject.parse(request.body()));
        }
        requestObject.put("url", String.format("%s HTTP/1.1", request.url()));
        if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
            JSONObject headers = new JSONObject();
            for (String field : request.headers().keySet()) {
                for (String value : valuesOrEmpty(request.headers(), field)) {
                    headers.put(field, value);
                }
            }
            requestObject.put("headers", headers);
            if (request.body() != null) {
                if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                    String
                            bodyText =
                            request.charset() != null ? new String(request.body(), request.charset()) : null;
                    requestObject
                            .put("body", String.format("%s", bodyText != null ? bodyText : "Binary data"));
                }
            }
        }
        logger.info("{}", requestObject);
    }

    protected Response logAndRebufferResponseLocal(Level logLevel, Response response,
                                                   long elapsedTime) throws IOException {
        String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ?
                " " + response.reason() : "";
        int status = response.status();
        JSONObject responseObject = new JSONObject();
        String url = response.request().url();
        responseObject.put("url", url);
        responseObject.put("status", status);
        responseObject.put("elapsedTime", elapsedTime);
        responseObject.put("reason", reason);
        if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
            int bodyLength = 0;
            if (response.body() != null && !(status == CODE_204 || status == CODE_205)) {
                if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                    responseObject.put("result", "");
                }
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                bodyLength = bodyData.length;
                if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0) {
                    JsonResult jsonResult = JSONObject
                            .parseObject(String.format("%s", decodeOrDefault(bodyData, UTF_8, "{}")),
                                    JsonResult.class);
                    responseObject
                            .put("result", jsonResult.toString());
                }
                logger.info("{}", responseObject);
                return response.toBuilder().body(bodyData).build();
            } else {
                responseObject.put("result", String.format("<--- END HTTP (%s-byte body)", bodyLength));
            }
        }
        logger.info("{}", responseObject);
        return response;
    }
}
