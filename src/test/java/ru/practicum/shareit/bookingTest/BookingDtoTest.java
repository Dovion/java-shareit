package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoId;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

public class BookingDtoTest {
    private final BookingDtoId bookingDtoId = new BookingDtoId(1L, 2L);
    private Booking booking;

    private Booking createBooking() {
        User owner = new User(1L, "testOwner", "test@test.ru");
        User booker = new User(2L, "testOwnerBooker", "test1@test.ru");
        Item item = new Item(1L, "testItem", "testDescriptionItem", true, owner, null);
        LocalDateTime start = LocalDateTime.parse("2022-09-10T10:42");
        LocalDateTime end = LocalDateTime.parse("2022-09-12T10:42");
        booking = new Booking(1L, start, end, item, booker, APPROVED);
        return booking;
    }

    @Test
    void toBookingDtoForItem() {
        booking = createBooking();
        BookingDtoId bookingDtoId = BookingMapper.toBookingDtoId(booking);
        assertEquals(bookingDtoId.getBookerId(), 2L);
    }

    @Test
    void getId() {
        Long id = bookingDtoId.getId();
        assertEquals(id, 1);
    }

    @Test
    void setId() {
        bookingDtoId.setId(5L);
        assertEquals(bookingDtoId.getId(), 5);
    }

    @Test
    void getBookerId() {
        Long bookerId = bookingDtoId.getBookerId();
        assertEquals(bookerId, 2);
    }

    @Test
    void setBookerId() {
        bookingDtoId.setBookerId(5L);
        assertEquals(bookingDtoId.getBookerId(), 5);
    }
}
