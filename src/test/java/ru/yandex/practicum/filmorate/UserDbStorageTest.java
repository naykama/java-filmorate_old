package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@JdbcTest
@Sql(
        scripts = "../../../../schema.sql",
        executionPhase = AFTER_TEST_METHOD
)
@Sql(
        scripts = "../../../../data.sql",
        executionPhase = AFTER_TEST_METHOD
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final User newUser = new User();
    private UserDbStorage userStorage;

    @BeforeEach
    public void setUser() {
        userStorage = new UserDbStorage(jdbcTemplate);
        newUser.setEmail("user@email.ru");
        newUser.setLogin("vanya123");
        newUser.setName("Ivan Petrov");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    private User createDopUser() {
        User secondUser = new User();
        secondUser.setEmail("user2@email.ru");
        secondUser.setLogin("Andrey123");
        secondUser.setName("Andrey Petrov");
        secondUser.setBirthday(LocalDate.of(2000, 1, 2));
        return secondUser;
    }

    @Test
    public void testGetUserById() {
        userStorage.createUser(newUser);
        System.out.println("testGetUserById AllUsers: " + userStorage.getAllUsers());
        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testNotFoundException() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    User notSavedUser = userStorage.getUserById(1); })
                .withMessage("User with id = 1 is not found");
        newUser.setId(1);
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    User notSavedUser = userStorage.updateUser(newUser); })
                .withMessage("There is not user with such id");
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> {
                    userStorage.addFriend(1,2); })
                .withMessage("Incorrect ids to add friend");
    }

    @Test
    public void testWithoutNameCreateUser() {
        newUser.setName(null);
        userStorage.createUser(newUser);
        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser.getName())
                .isNotNull()
                .isEqualTo("vanya123");
    }

    @Test
    public void testUpdateUser() {
        userStorage.createUser(newUser);
        User updatedUser = createDopUser();
        updatedUser.setId(1);
        userStorage.updateUser(updatedUser);
        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser.getEmail())
                .isNotNull()
                .isEqualTo("user2@email.ru");
    }

    @Test
    public void testIsUserExist() {
        assertThat(userStorage.isUserExist(1))
                .isFalse();
        userStorage.createUser(newUser);
        assertThat(userStorage.isUserExist(1))
                .isTrue();
    }

    @Test
    public void testAddFriend() {
        userStorage.createUser(newUser);
        userStorage.createUser(createDopUser());
        userStorage.addFriend(1, 2);
        assertThat(userStorage.getUserById(1).getFriends().size())
                .isEqualTo(1);
        assertThat(userStorage.getUserById(2).getFriends().size())
                .isEqualTo(0);
    }

    @Test
    public void testRemoveFriend() {
        userStorage.createUser(newUser);
        userStorage.createUser(createDopUser());
        userStorage.addFriend(1, 2);
        userStorage.removeFriend(1,2);
        assertThat(userStorage.getUserById(1).getFriends().size())
                .isEqualTo(0);
    }
}