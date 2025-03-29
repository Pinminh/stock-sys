package com.myproject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StockRealtimePriceView implements StockViewer {
    private final Map<String, Double> lastPrices = new HashMap<>();

    @Override
    public void onUpdate(StockPrice stockPrice) {
        String stockCode = stockPrice.getCode();
        double newPrice = stockPrice.getAvgPrice();
        if (lastPrices.get(stockCode) == null || !lastPrices.get(stockCode).equals(newPrice)) {
            lastPrices.put(stockCode, newPrice);
            Logger.logRealtime(stockCode, newPrice);
        }
    }

    public Map<String, Double> get() {
        return Collections.unmodifiableMap(lastPrices);
    }

    public void clear() {
        lastPrices.clear();
    }
}
