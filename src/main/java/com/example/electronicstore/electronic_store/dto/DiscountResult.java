package com.example.electronicstore.electronic_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DiscountResult {
    private double totalDiscount;
    private List<String> appliedDeals;
}
