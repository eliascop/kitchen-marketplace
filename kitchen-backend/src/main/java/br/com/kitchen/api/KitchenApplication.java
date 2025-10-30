package br.com.kitchen.api;

import br.com.kitchen.api.repository.jpa.impl.GenericRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "br.com.kitchen.api.repository.jpa",
		repositoryBaseClass = GenericRepositoryImpl.class
)
@EnableElasticsearchRepositories(
        basePackages = "br.com.kitchen.api.repository.search"
)
public class KitchenApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenApplication.class, args);
	}

}
