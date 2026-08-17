package com.example.shortener.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ShortUrlIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createFollowAndCheckStatsEndToEnd() throws Exception {
		String createResponse = mockMvc.perform(post("/api/urls")
						.contentType("application/json")
						.content("{\"originalUrl\": \"https://example.com/portfolio\"}"))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		String code = objectMapper.readTree(createResponse).get("code").asText();

		mockMvc.perform(get("/" + code))
				.andExpect(status().isFound());

		mockMvc.perform(get("/api/urls/" + code + "/stats"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hitCount").value(1))
				.andExpect(jsonPath("$.originalUrl").value("https://example.com/portfolio"));
	}
}
