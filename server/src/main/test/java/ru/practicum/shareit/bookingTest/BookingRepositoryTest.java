package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(new User(1L, "user1", "user1@test.ru"));
        booker = userRepository.save(new User(2L, "user2", "user2@test.ru"));
        item = itemRepository.save(new Item(1L, "item", "description", true, owner,
                null));
        booking = bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2), item, booker, APPROVED));
    }

    @Test
    void getBooker() {
        final List<Booking> bookings = bookingRepository.searchByBookerIdOrderByStartDesc(booker.getId(), Pageable.unpaged());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookerAndLocalDateTimeTest() {
        List<Booking> bookings = bookingRepository.searchByBookerIdAndStartAfter(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());
        assertEquals(0, bookings.size());
    }

    @Test
    void getBookingItemOwnerIdTest() {
        final List<Booking> bookings = bookingRepository.searchBookingByItemOwnerId(owner.getId(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingByBookerIdAndItemIdAndEndIsBeforeAndStatusTest() {
        final List<Booking> bookings = bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(booker.getId(), item.getId(),
                        LocalDateTime.now(), APPROVED);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void startBookingIsAfterTest() {
        final Booking booking1 = bookingRepository.save(new Booking(2L, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10), item, booker, APPROVED));
        final List<Booking> bookings = bookingRepository.searchBookingByItemOwnerIdAndStartIsAfter(owner.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking1.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsItemIdAndEndIsBeforeOrderByEndDescTest() {
        final List<Booking> bookings = bookingRepository
                .searchBookingsByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsItemIdTest() {
        List<Booking> bookings = bookingRepository
                .searchBookingsByItemIdAndStartIsAfterOrderByStartDesc(item.getId(), LocalDateTime.now());
        assertEquals(0, bookings.size());
    }

    @Test
    void startIsAfterOrderByStartDescTest() {
        final Booking booking1 = bookingRepository.save(new Booking(2L, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10), item, booker, APPROVED));
        final List<Booking> bookings = bookingRepository.searchBookingsByItemIdAndStartIsAfterOrderByStartDesc(item.getId(),
                LocalDateTime.now());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking1.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsBookerIdAndStatusTest() {
        final List<Booking> bookings = bookingRepository.searchBookingsByBookerIdAndStatus(booker.getId(), APPROVED,
                Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsItemOwnerIdTest() {
        final List<Booking> bookings = bookingRepository.searchBookingByItemOwnerId(item.getOwner().getId(),
                Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getCurrentBookingsBookerIdTest() {
        final Booking booking1 = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(10), item, booker, APPROVED));
        final List<Booking> bookings = bookingRepository.searchCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking1.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getCurrentBookingsItemOwnerIdTest() {
        final Booking booking1 = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(10), item, booker, APPROVED));
        final List<Booking> bookings = bookingRepository.searchCurrentBookingsByItemOwnerId(item.getOwner().getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking1.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsBookerIdAndEndIsBeforeTest() {
        final List<Booking> bookings = bookingRepository.searchBookingsByBookerIdAndEndIsBefore(booker.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void getBookingsItemOwnerIdAndEndIsBeforeTest() {
        final List<Booking> bookings = bookingRepository.searchBookingsByItemOwnerIdAndEndIsBefore(item.getOwner().getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookings.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }
}