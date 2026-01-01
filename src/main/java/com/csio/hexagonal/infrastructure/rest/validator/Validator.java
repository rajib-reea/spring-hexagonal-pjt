// package com.csio.hexagonal.infrastructure.rest.validator;

// import org.springframework.stereotype.Component;
// import org.springframework.validation.BeanPropertyBindingResult;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.SmartValidator;
// import org.springframework.web.server.ServerWebInputException;
// import reactor.core.publisher.Mono;

// import java.util.Objects;

// @Component
// public class Validator {

//     private static SmartValidator validator;

//     public static void setValidator(SmartValidator smartValidator) {
//         Validator.validator = smartValidator;
//     }

//     public static <T> Mono<T> validate(T target, String objectName) {
//         if (validator == null) {
//             throw new IllegalStateException(
//                 "Validator is not initialized. Please initialize Validator with a SmartValidator."
//             );
//         }

//         BindingResult result = new BeanPropertyBindingResult(target, objectName);
//         validator.validate(target, result);

//         if (result.hasErrors()) {
//             String errorMessage = Objects
//                     .requireNonNull(result.getAllErrors().get(0).getDefaultMessage());

//             return Mono.error(new ServerWebInputException(errorMessage));
//         }

//         return Mono.just(target);
//     }
// }
package com.csio.hexagonal.infrastructure.rest.validator;

import com.csio.hexagonal.domain.exception.InvalidCityNameException;
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

            // ✅ domain exception, not web exception
            return Mono.error(new InvalidCityNameException(errorMessage));
        }

        return Mono.just(target);
    }
}
