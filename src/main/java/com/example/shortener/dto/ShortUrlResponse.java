package com.example.shortener.dto;

import com.example.shortener.domain.ShortUrl;

public record ShortUrlResponse(
		String code,
		String originalUrl
) {
	public static ShortUrlResponse from(ShortUrl shortUrl) {
		return new ShortUrlResponse(shortUrl.getCode(), shortUrl.getOriginalUrl());
	}
}
