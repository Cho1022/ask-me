package com.cho1022.askme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AskmeBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
