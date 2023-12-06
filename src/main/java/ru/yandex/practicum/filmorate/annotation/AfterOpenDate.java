package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@Target({ METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { AfterOpenDateValidator.class })
public @interface AfterOpenDate {
    String message() default "Incorrect release date";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
