package com.verygoodbank.tes.service.impl;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.verygoodbank.tes.entity.EnrichedTrade;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import com.verygoodbank.tes.service.TradeSegmentationService;
import com.verygoodbank.tes.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    public static final int BATCH_SIZE = 1000;
    private final TradeSegmentationService segmentationService;
    private final TradeEnrichmentService enrichmentService;

    @Autowired
    public TradeServiceImpl(TradeSegmentationService segmentationService, TradeEnrichmentService enrichmentService) {
        this.segmentationService = segmentationService;
        this.enrichmentService = enrichmentService;
    }

    @Override
    public List<String> enrichTrades(Reader tradeCsvReader) throws IOException, CsvException {
        List<String[]> rows = parseCsv(tradeCsvReader);

        List<List<String[]>> segments = segmentationService.segmentTradeCsv(rows, BATCH_SIZE);

        List<EnrichedTrade> enrichedTrades = enrichmentService.enrichTrades(segments);

        List<String> csvOutput = new ArrayList<>();
        csvOutput.add("date,product_name,currency,price");

        csvOutput.addAll(enrichedTrades.stream()
                .map(trade -> String.join(",", trade.getDate(), trade.getProductName(), trade.getCurrency(), String.valueOf(trade.getPrice())))
                .toList());

        return csvOutput;
    }

    private List<String[]> parseCsv(Reader csvReader) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(csvReader)) {
            return reader.readAll();
        }
    }
}