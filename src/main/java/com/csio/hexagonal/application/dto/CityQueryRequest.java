package com.csio.hexagonal.application.dto;

import java.util.List;

public record CityQueryRequest(
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
