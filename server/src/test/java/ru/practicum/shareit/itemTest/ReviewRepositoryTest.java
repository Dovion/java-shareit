package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Review;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ReviewRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReviewRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    private Item item;
    private Review review;

    @BeforeEach
    public void beforeEach() {
        User user = userRepository.save(new User(1L, "testUser", "test@test.ru"));
        item = itemRepository.save(new Item(1L, "testItem", "testItemRequest",
                true, user, null));
        review = reviewRepository.save(new Review(1L, "testComment", item, user, LocalDateTime.now()));
    }

    @Test
    public void getAllCommentsByItemIdTest() {
        final List<Review> reviews = reviewRepository.findAllByItemId(item.getId());
        assertEquals(reviews.size(), 1);
        assertEquals(review.getId(), reviews.get(0).getId());
        assertEquals(review.getText(), reviews.get(0).getText());
    }
}
