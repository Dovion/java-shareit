package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional from(String state) throws ValidationException {
        Optional value = null;
        try {
            value = Optional.of(BookingState.valueOf(state));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        return value;
    }
}

