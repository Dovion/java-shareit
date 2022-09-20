package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepository {

    @Autowired
    private final ItemMapper itemMapper;
    Map<Long, Item> items = new HashMap<>();
    private Long id = Long.valueOf(0);

    public Item create(Item item) {
        item.setId(++id);
        items.put(id, item);
        return items.get(item.getId());
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item get(long id) {
        return items.get(id);
    }

    public List<ItemDto> getAllUserItems(long id) {
        List<ItemDto> resultList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>(items.values());
        for (var item : itemList) {
            if (item.getOwner().getId() == id) {
                resultList.add(itemMapper.toItemDto(item));
            }
        }
        return resultList;
    }

    public List<Item> getAll() {
        List<Item> itemList = new ArrayList<>(items.values());
        return itemList;
    }

    public List<Item> getItemByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        var lowerText = text.toLowerCase();
        Predicate<Item> name = item -> item.getName().toLowerCase().contains(lowerText);
        Predicate<Item> desc = item -> item.getDescription().toLowerCase().contains(lowerText);

        return getAll()
                .stream()
                .filter(name.or(desc))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
