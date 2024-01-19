package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return userStorage.getAllUsers()
                .stream()
                .filter(user -> userStorage.getUserById(userId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userStorage.getAllUsers()
                .stream()
                .filter(user -> userStorage.getUserById(id).getFriends().contains(user.getId())
                        && userStorage.getUserById(otherId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }
}
