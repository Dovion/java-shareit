package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookItemRequestDto bookingItemRequestDto) {
        return post("", userId, bookingItemRequestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAll(long userId, String status, int from, int size) {
        Map<String, Object> parameters = Map.of("state", status, "from", from, "size", size);

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingByOwner(long userId, String status, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", status, "from", from, "size", size);

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = new HashMap<>();
        if (approved == true) {
            parameters = Map.of("approved", true);
        } else {
            parameters = Map.of("approved", false);
        }


        return patch("/" + bookingId + "?approved={approved}", userId, parameters); // todo
    }
}
