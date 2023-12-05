package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class AfterOpenDateValidator implements ConstraintValidator<AfterOpenDate, LocalDate> {
private final LocalDate openDate = LocalDate.of(1895, Month.DECEMBER, 28);
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return ! value.isBefore(openDate);
    }
}
