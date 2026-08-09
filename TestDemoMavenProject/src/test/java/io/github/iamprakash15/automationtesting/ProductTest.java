package io.github.iamprakash15.automationtesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void normalizesOptionalFields() {
        Product product = new Product("  Cricket bat  ", null, true, "  https://example.test/bat  ");

        assertEquals("Cricket bat", product.name());
        assertEquals("Not available", product.price());
        assertEquals("https://example.test/bat", product.url());
    }

    @Test
    void rejectsBlankProductName() {
        assertThrows(IllegalArgumentException.class, () -> new Product("  ", "$10", false, ""));
    }
}
