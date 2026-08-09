package io.github.iamprakash15.automationtesting;

import java.util.Objects;

/** A product returned by a search-results page. */
public record Product(String name, String price, boolean primeEligible, String url) {

    public Product {
        name = requireNonBlank(name, "name");
        price = normalizeOptionalText(price, "Not available");
        url = normalizeOptionalText(url, "");
    }

    private static String requireNonBlank(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptionalText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
