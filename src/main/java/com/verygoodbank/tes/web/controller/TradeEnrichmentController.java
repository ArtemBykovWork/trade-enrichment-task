package com.verygoodbank.tes.web.controller;

import com.opencsv.exceptions.CsvException;
import com.verygoodbank.tes.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {

    private final TradeService tradeService;

    @Autowired
    public TradeEnrichmentController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/enrich")
    public ResponseEntity<String> enrichTrades(@RequestParam("file") MultipartFile file) {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
            List<String> enrichedTrades = tradeService.enrichTrades(reader);
            String response = String.join("\n", enrichedTrades);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body(response);
        } catch (IOException | CsvException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}