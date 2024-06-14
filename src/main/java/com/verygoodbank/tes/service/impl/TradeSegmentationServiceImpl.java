package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.service.TradeSegmentationService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeSegmentationServiceImpl implements TradeSegmentationService {

    @Override
    public List<List<String[]>> segmentTradeCsv(List<String[]> rows, int batchSize) {
        return Lists.partition(rows, batchSize);
    }
}
