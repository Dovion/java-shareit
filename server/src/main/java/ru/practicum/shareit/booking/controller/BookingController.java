package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.exception.ValidationException;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody BookingDtoShort bookingDtoShort,
                             @RequestHeader("X-Sharer-User-Id") long userId) throws ValidationException, EntityNotFoundException {
        log.info("Создаём новое бронирование...");
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable long id,
                                 @RequestHeader("X-Sharer-User-Id") long userId) throws EntityNotFoundException {
        log.info("Выводим бронирование...");
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(defaultValue = "ALL") String state,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "20") int size) throws Throwable {
//        log.info("Выводим все бронирования...");
//        if (from < 0) {
//            throw new ValidationException("Ошибка при выводе всех бронирований: передан отрицательный индекс");
//        }
//        BookingState.from(state).orElseThrow(() -> new ValidationException("Ошибка при выводе всех бронирований: передан неверный статус"));
        return bookingService.getAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) throws ValidationException, EntityNotFoundException {
        log.info("Выводим все бронирования пользователя...");
//        if (from < 0) {
//            throw new ValidationException("Ошибка при выводе всех бронирований: передан отрицательный индекс");
//        }
//        BookingState.from(state).orElseThrow(() -> new ValidationException("Ошибка при выводе всех бронирований пользователя: передан неверный статус"));
        return bookingService.getAllBookingByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId,
                              @RequestParam Boolean approved) throws ValidationException, EntityNotFoundException {
        log.info("Подтверждаем бронирование...");
        return bookingService.approve(userId, bookingId, approved);
    }
}