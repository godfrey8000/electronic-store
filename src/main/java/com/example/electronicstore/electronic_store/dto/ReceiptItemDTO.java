package com.example.electronicstore.electronic_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptItemDTO {
    private String productName;
    private int quantity;
    private double originalPrice;
    private double discount;
    private double finalPrice;
    private List<String> appliedDeals;
}
