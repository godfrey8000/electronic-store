package com.example.electronicstore.electronic_store;

import com.example.electronicstore.electronic_store.dto.DiscountResult;
import com.example.electronicstore.electronic_store.entities.*;
import com.example.electronicstore.electronic_store.repositories.*;
import com.example.electronicstore.electronic_store.services.BasketService;
import com.example.electronicstore.electronic_store.services.DiscountService;
import com.example.electronicstore.electronic_store.services.ProductService;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketItemRepository basketItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private BasketService basketService;

    private Basket basket;
    private Product product;
    private BasketItem basketItem;


    @BeforeEach
    void setup() {
        basket = new Basket(1L, List.of());
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(1000.0)
                .stockQuantity(10)
                .build();

        basketItem = BasketItem.builder()
                .id(1L)
                .basket(basket)
                .product(product)
                .quantity(2)
                .build();

    }

    @Test
    void testAddProductToBasketSuccess() {
        when(basketRepository.findById(1L)).thenReturn(Optional.of(basket));
        when(productService.getProductById(1L)).thenReturn(product);
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.of(basketItem));

        basketService.addProductToBasket(1L, 1L, 3);

        assertEquals(5, basketItem.getQuantity());
        verify(productService).adjustProductStock(1L, -3);
        verify(basketItemRepository).saveAndFlush(basketItem);
    }

    @Test
    void testAddProductToBasketInsufficientStock() {
        when(basketRepository.findById(1L)).thenReturn(Optional.of(basket));
        when(productService.getProductById(1L)).thenReturn(product);
        doThrow(new IllegalArgumentException("Insufficient stock")).when(productService).adjustProductStock(1L, -20);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                basketService.addProductToBasket(1L, 1L, 20)
        );

        assertEquals("Insufficient stock", exception.getMessage());
        verify(basketItemRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRemoveProductFromBasketSuccess() {
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.of(basketItem));

        basketService.removeProductFromBasket(1L, 1L);

        verify(productService).adjustProductStock(1L, 2);
        verify(basketItemRepository).delete(basketItem);
    }

    @Test
    void testRemoveProductFromBasketItemNotFound() {
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                basketService.removeProductFromBasket(1L, 1L)
        );
    }

    @Test
    void testReduceProductQuantityInBasketSuccess() {
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.of(basketItem));

        basketService.reduceProductQuantityInBasket(1L, 1L, 1);

        assertEquals(1, basketItem.getQuantity());
        verify(productService).adjustProductStock(1L, 1);
        verify(basketItemRepository).saveAndFlush(basketItem);
    }

    @Test
    void testReduceProductQuantityInBasketTooMuch() {
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.of(basketItem));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                basketService.reduceProductQuantityInBasket(1L, 1L, 5)
        );

        assertEquals("Cannot reduce more than current basket quantity: 2", exception.getMessage());
        verify(basketItemRepository, never()).saveAndFlush(any());
    }

    @Test
    void testCalculateReceiptWithDiscounts() {
        DiscountResult discountResult = new DiscountResult(500.0, List.of("BUY_1_GET_50_OFF_SECOND"));
        when(basketRepository.findById(1L)).thenReturn(Optional.of(basket));
        basket.setItems(List.of(basketItem));

        when(discountService.calculateDiscountWithDetails(1L, 1000.0, 2))
                .thenReturn(discountResult);

        var receipt = basketService.calculateReceipt(1L);

        assertNotNull(receipt);
        assertEquals(1500.0, receipt.getTotalPrice()); // 2 x 1000 - 500 discount
        assertEquals(500.0, receipt.getTotalDiscount());
        assertEquals(1, receipt.getItems().size());
        assertEquals("Laptop", receipt.getItems().get(0).getProductName());
        assertTrue(receipt.getItems().get(0).getAppliedDeals().contains("BUY_1_GET_50_OFF_SECOND"));
    }

    @Test
    void testConcurrentUpdateException() {
        when(basketRepository.findById(1L)).thenReturn(Optional.of(basket));
        when(productService.getProductById(1L)).thenReturn(product);
        when(basketItemRepository.findByBasketIdAndProductId(1L, 1L)).thenReturn(Optional.of(basketItem));
        doThrow(OptimisticLockException.class).when(basketItemRepository).saveAndFlush(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                basketService.addProductToBasket(1L, 1L, 1)
        );

        assertEquals("Concurrent update detected, please try again.", exception.getMessage());
    }
}
