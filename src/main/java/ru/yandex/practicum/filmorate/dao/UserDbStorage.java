package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<Long> existUserId = new HashSet<>();

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet userSet = jdbcTemplate.queryForRowSet("SELECT user_id FROM users");
        while (userSet.next()) {
            existUserId.add(userSet.getLong("film_id"));
        }
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Number userId = simpleJdbcInsert.executeAndReturnKey(convertUserToMap(user));
        user.setId(userId.longValue());
        existUserId.add(user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", user.getId());
        if (!filmRows.next()) {
            log.error("User update failed. There is not user with such id");
            throw new NotFoundException("There is not user with such id");
        }
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        getAllUserMap();
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        getAllUserMap();
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id = %d is not found", id));
        }
        return users.get(id);
    }

    @Override
    public boolean isUserExist(long id) {
        return existUserId.contains(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends");
        if (!(isUserExist(userId) && isUserExist(friendId)) || userId == friendId) {
            throw new NotFoundException("Incorrect ids to add friend");
        }
        while (userRows.next()) {
            long dbUserId = userRows.getLong("user_id");
            long dbFriendId = userRows.getLong("friend_id");
            if (dbUserId == userId && dbFriendId == friendId) {
                return;
            } else if (dbUserId == friendId && dbFriendId == userId) {
                if (userRows.getString("status").equals("unconfirmed")) {
                    jdbcTemplate.update("UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?",
                            "confirmed", friendId, userId);
                }
                return;
            }
        }
        jdbcTemplate.update("INSERT INTO friends VALUES(?, ?, ?)", userId, friendId, "unconfirmed");
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    private Map<String, Object>  convertUserToMap(User user) {
        return Map.of("email", user.getEmail(), "login", user.getLogin(), "user_name", user.getName(),
                "birthday", user.getBirthday());
    }

    private void getAllUserMap() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS AS u\n" +
                "LEFT OUTER JOIN (SELECT f1.user_id AS user_id, f1.FRIEND_ID AS friend_id\n" +
                "FROM FRIENDS AS f1\n" +
                "UNION\n" +
                "SELECT f2.friend_id AS user_id, f2.user_id AS friend_id\n" +
                "FROM FRIENDS AS f2\n" +
                "WHERE f2.status = 'confirmed'\n" +
                ") AS fr ON u.user_id=fr.user_id;");
        SqlRowSetMetaData md = userRows.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            System.out.println("Colomn name: " + i + " " + md.getColumnName(i));
        }
        long newUserId = 0;
        while (userRows.next()) {
            if (userRows.getLong("user_id") != newUserId) {
                User user = new User();
                user.setId(userRows.getLong("user_id"));
                user.setEmail(userRows.getString("email"));
                user.setLogin(userRows.getString("login"));
                user.setName(userRows.getString("user_name"));
                user.setBirthday(userRows.getDate("birthday").toLocalDate());
                users.put(user.getId(), user);
                newUserId = user.getId();
            }
            if (userRows.getObject("friend_id") != null) {
                users.get(newUserId).getFriends().add(userRows.getLong("friend_id"));
            }
            log.debug("User: " + users.get(newUserId));
        }
    }
}
