package com.csio.hexagonal.infrastructure.store.persistence.specification;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CitySpecification {

    private static final Logger log = LoggerFactory.getLogger(CitySpecification.class);

    /**
     * Build full Specification based on search string + filter groups
     */
    public static Specification<CityEntity> buildSpecification(
            String search,
            CityFindAllRequest.Filter filter // changed from List<FilterGroup> to Filter
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            StringBuilder predicateStr = new StringBuilder();

            // Apply search
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("state")), pattern)
                );
                predicate = cb.and(predicate, searchPredicate);
                String searchStr = String.format("(name LIKE '%%%s%%' OR state LIKE '%%%s%%')", search, search);
                log.info("Search Predicate: {}", searchStr);
                predicateStr.append(searchStr);
            }

            // Apply filter groups if they exist
            if (filter != null && filter.filterGroups() != null && !filter.filterGroups().isEmpty()) {
                Predicate combinedGroupPredicate;
                StringBuilder groupStr = new StringBuilder();

                // Use top-level operator from filter (AND / OR)
                CityFindAllRequest.LogicalOperator topOperator = filter.operator();

                if (topOperator == CityFindAllRequest.LogicalOperator.AND) {
                    combinedGroupPredicate = cb.conjunction();
                    for (int i = 0; i < filter.filterGroups().size(); i++) {
                        CityFindAllRequest.FilterGroup group = filter.filterGroups().get(i);
                        Predicate groupPredicate = buildGroupPredicate(group, root, cb);
                        combinedGroupPredicate = cb.and(combinedGroupPredicate, groupPredicate);

                        groupStr.append("(").append(groupPredicateToString(group)).append(")");
                        if (i < filter.filterGroups().size() - 1) groupStr.append(" AND ");
                    }
                } else { // OR at top-level
                    combinedGroupPredicate = cb.disjunction();
                    for (int i = 0; i < filter.filterGroups().size(); i++) {
                        CityFindAllRequest.FilterGroup group = filter.filterGroups().get(i);
                        Predicate groupPredicate = buildGroupPredicate(group, root, cb);
                        combinedGroupPredicate = cb.or(combinedGroupPredicate, groupPredicate);

                        groupStr.append("(").append(groupPredicateToString(group)).append(")");
                        if (i < filter.filterGroups().size() - 1) groupStr.append(" OR ");
                    }
                }

                // Combine search AND filter groups
                predicate = cb.and(predicate, combinedGroupPredicate);
                if (!predicateStr.isEmpty()) predicateStr.append(" AND ");
                predicateStr.append("(").append(groupStr).append(")");
            }

            log.info("Combined Predicate: {}", predicateStr);
            return predicate;
        };
    }

    /**
     * Build Predicate for one FilterGroup
     */
    private static Predicate buildGroupPredicate(
            CityFindAllRequest.FilterGroup group,
            Root<CityEntity> root,
            CriteriaBuilder cb
    ) {
        List<CityFindAllRequest.FilterCondition> conditions = group.conditions();
        Predicate groupPredicate;

        if (group.operator() == CityFindAllRequest.LogicalOperator.AND) {
            groupPredicate = cb.conjunction();
            for (CityFindAllRequest.FilterCondition cond : conditions) {
                groupPredicate = cb.and(groupPredicate, buildConditionPredicate(cond, root, cb));
                log.info("AND Condition: {}", conditionToString(cond));
            }
        } else { // OR
            groupPredicate = cb.disjunction();
            for (CityFindAllRequest.FilterCondition cond : conditions) {
                groupPredicate = cb.or(groupPredicate, buildConditionPredicate(cond, root, cb));
                log.info("OR Condition: {}", conditionToString(cond));
            }
        }
        log.info("Group Predicate: {}", groupPredicateToString(group));
        return groupPredicate;
    }

    /**
     * Convert FilterGroup to readable string
     */
    private static String groupPredicateToString(CityFindAllRequest.FilterGroup group) {
        StringBuilder sb = new StringBuilder();
        String op = group.operator().name();
        sb.append("(");
        for (int i = 0; i < group.conditions().size(); i++) {
            sb.append(conditionToString(group.conditions().get(i)));
            if (i < group.conditions().size() - 1) sb.append(" ").append(op).append(" ");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Build Predicate for one FilterCondition with correct entity field mapping
     */
    private static Predicate buildConditionPredicate(
            CityFindAllRequest.FilterCondition condition,
            Root<CityEntity> root,
            CriteriaBuilder cb
    ) {
        String fieldName = mapFilterFieldToEntityField(condition.field());
        Path<?> path = root.get(fieldName);

        return switch (condition.operator()) {
            case EQUALS -> cb.equal(cb.lower(path.as(String.class)), condition.value().toLowerCase());
            case LIKE -> cb.like(cb.lower(path.as(String.class)), "%" + condition.value().toLowerCase() + "%");
            case GT -> cb.greaterThan(path.as(String.class), condition.value());
            case GTE -> cb.greaterThanOrEqualTo(path.as(String.class), condition.value());
            case LT -> cb.lessThan(path.as(String.class), condition.value());
            case LTE -> cb.lessThanOrEqualTo(path.as(String.class), condition.value());
        };
    }

    private static String conditionToString(CityFindAllRequest.FilterCondition cond) {
        return cond.field() + " " + cond.operator() + " '" + cond.value() + "'";
    }

    private static String mapFilterFieldToEntityField(String filterField) {
        return switch (filterField) {
            case "active" -> "isActive";
            default -> filterField;
        };
    }
}
