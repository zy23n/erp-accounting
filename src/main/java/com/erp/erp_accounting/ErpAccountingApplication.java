package com.erp.erp_accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ErpAccountingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpAccountingApplication.class, args);
	}

}
