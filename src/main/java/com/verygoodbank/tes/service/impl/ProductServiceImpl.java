package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.service.ProductService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductServiceImpl implements ProductService {

    public static final String DEFAULT_NAME_VALUE = "Missing Product Name";
    private final ConcurrentHashMap<Integer, String> productMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws IOException {
        loadProducts("product.csv");
    }

    @Override
    public void loadProducts(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int productId = Integer.parseInt(parts[0].trim());
                    String productName = parts[1].trim();
                    productMap.put(productId, productName);
                }
            }
        }
    }

    @Override
    public String getProductNameById(int productId) {
        return productMap.getOrDefault(productId, DEFAULT_NAME_VALUE);
    }

}