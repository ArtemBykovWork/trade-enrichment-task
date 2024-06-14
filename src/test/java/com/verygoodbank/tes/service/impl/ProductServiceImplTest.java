package com.verygoodbank.tes.service.impl;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Before
    public void setUp() throws IOException {
        productService.init();
    }

    @Test
    public void testGetProductNameById_existingProduct() {
        int existingProductId = 123;

        String productName = productService.getProductNameById(existingProductId);

        assertEquals("Product Name 123", productName);
    }

    @Test
    public void testGetProductNameById_nonExistingProduct() {
        int nonExistingProductId = 999;

        String productName = productService.getProductNameById(nonExistingProductId);

        assertEquals(ProductServiceImpl.DEFAULT_NAME_VALUE, productName);
    }

    @Test
    public void testGetProductNameById_productWithWhitespace() {
        int productIdWithWhitespace = 456;

        String productName = productService.getProductNameById(productIdWithWhitespace);

        assertEquals("Product Name 456", productName);
    }

    @Test
    public void testGetProductNameById_productWithLeadingZeros() {
        int productIdWithLeadingZeros = 001;

        String productName = productService.getProductNameById(productIdWithLeadingZeros);

        assertEquals("Treasury Bills Domestic", productName);
    }

    @Test
    public void testGetProductNameById_productIdAsString() {
        String productIdAsString = "123";

        String productName = productService.getProductNameById(Integer.parseInt(productIdAsString));

        assertEquals("Product Name 123", productName);
    }
}