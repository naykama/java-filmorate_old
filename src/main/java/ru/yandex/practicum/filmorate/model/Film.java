package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.AfterOpenDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Film without name")
    private String name;
    @Size(max = 200, message = "Film description contains more than 200 characters")
    private String description;
    @AfterOpenDate
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Film duration is negative")
    private int duration;
    private Set<Long> likes = new HashSet<>();
}
