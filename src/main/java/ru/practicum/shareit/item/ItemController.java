package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailureException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Validated({Create.class}) @RequestBody ItemDto itemDto) throws EntityNotFoundException {
        log.info("Создаём новый Item...");
        return itemServiceImpl.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId, @Validated({Update.class}) @RequestBody ItemDto itemDto) throws EntityNotFoundException, FailureException {
        log.info("Обновляем Item...");
        return itemServiceImpl.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    ItemDto get(@PathVariable long itemId) {
        log.info("Выводим Item...");
        return itemServiceImpl.get(itemId);
    }

    @GetMapping
    List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выводим список Item`ов...");
        return itemServiceImpl.getAllUserItems(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam(required = false) String text) {
        log.info("Выводим Item по текстовому поиску...");
        return itemServiceImpl.getItemByText(text);
    }
}
