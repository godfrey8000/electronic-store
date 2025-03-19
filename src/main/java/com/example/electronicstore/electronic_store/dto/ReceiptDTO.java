package com.example.electronicstore.electronic_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptDTO {
    private List<ReceiptItemDTO> items;
    private double totalPrice;
    private double totalDiscount;
}
