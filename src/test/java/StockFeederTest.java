import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.myproject.StockFeeder;


public class StockFeederTest {
    private static StockFeeder feeder;

    @BeforeClass
    public static void setupBeforeAll() {
        feeder = StockFeeder.getInstance();
        System.out.println("Test StockFeeder...");
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

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            System.out.println("Finished test: " + description.getMethodName());
        }
    };
}
