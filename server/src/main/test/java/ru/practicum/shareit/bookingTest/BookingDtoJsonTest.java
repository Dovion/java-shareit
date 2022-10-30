package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        User owner = new User(1L, "testOwner", "test@test.ru");
        User booker = new User(2L, "testOwnerBooker", "test1@test.ru");
        Item item = new Item(1L, "testItem", "testDescriptionItem", true, owner, null);
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.parse("2022-09-10T10:42"),
                LocalDateTime.parse("2022-09-12T10:42"),
                item,
                booker,
                BookingStatus.APPROVED);

        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-10T10:42:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-09-12T10:42:00");
        (assertThat(result).extractingJsonPathValue("$.item").asString()).isEqualTo("{id=1, name=testItem, description=testDescriptionItem, available=true, owner={id=1, name=testOwner, email=test@test.ru}, itemRequest=null}");
        (assertThat(result).extractingJsonPathValue("$.booker").asString()).isEqualTo("{id=2, name=testOwnerBooker, email=test1@test.ru}");
        (assertThat(result).extractingJsonPathValue("$.status").asString()).isEqualTo("APPROVED");
    }
}
