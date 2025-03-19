package com.example.electronicstore.electronic_store.repositories;

import com.example.electronicstore.electronic_store.entities.DiscountDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscountDealRepository extends JpaRepository<DiscountDeal, Long> {
    List<DiscountDeal> findByProductId(Long productId);
}
