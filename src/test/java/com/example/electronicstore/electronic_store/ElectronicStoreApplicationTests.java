package com.example.electronicstore.electronic_store;

import com.example.electronicstore.electronic_store.entities.Product;
import com.example.electronicstore.electronic_store.services.BasketService;
import com.example.electronicstore.electronic_store.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ElectronicStoreApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private BasketService basketService;

	@Test
	void contextLoads() {
	}



}
