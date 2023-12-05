package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.AfterOpenDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
@Data
public class Film {
    private int id;
    @NotBlank(message = "Film without name")
    private String name;
    @Size(max = 200, message = "Film description contains more than 200 characters")
    private String description;
    @AfterOpenDate
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Film duration is negative")
    private int duration;
}
