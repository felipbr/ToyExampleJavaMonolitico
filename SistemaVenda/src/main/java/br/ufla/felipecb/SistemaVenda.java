package br.ufla.felipecb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "br.ufla.felipecb.dao")
@SpringBootApplication
public class SistemaVenda {

	public static void main(String[] args) {
		SpringApplication.run(SistemaVenda.class, args);
	}

}
