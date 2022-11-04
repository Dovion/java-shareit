package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemList;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) throws EntityNotFoundException {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при создании запроса: передан неверный id пользователя"));
        itemRequest.setRequester(user);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithItemList> getAll(Long userId) throws EntityNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при получении запросов: передан неверный id пользователя"));
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream().map(itemRequestMapper::toItemRequestDtoWithItems).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItemList getItemRequest(Long userId, Long itemRequestId) throws EntityNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка получении запроса: передан неверный id пользователя"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() -> new EntityNotFoundException("Ошибка при создании запроса: передан неверный id запроса"));
        return itemRequestMapper.toItemRequestDtoWithItems(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItemList> getAllWithPageable(Long userId, Integer from, Integer size) throws EntityNotFoundException {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при получении запроса с пагинацией: передан неверный id пользователя"));
        return itemRequestRepository.findAll(pageable).stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId)).map(itemRequestMapper::toItemRequestDtoWithItems).collect(Collectors.toList());
    }
}

