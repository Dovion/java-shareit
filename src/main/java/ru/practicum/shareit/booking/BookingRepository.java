package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    LocalDateTime x = LocalDateTime.now();

    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> searchBookingByItemOwnerId(long id);

    List<Booking> searchBookingByBookerIdAndItemIdAndEndIsBefore(long id, long itemId, LocalDateTime time);

    List<Booking> searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findBookingsByBookerIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStatusEqualsOrderByStatusDesc(long id, BookingStatus status);

    List<Booking> searchBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time1, LocalDateTime time2);

    List<Booking> searchBookingsByItemOwnerIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> searchBookingsByItemOwnerIdAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(long id, BookingStatus status);
}