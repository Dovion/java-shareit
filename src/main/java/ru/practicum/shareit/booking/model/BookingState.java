package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional from(String stringState) throws ValidationException {
        Optional value = null;
        try {
            value = Optional.of(BookingState.valueOf(stringState));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + stringState);
        }
        return value;
    }
}

