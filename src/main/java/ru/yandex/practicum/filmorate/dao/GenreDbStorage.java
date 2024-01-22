package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    Map<Integer, Genre> genres = new HashMap<>();

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet genreSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres");
        while (genreSet.next()) {
            genres.put(genreSet.getInt("genre_id"),
                    new Genre(genreSet.getInt("genre_id"), genreSet.getString("genre_name")));
        }

    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre getGenreById(int id) {
        if (!genres.containsKey(id)) {
            throw new NotFoundException(String.format("Genre with id = %d not found", id));
        }
        return genres.get(id);
    }
}
