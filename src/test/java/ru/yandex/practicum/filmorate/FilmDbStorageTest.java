package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
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

public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final Film newFilm = new Film();
    private FilmDbStorage filmStorage;
    private MpaDbStorage mpaDbStorage;

    @BeforeEach
    public void setFilm() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        mpaDbStorage.getAllMpa();
        filmStorage = new FilmDbStorage(jdbcTemplate, genreDbStorage, mpaDbStorage);
        newFilm.setName("newFilm");
        newFilm.setDescription("newFilmDesc");
        newFilm.setReleaseDate(LocalDate.of(1990, 1, 1));
        newFilm.setDuration(50);
        newFilm.setMpa(new Mpa(1,"G"));
    }

    private Film createDopFilm() {
        Film secondFilm = new Film();
        secondFilm.setName("secondFilm");
        secondFilm.setDescription("secondFilmDesc");
        secondFilm.setReleaseDate(LocalDate.of(1990, 1, 1));
        secondFilm.setDuration(70);
        secondFilm.setMpa(new Mpa(1,"G"));
        return secondFilm;
    }

    @Test
    public void testCreateFilm() {
        System.out.println("MPaAll: "+ mpaDbStorage.getAllMpa());
        filmStorage.createFilm(newFilm);
        Film savedFilm = filmStorage.getFilmById(1);
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }

    @Test
    public void testNotFoundException() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {filmStorage.getFilmById(1); })
                .withMessage("Film with id = 1 is not found");
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {filmStorage.updateFilm(newFilm); })
                .withMessage("There is not film with such id");
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {filmStorage.addLike(1,1); })
                .withMessage("Film or User with such ids are not exist");
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.createFilm(newFilm);
        Film updatedFilm = createDopFilm();
        updatedFilm.setId(1);
        filmStorage.updateFilm(updatedFilm);
        Film savedFilm = filmStorage.getFilmById(1);
        assertThat(savedFilm.getName())
                .isEqualTo("secondFilm");
    }

    @Test
    public void testGetAllFilms() {
        assertThat(filmStorage.getAllFilms().size())
                .isEqualTo(0);
        filmStorage.createFilm(newFilm);
        assertThat(filmStorage.getAllFilms().size())
                .isEqualTo(1);
        filmStorage.createFilm(createDopFilm());
        assertThat(filmStorage.getAllFilms().size())
                .isEqualTo(2);
    }

    @Test
    public void testIsFilmExist() {
        assertThat(filmStorage.isFilmExist(1))
                .isFalse();
        filmStorage.createFilm(newFilm);
        assertThat(filmStorage.isFilmExist(1))
                .isTrue();
    }

    @Test
    public void testAddLike() {
        filmStorage.createFilm(newFilm);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User user = new User();
        user.setEmail("fff@mail.ru");
        user.setLogin("fff");
        user.setBirthday(LocalDate.of(2004, 12,5));
        userStorage.createUser(user);
        filmStorage.addLike(1,1);
        assertThat(filmStorage.getFilmById(1).getLikes().size())
                .isEqualTo(1);
    }

    @Test
    public void testRemoveLike() {
        filmStorage.createFilm(newFilm);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User user = new User();
        user.setEmail("fff@mail.ru");
        user.setLogin("fff");
        user.setBirthday(LocalDate.of(2004, 12,5));
        userStorage.createUser(user);
        filmStorage.addLike(1,1);
        assertThat(filmStorage.getFilmById(1).getLikes().size())
                .isEqualTo(1);
        filmStorage.removeLike(1,1);
        assertThat(filmStorage.getFilmById(1).getLikes().size())
                .isEqualTo(0);
    }
}
