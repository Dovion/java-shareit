package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemList;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;
    private UserRepository userRepository;
    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        ItemRepository itemRepository = mock(ItemRepository.class);
        itemRequestMapper = new ItemRequestMapper(itemRepository);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRequestMapper, userRepository);
        itemRequest = createItemRequestExample();
    }

    private ItemRequest createItemRequestExample() {
        User user = new User(1L, "test", "test@test.ru");
        return new ItemRequest(1L, "itemRequestDescription", user, LocalDateTime.now());
    }

    @Test
    public void createValidItemRequest() throws EntityNotFoundException {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequester().getId();
        User user = itemRequest.getRequester();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest),
                userId);
        assertEquals(itemRequestId, itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void getItemRequest() throws EntityNotFoundException {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequester().getId();
        User user = itemRequest.getRequester();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        ItemRequestDtoWithItemList itemRequestDtoWithItems = itemRequestService.getItemRequest(userId, itemRequestId);
        assertEquals(itemRequestId, itemRequestDtoWithItems.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoWithItems.getCreated());
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    public void getAllItemRequests() throws EntityNotFoundException {
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequester().getId();
        User user = itemRequest.getRequester();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.singletonList(itemRequest));
        itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest), userId);
        final List<ItemRequestDtoWithItemList> itemRequestDtoWithItems = itemRequestService.getAll(userId);
        assertEquals(itemRequestDtoWithItems.size(), 1);
        assertEquals(itemRequestId, itemRequestDtoWithItems.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoWithItems.get(0).getCreated());
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    public void getAllItemRequestWithPageableEmpty() throws EntityNotFoundException {
        Long userId = itemRequest.getRequester().getId();
        User user = itemRequest.getRequester();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAll(PageRequest.of(0, 20, Sort.by("created"))))
                .thenReturn(Page.empty());
        itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest), userId);
        final List<ItemRequestDtoWithItemList> itemRequestDtoWithItems = itemRequestService
                .getAllWithPageable(userId, 0, 20);
        assertTrue(itemRequestDtoWithItems.isEmpty());
        verify(itemRequestRepository, times(1))
                .findAll(PageRequest.of(0, 20, Sort.by("created")));
    }

    @Test
    public void createItemRequestUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Неверный идентификатор запроса"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest), 3L));
        assertEquals("Неверный идентификатор запроса", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getItemRequestUnknownUser() {
        Long itemRequestId = itemRequest.getId();
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemRequestService.getItemRequest(3L, itemRequestId));
        assertEquals("Передан неверный id пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getAllItemRequestsUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemRequestService.getAll(3L));
        assertEquals("Передан неверный id пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getAllItemRequestWithPageableUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemRequestService.getAllWithPageable(3L, 0, 20));
        assertEquals("Передан неверный id пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }
}
