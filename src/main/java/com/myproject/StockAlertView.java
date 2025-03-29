package com.myproject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class StockAlertView implements StockViewer {
    private double alertThresholdHigh;
    private double alertThresholdLow;
    private Map<String, Double> lastAlertedPrices = new HashMap<>(); // Stores last alerted price per stock

    public StockAlertView(double highThreshold, double lowThreshold) {
        updateThreshold(highThreshold, lowThreshold);
    }

    public void updateThreshold(double highThreshold, double lowThreshold) {
        alertThresholdHigh = highThreshold;
        alertThresholdLow = lowThreshold;
    }

    @Override
    public void onUpdate(StockPrice stockPrice) {
        String stockCode = stockPrice.getCode();
        double newPrice = stockPrice.getAvgPrice();
        if (isOutOfThreshold(newPrice) && isUpdateFeasible(stockCode, newPrice)) {
            lastAlertedPrices.put(stockCode, newPrice);
            Logger.logAlert(stockCode, newPrice);
        }
    }

    public boolean isOutOfThreshold(double price) {
        return price < alertThresholdLow || price > alertThresholdHigh;
    }

    public Map<String, Double> get() {
        return Collections.unmodifiableMap(lastAlertedPrices);
    } 

    public void clear() {
        lastAlertedPrices.clear();
    }

    private boolean isUpdateFeasible(String stockCode, double newPrice) {
        if (lastAlertedPrices.get(stockCode) == null) return true;
        return !lastAlertedPrices.get(stockCode).equals(newPrice);
    }

    // private void alertAbove(String stockCode, double price) {
    //     // Call Logger to log the alert
    //     Logger.notImplementedYet("alertAbove");
    // }

    // private void alertBelow(String stockCode, double price) {
    //     // Call Logger to log the alert
    //     Logger.notImplementedYet("alertBelow");
    // }
}
