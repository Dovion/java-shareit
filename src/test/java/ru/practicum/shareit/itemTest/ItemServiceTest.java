package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ReviewDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ReviewMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Review;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ReviewRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {
    private ItemService itemService;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private BookingRepository bookingRepository;
    private Item item;
    private User user;
    private Review comment;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, reviewRepository, bookingRepository,
                itemRequestRepository);
        item = createValidItemExample();
        comment = createValidCommentExample(item,user);
    }

    private Item createValidItemExample() {
        user = new User(1L, "testUser", "test@test.ru");
        User user1 = new User(2L, "testUser1", "test1@test.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "testItemRequest", user1, LocalDateTime.now());
        return new Item(1L, "testItem", "itemDescription", true, user, itemRequest);
    }

    private Review createValidCommentExample(Item item, User user) {
        return new Review(1L, "testComment", item, user, LocalDateTime.now());
    }

    private Booking createValidBookingExample(Item item, User user) {
        return new Booking(1L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2), item, user,
                APPROVED);
    }

    @Test
    public void createValidItem() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto itemDto = itemService.create(userId, ItemMapper.toItemDto(item));
        assertEquals(itemId, itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void updateItem() throws EntityNotFoundException {
        Item item1 = createValidItemExample();
        Long itemId = item.getId();
        item1.setName("testItem2");
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto itemDto = itemService.update(user.getId(), itemId, ItemMapper.toItemDto(item1));
        assertEquals(itemId, itemDto.getId());
        assertEquals("testItem2", itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void updateUnknownItem() {
        Item item1 = createValidItemExample();
        Long itemId = item.getId();
        item1.setName("testItem2");
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Throwable throwable = assertThrows(EntityNotFoundException.class, () -> itemService.update(anyLong(), itemId, ItemMapper.toItemDto(item1)));
        assertNotNull(throwable.getMessage());
        assertEquals("Ошибка при обновлении вещи: поступил запрос на обновление чужой вещи", throwable.getMessage());
    }

    @Test
    public void getItemById() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        ItemInfoDto itemDtoBooking = itemService.getItem(itemId, userId);
        assertEquals(itemId, itemDtoBooking.getId());
        assertEquals(item.getName(), itemDtoBooking.getName());
        assertEquals(item.getDescription(), itemDtoBooking.getDescription());
        assertEquals(item.getAvailable(), itemDtoBooking.getAvailable());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    public void getItemByIdWithReview() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        when(reviewRepository.save(createValidCommentExample(item,user))).thenReturn(comment);
        ItemInfoDto itemDtoBooking = itemService.getItem(itemId, userId);
        assertEquals(itemId, itemDtoBooking.getId());
        assertEquals(item.getName(), itemDtoBooking.getName());
        assertEquals(item.getDescription(), itemDtoBooking.getDescription());
        assertEquals(item.getAvailable(), itemDtoBooking.getAvailable());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    public void getAllItemsByUserId() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findByOwnerId(userId, PageRequest.of(0, 20)))
                .thenReturn(Collections.singletonList(item));
        itemService.create(userId, ItemMapper.toItemDto(item));
        final List<ItemInfoDto> itemDtoBookings = itemService.getAllItemsByUser(userId, 0, 20);
        assertEquals(itemId, itemDtoBookings.get(0).getId());
        assertEquals(item.getName(), itemDtoBookings.get(0).getName());
        assertEquals(item.getDescription(), itemDtoBookings.get(0).getDescription());
        assertEquals(item.getAvailable(), itemDtoBookings.get(0).getAvailable());
        verify(itemRepository, times(1)).findByOwnerId(userId, PageRequest.of(0, 20));
    }

    @Test
    public void getAllItemsByUserIdWithReview() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findByOwnerId(userId, PageRequest.of(0, 20)))
                .thenReturn(Collections.singletonList(item));
        itemService.create(userId, ItemMapper.toItemDto(item));
        when(reviewRepository.save(createValidCommentExample(item,user))).thenReturn(comment);
        final List<ItemInfoDto> itemDtoBookings = itemService.getAllItemsByUser(userId, 0, 20);
        assertEquals(itemId, itemDtoBookings.get(0).getId());
        assertEquals(item.getName(), itemDtoBookings.get(0).getName());
        assertEquals(item.getDescription(), itemDtoBookings.get(0).getDescription());
        assertEquals(item.getAvailable(), itemDtoBookings.get(0).getAvailable());
        verify(itemRepository, times(1)).findByOwnerId(userId, PageRequest.of(0, 20));
    }

    @Test
    public void findByTextTest() throws EntityNotFoundException {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        final List<Item> items = new ArrayList<>();
        items.add(item);
        String text = item.getDescription().substring(0, 3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findByText(text, PageRequest.of(0, 20))).thenReturn(items);
        itemService.create(userId, ItemMapper.toItemDto(item));
        final List<ItemDto> itemDtoList = itemService.findByText(text, 0, 20);
        assertEquals(itemId, itemDtoList.get(0).getId());
        assertEquals(item.getName(), itemDtoList.get(0).getName());
        assertEquals(item.getDescription(), itemDtoList.get(0).getDescription());
        assertEquals(item.getAvailable(), itemDtoList.get(0).getAvailable());
        verify(itemRepository, times(1)).findByText(text, PageRequest.of(0, 20));
    }

    @Test
    public void findByNullText() throws EntityNotFoundException {
        Long userId = item.getOwner().getId();
        final List<Item> items = new ArrayList<>();
        items.add(item);
        String text = "";
        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findByText(text, PageRequest.of(0, 20))).thenReturn(items);
        itemService.create(userId, ItemMapper.toItemDto(item));
        final List<ItemDto> itemDtoList = itemService.findByText(text, 0, 20);
        assertEquals(new ArrayList<>(), itemDtoList);
    }

    @Test
    public void createCommentForItem() throws ValidationException, EntityNotFoundException {
        User userWriteComment = item.getItemRequest().getRequester();
        Review comment = createValidCommentExample(item, userWriteComment);
        Booking booking = createValidBookingExample(item, userWriteComment);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userWriteComment.getId())).thenReturn(Optional.of(userWriteComment));
        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);
        when(bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(bookingsList);
        when(reviewRepository.save(any(Review.class))).thenReturn(comment);
        ReviewDto commentDto1 = ReviewMapper.toReviewDto(comment);
        ReviewDto commentDto = itemService.createReview(userWriteComment.getId(), item.getId(), commentDto1);
        assertEquals("testComment", commentDto.getText());
        assertEquals(userWriteComment.getName(), commentDto.getAuthorName());
        assertEquals(comment.getId(), commentDto.getId());
        verify(reviewRepository, times(1)).save(any());
    }

    @Test
    public void createItemUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Неверный идентификатор пользователя"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemService.create(3L, ItemMapper.toItemDto(item)));
        assertEquals("Неверный идентификатор пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getUnknownItem() {
        Long userId = item.getOwner().getId();
        when(itemRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Неверный идентификатор вещи"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () -> itemService.getItem(2L, userId));
        assertEquals("Неверный идентификатор вещи", throwable.getMessage());
    }

    @Test
    public void updateItemUnknownUser() {
        Item item1 = createValidItemExample();
        item1.setName("testItem1");
        when(itemRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Неверный идентификатор вещи"));
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemService.update(user.getId(), 3L, ItemMapper.toItemDto(item1)));
        assertEquals("Неверный идентификатор вещи", throwable.getMessage());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    public void createCommentUnknownUser() {
        User userWriteComment = item.getItemRequest().getRequester();
        Review comment = createValidCommentExample(item, userWriteComment);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenThrow(new ArrayIndexOutOfBoundsException("Неверный идентификатор пользователя"));
        ReviewDto commentDto1 = ReviewMapper.toReviewDto(comment);
        Throwable throwable = assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                itemService.createReview(3L, item.getId(), commentDto1));
        assertEquals("Неверный идентификатор пользователя", throwable.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }
}
