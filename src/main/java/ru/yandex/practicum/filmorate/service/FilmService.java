package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final int DEFAULT_FILM_COUNT = 10;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        if (!(userStorage.isUserExist(userId) && filmStorage.isFilmExist(filmId))) {
            throw new NotFoundException("Film or user with such id not found");
        }
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getBestFilms(Integer count) {
        int filmsCount = count == null ? DEFAULT_FILM_COUNT : count;
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(filmsCount)
                .collect(Collectors.toList());
    }
}
