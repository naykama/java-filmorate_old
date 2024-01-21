package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
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

public class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaDbStorage mpaDbStorage;

    @BeforeEach
    public void createGenre() {
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    public void testGetAllGenres() {
        List<Mpa> mpa = mpaDbStorage.getAllMpa();
        assertThat(mpa.size())
                .isEqualTo(5);
    }

    @Test
    public void testGetGenreById() {
        Mpa trueMpa = mpaDbStorage.getMpaById(1);
        assertThat(trueMpa)
                .isEqualTo(new Mpa(1, "G"));

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    mpaDbStorage.getMpaById(10); })
                .withMessage("Mpa with id = 10 not found");
    }
}

