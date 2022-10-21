package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemList;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) throws EntityNotFoundException;

    List<ItemRequestDtoWithItemList> getAll(Long userId) throws EntityNotFoundException;

    List<ItemRequestDtoWithItemList> getAllWithPageable(Long userId, Integer from, Integer size) throws EntityNotFoundException;

    ItemRequestDtoWithItemList getItemRequest(Long userId, Long itemRequestId) throws EntityNotFoundException;
}
