
package dev.educosta.application.port.in;

import dev.educosta.application.usecase.CreateCityCommand;

public interface CreateCityUseCase {
    void create(CreateCityCommand command);
}
