package com.csio.hexagonal.infrastructure.rest.validator;

import com.csio.hexagonal.infrastructure.rest.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class Validator {

    private static SmartValidator validator;

    public Validator(SmartValidator smartValidator) {
        Validator.validator = smartValidator;
    }

    public static <T> Mono<T> validate(T target, String objectName) {

        BindingResult result = new BeanPropertyBindingResult(target, objectName);
        validator.validate(target, result);

        if (result.hasErrors()) {
            String errorMessage = Objects
                    .requireNonNull(result.getAllErrors().get(0).getDefaultMessage());

            // ✅ REST layer exception, not domain exception
            return Mono.error(new ValidationException(errorMessage));
        }

        return Mono.just(target);
    }
}
