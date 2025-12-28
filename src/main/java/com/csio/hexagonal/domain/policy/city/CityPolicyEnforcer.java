
ackage com.csio.hexagonal.domain.policy.city;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.service.CityUniquenessChecker;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CityPolicyEnforcer implements CityPolicy {

    @Override
    public void ensureUnique(City city, List<City> existingCities) {
        boolean exists = existingCities.stream()
                .anyMatch(c -> c.name().equalsIgnoreCase(city.name()));
        if (exists) {
            throw new DuplicateCityException(city.name());
        }
    }
}
