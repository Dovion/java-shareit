package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT item FROM Item item " + "WHERE UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%')) " + " OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%'))")
    List<Item> findByText(String text, Pageable pageable);

    List<Item> findAllByItemRequestId(Long itemRequestId);

    List<Item> findByOwnerId(Long userId, Pageable pageable);
}
