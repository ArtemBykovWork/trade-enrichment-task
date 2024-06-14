package com.verygoodbank.tes.service;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public interface TradeService {
    List<String> enrichTrades(Reader tradeCsvReader) throws IOException, CsvException;
}
