package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(BookingDtoShort bookingDtoShort, long userId) throws ValidationException, EntityNotFoundException {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart())) {
            throw new ValidationException("Ошибка при создании бронирования: время окончания бронирования неверное");
        }
        Booking booking = BookingMapper.fromShortToBooking(bookingDtoShort);
        booking.setBooker(userRepository.findById(userId).orElseThrow());
        Item item = itemRepository.findById(bookingDtoShort.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Ошибка при создании бронирования: неверный Id вещи"));
        if (!item.getAvailable()) {
            throw new ValidationException("Ошибка при создании бронирования: поступил запрос на бронирование недоступной вещи");
        }
        if (item.getOwner().getId() == userId) {
            throw new EntityNotFoundException("Ошибка при создании бронирования: поступил запрос на бронирование своей же вещи");
        }
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) throws EntityNotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Ошибка при получении бронирования: неверный Id бронирования"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Ошибка при получении бронирования: неверный Id пользователя");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getAll(long userId, String state) throws EntityNotFoundException, ValidationException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при получении всех бронирований: передан неверный Id пользователя"));
        /////// List<BookingDto> result = bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
        ////////       .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findBookingsByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findBookingsByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findBookingsByBookerIdAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStatusDesc(userId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findBookingsByBookerIdAndStatusEqualsOrderByStatusDesc(userId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(long userId, String state) throws EntityNotFoundException, ValidationException {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при получении всех бронирований пользователя: передан неверный Id пользователя"));
        List<BookingDto> result = bookingRepository.searchBookingByItemOwnerId(userId).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());//////
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Ошибка при получении всех бронирований пользователя:");
        }
        switch (BookingState.valueOf(state)) {
            case ALL:
                var x = bookingRepository.searchBookingByItemOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                x.sort(Comparator.comparing(BookingDto::getStart).reversed());
                return x;
            case CURRENT:
                return bookingRepository.searchBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.searchBookingsByItemOwnerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.searchBookingsByItemOwnerIdAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(userId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(userId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public BookingDto approve(long userId, long bookingId, Boolean approved) throws EntityNotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Ошибка подтверждения бронирования: передан неверный Id пользователя"));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        if (bookingDto.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Ошибка подтверждения бронирования: передан запрос на подтверждение бронирования чужой вещи");
        }
        if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Ошибка подтверждения бронирования: бронирование уже подтверждено");
        }
        if (approved == null) {
            throw new ValidationException("Ошибка подтверждения бронирования: отсутствует булевое значение для подтверждения бронирования");
        } else if (approved) {
            bookingDto.setStatus(BookingStatus.APPROVED);
        } else {
            bookingDto.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto)));
    }
}

