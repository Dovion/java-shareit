package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    public ItemDto createItem(ItemDto itemDto, long userId) throws EntityNotFoundException {
        User owner = userRepository.findById(userId);
        if (owner == null) {
            throw new EntityNotFoundException("Ошибка при создании Item`a: передан неверный id владельца");
        }
        itemDto.setOwner(owner);
        Item item = ItemMapper.toItem(itemDto);
        itemRepository.save(item);
        var resultDto = ItemMapper.toItemDto(itemRepository.getItemById(item.getId()));
        log.info("Item успешно добавлен");
        return resultDto;
    }

    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) throws EntityNotFoundException {
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Ошибка при обновлении Item`a: передан неверный id");
        }
        if (userRepository.findById(userId) == null || !item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Ошибка при обновлении Item`a: попытка обновления чужого предмета");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getItemRequest() != null) {
            item.setRequest(new ItemRequest());
        }
        itemRepository.update(item);
        log.info("Item успешно обновлён");
        return ItemMapper.toItemDto(item);
    }


    public ItemDto getItemById(long id) {
        log.info("Вывод Item`a произошёл успешно");
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    public List<ItemDto> getAllUserItems(long id) {
        log.info("Вывод списка Item`ов произошёл успешно");
        return itemRepository.getAllUserItems(id);
    }

    public List<ItemDto> getItemByText(String text) {
        var list = itemRepository.getItemByText(text);
        List<ItemDto> resultList = new ArrayList<>();
        for (var item : list) {
            resultList.add(ItemMapper.toItemDto(item));
        }
        log.info("Вывод Item`a по текстовому поиску произошёл успешно");
        return resultList;
    }

}