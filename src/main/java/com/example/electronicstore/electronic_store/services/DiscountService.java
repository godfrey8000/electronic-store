package com.example.electronicstore.electronic_store.services;

import com.example.electronicstore.electronic_store.dto.DiscountResult;
import com.example.electronicstore.electronic_store.entities.DiscountDeal;
import com.example.electronicstore.electronic_store.entities.Product;
import com.example.electronicstore.electronic_store.repositories.DiscountDealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountDealRepository discountDealRepository;
    private final ProductService productService;

    public DiscountResult calculateDiscountWithDetails(Long productId, double price, int quantity) {
        List<DiscountDeal> deals = discountDealRepository.findByProductId(productId);
        double totalDiscount = 0;
        List<String> appliedDeals = new ArrayList<>();

        for (DiscountDeal deal : deals) {
            switch (deal.getDealName()) {
                case "BUY_1_GET_50_OFF_SECOND":
                    int eligiblePairs = quantity / 2;
                    if (eligiblePairs > 0) {
                        double discount = (price / 2) * eligiblePairs;
                        totalDiscount += discount;
                        appliedDeals.add(deal.getDescription());
                    }
                    break;

                case "BLACK_FRIDAY_20_PERCENT":
                    double bfDiscount = price * quantity * 0.20;
                    totalDiscount += bfDiscount;
                    appliedDeals.add(deal.getDescription());
                    break;


            }
        }

        return new DiscountResult(totalDiscount, appliedDeals);
    }

    public DiscountDeal createDiscount(Long productId, DiscountDeal deal) {
        Product product = productService.getProductById(productId);

        deal.setProduct(product);
        return discountDealRepository.save(deal);
    }

    public DiscountDeal getDiscount(Long discountId) {
        return discountDealRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Discount with id " + discountId + " not found"));
    }

    public DiscountDeal updateDiscount(Long discountId, DiscountDeal updatedDeal) {
        DiscountDeal existing = discountDealRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Discount with id " + discountId + " not found"));

        existing.setDealName(updatedDeal.getDealName());
        existing.setDescription(updatedDeal.getDescription());
        return discountDealRepository.save(existing);
    }

    public void deleteDiscount(Long discountId) {
        DiscountDeal existing = discountDealRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Discount with id " + discountId + " not found"));
        discountDealRepository.delete(existing);
    }
}

