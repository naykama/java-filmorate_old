package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void invalidNameShouldFailValidation() {
        Film film = new Film();
        film.setDescription("Test for invalid name");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Film without name");
        film.setName("   ");
        violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Film without name");
    }

    @Test
    public void toLongDescriptionShouldFailValidation() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(100);
        StringBuilder toLongDescription = new StringBuilder("o");
        for (int i = 0; i < 200; i++) {
            toLongDescription.append("o");
        }
        film.setDescription(toLongDescription.toString());
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Film description contains more than 200 characters");
    }

    @Test
    public void invalidReleaseDateShouldFailValidation() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1660,3,25));
        film.setDuration(100);
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Incorrect release date");
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28));
        violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 0);
    }

    @Test
    public void invalidDurationShouldFailValidation() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1967,3,25));
        film.setDuration(0);
        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 0);
        film.setDuration(-1);
        violations = new ArrayList<>(validator.validate(film));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Film duration is negative");
    }
}
