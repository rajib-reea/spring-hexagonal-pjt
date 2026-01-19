package com.csio.hexagonal.infrastructure.rest.response.helper;

import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.SuccessResponseWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseHelperTest {

    @Test
    void shouldCreateSuccessResponseWrapper() {
        // Arrange
        String data = "Test Data";

        // Act
        SuccessResponseWrapper<String> result = ResponseHelper.success(data);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.status());
        assertEquals(data, result.data());
    }

    @Test
    void shouldCreateSuccessResponseWrapperWithComplexObject() {
        // Arrange
        TestDto dto = new TestDto("id123", "Test Name");

        // Act
        SuccessResponseWrapper<TestDto> result = ResponseHelper.success(dto);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.status());
        assertEquals(dto, result.data());
    }

    @Test
    void shouldCreatePageResponseWrapperFromSpringPage() {
        // Arrange
        List<String> content = Arrays.asList("Item1", "Item2", "Item3");
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(content, pageable, 3);

        // Act
        PageResponseWrapper<String> result = ResponseHelper.page(page);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.status());
        assertEquals(1, result.meta().page());
        assertEquals(10, result.meta().size());
        assertEquals(0L, result.meta().offset());
        assertEquals(3L, result.meta().totalElements());
        assertEquals(1, result.meta().totalPages());
        assertEquals(3, result.data().size());
    }

    @Test
    void shouldCreatePageResponseWrapperForPage2() {
        // Arrange
        List<String> content = Arrays.asList("Item4", "Item5");
        Pageable pageable = PageRequest.of(1, 3);
        Page<String> page = new PageImpl<>(content, pageable, 5);

        // Act
        PageResponseWrapper<String> result = ResponseHelper.page(page);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.meta().page());
        assertEquals(3, result.meta().size());
        assertEquals(3L, result.meta().offset());
        assertEquals(5L, result.meta().totalElements());
        assertEquals(2, result.meta().totalPages());
    }

    @Test
    void shouldCreateEmptyPageResponseWrapper() {
        // Arrange
        List<String> content = List.of();
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(content, pageable, 0);

        // Act
        PageResponseWrapper<String> result = ResponseHelper.page(page);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.status());
        assertEquals(0L, result.meta().totalElements());
        assertTrue(result.data().isEmpty());
    }

    private record TestDto(String id, String name) {}
}
