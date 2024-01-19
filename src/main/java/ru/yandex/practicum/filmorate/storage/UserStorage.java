package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    boolean isUserExist(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);
}
