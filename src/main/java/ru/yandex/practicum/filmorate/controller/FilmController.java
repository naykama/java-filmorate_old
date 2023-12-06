package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.AfterOpenDateValidator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
    if (postRequestIsValid(film)) {
        film.setId(id);
        films.put(id++, film);
        log.debug("Film {} was created", film);
    }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film update failed. There is not film with such id");
            throw new ValidationException("There is not film with such id");
        }
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

    private boolean postRequestIsValid(Film film) {
        final LocalDate filmBirthDate = AfterOpenDateValidator.getOPEN_DATE();
        if (film.getName().isBlank()) {
            log.error("Film create failed. Film without name");
            throw new ValidationException("Film without name");
        } else if (film.getDescription().length() > 200) {
            log.error("Film create failed. Film description contains more than 200 characters");
            throw new ValidationException("Film description contains more than 200 characters");
        } else if (film.getReleaseDate().isBefore(filmBirthDate)) {
            log.error("Film create failed. Incorrect release date");
            throw new ValidationException("Incorrect release date");
        } else if (film.getDuration() < 0) {
            log.error("Film create failed. Duration is negative");
            throw new ValidationException("Film duration is negative");
        } else {
            return true;
        }
    }
}