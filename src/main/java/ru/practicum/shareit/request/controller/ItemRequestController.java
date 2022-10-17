package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemList;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) throws EntityNotFoundException {
        log.info("Создаём запрос...");
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItemList> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) throws EntityNotFoundException {
        log.info("Выводим все запросы...");
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItemList> getAllWithPageable(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") @Min(0) Integer from, @RequestParam(defaultValue = "20") @Positive Integer size) throws EntityNotFoundException {
        log.info("Выводим все запрос с пагинацией...");
        return itemRequestService.getAllWithPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItemList getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) throws EntityNotFoundException {
        log.info("Выводим запрос...");
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
