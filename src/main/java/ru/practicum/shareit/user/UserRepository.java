package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    Map<Long, User> users = new HashMap<>();
    private static Long id = Long.valueOf(0);

    public User create(User user) {
        user.setId(++id);
        users.put(id, user);
        return user;

    }

    public User get(Long userId) {
        return users.get(userId);
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public void delete(Long id) {
        users.remove(id);
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>(users.values());
        return list;
    }
}
