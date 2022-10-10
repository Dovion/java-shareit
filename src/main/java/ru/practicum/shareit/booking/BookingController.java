package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDtoShort bookingDtoShort,
                             @RequestHeader("X-Sharer-User-Id") long userId) throws ValidationException, EntityNotFoundException {
        log.info("Создаём новое бронирование...");
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) throws EntityNotFoundException {
        log.info("Выводим бронирование...");
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(defaultValue = "ALL") String state) throws Throwable {
        log.info("Выводим все бронирования...");
        BookingState.from(state).orElseThrow(() -> new ValidationException("Ошибка при выводе всех бронирований: передан неверный статус"));
        return bookingService.getAll(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) throws Throwable {
        log.info("Выводим все бронирования пользователя...");
        BookingState.from(state).orElseThrow(() -> new ValidationException("Ошибка при выводе всех бронирований пользователя: передан неверный статус"));
        return bookingService.getAllBookingByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId, @RequestParam Boolean approved) throws ValidationException, EntityNotFoundException {
        log.info("Подтверждаем бронирование...");
        return bookingService.approve(userId, bookingId, approved);
    }
}