////////////////////////////////////

@Override
public List<City> findAllWithFilters(CityFindAllRequest request, String token) {
try {
// Delegate to pagination if no filterGroups
if (request.filterGroups() == null || request.filterGroups().isEmpty()) {
log.info("No filters provided, delegating to findAllWithPagination");
return findAllWithPagination(
request.page(),
request.size(),
request.search(),
buildSortString(request.sort()), // string still used in findAllWithPagination
token
);
}

        // Build Pageable with proper Sort object
        Pageable pageable = PageRequest.of(request.page(), request.size(), buildSort(request.sort()));

        // Apply search (if provided)
        String search = request.search();
        Page<CityEntity> result;
        if (search == null || search.isBlank()) {
            result = repo.findAll(pageable);
        } else {
            result = repo.findByNameOrState(search, pageable);
        }

        log.info("Result of pagination: page {} of {} with total elements {}",
                result.getNumber(), result.getTotalPages(), result.getTotalElements());

        // If no results, return empty list
        if (result.getTotalPages() == 0) {
            return List.of();
        }
        if (request.page() > result.getTotalPages()) {
            throw new IllegalArgumentException(
                    String.format("Requested page %d exceeds total pages %d", request.page(), result.getTotalPages())
            );
        }

        // Apply filterGroups in memory
        List<CityEntity> filtered = result.getContent().stream()
                .filter(entity -> request.filterGroups().stream().allMatch(group -> evaluateGroup(entity, group)))
                .toList();

        // Map to domain model
        return filtered.stream().map(CityMapper::toModel).toList();

    } catch (DataAccessException ex) {
        log.error("Database error while fetching cities with filters", ex);
        throw new DatabaseException("Failed to fetch filtered cities", ex);
    }
}

    // Evaluate one group (AND/OR logic)
    private static boolean evaluateGroup(CityEntity entity, CityFindAllRequest.FilterGroup group) {
        if (group.operator() == CityFindAllRequest.LogicalOperator.AND) {
            return group.conditions().stream().allMatch(cond -> isConditionResult(entity, cond));
        } else { // OR
            return group.conditions().stream().anyMatch(cond -> isConditionResult(entity, cond));
        }
    }

    // Check a single condition
    private static boolean isConditionResult(CityEntity entity, CityFindAllRequest.FilterCondition condition) {
        return switch (condition.field()) {
            case "state" -> entity.getState().equalsIgnoreCase(condition.value());
            case "active" -> entity.getIsActive().toString().equalsIgnoreCase(condition.value());
            case "name" -> {
                if (condition.operator() == CityFindAllRequest.Operator.LIKE) {
                    yield entity.getName().toLowerCase().contains(condition.value().toLowerCase());
                } else { // EQUALS
                    yield entity.getName().equalsIgnoreCase(condition.value());
                }
            }
            default -> true;
        };
    }

    // Build Sort object for PageRequest
    private Sort buildSort(List<CityFindAllRequest.SortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return Sort.by("name").ascending();
        }
        var first = sortOrders.getFirst();
        return first.direction() == CityFindAllRequest.Direction.ASC
                ? Sort.by(first.field()).ascending()
                : Sort.by(first.field()).descending();
    }

    // Keep this if findAllWithPagination still expects string sort
    private String buildSortString(List<CityFindAllRequest.SortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) return "name,asc";
        var first = sortOrders.getFirst();
        return first.field() + "," + first.direction().name().toLowerCase();
    }