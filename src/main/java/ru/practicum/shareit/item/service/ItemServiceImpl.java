package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemMapper itemMapper;

    public ItemDto create(ItemDto itemDto, long userId) throws EntityNotFoundException {
        User owner = userRepository.get(userId);
        if (owner == null) {
            throw new EntityNotFoundException("Ошибка при создании Item`a: передан неверный id владельца");
        }
        itemDto.setUserId(userId);
        Item item = itemMapper.toItem(itemDto);
        itemRepository.create(item);
        var resultDto = itemMapper.toItemDto(itemRepository.get(item.getId()));
        log.info("Item успешно добавлен");
        return resultDto;
    }

    public ItemDto update(ItemDto itemDto, long userId, long itemId) throws EntityNotFoundException {
        Item item = itemRepository.get(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Ошибка при обновлении Item`a: передан неверный id");
        }
        if (userRepository.get(userId) == null || !item.getOwner().getId().equals(userId)) {
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
        if (itemDto.getItemRequestId() != null) {
            item.setRequest(new ItemRequest());
        }
        itemRepository.update(item);
        log.info("Item успешно обновлён");
        return itemMapper.toItemDto(item);
    }


    public ItemDto get(long id) {
        log.info("Вывод Item`a произошёл успешно");
        return itemMapper.toItemDto(itemRepository.get(id));
    }

    public List<ItemDto> getAllUserItems(long id) {
        log.info("Вывод списка Item`ов произошёл успешно");
        return itemRepository.getAllUserItems(id);
    }

    public List<ItemDto> getItemByText(String text) {
        var list = itemRepository.getItemByText(text);
        List<ItemDto> resultList = new ArrayList<>();
        for (var item : list) {
            resultList.add(itemMapper.toItemDto(item));
        }
        log.info("Вывод Item`a по текстовому поиску произошёл успешно");
        return resultList;
    }

}
