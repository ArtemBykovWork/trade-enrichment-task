package com.verygoodbank.tes.service;

import java.io.IOException;

public interface ProductService {
    void loadProducts(String fileName) throws IOException;
    String getProductNameById(int productId);
}
