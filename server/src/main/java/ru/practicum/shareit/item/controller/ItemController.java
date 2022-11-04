package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ReviewDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) throws EntityNotFoundException {
        log.info("Создаём новую вещь...");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) throws EntityNotFoundException {
        log.info("Обновляем вещь...");
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItem(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) throws EntityNotFoundException {
        log.info("Выводим вещь...");
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemInfoDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        log.info("Выводим вещи пользователя...");
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "20") int size) {
        log.info("Вывод вещи по текстовому запросу...");
        return itemService.findByText(text, from, size);
    }


    @PostMapping("/{itemId}/comment")
    public ReviewDto creteReview(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody ReviewDto commentDto,
                                 @PathVariable long itemId) throws ValidationException, EntityNotFoundException {
        log.info("Создаём отзыв...");
        return itemService.createReview(userId, itemId, commentDto);
    }
}
