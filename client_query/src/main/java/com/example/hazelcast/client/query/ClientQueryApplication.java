package com.example.hazelcast.client.query;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EntityScan("com.example.hazelcast.shared.model")
@Configuration
public class ClientQueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientQueryApplication.class, args);
	}

	@Bean
	public ClientConfig clientConfig() throws Exception{
		ClientConfig clientConfig = new ClientConfig();
		ClientNetworkConfig clientNetworkConfig = clientConfig.getNetworkConfig();
		clientNetworkConfig.setConnectionAttemptLimit(0);//unlimited, try to recconect
		return clientConfig;
	}

	@Bean(name = "ClientQueryInstance", destroyMethod = "shutdown")
	public HazelcastInstance createClientInstance(ClientConfig clientConfig) throws Exception{
		return HazelcastClient.newHazelcastClient(clientConfig);
	}

	@Configuration
	@EnableSwagger2
	public class SwaggerConfig {
		@Bean
		public Docket api() {
			return new Docket(DocumentationType.SWAGGER_2)
					.select()
					.apis(RequestHandlerSelectors.any())
					.paths(PathSelectors.any())
					.build();
		}
	}
}
