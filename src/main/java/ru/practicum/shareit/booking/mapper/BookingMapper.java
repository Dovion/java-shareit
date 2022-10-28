package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoId;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

@Component
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static Booking fromShortToBooking(BookingDtoShort bookingDtoShort) {
        return new Booking(bookingDtoShort.getId(),
                bookingDtoShort.getStart(),
                bookingDtoShort.getEnd(),
                null,
                null,
                BookingStatus.WAITING);
    }

    public static BookingDtoId toBookingDtoId(Booking booking) {
        return new BookingDtoId(booking.getId(), booking.getBooker().getId());
    }
}