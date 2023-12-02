package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

//import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
public class Film {
    private int id;
//    @NotNull(message = "Film without name")
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

}
