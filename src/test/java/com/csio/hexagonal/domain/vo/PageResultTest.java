package com.csio.hexagonal.domain.vo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void shouldCreatePageResult() {
        // Arrange
        List<String> content = Arrays.asList("Item1", "Item2", "Item3");
        int page = 1;
        int size = 10;
        long totalElements = 3;
        int totalPages = 1;

        // Act
        PageResult<String> result = PageResult.of(content, page, size, totalElements, totalPages);

        // Assert
        assertNotNull(result);
        assertEquals(content, result.content());
        assertEquals(page, result.page());
        assertEquals(size, result.size());
        assertEquals(totalElements, result.totalElements());
        assertEquals(totalPages, result.totalPages());
    }

    @Test
    void shouldCreateEmptyPageResult() {
        // Arrange
        List<String> content = Collections.emptyList();

        // Act
        PageResult<String> result = PageResult.of(content, 1, 10, 0, 0);

        // Assert
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    void shouldHandleMultiplePages() {
        // Arrange
        List<String> content = Arrays.asList("Item1", "Item2");
        int page = 2;
        int size = 2;
        long totalElements = 10;
        int totalPages = 5;

        // Act
        PageResult<String> result = PageResult.of(content, page, size, totalElements, totalPages);

        // Assert
        assertEquals(2, result.page());
        assertEquals(2, result.size());
        assertEquals(10, result.totalElements());
        assertEquals(5, result.totalPages());
    }
}
