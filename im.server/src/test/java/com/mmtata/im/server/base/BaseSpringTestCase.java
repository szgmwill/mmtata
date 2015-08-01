package com.mmtata.im.server.base;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * spring测试基类
 * 
 * @author dell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "classpath:conf-spring/spring-service.xml"
})
@Transactional
public abstract class BaseSpringTestCase {
	
	public BaseSpringTestCase() {
		//set env to get config
		System.setProperty("env", "local");
	}
}
