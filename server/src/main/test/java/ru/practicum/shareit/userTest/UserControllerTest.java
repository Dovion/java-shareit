package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        userDto = createValidUserDtoExample();
    }

    private UserDto createValidUserDtoExample() {
        return new UserDto(1L, "test", "test@test.ru");
    }

    @Test
    public void createValidUser() throws Exception {
        when(userService.create(userDto)).thenReturn(userDto);
        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test\",\"email\": \"test@test.ru\"}"));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    public void getUserById() throws Exception {
        Long userDtoId = userDto.getId();
        when(userService.getUser(userDtoId)).thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test\",\"email\": \"test@test.ru\"}"));
        verify(userService, times(1)).getUser(userDtoId);
    }

    @Test
    public void getAllUsers() throws Exception {
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        when(userService.getAll()).thenReturn(userDtoList);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"test\",\"email\": \"test@test.ru\"}]"));
        verify(userService, times(1)).getAll();
    }

    @Test
    public void updateValidUser() throws Exception {
        UserDto userDto1 = createValidUserDtoExample();
        Long userDtoId = userDto.getId();
        userDto1.setName("test1");
        userService.create(userDto);
        when(userService.update(userDtoId, userDto1)).thenReturn(userDto1);
        mockMvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test1\",\"email\": \"test@test.ru\"}"));
        verify(userService, times(1)).update(userDtoId, userDto1);
    }

    @Test
    public void deleteUser() throws Exception {
        Long userDtoId = userDto.getId();
        userService.create(userDto);
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
        verify(userService, times(1)).delete(userDtoId);
    }
}
