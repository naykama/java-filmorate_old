package ru.yandex.practicum.filmorate;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
//
//    private static Validator validator;
//
//    @BeforeAll
//    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    public void InvalidNameShouldFailValidation() {
//        Film film = new Film();
//        film.setDescription("Test for invalid name");
//        film.setReleaseDate(LocalDate.of(1967,3,25));
//        film.setDuration(100);
//        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
//        assertEquals(violations.size(), 1);
//        assertEquals(violations.get(0).getMessage(), "Film without name");
//    }
}
