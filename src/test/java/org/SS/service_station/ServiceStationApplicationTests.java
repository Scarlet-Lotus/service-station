package org.SS.service_station;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // exclude = {KafkaAutoConfiguration.class}
@Testcontainers
class ServiceStationApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
			.withDatabaseName("service_station_test")
			.withUsername("admin")
			.withPassword("bratan06031975");

	@Container
	static KafkaContainer kafka = new KafkaContainer(
			DockerImageName.parse("apache/kafka:3.9.1") // confluentinc/cp-kafka:7.5.0 "apache/kafka:3.9.1"
					.asCompatibleSubstituteFor("apache/kafka"))
			.withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

	@BeforeAll
	static void setup() {
		System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
	}

	@PostConstruct
	void createTopics() {
		try (AdminClient adminClient = AdminClient.create(Collections.singletonMap(
				"bootstrap.servers", kafka.getBootstrapServers()))) {
			adminClient.createTopics(Collections.singletonList(
					new NewTopic("status-updates", 1, (short) 1)
			));
		} catch (Exception e) {
			throw new RuntimeException("Failed to create Kafka topics", e);
		}
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		// PostgreSQL setting
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		// Kafka setting
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Test
	void contextLoads() {
		System.out.println("PostgreSQL container started with JDBC URL: " + postgres.getJdbcUrl());
		System.out.println("Kafka container started with Bootstrap Servers: " + kafka.getBootstrapServers());
	}

}
