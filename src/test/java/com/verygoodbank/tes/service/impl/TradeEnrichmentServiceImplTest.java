package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.entity.EnrichedTrade;
import com.verygoodbank.tes.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class TradeEnrichmentServiceImplTest {

    @InjectMocks
    private TradeEnrichmentServiceImpl tradeEnrichmentService;

    @Mock
    private ProductService productService;

    @Test
    public void testEnrichTrades_ValidData() {
        when(productService.getProductNameById(anyInt())).thenReturn("Product");

        List<List<String[]>> segments = new ArrayList<>();
        List<String[]> segment1 = new ArrayList<>();
        segment1.add(new String[]{"date","product_id","currency,price"});
        segments.add(segment1);
        List<String[]> segment2 = new ArrayList<>();
        segment2.add(new String[]{"20240101", "1", "USD", "100.0"});
        segments.add(segment2);

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertEquals(1, enrichedTrades.size());
        EnrichedTrade enrichedTrade = enrichedTrades.get(0);
        assertEquals("20240101", enrichedTrade.getDate());
        assertEquals("Product", enrichedTrade.getProductName());
        assertEquals("USD", enrichedTrade.getCurrency());
        assertEquals(100.0, enrichedTrade.getPrice(), 0.001);
    }

    @Test
    public void testEnrichTrades_InvalidDate() {
        List<List<String[]>> segments = new ArrayList<>();
        List<String[]> segment1 = new ArrayList<>();
        segment1.add(new String[]{"date","product_id","currency,price"});
        segments.add(segment1);
        List<String[]> segment = new ArrayList<>();
        segment.add(new String[]{"2024-01-01", "1", "USD", "100.0"});
        segments.add(segment);

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertTrue(enrichedTrades.isEmpty());
    }

    @Test
    public void testEnrichTrades_ProductServiceReturnsNull() {
        List<List<String[]>> segments = new ArrayList<>();
        List<String[]> segment = new ArrayList<>();
        segment.add(new String[]{"20240101", "1", "USD", "100.0"});
        segments.add(segment);

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertTrue(enrichedTrades.isEmpty());
    }

    @Test
    public void testEnrichTrades_ConcurrentExecution() throws InterruptedException, ExecutionException {
        when(productService.getProductNameById(anyInt())).thenReturn("Product");

        List<List<String[]>> segments = new ArrayList<>();
        List<String[]> segment1 = new ArrayList<>();
        segment1.add(new String[]{"date","product_id","currency,price"});
        segments.add(segment1);
        List<String[]> segment3 = new ArrayList<>();
        segment3.add(new String[]{"20240101", "1", "USD", "100.0"});
        List<String[]> segment2 = new ArrayList<>();
        segment2.add(new String[]{"20240102", "2", "EUR", "200.0"});
        segments.add(segment1);
        segments.add(segment3);
        segments.add(segment2);

        List<Future<?>> futures = new ArrayList<>();
        for (List<String[]> segment : segments) {
            futures.add(tradeEnrichmentService.getExecutorService().submit(() -> {
                tradeEnrichmentService.enrichTrades(List.of(segment));
                return null;
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        assertEquals(2, tradeEnrichmentService.enrichTrades(segments).size());
    }

    @Test
    public void testEnrichTrades_EmptySegments() {
        List<List<String[]>> segments = new ArrayList<>();

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertTrue(enrichedTrades.isEmpty());
    }

    @Test
    public void testEnrichTrades_NullSegments() {
        List<List<String[]>> segments = null;

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertTrue(enrichedTrades.isEmpty());
    }

    @Test
    public void testEnrichTrades_EmptyRowsInSegment() {
        List<List<String[]>> segments = new ArrayList<>();
        segments.add(new ArrayList<>());

        List<EnrichedTrade> enrichedTrades = tradeEnrichmentService.enrichTrades(segments);

        assertTrue(enrichedTrades.isEmpty());
    }
}