package com.example.shortener.controller;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.exception.ShortUrlNotFoundException;
import com.example.shortener.service.ShortUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortUrlController.class)
class ShortUrlControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ShortUrlService shortUrlService;

	@Test
	void createWithInvalidUrlReturns400() throws Exception {
		mockMvc.perform(post("/api/urls")
						.contentType("application/json")
						.content("{\"originalUrl\": \"not-a-url\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void redirectReturns302WithLocationHeader() throws Exception {
		ShortUrl shortUrl = ShortUrl.builder().id(1L).code("abc").originalUrl("https://example.com").hitCount(1).build();
		when(shortUrlService.recordHit(eq("abc"))).thenReturn(shortUrl);

		mockMvc.perform(get("/abc"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", "https://example.com"));
	}

	@Test
	void redirectForUnknownCodeReturns404() throws Exception {
		when(shortUrlService.recordHit(eq("missing"))).thenThrow(new ShortUrlNotFoundException("No URL found for code: missing"));

		mockMvc.perform(get("/missing"))
				.andExpect(status().isNotFound());
	}
}
