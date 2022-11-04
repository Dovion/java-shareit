package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) throws EntityNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Ошибка при обновлении пользователя: передан неверный Id пользователя"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) throws EntityNotFoundException {
        getUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUser(Long userId) throws EntityNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Ошибка при получении пользователя: передан неверный Id пользователя"));
        return UserMapper.toUserDto(user);
    }
}