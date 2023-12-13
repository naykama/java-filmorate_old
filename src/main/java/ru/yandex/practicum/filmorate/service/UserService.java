package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void add(long userId, long friendId) {
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void remove(long userId, long friendId) {
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> get(long userId) {
        return userStorage.get()
                .stream()
                .filter(user -> userStorage.getUserById(userId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userStorage.get()
                .stream()
                .filter(user -> userStorage.getUserById(id).getFriends().contains(user.getId())
                        && userStorage.getUserById(otherId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }
}
