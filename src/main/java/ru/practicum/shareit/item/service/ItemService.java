package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ReviewDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto) throws EntityNotFoundException;

    ItemDto update(Long userId, Long itemId, ItemDto itemDto) throws EntityNotFoundException;

    ItemInfoDto getItem(Long itemId, Long userId) throws EntityNotFoundException;

    List<ItemInfoDto> getAllItemsByUser(Long userId);

    List<ItemDto> findByText(String text);

    ReviewDto createReview(Long userId, Long itemId, ReviewDto reviewDto) throws EntityNotFoundException, ValidationException;
}