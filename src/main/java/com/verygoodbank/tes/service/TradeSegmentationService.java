package com.verygoodbank.tes.service;

import java.util.List;

public interface TradeSegmentationService {
    List<List<String[]>> segmentTradeCsv(List<String[]> rows, int batchSize);
}
