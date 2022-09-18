package ru.practicum.shareit.request;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    long id;
    String description;
    User requester;
    LocalDateTime created;

}
