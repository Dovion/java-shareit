package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.FailureException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Validated({Create.class}) @RequestBody ItemDto itemDto) throws EntityNotFoundException {
        log.info("Создаём новый Item...");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId, @Validated({Update.class}) @RequestBody ItemDto itemDto) throws EntityNotFoundException, FailureException {
        log.info("Обновляем Item...");
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItem(@PathVariable long itemId) {
        log.info("Выводим Item...");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выводим список Item`ов...");
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam(required = false) String text) {
        log.info("Выводим Item по текстовому поиску...");
        return itemService.getItemByText(text);
    }
}
