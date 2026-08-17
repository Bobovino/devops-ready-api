package com.example.shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest(
		@NotBlank @Pattern(regexp = "^https?://.+", message = "must be a valid http(s) URL") String originalUrl
) {
}
