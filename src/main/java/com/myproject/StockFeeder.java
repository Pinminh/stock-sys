package com.myproject;

import java.util.*;

public class StockFeeder {
    private List<Stock> stockList = new ArrayList<>();
    private Map<String, List<StockViewer>> viewers = new HashMap<>();
    private static StockFeeder instance = null;

    private StockFeeder() {}

    public static StockFeeder getInstance() {
        if (instance == null) {
            instance = new StockFeeder();
            return instance;
        }
        return instance;
    }

    public void addStock(Stock stock) {
        String newStockCode = stock.getCode();
        stockList.removeIf(
            oldStock -> oldStock.getCode().equals(newStockCode)
        );
        stockList.add(stock);
    }

    public boolean hasNoStock() {
        return stockList.isEmpty();
    }

    public Stock getStockWithCode(String stockCode) {
        Optional<Stock> stockOption = stockList.stream()
            .filter(stock -> stock.getCode().equals(stockCode))
            .findAny();
        return stockOption.isPresent() ? stockOption.get() : null;
    }

    public void registerViewer(String code, StockViewer stockViewer) {
        // TODO: Implement registration logic, including checking stock existence
    }    

    public void unregisterViewer(String code, StockViewer stockViewer) {
        // TODO: Implement unregister logic, including error logging
    }

    public void notify(StockPrice stockPrice) {
        // TODO: Implement notifying registered viewers about price updates
    }
}
