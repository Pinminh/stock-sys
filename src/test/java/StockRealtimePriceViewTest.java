import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.myproject.StockRealtimePriceView;
import com.myproject.StockPrice;


public class StockRealtimePriceViewTest {
    private static StockRealtimePriceView viewer;
    private double doubleError = 0.00001;
    
    @BeforeClass
    public static void setupBeforeAll() {
        viewer = new StockRealtimePriceView();
        System.out.println("Test StockRealtimePriceView...");
    }

    @Before
    public void setupBeforeEach() {
        viewer.clear();
    }

    @Test
    public void updatePriceWhenHistoryNotFound() {
        StockPrice stockPrice = new StockPrice("codeA", 10.0, 25, 1743250124);
        viewer.onUpdate(stockPrice);
        Map<String, Double> priceMap = viewer.get("codeA");
        assertEquals(10.0, priceMap.get("codeA"), doubleError);
    }

    @Test
    public void updatePriceWhenHistoryChanged() {
        StockPrice[] prices = {
            new StockPrice("codeA", 10.0, 25, 1743250124),
            new StockPrice("codeB", 9.0, 25, 1743250136),
            new StockPrice("codeA", 10.01, 25, 1743250181),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get("codeA");
        assertEquals(10.01, priceMap.get("codeA"), doubleError);
        assertEquals(9.0, priceMap.get("codeB"), doubleError);
    }

    @Test
    public void updatePriceWhenHistoryChangedManyTimes() {
        StockPrice[] prices = {
            new StockPrice("codeA", 10.0, 25, 1743250124),
            new StockPrice("codeA", 9.0, 25, 1743250136),
            new StockPrice("codeA", 10.01, 25, 1743250181),
            new StockPrice("codeA", 10.0, 25, 1743250124),
            new StockPrice("codeA", 1.0, 25, 1743250136),
            new StockPrice("codeA", 2.543, 25, 1743250181),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get("codeA");
        assertEquals(2.543, priceMap.get("codeA"), doubleError);
    }

    @Test
    public void shouldNotUpdatePriceWhenHistoryNotChanged() {
        StockPrice[] prices = {
            new StockPrice("codeA", 10.0, 25, 1743250124),
            new StockPrice("codeA", 10.0, 25, 1743250136),
            new StockPrice("codeA", 10.0, 25, 1743250181),
            new StockPrice("codeA", 10.0, 25, 1743250124),
        };
        for (StockPrice price : prices) viewer.onUpdate(price);
        Map<String, Double> priceMap = viewer.get("codeA");
        assertEquals(10.0, priceMap.get("codeA"), doubleError);
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
