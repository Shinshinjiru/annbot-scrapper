package com.shinshinjiru.annbotscrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AnnbotScrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnbotScrapperApplication.class, args).close();
		System.exit(0);
	}

}
