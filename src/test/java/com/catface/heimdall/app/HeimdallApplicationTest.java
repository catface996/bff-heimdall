package com.catface.heimdall.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HeimdallApplicationTest {

  @Test
  public void contextLoads() throws InterruptedException {
    log.info("打一行日志到阿里云服务");
  }

}

