package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    Item reservedItem;
    User booker;

}
