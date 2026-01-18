package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.application.dto.CityQueryRequest;
import com.csio.hexagonal.application.dto.PageResult;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between infrastructure DTOs and application/domain models
 */
public final class CityDtoMapper {

    private CityDtoMapper() {}

    /**
     * Convert infrastructure request DTO to application DTO
     */
    public static CityQueryRequest toApplicationRequest(CityFindAllRequest infraRequest) {
        CityQueryRequest.Filter appFilter = null;
        
        if (infraRequest.filter() != null) {
            appFilter = new CityQueryRequest.Filter(
                    CityQueryRequest.LogicalOperator.valueOf(infraRequest.filter().operator().name()),
                    infraRequest.filter().filterGroups().stream()
                            .map(CityDtoMapper::toApplicationFilterGroup)
                            .collect(Collectors.toList())
            );
        }
        
        List<CityQueryRequest.SortOrder> appSort = null;
        if (infraRequest.sort() != null) {
            appSort = infraRequest.sort().stream()
                    .map(s -> new CityQueryRequest.SortOrder(
                            s.field(),
                            CityQueryRequest.Direction.valueOf(s.direction().name())
                    ))
                    .collect(Collectors.toList());
        }
        
        return new CityQueryRequest(
                appFilter,
                infraRequest.page(),
                infraRequest.size(),
                infraRequest.search(),
                appSort
        );
    }
    
    private static CityQueryRequest.FilterGroup toApplicationFilterGroup(CityFindAllRequest.FilterGroup infraGroup) {
        return new CityQueryRequest.FilterGroup(
                CityQueryRequest.LogicalOperator.valueOf(infraGroup.operator().name()),
                infraGroup.conditions().stream()
                        .map(CityDtoMapper::toApplicationFilterCondition)
                        .collect(Collectors.toList())
        );
    }
    
    private static CityQueryRequest.FilterCondition toApplicationFilterCondition(CityFindAllRequest.FilterCondition infraCondition) {
        return new CityQueryRequest.FilterCondition(
                infraCondition.field(),
                CityQueryRequest.Operator.valueOf(infraCondition.operator().name()),
                infraCondition.value()
        );
    }
    
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
