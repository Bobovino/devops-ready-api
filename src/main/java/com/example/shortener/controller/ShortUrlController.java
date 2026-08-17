package com.example.shortener.controller;

import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.dto.ShortUrlStatsResponse;
import com.example.shortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ShortUrlController {

	private final ShortUrlService shortUrlService;

	public ShortUrlController(ShortUrlService shortUrlService) {
		this.shortUrlService = shortUrlService;
	}

	@PostMapping("/api/urls")
	@ResponseStatus(HttpStatus.CREATED)
	public ShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request) {
		return ShortUrlResponse.from(shortUrlService.create(request));
	}

	@GetMapping("/api/urls/{code}/stats")
	public ShortUrlStatsResponse stats(@PathVariable String code) {
		return ShortUrlStatsResponse.from(shortUrlService.getByCode(code));
	}

	@GetMapping("/{code}")
	public ResponseEntity<Void> redirect(@PathVariable String code) {
		var shortUrl = shortUrlService.recordHit(code);
		return ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(shortUrl.getOriginalUrl()))
				.build();
	}
}
