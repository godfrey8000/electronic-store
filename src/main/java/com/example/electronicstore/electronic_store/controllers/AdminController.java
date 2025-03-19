package com.example.electronicstore.electronic_store.controllers;


import com.example.electronicstore.electronic_store.entities.DiscountDeal;
import com.example.electronicstore.electronic_store.entities.Product;
import com.example.electronicstore.electronic_store.services.DiscountService;
import com.example.electronicstore.electronic_store.services.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin APIs", description = "APIs for managing products and discounts (Admin only)")
public class AdminController {

    private final ProductService productService;
    private final DiscountService discountService;

    @Operation(summary = "Create a new product")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating product: " + e.getMessage());
        }
    }

    @Operation(summary = "Get product by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving product: " + e.getMessage());
        }
    }

    @Operation(summary = "Update product by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        try {
            Product product = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete product by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProduct(@PathVariable Long id) {
        try {
            productService.removeProduct(id);
            return ResponseEntity.ok("Product removed successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing product: " + e.getMessage());
        }
    }

    @Operation(summary = "Create discount deal for a product")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @PostMapping("/{productId}/discounts")
    public ResponseEntity<?> createDiscount(@PathVariable Long productId,
                                            @RequestBody DiscountDeal discountDeal) {
        try {
            DiscountDeal createdDeal = discountService.createDiscount(productId, discountDeal);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDeal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating discount: " + e.getMessage());
        }
    }

    @Operation(summary = "Get discount by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @GetMapping("/discounts/{discountId}")
    public ResponseEntity<?> getDiscount(@PathVariable Long discountId) {
        try {
            DiscountDeal deal = discountService.getDiscount(discountId);
            return ResponseEntity.ok(deal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Discount not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving discount: " + e.getMessage());
        }
    }

    @Operation(summary = "Update discount by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @PutMapping("/discounts/{discountId}")
    public ResponseEntity<?> updateDiscount(@PathVariable Long discountId,
                                            @RequestBody DiscountDeal updatedDeal) {
        try {
            DiscountDeal deal = discountService.updateDiscount(discountId, updatedDeal);
            return ResponseEntity.ok(deal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Discount not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating discount: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete discount by ID")
    @Parameters({
            @Parameter(name = "Authorization", description = "JWT access token", in = ParameterIn.HEADER, required = true)
    })
    @DeleteMapping("/discounts/{discountId}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Long discountId) {
        try {
            discountService.deleteDiscount(discountId);
            return ResponseEntity.ok("Discount removed successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Discount not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting discount: " + e.getMessage());
        }
    }
}
