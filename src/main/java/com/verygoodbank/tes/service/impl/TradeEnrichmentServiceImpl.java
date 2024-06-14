package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.entity.EnrichedTrade;
import com.verygoodbank.tes.service.ProductService;
import com.verygoodbank.tes.service.TradeEnrichmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final Logger LOGGER = Logger.getLogger(TradeEnrichmentServiceImpl.class.getName());


    private ProductService productService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private ExecutorService executorService;

    @Autowired
    public TradeEnrichmentServiceImpl(ProductService productService) {
        this.productService = productService;
        this.executorService = createExecutorService();
    }

    public List<EnrichedTrade> enrichTrades(List<List<String[]>> segments) {
        if (segments == null) {
            LOGGER.log(Level.WARNING, "Segments is null, returning empty list.");
            return Collections.emptyList();
        }

        List<EnrichedTrade> enrichedTrades = new CopyOnWriteArrayList<>();
        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (List<String[]> segment : segments) {
            for (int i = 0; i < segment.size(); i++) {
                String[] row = segment.get(i);
                int finalI = i;
                try {
                    futures.add(executorService.submit(() -> {
                        if (counter.getAndIncrement() == 0 && finalI == 0) {
                            return;
                        }
                        EnrichedTrade enrichedTrade = enrichTrade(row);
                        if (enrichedTrade != null) {
                            enrichedTrades.add(enrichedTrade);
                        }
                    }));
                } catch (RejectedExecutionException e) {
                    executorService = createExecutorService();
                    futures.add(executorService.submit(() -> {
                    }));
                }
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }

        shutdownExecutorService();

        return enrichedTrades;
    }


    private EnrichedTrade enrichTrade(String[] row) {
        try {
            String dateStr = row[0];
            if (!isValidDate(dateStr, DATE_FORMAT)) {
                LOGGER.log(Level.WARNING, "Invalid date format for row: " + Arrays.toString(row));
                return null;
            }

            String date = dateStr;
            int productId = Integer.parseInt(row[1]);
            String currency = row[2];
            double price = Double.parseDouble(row[3]);

            String productName = productService.getProductNameById(productId);

            return new EnrichedTrade(date, productName, currency, price);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    private boolean isValidDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}