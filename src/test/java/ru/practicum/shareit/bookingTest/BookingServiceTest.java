package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private Booking booking;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        booking = createBookingExample();
    }

    private Booking createBookingExample() {
        User owner = new User(1L, "testOwner", "testOwner@test.ru");
        User booker = new User(2L, "testBooker", "testBooker@test.ru");
        Item item = new Item(1L, "testItem", "testDescription", true, owner, null);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        booking = new Booking(1L, start, end, item, booker, APPROVED);
        return booking;
    }

    private static BookingDtoShort toBookingDtoShort(Booking booking) {
        return new BookingDtoShort(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    @Test
    public void createValidBooking() throws ValidationException, EntityNotFoundException {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.create(toBookingDtoShort(booking), bookerId);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void createBookingWithIncorrectTime() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        booking.setEnd(LocalDateTime.now().minusDays(10));
        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.create(toBookingDtoShort(booking), bookerId));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при создании бронирования: время окончания бронирования неверное", throwable.getMessage());
    }

    @Test
    public void createUnavailableBooking() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        booking.getItem().setAvailable(false);
        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.create(toBookingDtoShort(booking), bookerId));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при создании бронирования: поступил запрос на бронирование недоступной вещи", throwable.getMessage());
    }

    @Test
    public void createOwnersBookingForOwner() {
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        User owner = booking.getItem().getOwner();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> bookingService.create(toBookingDtoShort(booking), ownerId));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при создании бронирования: поступил запрос на бронирование своей же вещи", throwable.getMessage());
    }

    @Test
    public void getBooking() throws EntityNotFoundException {
        Long bookerId = booking.getBooker().getId();
        Long bookingId = booking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingDto bookingDto = bookingService.getBooking(bookingId, bookerId);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
    }


    @Test
    public void getAllBookings() throws ValidationException, EntityNotFoundException {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchByBookerIdOrderByStartDesc(bookerId, PageRequest.of(0, 20, Sort.by("start")
                .descending()))).thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "ALL", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus());
        verify(bookingRepository, times(1)).searchByBookerIdOrderByStartDesc(bookerId,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void getAllBookingWaiting() throws ValidationException, EntityNotFoundException {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        booking.setStatus(WAITING);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingsByBookerIdAndStatus(bookerId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "WAITING", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        verify(bookingRepository, times(1)).searchBookingsByBookerIdAndStatus(bookerId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void getAllBookingsRejected() throws ValidationException, EntityNotFoundException {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        booking.setStatus(REJECTED);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingsByBookerIdAndStatus(bookerId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "REJECTED", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        verify(bookingRepository, times(1)).searchBookingsByBookerIdAndStatus(bookerId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void getAllUserBookings() throws ValidationException, EntityNotFoundException {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerId(itemUserId,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "ALL", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        verify(bookingRepository, times(1)).searchBookingByItemOwnerId(itemUserId,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void getAllUserBookingsWaiting() throws ValidationException, EntityNotFoundException {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        booking.setStatus(WAITING);
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(itemUserId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "WAITING", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        verify(bookingRepository, times(1)).searchBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(itemUserId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void getAllUserBookingsRejected() throws ValidationException, EntityNotFoundException {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();
        booking.setStatus(REJECTED);
        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(itemUserId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "REJECTED", 0, 20);
        assertEquals(bookingDtoList.size(), 1);
        assertEquals(booking.getId(), bookingDtoList.get(0).getId());
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName());
        verify(bookingRepository, times(1)).searchBookingsByItemOwnerIdAndStatusEqualsOrderByStatusDesc(itemUserId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    @Test
    public void approveBookingApproved() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approve(itemUserId,
                bookingId, true));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка подтверждения бронирования: бронирование уже подтверждено", throwable.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void approveBookingWithoutBooleanValue() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();
        booking.setStatus(WAITING);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approve(itemUserId,
                bookingId, null));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка подтверждения бронирования: отсутствует булевое значение для подтверждения бронирования", throwable.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void approveSettledBooking() {
        Long bookingId = booking.getId();
        booking.setStatus(WAITING);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> bookingService.approve(anyLong(),
                bookingId, true));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка подтверждения бронирования: передан запрос на подтверждение бронирования чужой вещи", throwable.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    public void createBookingUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                bookingService.create(toBookingDtoShort(booking), 3L));
        assertEquals("Передан неверный id пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getUnknownBooking() {
        Long bookingId = booking.getId();
        when(bookingRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id бронирования"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                bookingService.getBooking(bookingId, 3L));
        assertEquals("Передан неверный id бронирования", throwable.getMessage());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getUnknownUserBooking() {
        Long bookingId = booking.getId();
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, anyLong()));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при получении бронирования: неверный Id пользователя", throwable.getMessage());
    }

    @Test
    public void getAllBookingsUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Передан неверный id пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                bookingService.getAll(3L, "ALL", 0, 20));
        assertEquals("Передан неверный id пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }
}
