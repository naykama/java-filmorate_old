package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (postRequestIsValid(user)) {
            userService.createUser(user);
            log.debug("User {} was created", user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug(String.format("UserController: id = %d, friendId = %d\n", id, friendId));
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    private boolean postRequestIsValid(User user) {
        if (user.getEmail().isBlank() || user.getEmail().indexOf('@') == -1) {
            log.error("User create failed. Incorrect email");
            throw new ValidationException("Incorrect email");
        } else if (user.getLogin().isBlank() || user.getLogin().indexOf(' ') != -1) {
            log.error("User create failed. Incorrect login");
            throw new ValidationException("Incorrect login");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("User create failed. Incorrect date of birth {}", user.getBirthday());
            throw new ValidationException("Incorrect date of birth");
        } else {
            return true;
        }
    }
}
