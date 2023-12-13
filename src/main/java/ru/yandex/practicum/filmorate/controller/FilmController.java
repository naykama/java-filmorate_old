package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.AfterOpenDateValidator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    private static final int DEFAULT_FILM_COUNT = 10;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (postRequestIsValid(film)) {
            filmStorage.create(film);
            log.debug("Film {} was created", film);
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping
    public List<Film> get() {
        return filmStorage.get();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
        log.debug("addLike: " + filmStorage.getFilmById(2).toString());
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(required = false) Integer count) {
        if (count == null) {
            return filmService.getBestFilms(DEFAULT_FILM_COUNT);
        } else {
            return filmService.getBestFilms(count);
        }
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        return filmStorage.getFilmById(id);
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