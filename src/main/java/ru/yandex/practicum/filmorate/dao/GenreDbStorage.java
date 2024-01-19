package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        SqlRowSet genreSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres");
        List<Genre> genreList = new ArrayList<>();
        while (genreSet.next()) {
            genreList.add(new Genre(genreSet.getInt("genre_id"), genreSet.getString("genre_name")));
        }
        return genreList;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet genreSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", id);
        if (!genreSet.next()) {
            throw new NotFoundException(String.format("Genre with id = %d not found", id));
        }
        return new Genre(genreSet.getInt("genre_id"), genreSet.getString("genre_name"));
    }
}
