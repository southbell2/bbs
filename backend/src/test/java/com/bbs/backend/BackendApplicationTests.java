package com.bbs.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {"spring.config.location=classpath:application.yml,classpath:aws.yml"})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
