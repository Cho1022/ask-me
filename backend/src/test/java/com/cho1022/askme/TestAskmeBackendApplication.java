package com.cho1022.askme;

import org.springframework.boot.SpringApplication;

public class TestAskmeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(AskmeBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
