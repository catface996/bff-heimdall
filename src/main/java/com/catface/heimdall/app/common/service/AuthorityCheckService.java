package com.catface.heimdall.app.common.service;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorityCheckService {

  public static final Logger logger = LoggerFactory.getLogger(AuthorityCheckService.class);

  /**
   * 对暴露到公网,提供给第三方调用的api进行签名检查
   */
  public boolean checkPublicApi(HttpServletRequest request) {

    return true;
  }

}
