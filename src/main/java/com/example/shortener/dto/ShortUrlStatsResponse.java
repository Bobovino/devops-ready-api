package com.example.shortener.dto;

import com.example.shortener.domain.ShortUrl;

import java.time.Instant;

public record ShortUrlStatsResponse(
		String code,
		String originalUrl,
		Instant createdAt,
		long hitCount
) {
	public static ShortUrlStatsResponse from(ShortUrl shortUrl) {
		return new ShortUrlStatsResponse(shortUrl.getCode(), shortUrl.getOriginalUrl(), shortUrl.getCreatedAt(), shortUrl.getHitCount());
	}
}
