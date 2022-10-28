package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoId;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ReviewDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ReviewMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Review;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ReviewRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) throws EntityNotFoundException {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при создании вещи: передан неверный Id пользователя"));
        item.setOwner(user);
        Long requestId = itemDto.getRequestId();

        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Неверный идентификатор запроса")));
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) throws EntityNotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Ошибка при обновлении вещи: передан неверный Id пользователя"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Ошибка при обновлении вещи: поступил запрос на обновление чужой вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemInfoDto getItem(Long itemId, Long userId) throws EntityNotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Ошибка при получении вещи: передан неверный Id пользователя"));
        ItemInfoDto itemDtoBooking = ItemMapper.toItemDtoWithBooking(item);

        if (item.getOwner().getId().equals(userId)) {
            createItemDtoWithBooking(itemDtoBooking);
        }
        List<Review> reviews = reviewRepository.findAllByItemId(itemId);
        if (!reviews.isEmpty()) {
            itemDtoBooking.setComments(reviews.stream().map(ReviewMapper::toReviewDto).collect(Collectors.toList()));
        }
        return itemDtoBooking;
    }

    @Override
    public List<ItemInfoDto> getAllItemsByUser(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<ItemInfoDto> userItemList = itemRepository.findByOwnerId(userId, pageable).stream().map(ItemMapper::toItemDtoWithBooking).collect(Collectors.toList());
        for (ItemInfoDto itemDtoBooking : userItemList) {
            createItemDtoWithBooking(itemDtoBooking);
            List<Review> reviews = reviewRepository.findAllByItemId(itemDtoBooking.getId());
            if (!reviews.isEmpty()) {
                itemDtoBooking.setComments(reviews.stream().map(ReviewMapper::toReviewDto).collect(Collectors.toList()));
            }
        }
        userItemList.sort(Comparator.comparing(ItemInfoDto::getId));
        return userItemList;
    }

    @Override
    public List<ItemDto> findByText(String text, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        if (!text.isBlank()) {
            return itemRepository.findByText(text, pageable).stream().filter(Item::getAvailable).map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void createItemDtoWithBooking(ItemInfoDto itemDtoBooking) {
        List<Booking> lastBookings = bookingRepository.searchBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoBooking.getId(), LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            BookingDtoId lastBooking = BookingMapper.toBookingDtoId(lastBookings.get(0));
            itemDtoBooking.setLastBooking(lastBooking);
        }
        List<Booking> nextBookings = bookingRepository.searchBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoBooking.getId(), LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            BookingDtoId nextBooking = BookingMapper.toBookingDtoId(nextBookings.get(0));
            itemDtoBooking.setNextBooking(nextBooking);
        }
    }

    @Override
    public ReviewDto createReview(Long userId, Long itemId, ReviewDto reviewDto) throws EntityNotFoundException, ValidationException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Ошибка при создании отзыва: неверный Id вещи"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Ошибка при создании отзыва: неверный Id пользователя"));
        bookingRepository.searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(userId, itemId, LocalDateTime.now(),
                        BookingStatus.APPROVED).stream().filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                        .findAny().orElseThrow(() -> new ValidationException("Ошибка при создании отзыва: передан запрос на создание отзыва при отсуствии бронирования вещи"));
        Review review = ReviewMapper.toReview(reviewDto);
        review.setItem(item);
        review.setAuthor(user);
        reviewRepository.save(review);
        return ReviewMapper.toReviewDto(review);
    }
}
