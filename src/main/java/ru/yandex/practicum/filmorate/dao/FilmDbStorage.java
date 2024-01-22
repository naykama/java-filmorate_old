package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Map<Long, Film> filmMap = new HashMap<>();
    private final Set<Long> existFilmId = new HashSet<>();
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final Map<Integer, Genre> genres = new HashMap<>();
    private final Map<Integer, Mpa> mpas = new HashMap<>();

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet filmSet = jdbcTemplate.queryForRowSet("SELECT film_id FROM films");
        while (filmSet.next()) {
            existFilmId.add(filmSet.getLong("film_id"));
        }
        this.genreDbStorage = genreDbStorage;
        getGenresFromDb();
        this.mpaDbStorage = mpaDbStorage;
        getMpaFromDb();
    }

    @Override
    public Film getFilmById(long filmId) {
        if (!isFilmExist(filmId)) {
            throw new NotFoundException(String.format("Film with id = %d is not found", filmId));
        }
        getAllFilmMap();
        return filmMap.get(filmId);
    }

    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Number filmId = simpleJdbcInsert.executeAndReturnKey(convertFromFilmToMap(film));
        film.setId(filmId.longValue());
        setGenreName(film);
        existFilmId.add(film.getId());
        insertGenresForFilm(film);
        setMpaName(film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!isFilmExist(film.getId())) {
            log.error("Film update failed. There is not film with such id");
            throw new NotFoundException("There is not film with such id");
        }
        jdbcTemplate.update("UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                        " WHERE film_id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        removeGenresForFilm(film);
        insertGenresForFilm(film);
        setGenreName(film);
        setMpaName(film);
        return film;
    }

    public List<Film> getAllFilms() {
        getAllFilmMap();
        return new ArrayList<>(filmMap.values());
    }

    public void addLike(long filmId, long userId) {
        if (!(isFilmExist(filmId) && isFilmExist(userId))) {
            throw new NotFoundException("Film or User with such ids are not exist");
        }
        jdbcTemplate.update("MERGE INTO likes VALUES (?, ?)", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        if (!(isFilmExist(filmId) && isFilmExist(userId))) {
            throw new NotFoundException("Films with such ids are not exist");
        }
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);

    }

    private Map<String, Object> convertFromFilmToMap(Film film) {
        return Map.of("film_name", film.getName(), "description", film.getDescription(),
                "release_date", film.getReleaseDate(), "duration",
                film.getDuration(), "mpa_id", film.getMpa().getId());
    }

    private void removeGenresForFilm(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
    }

    private void insertGenresForFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("MERGE INTO film_genre VALUES(?, ?);", film.getId(), genre.getId());
        }
    }

    public boolean isFilmExist(long id) {
        return existFilmId.contains(id);
    }

    private void getAllFilmMap() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT f.film_id AS film_id,\n" +
                "f.film_name AS film_name,\n" +
                "f.description AS description, \n" +
                "f.release_date AS release_date,\n" +
                "f.duration AS duration,\n" +
                "mp.mpa_id AS mpa_id,\n" +
                "mp.mpa_name AS mpa_name,\n" +
                "g.genre_id AS genre_id,\n" +
                "g.genre_name AS genre_name,\n" +
                "l.user_id AS user_id \n" +
                "FROM films AS f\n" +
                "LEFT OUTER JOIN mpa AS mp ON f.mpa_id = mp.mpa_id\n" +
                "LEFT OUTER JOIN film_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id");
        long newFilmId = 0;
        while (filmRows.next()) {
            if (filmRows.getLong("film_id") != newFilmId) {
                Film film = new Film();
                film.setId(filmRows.getLong("film_id"));
                film.setName(filmRows.getString("film_name"));
                film.setDescription(filmRows.getString("description"));
                film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
                film.setDuration(filmRows.getInt("duration"));
                film.setMpa(new Mpa(filmRows.getInt("mpa_id"), filmRows.getString("mpa_name")));
                newFilmId = film.getId();
                filmMap.put(film.getId(), film);
            }
            if (filmRows.getObject("genre_id") != null) {
                Genre genre = new Genre(filmRows.getInt("genre_id"),
                        filmRows.getString("genre_name"));
                filmMap.get(newFilmId).getGenres().add(genre);
            }
            if (filmRows.getObject("user_id") != null) {
                filmMap.get(newFilmId).getLikes().add(filmRows.getLong("user_id"));
            }
        }
    }

    private void getGenresFromDb() {
        for (Genre genre : genreDbStorage.getAllGenres()) {
            genres.put(genre.getId(), genre);
        }
    }

    private void setGenreName(Film film) {
        for (Genre genre : film.getGenres()) {
            genre.setName(genres.get(genre.getId()).getName());
        }
        Set<Genre> newGenres = new HashSet<>(film.getGenres().stream().sorted().collect(Collectors.toList()));
        film.setGenres(newGenres);
    }

    private void getMpaFromDb() {
        for (Mpa mpa : mpaDbStorage.getAllMpa()) {
            mpas.put(mpa.getId(), mpa);
        }
    }

    private void setMpaName(Film film) {
        film.getMpa().setName(mpas.get(film.getMpa().getId()).getName());
    }
}