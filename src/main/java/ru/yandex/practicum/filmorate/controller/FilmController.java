package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.AfterOpenDateValidator;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (postRequestIsValid(film)) {
            filmService.createFilm(film);
            log.debug("Film {} was created", film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
        log.debug("addLike: filmId = %d, userId = %d", filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(required = false) Integer count) {
        return filmService.getBestFilms(count);
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