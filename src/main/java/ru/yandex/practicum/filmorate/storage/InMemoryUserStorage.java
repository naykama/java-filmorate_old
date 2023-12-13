package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        if (user.getName().isBlank()) {
                user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(id++, user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User update failed. There is not user with such id");
            throw new NotFoundException("There is not user with such id");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id = %d is not found", id));
        }
        return users.get(id);
    }

    @Override
    public boolean isUserExist(long id) {
        return users.containsKey(id);
    }
}
