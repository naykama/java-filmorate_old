package ru.yandex.practicum.filmorate.annotation;

import lombok.Getter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class AfterOpenDateValidator implements ConstraintValidator<AfterOpenDate, LocalDate> {
    @Getter
    private static final LocalDate OPEN_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !value.isBefore(OPEN_DATE);
    }
}
