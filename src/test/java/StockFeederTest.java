import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.Map;

import com.myproject.StockFeeder;
import com.myproject.StockTickerView;
import com.myproject.Stock;
import com.myproject.StockAlertView;
import com.myproject.StockPrice;
import com.myproject.StockRealtimePriceView;


public class StockFeederTest {
    private static StockFeeder feeder;
    private double doubleError = 0.00001;

    @BeforeClass
    public static void setupBeforeAll() {
        feeder = StockFeeder.getInstance();
        System.out.println("Test StockFeeder...");
    }

    @Before
    public void setupBeforeEach() {
        feeder.clear();
    }

    @Test
    public void singletonFeederShouldBeTheSame() {
        StockFeeder newFeeder = StockFeeder.getInstance();
        assertSame(feeder, newFeeder);
    }

    @Test
    public void singletonFeederShouldBeTheSameAfterManyGetInstance() {
        StockFeeder newFeeder = StockFeeder.getInstance();
        for (int iter = 0; iter < 10; ++iter) {
            newFeeder = StockFeeder.getInstance();
        }
        assertSame(feeder, newFeeder);
    }

    @Test
    public void stockListShouldBeEmptyWhenNotAddStock() {
        assertTrue(feeder.hasNoStock());
    }

    @Test
    public void stockListShouldBeNotEmptyWhenStockAdded() {
        feeder.addStock(new Stock("codeZ", "nameZ"));
        assertFalse(feeder.hasNoStock());
    }

    @Test
    public void addStockReplaceDuplicatedStockCode() {
        feeder.addStock(new Stock("codeA", "nameA"));
        feeder.addStock(new Stock("codeA", "nameB"));
        feeder.addStock(new Stock("codeA", "nameC"));
        Stock stockA = feeder.getStockWithCode("codeA");
        assertEquals("nameC", stockA.getName());
    }

    @Test
    public void addStockWithTwoDistinctStock() {
        feeder.addStock(new Stock("codeM", "nameM"));
        feeder.addStock(new Stock("codeN", "nameN"));
        assertNotNull(feeder.getStockWithCode("codeM"));
        assertNotNull(feeder.getStockWithCode("codeN"));
    }

    @Test
    public void hasStockWithCodeWorksOnNewStock() {
        feeder.addStock(new Stock("codeF", "nameF"));
        assertTrue(feeder.hasStockWithCode("codeF"));
    }

    @Test
    public void hasStockWithCodeWorksOnNonExistingStock() {
        assertFalse(feeder.hasStockWithCode("weird_code_here"));
    }

    @Test
    public void notFoundViewerWhenNotRegisterGivenStockViewer() {
        StockTickerView tickerView = new StockTickerView();
        assertFalse(feeder.hasViewerWithStockCode(tickerView, "codeX"));
    }

    @Test
    public void viewerShouldBeFoundWhenRegisteredWithStockFeeder() {
        Stock stock = new Stock("stockCode", "stockName");
        feeder.addStock(stock);
        StockTickerView viewer = new StockTickerView();
        feeder.registerViewer("stockCode", viewer);
        assertTrue(feeder.hasViewerWithStockCode(viewer, "stockCode"));
    }

    @Test
    public void viewerShouldNotRegisterWhenStockNotFound() {
        StockTickerView viewer = new StockTickerView();
        feeder.registerViewer("stockAAA", viewer);
        assertFalse(feeder.hasViewerWithStockCode(viewer, "stockAAA"));
    }

    @Test
    public void viewerShouldNotRegisterWhenViewerDuplicated() {
        Stock stock = new Stock("stockFFF", "nameFFF");
        feeder.addStock(stock);
        StockTickerView viewer = new StockTickerView();
        feeder.registerViewer("stockFFF", viewer);
        feeder.registerViewer("stockFFF", viewer);
        assertTrue(feeder.hasViewerWithStockCode(viewer, "stockFFF"));
    }

