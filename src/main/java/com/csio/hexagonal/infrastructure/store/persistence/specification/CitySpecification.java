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
            CityFindAllRequest.Filter filter
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            StringBuilder predicateStr = new StringBuilder();

            /* ---------------- SEARCH ---------------- */
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";

                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("state")), pattern)
                );

                predicate = cb.and(predicate, searchPredicate);

                String searchStr =
                        "(name LIKE '%" + search + "%' OR state LIKE '%" + search + "%')";
                log.info("Search Predicate: {}", searchStr);
                predicateStr.append(searchStr);
            }

            /* ---------------- FILTER GROUPS ---------------- */
            if (filter != null && filter.filterGroups() != null && !filter.filterGroups().isEmpty()) {

                Predicate combinedGroupPredicate;
                StringBuilder groupStr = new StringBuilder();

                if (filter.operator() == CityFindAllRequest.LogicalOperator.AND) {
                    combinedGroupPredicate = cb.conjunction();

                    for (int i = 0; i < filter.filterGroups().size(); i++) {
                        CityFindAllRequest.FilterGroup group = filter.filterGroups().get(i);
                        Predicate groupPredicate = buildGroupPredicate(group, root, cb);

                        combinedGroupPredicate = cb.and(combinedGroupPredicate, groupPredicate);

                        groupStr.append("(").append(groupPredicateToString(group)).append(")");
                        if (i < filter.filterGroups().size() - 1) {
                            groupStr.append(" AND ");
                        }
                    }
                } else {
                    combinedGroupPredicate = cb.disjunction();

                    for (int i = 0; i < filter.filterGroups().size(); i++) {
                        CityFindAllRequest.FilterGroup group = filter.filterGroups().get(i);
                        Predicate groupPredicate = buildGroupPredicate(group, root, cb);

                        combinedGroupPredicate = cb.or(combinedGroupPredicate, groupPredicate);

                        groupStr.append("(").append(groupPredicateToString(group)).append(")");
                        if (i < filter.filterGroups().size() - 1) {
                            groupStr.append(" OR ");
                        }
                    }
                }

                predicate = cb.and(predicate, combinedGroupPredicate);

                if (!predicateStr.isEmpty()) {
                    predicateStr.append(" AND ");
                }
                predicateStr.append("(").append(groupStr).append(")");
            }

            log.info("Combined Predicate: {}", predicateStr);
            return predicate;
        };
    }

    /* ---------------- GROUP ---------------- */
    private static Predicate buildGroupPredicate(
            CityFindAllRequest.FilterGroup group,
            Root<CityEntity> root,
            CriteriaBuilder cb
    ) {
        Predicate groupPredicate;

        if (group.operator() == CityFindAllRequest.LogicalOperator.AND) {
            groupPredicate = cb.conjunction();
            for (CityFindAllRequest.FilterCondition cond : group.conditions()) {
                log.info("AND Condition: {}", conditionToString(cond));
                groupPredicate = cb.and(groupPredicate, buildConditionPredicate(cond, root, cb));
            }
        } else {
            groupPredicate = cb.disjunction();
            for (CityFindAllRequest.FilterCondition cond : group.conditions()) {
                log.info("OR Condition: {}", conditionToString(cond));
                groupPredicate = cb.or(groupPredicate, buildConditionPredicate(cond, root, cb));
            }
        }

        log.info("Group Predicate: {}", groupPredicateToString(group));
        return groupPredicate;
    }

    /* ---------------- CONDITION ---------------- */
    private static Predicate buildConditionPredicate(
            CityFindAllRequest.FilterCondition condition,
            Root<CityEntity> root,
            CriteriaBuilder cb
    ) {
        String fieldName = mapFilterFieldToEntityField(condition.field());
        Path<?> path = root.get(fieldName);
        Class<?> javaType = path.getJavaType();

        // ✅ Boolean handling (IMPORTANT FIX)
        if (javaType == Boolean.class || javaType == boolean.class) {
            Boolean boolValue = Boolean.parseBoolean(condition.value());

            return switch (condition.operator()) {
                case EQUALS -> cb.equal(path, boolValue);
                default -> throw new IllegalArgumentException(
                        "Operator " + condition.operator() + " not supported for Boolean field"
                );
            };
        }

        // ✅ String handling
        if (javaType == String.class) {
            String value = condition.value().toLowerCase();

            return switch (condition.operator()) {
                case EQUALS -> cb.equal(cb.lower(path.as(String.class)), value);
                case LIKE -> cb.like(cb.lower(path.as(String.class)), "%" + value + "%");
                case GT -> cb.greaterThan(path.as(String.class), value);
                case GTE -> cb.greaterThanOrEqualTo(path.as(String.class), value);
                case LT -> cb.lessThan(path.as(String.class), value);
                case LTE -> cb.lessThanOrEqualTo(path.as(String.class), value);
            };
        }

        throw new IllegalArgumentException(
                "Unsupported field type: " + javaType
        );
    }

    /* ---------------- HELPERS ---------------- */
    private static String groupPredicateToString(CityFindAllRequest.FilterGroup group) {
        StringBuilder sb = new StringBuilder("(");
        String op = group.operator().name();

        for (int i = 0; i < group.conditions().size(); i++) {
            sb.append(conditionToString(group.conditions().get(i)));
            if (i < group.conditions().size() - 1) {
                sb.append(" ").append(op).append(" ");
            }
        }
        sb.append(")");
        return sb.toString();
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
