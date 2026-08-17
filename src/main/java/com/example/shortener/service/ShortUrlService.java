package com.example.shortener.service;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.exception.ShortUrlNotFoundException;
import com.example.shortener.repository.ShortUrlRepository;
import com.example.shortener.util.Base62Encoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {

	private final ShortUrlRepository shortUrlRepository;

	public ShortUrlService(ShortUrlRepository shortUrlRepository) {
		this.shortUrlRepository = shortUrlRepository;
	}

	@Transactional
	public ShortUrl create(CreateShortUrlRequest request) {
		ShortUrl shortUrl = shortUrlRepository.save(ShortUrl.builder()
				.originalUrl(request.originalUrl())
				.createdAt(Instant.now())
				.hitCount(0)
				.build());

		shortUrl.setCode(Base62Encoder.encode(shortUrl.getId()));
		return shortUrl;
	}

	public ShortUrl getByCode(String code) {
		return shortUrlRepository.findByCode(code)
				.orElseThrow(() -> new ShortUrlNotFoundException("No URL found for code: " + code));
	}

	@Transactional
	public ShortUrl recordHit(String code) {
		ShortUrl shortUrl = getByCode(code);
		shortUrl.setHitCount(shortUrl.getHitCount() + 1);
		return shortUrl;
	}
}
