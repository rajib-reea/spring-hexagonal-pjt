package com.csio.hexagonal.infrastructure.store.persistence.specification;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CitySpecification {

    /**
     * Build full Specification based on search string + filter groups
     */
    public static Specification<CityEntity> buildSpecification(
            String search,
            List<CityFindAllRequest.FilterGroup> filterGroups
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Apply search (name OR state LIKE %search%)
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("state")), pattern)
                );
                predicate = cb.and(predicate, searchPredicate);
            }

            // Apply filter groups
            if (filterGroups != null && !filterGroups.isEmpty()) {
                for (CityFindAllRequest.FilterGroup group : filterGroups) {
                    Predicate groupPredicate = buildGroupPredicate(group, root, cb);
                    predicate = cb.and(predicate, groupPredicate); // combine groups with AND
                }
            }

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
            }
        } else { // OR
            groupPredicate = cb.disjunction();
            for (CityFindAllRequest.FilterCondition cond : conditions) {
                groupPredicate = cb.or(groupPredicate, buildConditionPredicate(cond, root, cb));
            }
        }
        return groupPredicate;
    }

    /**
     * Build Predicate for one FilterCondition
     */
    private static Predicate buildConditionPredicate(
            CityFindAllRequest.FilterCondition condition,
            Root<CityEntity> root,
            CriteriaBuilder cb
    ) {
        Path<String> path = root.get(condition.field());

        return switch (condition.operator()) {
            case EQUALS -> cb.equal(cb.lower(path), condition.value().toLowerCase());
            case LIKE -> cb.like(cb.lower(path), "%" + condition.value().toLowerCase() + "%");
            case GT -> cb.greaterThan(path, condition.value());
            case GTE -> cb.greaterThanOrEqualTo(path, condition.value());
            case LT -> cb.lessThan(path, condition.value());
            case LTE -> cb.lessThanOrEqualTo(path, condition.value());
        };
    }
}
