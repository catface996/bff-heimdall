package com.catface.heimdall.app.controller;

import com.catface.common.model.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ErrorHandlerController implements ErrorController {


  /**
   * 出异常后进入该方法，交由下面的方法处理
   */
  @Override
  public String getErrorPath() {
    return "/error";
  }

  @RequestMapping("/error")
  public Object error() {
    log.error("路由异常!");
    return JsonResult.rpcError("路由异常!");
  }

  @RequestMapping("/unRecognize")
  public Object unRecognize() {
    log.error("不支持的路由规则!");
    return JsonResult.rpcError("不支持的路由规则!");
  }

}
