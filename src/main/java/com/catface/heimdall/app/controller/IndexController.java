package com.catface.heimdall.app.controller;

import com.catface.common.model.JsonResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.text.MessageFormat;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class IndexController {

	private static final String MESSAGE = "Hello,heimdall!IP:{0},env:{1}";

	@Value("${spring.profiles.active}")
	private String env;

	@SneakyThrows
	@RequestMapping({"/", "/index"})
	public JsonResult<String> index() {
		String ip = InetAddress.getLocalHost().getHostAddress();
		String message = MessageFormat.format(MESSAGE, ip, env);
		log.info(message);
		return JsonResult.success(message);
	}
}
