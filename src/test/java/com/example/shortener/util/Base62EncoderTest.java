package com.example.shortener.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

	@Test
	void encodesZeroAsFirstAlphabetCharacter() {
		assertThat(Base62Encoder.encode(0)).isEqualTo("0");
	}

	@Test
	void encodesDistinctValuesToDistinctCodes() {
		assertThat(Base62Encoder.encode(1)).isNotEqualTo(Base62Encoder.encode(2));
		assertThat(Base62Encoder.encode(61)).isEqualTo("z");
		assertThat(Base62Encoder.encode(62)).isEqualTo("10");
	}

	@Test
	void producesShortCodesForLargeIds() {
		assertThat(Base62Encoder.encode(1_000_000_000L).length()).isLessThanOrEqualTo(7);
	}
}
