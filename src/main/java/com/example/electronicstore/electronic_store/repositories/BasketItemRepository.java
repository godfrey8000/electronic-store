package com.example.electronicstore.electronic_store.repositories;

import com.example.electronicstore.electronic_store.entities.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    Optional<BasketItem> findByBasketIdAndProductId(Long basketId, Long productId);
    void deleteByBasketIdAndProductId(Long basketId, Long productId);
}
