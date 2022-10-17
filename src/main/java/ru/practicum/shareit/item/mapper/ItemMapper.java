package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;


@Component
public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null, null);
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null);
        ItemRequest itemRequest = item.getItemRequest();

        if (itemRequest != null) {
            itemDto.setRequestId(itemRequest.getId());
        }
        return itemDto;
    }

    public static ItemInfoDto toItemDtoWithBooking(Item item) {
        return new ItemInfoDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, new ArrayList<>());
    }
}