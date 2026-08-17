package com.example.shortener.service;

import com.example.shortener.domain.ShortUrl;
import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.exception.ShortUrlNotFoundException;
import com.example.shortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

	@Mock
	private ShortUrlRepository shortUrlRepository;

	@InjectMocks
	private ShortUrlService shortUrlService;

	@Test
	void createAssignsABase62CodeDerivedFromTheGeneratedId() {
		when(shortUrlRepository.save(any())).thenAnswer(invocation -> {
			ShortUrl arg = invocation.getArgument(0);
			arg.setId(125L);
			return arg;
		});

		ShortUrl result = shortUrlService.create(new CreateShortUrlRequest("https://example.com/very/long/path"));

		assertThat(result.getCode()).isEqualTo("21");
	}

	@Test
	void recordHitIncrementsCounter() {
		ShortUrl shortUrl = ShortUrl.builder().id(1L).code("abc").originalUrl("https://example.com").hitCount(3).build();
		when(shortUrlRepository.findByCode("abc")).thenReturn(Optional.of(shortUrl));

		ShortUrl result = shortUrlService.recordHit("abc");

		assertThat(result.getHitCount()).isEqualTo(4);
	}

	@Test
	void unknownCodeThrows() {
		when(shortUrlRepository.findByCode("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> shortUrlService.getByCode("missing"))
				.isInstanceOf(ShortUrlNotFoundException.class);
	}
}
