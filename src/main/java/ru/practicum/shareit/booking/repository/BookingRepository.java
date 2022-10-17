package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " + "FROM Booking b LEFT JOIN User AS us ON b.booker.id = us.id " + "WHERE us.id = ?1 " + "AND ?2 BETWEEN b.start AND b.end " + "ORDER BY b.start DESC")
    List<Booking> searchCurrentBookingsByBookerId(long userId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b " + "FROM Booking b LEFT JOIN Item AS i ON b.item.id = i.id " + "LEFT JOIN User AS us ON i.owner.id = us.id " + "WHERE us.id = ?1 " + "AND ?2 BETWEEN b.start AND b.end " + "ORDER BY b.start DESC")
    List<Booking> searchCurrentBookingsByItemOwnerId(long userId, LocalDateTime time, Pageable pageable);

    List<Booking> searchByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> searchBookingByItemOwnerId(long id, Pageable pageable);

    List<Booking> searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(long id, long itemId, LocalDateTime time, BookingStatus status);

    List<Booking> searchByBookerIdAndStartAfter(long userId, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByItemIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time);

    List<Booking> searchBookingsByItemIdAndStartIsAfterOrderByStartDesc(long id, LocalDateTime time);

    List<Booking> searchBookingsByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time1, LocalDateTime time2, Pageable pageable);

    List<Booking> searchBookingsByBookerIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByBookerIdAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time1, LocalDateTime time2, Pageable pageable);

    List<Booking> searchBookingsByItemOwnerIdAndEndIsBeforeOrderByEndDesc(long id, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByBookerIdAndEndIsBefore(long userId, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByItemOwnerIdAndEndIsBefore(long userId, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByItemOwnerIdAndEndIsAfterOrderByEndDesc(long id, LocalDateTime time, Pageable pageable);

    List<Booking> searchBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(long id, BookingStatus status, Pageable pageable);

    List<Booking> searchBookingsByBookerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

}