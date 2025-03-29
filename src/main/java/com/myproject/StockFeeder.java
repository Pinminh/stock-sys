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

    public boolean hasStockWithCode(String stockCode) {
        return stockList.stream().anyMatch(
            stock -> stock.getCode().equals(stockCode)
        );
    }

    public boolean hasViewerWithStockCode(StockViewer viewer, String stockCode) {
        List<StockViewer> viewerList = viewers.get(stockCode);
        if (viewerList == null) return false;

        return viewerList.stream().anyMatch(viewerInList -> viewerInList == viewer);
    }

    public Stock getStockWithCode(String stockCode) {
        Optional<Stock> stockOption = stockList.stream()
            .filter(stock -> stock.getCode().equals(stockCode))
            .findAny();
        return stockOption.isPresent() ? stockOption.get() : null;
    }

    public void clear() {
        stockList.clear();
        viewers.clear();
    }

    public void registerViewer(String code, StockViewer stockViewer) {
        if (!hasStockWithCode(code) || hasViewerWithStockCode(stockViewer, code)) {
            Logger.errorRegister(code);
            return;
        }

        if (viewers.get(code) == null)
            viewers.put(code, new ArrayList<StockViewer>());
        
        viewers.get(code).add(stockViewer);
    }    

    public void unregisterViewer(String code, StockViewer stockViewer) {
        if (!hasStockWithCode(code) || !hasViewerWithStockCode(stockViewer, code)) {
            Logger.errorUnregister(code);
            return;
        }

        if (viewers.get(code) == null) {
            Logger.errorUnregister(code);
            return;
        }

        viewers.get(code).remove(stockViewer);
    }

    public void notify(StockPrice stockPrice) {
        String stockCode = stockPrice.getCode();
        if (viewers.get(stockCode) == null) return;

        for (StockViewer viewer : viewers.get(stockCode))
            viewer.onUpdate(stockPrice);
    }
}
