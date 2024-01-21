package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@JdbcTest
@Sql(
        scripts = "../../../../schema.sql"
)
@Sql(
        scripts = "../../../../data.sql"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @BeforeEach
    public void createGenre() {
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertThat(genres.size())
                .isEqualTo(6);
    }

    @Test
    public void testGetGenreById() {
        Genre trueGenre = genreDbStorage.getGenreById(1);
        assertThat(trueGenre)
                .isEqualTo(new Genre(1, "Комедия"));

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    genreDbStorage.getGenreById(10); })
                .withMessage("Genre with id = 10 not found");
    }

}
