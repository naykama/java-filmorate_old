package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void invalidLoginShouldFailValidation() {
        User user = new User();
        user.setEmail("wwo@mal.ru");
        List<ConstraintViolation<User>>  violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 1);
        user.setLogin("Vladimir Kim");
        violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Login has space");
        user.setLogin(" ");
        violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 2);
    }

    @Test
    public void invalidEmailShouldFailValidation() {
        User user = new User();
        user.setLogin("VladimirKim");
        List<ConstraintViolation<User>>  violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 1);
        System.out.println(violations);
        user.setEmail("dfft@");
        violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Incorrect email");
    }

    @Test
    public void invalidBirthdayShouldFailValidation() {
        User user = new User();
        user.setLogin("VladimirKim");
        user.setEmail("wwo@mal.ru");
        user.setBirthday(LocalDate.now());
        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user));
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getMessage(), "Incorrect date of birth");
    }
}