    @Test
    public void viewerShouldNotUnregisterWhenStockNotFound() {
        Stock stock = new Stock("stockFFF", "nameFFF");
        feeder.addStock(stock);
        StockTickerView viewer = new StockTickerView();
        feeder.registerViewer("stockFFF", viewer);
        feeder.unregisterViewer("stockAAA", viewer);
        assertTrue(feeder.hasViewerWithStockCode(viewer, "stockFFF"));
    }

    @Test
    public void viewerShouldNotUnregisterWhenNotRegistered() {
        Stock stock = new Stock("StockFFF", "nameFFF");
        feeder.addStock(stock);
        StockTickerView viewer = new StockTickerView();
        feeder.unregisterViewer("stockFFF", viewer);
        assertFalse(feeder.hasViewerWithStockCode(viewer, "stockFFF"));
    }

    @Test
    public void viewerCanUnregisterWhenPreviouslyRegistered() {
        feeder.addStock(new Stock("codeE", "codeE"));
        StockTickerView viewer = new StockTickerView();
        feeder.registerViewer("codeE", viewer);
        assertTrue(feeder.hasViewerWithStockCode(viewer, "codeE"));
        feeder.unregisterViewer("codeE", viewer);
        assertFalse(feeder.hasViewerWithStockCode(viewer, "codeE"));
    }

    @Test
    public void notifyAlertViewWithMultipleStocks() {
        feeder.addStock(new Stock("codeA", "nameA"));
        feeder.addStock(new Stock("codeB", "nameB"));
        StockAlertView alertView = new StockAlertView(60, 20);
        feeder.registerViewer("codeA", alertView);
        feeder.registerViewer("codeB", alertView);
        StockPrice[] prices = {
            new StockPrice("codeA", 30.0, 25, 1743250124),
            new StockPrice("codeA", 40.0, 25, 1743250136),
            new StockPrice("codeB", 10.0, 25, 1743250181),
            new StockPrice("codeA", 62.0, 25, 1743250124),
            new StockPrice("codeB", 10.0, 25, 1743250181),
            new StockPrice("codeA", 52.0, 25, 1743250124),
        };
        for (StockPrice price : prices) feeder.notify(price);
        Map<String, Double> priceMap = alertView.get();
        assertEquals(62.0, priceMap.get("codeA"), doubleError);
        assertEquals(10.0, priceMap.get("codeB"), doubleError);
    }

    @Test
    public void notifyMultipleViewsWithMultipleStocks() {
        feeder.addStock(new Stock("codeA", "nameA"));
        feeder.addStock(new Stock("codeB", "nameB"));
        StockAlertView alertView = new StockAlertView(60, 20);
        feeder.registerViewer("codeA", alertView);
        feeder.registerViewer("codeB", alertView);
        StockTickerView tickerView = new StockTickerView();
        feeder.registerViewer("codeA", tickerView);
        feeder.registerViewer("codeB", tickerView);
        StockRealtimePriceView realtimeView = new StockRealtimePriceView();
        feeder.registerViewer("codeA", realtimeView);
        feeder.registerViewer("codeB", realtimeView);
        StockPrice[] prices = {
            new StockPrice("codeA", 70.0, 25, 1743250124),
            new StockPrice("codeA", 40.0, 25, 1743250136),
            new StockPrice("codeA", 40.0, 25, 1743250136),
            new StockPrice("codeB", 10.0, 25, 1743250181),
            new StockPrice("codeA", 62.0, 25, 1743250194),
            new StockPrice("codeB", 30.0, 25, 1743250201),
            new StockPrice("codeA", 52.0, 25, 1743250224),
            new StockPrice("codeA", 52.0, 25, 1743250224),
        };
        for (StockPrice price : prices) feeder.notify(price);
        Map<String, Double> alertMap = alertView.get();
        Map<String, Double> realtimeMap = realtimeView.get();
        assertEquals(62.0, alertMap.get("codeA"), doubleError);
        assertEquals(10.0, alertMap.get("codeB"), doubleError);
        assertEquals(52.0, realtimeMap.get("codeA"), doubleError);
        assertEquals(30.0, realtimeMap.get("codeB"), doubleError);
    }

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            System.out.println("\n< Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            System.out.println("> Finished test: " + description.getMethodName() + "\n");
        }
    };
}
