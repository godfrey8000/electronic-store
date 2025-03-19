package com.example.electronicstore.electronic_store.repositories;

import com.example.electronicstore.electronic_store.entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
}
