package com.example.electronicstore.electronic_store.controllers;

import com.example.electronicstore.electronic_store.dto.ReceiptDTO;
import com.example.electronicstore.electronic_store.services.BasketService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/customer/basket")
@RequiredArgsConstructor
@Tag(name = "Customer Basket APIs", description = "APIs for customers to manage their basket")
public class CustomerController {

    private final BasketService basketService;

    @PostMapping("/{basketId}/products/{productId}")
    @Operation(summary = "Add product to basket")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    public ResponseEntity<?> addProduct(@PathVariable Long basketId,
                                        @PathVariable Long productId,
                                        @RequestParam(defaultValue = "1") int quantity) {
        try {
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be positive.");
            }
            basketService.addProductToBasket(basketId, productId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("basketId", basketId);
            response.put("productId", productId);
            response.put("quantity", quantity);
            response.put("message", "Product added to basket");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding product to basket: " + e.getMessage());
        }
    }

    @DeleteMapping("/{basketId}/products/{productId}")
    @Operation(summary = "Remove product entirely from basket")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    public ResponseEntity<?> removeProductEntirely(@PathVariable Long basketId,
                                                   @PathVariable Long productId) {
        try {
            basketService.removeProductFromBasket(basketId, productId);

            Map<String, Object> response = new HashMap<>();
            response.put("basketId", basketId);
            response.put("productId", productId);
            response.put("message", "Product remove from basket");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing product from basket: " + e.getMessage());
        }
    }

    @PutMapping("/{basketId}/products/{productId}/reduce")
    @Operation(summary = "Reduce product quantity in basket")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    public ResponseEntity<?> reduceProductQuantity(@PathVariable Long basketId,
                                                   @PathVariable Long productId,
                                                   @RequestParam(defaultValue = "1") int quantity) {
        try {
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be positive.");
            }
            basketService.reduceProductQuantityInBasket(basketId, productId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("basketId", basketId);
            response.put("productId", productId);
            response.put("quantity", quantity);
            response.put("message", "Product reduce from basket");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reducing product quantity: " + e.getMessage());
        }
    }

    @GetMapping("/{basketId}/receipt")
    @Operation(summary = "Get basket receipt with applied discounts")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    public ResponseEntity<?> getReceipt(@PathVariable Long basketId) {
        try {
            ReceiptDTO receipt = basketService.calculateReceipt(basketId);
            return ResponseEntity.ok(receipt);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving receipt: " + e.getMessage());
        }
    }
}
