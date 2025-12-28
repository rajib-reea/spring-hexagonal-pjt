
package com.csio.hexagonal.application.port.in;

import com.csio.hexagonal.application.usecase.CreateCityCommand;

public interface CreateCityUseCase {
    void create(CreateCityCommand command);
}
