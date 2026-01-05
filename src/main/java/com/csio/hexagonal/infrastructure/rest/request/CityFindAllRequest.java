package com.csio.hexagonal.infrastructure.rest.request;

import java.util.List;

public record CityFindAllRequest(
        LogicalOperator operator,
        List<FilterGroup> filterGroups,
        int page,
        int size,
        String search,                   // add this
        List<SortOrder> sort
) {

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
