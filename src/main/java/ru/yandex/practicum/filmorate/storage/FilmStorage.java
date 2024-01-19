package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(long id);

    boolean isFilmExist(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
