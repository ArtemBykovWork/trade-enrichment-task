package com.verygoodbank.tes.service.impl;


import com.opencsv.exceptions.CsvException;
import com.verygoodbank.tes.entity.EnrichedTrade;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import com.verygoodbank.tes.service.TradeSegmentationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TradeServiceImplTest {

    @InjectMocks
    private TradeServiceImpl tradeService;

    @Mock
    private TradeSegmentationService segmentationService;

    @Mock
    private TradeEnrichmentService enrichmentService;

    private File tempFile;

    @Before
    public void setUp() throws IOException {
        tempFile = File.createTempFile("test-trades", ".csv");
        PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
        writer.println("20240101,1,USD,100.0");
        writer.println("20240102,2,EUR,150.0");
        writer.close();
    }

    @After
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void enrichTrades_ValidCsvData_ReturnsEnrichedTrades() throws IOException, CsvException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"20240101", "1", "USD", "100.0"});
        rows.add(new String[]{"20240102", "2", "EUR", "150.0"});
        when(segmentationService.segmentTradeCsv(any(), anyInt())).thenReturn(List.of(rows));

        List<EnrichedTrade> expectedEnrichedTrades = new ArrayList<>();
        expectedEnrichedTrades.add(new EnrichedTrade("20240101", "Product 1", "USD", 100.0));
        expectedEnrichedTrades.add(new EnrichedTrade("20240102", "Product 2", "EUR", 150.0));
        when(enrichmentService.enrichTrades(any())).thenReturn(expectedEnrichedTrades);

        try (Reader fileReader = new FileReader(tempFile)) {
            List<String> actualEnrichedTrades = tradeService.enrichTrades(fileReader);

            List<String> expectedCsvOutput = new ArrayList<>();
            expectedCsvOutput.add("date,product_name,currency,price");
            expectedCsvOutput.add("20240101,Product 1,USD,100.0");
            expectedCsvOutput.add("20240102,Product 2,EUR,150.0");

            assertEquals(expectedCsvOutput, actualEnrichedTrades);
        }

        verify(segmentationService, times(1)).segmentTradeCsv(any(), anyInt());
        verify(enrichmentService, times(1)).enrichTrades(any());
    }
}