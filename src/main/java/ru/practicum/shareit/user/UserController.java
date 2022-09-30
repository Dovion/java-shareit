package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailureException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) throws FailureException {
        log.info("Создаем пользователя...");
        return userServiceImpl.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) throws EntityNotFoundException, FailureException {
        log.info("Обновляем пользователя...");
        return userServiceImpl.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Удаляем пользователя...");
        userServiceImpl.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("Выводим пользователя...");
        return userServiceImpl.get(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Выводим список всех пользователей...");
        return userServiceImpl.getAllUsers();
    }
}
