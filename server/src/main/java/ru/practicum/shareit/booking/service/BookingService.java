package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.exception.ValidationException;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoShort bookingDtoShort, long userId) throws ValidationException, EntityNotFoundException;

    BookingDto getBooking(long bookingId, long userId) throws EntityNotFoundException;

    List<BookingDto> getAll(long userId, String state, int from, int size) throws EntityNotFoundException, ValidationException;

    BookingDto approve(long userId, long bookingId, Boolean approved) throws EntityNotFoundException, ValidationException;

    List<BookingDto> getAllBookingByOwner(long userId, String state, int from, int size) throws EntityNotFoundException, ValidationException;


}