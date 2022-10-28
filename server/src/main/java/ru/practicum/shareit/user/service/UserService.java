package ru.practicum.shareit.user.service;

import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto) throws EntityNotFoundException;

    List<UserDto> getAll();

    void delete(Long userId) throws EntityNotFoundException;

    UserDto getUser(Long userId) throws EntityNotFoundException;
}
