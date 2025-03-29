import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.myproject.StockAlertView;
import com.myproject.StockPrice;


public class StockAlertViewTest {
    private static StockAlertView viewer;
    private double doubleError = 0.00001;
    
    @BeforeClass
    public static void setupBeforeAll() {
        viewer = new StockAlertView(0, 0);
        System.out.println("Test StockAlertView...");
    }

    @Before
    public void setupBeforeEach() {
        viewer.clear();
    }

    @Test
    public void updateLastAlertPriceWhenOutOfThreshold() {
        viewer.updateThreshold(40, 20);
        StockPrice[] prices = {
            new StockPrice("codeA", 60.0, 25, 1743250124),
            new StockPrice("codeA", 30.0, 25, 1743250124),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get();
        assertEquals(60.0, priceMap.get("codeA"), doubleError);
    }

    @Test
    public void updateLastAlertPriceWhenOutOfThresholdMultipleStock() {
        viewer.updateThreshold(40, 20);
        StockPrice[] prices = {
            new StockPrice("codeA", 60.0, 25, 1743250124),
            new StockPrice("codeB", 30.0, 25, 1743250124),
            new StockPrice("codeA", 32.0, 25, 1743250124),
            new StockPrice("codeB", 10.0, 25, 1743250124),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get();
        assertEquals(60.0, priceMap.get("codeA"), doubleError);
        assertEquals(10.0, priceMap.get("codeB"), doubleError);
    }

    @Test
    public void shouldNotUpdateLastAlertPriceWhenOutOfThresholdNotFound() {
        viewer.updateThreshold(100, 10);
        StockPrice[] prices = {
            new StockPrice("codeA", 60.0, 25, 1743250124),
            new StockPrice("codeB", 30.0, 25, 1743250124),
            new StockPrice("codeA", 32.0, 25, 1743250124),
            new StockPrice("codeB", 25.0, 25, 1743250124),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get();
        assertNull(priceMap.get("codeA"));
    }

    @Test
    public void shouldNotUpdateLastAlertPriceWhenOutOfThresholdNotFoundWithPreviousHistory() {
        viewer.updateThreshold(50, 10);
        StockPrice[] prices = {
            new StockPrice("codeA", 60.0, 25, 1743250124),
            new StockPrice("codeA", 30.0, 25, 1743250124),
            new StockPrice("codeA", 12.0, 25, 1743250124),
            new StockPrice("codeA", 25.0, 25, 1743250124),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get();
        assertEquals(60.0, priceMap.get("codeA"), doubleError);
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
