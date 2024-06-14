package com.verygoodbank.tes.service;

import com.verygoodbank.tes.entity.EnrichedTrade;

import java.util.List;

public interface TradeEnrichmentService {
    List<EnrichedTrade> enrichTrades(List<List<String[]>> segments);

}
