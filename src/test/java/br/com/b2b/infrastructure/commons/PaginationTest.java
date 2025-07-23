package br.com.b2b.infrastructure.commons;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pagination Record Tests")
class PaginationTest {

    @Test
    @DisplayName("Should correctly create a Pagination instance and verify its properties")
    void shouldCreateInstanceAndVerifyProperties() {
        // Given
        List<String> items = List.of("item1", "item2");

        // When
        Pagination<String> pagination = new Pagination<>(1, 10, 2L, items);

        // Then
        assertThat(pagination.currentPage()).isEqualTo(1);
        assertThat(pagination.perPage()).isEqualTo(10);
        assertThat(pagination.total()).isEqualTo(2L);
        assertThat(pagination.items()).isEqualTo(items);
    }

    @Test
    @DisplayName("Should correctly create a Pagination instance using the builder")
    void shouldCreateInstanceUsingBuilder() {
        // Given
        List<Integer> items = List.of(100, 200, 300);

        // When
        Pagination<Integer> pagination = Pagination.<Integer>builder()
                .currentPage(2)
                .perPage(5)
                .total(15L)
                .items(items)
                .build();

        // Then
        assertThat(pagination.currentPage()).isEqualTo(2);
        assertThat(pagination.perPage()).isEqualTo(5);
        assertThat(pagination.total()).isEqualTo(15L);
        assertThat(pagination.items()).containsExactly(100, 200, 300);
    }

    @Test
    @DisplayName("map should transform item types while preserving pagination metadata")
    void map_shouldTransformItemsAndPreserveMetadata() {
        // Given
        List<String> originalItems = List.of("apple", "banana", "cherry");
        Pagination<String> originalPagination = new Pagination<>(1, 3, 3L, originalItems);
        Function<String, Integer> lengthMapper = String::length;

        // When
        Pagination<Integer> newPagination = originalPagination.map(lengthMapper);

        // Then
        // Pagination metadata should be unchanged
        assertThat(newPagination.currentPage()).isEqualTo(originalPagination.currentPage());
        assertThat(newPagination.perPage()).isEqualTo(originalPagination.perPage());
        assertThat(newPagination.total()).isEqualTo(originalPagination.total());

        // The items list should be transformed
        assertThat(newPagination.items())
                .isNotNull()
                .hasSize(3)
                .containsExactly(5, 6, 6); // lengths of "apple", "banana", "cherry"
    }

    @Test
    @DisplayName("map should handle an empty item list correctly")
    void map_shouldHandleEmptyList() {
        // Given
        Pagination<String> originalPagination = new Pagination<>(1, 10, 0L, List.of());
        Function<String, Integer> anyMapper = String::length;

        // When
        Pagination<Integer> newPagination = originalPagination.map(anyMapper);

        // Then
        // Pagination metadata should be unchanged
        assertThat(newPagination.currentPage()).isEqualTo(1);
        assertThat(newPagination.perPage()).isEqualTo(10);
        assertThat(newPagination.total()).isEqualTo(0L);

        // The new items list should be empty
        assertThat(newPagination.items()).isNotNull().isEmpty();
    }
}
