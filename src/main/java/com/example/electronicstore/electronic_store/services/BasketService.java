package com.example.electronicstore.electronic_store.services;

import com.example.electronicstore.electronic_store.dto.DiscountResult;
import com.example.electronicstore.electronic_store.dto.ReceiptDTO;
import com.example.electronicstore.electronic_store.dto.ReceiptItemDTO;
import com.example.electronicstore.electronic_store.entities.*;
import com.example.electronicstore.electronic_store.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ProductService productService;
    private final DiscountService discountService;

    @Transactional
    public void addProductToBasket(Long basketId, Long productId, int quantity) {
        try {

            Basket basket = basketRepository.findById(basketId).orElseGet(() -> {
                Basket newBasket = new Basket();
                basketRepository.saveAndFlush(newBasket);
                return newBasket;
            });

            Product product = productService.getProductById(productId);

            BasketItem basketItem = basketItemRepository.findByBasketIdAndProductId(basketId, productId)
                    .orElse(BasketItem.builder()
                            .basket(basket)
                            .product(product)
                            .quantity(0)
                            .build());

            int newBasketQuantity = basketItem.getQuantity() + quantity;

            productService.adjustProductStock(productId, -quantity);

            if (newBasketQuantity == 0) {
                basketItemRepository.delete(basketItem);
            } else {
                basketItem.setQuantity(newBasketQuantity);
                basketItemRepository.saveAndFlush(basketItem);
            }

        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected, please try again.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while adding product to basket.");
        }
    }


    @Transactional
    public void removeProductFromBasket(Long basketId, Long productId) {
        try {
            BasketItem basketItem = basketItemRepository.findByBasketIdAndProductId(basketId, productId)
                    .orElseThrow(() -> new EntityNotFoundException("Basket item not found"));

            // restore stock back to product
            int quantityToRestore = basketItem.getQuantity();
            productService.adjustProductStock(productId, quantityToRestore);

            // remove the item entirely from basket
            basketItemRepository.delete(basketItem);
            basketItemRepository.flush();

        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected, please try again.");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while removing product from basket.");
        }
    }

    @Transactional
    public void reduceProductQuantityInBasket(Long basketId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        try {
            BasketItem basketItem = basketItemRepository.findByBasketIdAndProductId(basketId, productId)
                    .orElseThrow(() -> new EntityNotFoundException("Basket item not found"));

            if (basketItem.getQuantity() < quantity) {
                throw new IllegalArgumentException("Cannot reduce more than current basket quantity: "
                        + basketItem.getQuantity());
            }

            basketItem.setQuantity(basketItem.getQuantity() - quantity);
            productService.adjustProductStock(productId, quantity); // Clearly restore stock

            if (basketItem.getQuantity() == 0) {
                basketItemRepository.delete(basketItem);
            } else {
                basketItemRepository.saveAndFlush(basketItem);
            }

        } catch (OptimisticLockException e) {
            throw new RuntimeException("Concurrent update detected, please try again.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }


    @Transactional(readOnly = true)
    public ReceiptDTO calculateReceipt(Long basketId) {
        Basket basket = basketRepository.findById(basketId).orElseThrow();

        List<ReceiptItemDTO> receiptItems = basket.getItems().stream().map(item -> {
            double itemTotal = item.getProduct().getPrice() * item.getQuantity();

            DiscountResult discountResult = discountService.calculateDiscountWithDetails(
                    item.getProduct().getId(),
                    item.getProduct().getPrice(),
                    item.getQuantity()
            );

            double finalPrice = itemTotal - discountResult.getTotalDiscount();

            return new ReceiptItemDTO(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    itemTotal,
                    discountResult.getTotalDiscount(),
                    finalPrice,
                    discountResult.getAppliedDeals()
            );
        }).toList();

        double total = receiptItems.stream().mapToDouble(ReceiptItemDTO::getFinalPrice).sum();
        double discountApplied = receiptItems.stream().mapToDouble(ReceiptItemDTO::getDiscount).sum();

        return new ReceiptDTO(receiptItems, total, discountApplied);
    }
}
