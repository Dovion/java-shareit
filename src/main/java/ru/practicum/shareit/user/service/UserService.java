package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailureException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public UserDto create(UserDto userDto) throws FailureException;

    public UserDto update(Long id, UserDto userDto) throws EntityNotFoundException, FailureException;

    public void delete(Long id);

    public UserDto get(Long id);

    public List<UserDto> getAllUsers();


}
