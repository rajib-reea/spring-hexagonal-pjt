package com.csio.hexagonal.infrastructure.store.persistence.specification;

import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CitySpecificationTest {

    @Test
    void shouldBuildSpecificationWithSearchOnly() {
        // Arrange
        String search = "New York";

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(search, null);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithNullSearch() {
        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, null);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithBlankSearch() {
        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification("   ", null);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithFilterEqualsCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "New York");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithFilterLikeCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.LIKE, "New");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithFilterGreaterThanCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.GT, "A");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithMultipleConditionsAndOperator() {
        // Arrange
        CityFilterQuery.FilterCondition condition1 = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "New York");
        CityFilterQuery.FilterCondition condition2 = new CityFilterQuery.FilterCondition(
                "state", CityFilterQuery.Operator.EQUALS, "NY");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition1, condition2));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithMultipleConditionsOrOperator() {
        // Arrange
        CityFilterQuery.FilterCondition condition1 = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "New York");
        CityFilterQuery.FilterCondition condition2 = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "Los Angeles");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.OR, List.of(condition1, condition2));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.OR, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithMultipleFilterGroups() {
        // Arrange
        CityFilterQuery.FilterCondition condition1 = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "New York");
        CityFilterQuery.FilterGroup group1 = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition1));
        
        CityFilterQuery.FilterCondition condition2 = new CityFilterQuery.FilterCondition(
                "state", CityFilterQuery.Operator.EQUALS, "CA");
        CityFilterQuery.FilterGroup group2 = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition2));
        
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.OR, List.of(group1, group2));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithSearchAndFilter() {
        // Arrange
        String search = "New";
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "state", CityFilterQuery.Operator.EQUALS, "NY");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(search, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithBooleanFieldCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "active", CityFilterQuery.Operator.EQUALS, "true");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithLessThanCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.LT, "Z");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithGreaterThanOrEqualCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.GTE, "A");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void shouldBuildSpecificationWithLessThanOrEqualCondition() {
        // Arrange
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.LTE, "Z");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));

        // Act
        Specification<CityEntity> spec = CitySpecification.buildSpecification(null, filter);

        // Assert
        assertNotNull(spec);
    }
}
