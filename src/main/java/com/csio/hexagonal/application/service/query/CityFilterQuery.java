package com.csio.hexagonal.application.service.query;

import java.util.List;

/**
 * Domain query object for filtering and paginating cities.
 * This belongs to the application layer and represents the filtering/pagination
 * requirements independent of any infrastructure concerns.
 */
public record CityFilterQuery(
        Filter filter,
        int page,
        int size,
        String search,
        List<SortOrder> sort
) {

    public record Filter(
            LogicalOperator operator,
            List<FilterGroup> filterGroups
    ) {}

    public record FilterGroup(
            LogicalOperator operator,
            List<FilterCondition> conditions
    ) {}

    public record FilterCondition(
            String field,
            Operator operator,
            String value
    ) {}

    public record SortOrder(
            String field,
            Direction direction
    ) {}

    public enum LogicalOperator { AND, OR }

    public enum Operator { EQUALS, LIKE, GT, LT, GTE, LTE }

    public enum Direction { ASC, DESC }
}
