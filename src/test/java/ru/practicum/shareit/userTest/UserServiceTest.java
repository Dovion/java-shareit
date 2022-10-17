package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = createValidUserExample();
    }

    private User createValidUserExample() {
        return new User(1L, "test", "test@test.ru");
    }

    @Test
    public void createValidUser() {
        Long userId = user.getId();
        when(userRepository.save(user)).thenReturn(user);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        assertEquals(userId, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getUserById() throws EntityNotFoundException {
        Long userId = user.getId();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.create(UserMapper.toUserDto(user));
        UserDto userDto = userService.getUser(userId);
        assertEquals(userId, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getAllUsers() {
        Long userId = user.getId();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        userService.create(UserMapper.toUserDto(user));
        final List<UserDto> userDtoList = userService.getAll();
        assertEquals(userDtoList.size(), 1);
        assertEquals(userId, userDtoList.get(0).getId());
        assertEquals(user.getName(), userDtoList.get(0).getName());
        assertEquals(user.getEmail(), userDtoList.get(0).getEmail());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void updateValidUser() throws EntityNotFoundException {
        User user1 = createValidUserExample();
        Long userId = user.getId();
        user1.setName("test1");
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = userService.update(userId, UserMapper.toUserDto(user1));
        assertEquals(userId, userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void updateUnknownUser() {
        User user1 = createValidUserExample();
        Long userId = user.getId();
        user1.setName("test1");
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> userService.update(anyLong(), UserMapper.toUserDto(user1)));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при обновлении пользователя: передан неверный Id пользователя", throwable.getMessage());
    }

    @Test
    public void deleteUser() throws EntityNotFoundException {
        Long userId = user.getId();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.create(UserMapper.toUserDto(user));
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void getUnknownUser() {
        Long userId = user.getId();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.create(UserMapper.toUserDto(user));
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> userService.getUser(anyLong()));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при получении пользователя: передан неверный Id пользователя", throwable.getMessage());
    }

}