package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;

/**
 * Maps between infrastructure DTOs and application/domain models
 */
public final class CityDtoMapper {

    private CityDtoMapper() {}

    /**
     * Convert domain model to infrastructure response DTO
     */
    public static CityResponse toResponse(City city) {
        return new CityResponse(
                city.getId().value().toString(),
                city.isActive(),
                city.getName(),
                city.getState().value()
        );
    }
    
    /**
     * Convert application PageResult to infrastructure PageResponseWrapper
     */
    public static <T> PageResponseWrapper<T> toPageResponseWrapper(PageResult<T> pageResult) {
        return new PageResponseWrapper<>(
                200,
                new PageResponseWrapper.Meta(
                        pageResult.page(),
                        pageResult.size(),
                        (long) (pageResult.page() - 1) * pageResult.size(),
                        pageResult.totalElements(),
                        pageResult.totalPages()
                ),
                pageResult.content()
        );
    }
}
