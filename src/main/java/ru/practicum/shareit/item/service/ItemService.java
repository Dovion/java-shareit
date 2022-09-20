package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto create(ItemDto itemDto, long userId) throws EntityNotFoundException;

    public ItemDto update(ItemDto itemDto, long userId, long itemId) throws EntityNotFoundException;

    public ItemDto get(long id);

    public List<ItemDto> getAllUserItems(long id);

    public List<ItemDto> getItemByText(String text);


}
