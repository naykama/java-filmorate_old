package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @NotBlank
    @Email(message = "Incorrect email")
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S++", message = "Login has space")
    private String login;
    private String name;
    @Past(message = "Incorrect date of birth")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
