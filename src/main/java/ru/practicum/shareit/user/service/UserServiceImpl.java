package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailureException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    @Autowired
    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) throws FailureException {
        var allUsers = userRepository.getAllUsers();
        if (!allUsers.isEmpty()) {
            for (var user1 : allUsers) {
                if (user1.getEmail().equals(userDto.getEmail())) {
                    throw new FailureException("Ошибка при создании пользователя: данный email уже используется");
                }
            }
        }
        User user = userRepository.create(UserMapper.toUser(userDto));
        var resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь успешно добавлен");
        return resultDto;
    }

    public UserDto update(Long id, UserDto userDto) throws EntityNotFoundException, FailureException {
        User user = userRepository.get(id);
        if (user == null) {
            throw new EntityNotFoundException("Ошибка при обновлении пользователя: передан неверный id");
        }
        var allUsers = userRepository.getAllUsers();
        if (!allUsers.isEmpty()) {
            for (var user1 : allUsers) {
                if (user1.getEmail().equals(userDto.getEmail())) {
                    throw new FailureException("Ошибка при обновлении пользователя: данный email уже используется");
                }
            }
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        userRepository.update(user);
        var resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь успешно обновлён");
        return resultDto;
    }

    public void delete(Long id) {
        userRepository.delete(id);
        log.info("Пользователь успешно удалён");
    }

    public UserDto get(Long id) {
        User user = userRepository.get(id);
        var resultDto = UserMapper.toUserDto(user);
        log.info("Пользователь успешно выведен");
        return resultDto;
    }

    public List<UserDto> getAllUsers() {
        var usersList = userRepository.getAllUsers();
        List<UserDto> usersDtoList = new ArrayList<>();
        for (var user : usersList) {
            usersDtoList.add(UserMapper.toUserDto(user));
        }
        log.info("Список пользователей успешно выведен");
        return usersDtoList;
    }


}
