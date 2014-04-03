package org.trucngn;

import static org.assertj.core.api.Assertions.*;

import org.pmw.tinylog.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * A test class to demonstrate testng-listeners examples.
 * 
 * @author Truc Nguyen
 */
@Test
public class ListenerDemoTest extends TestBase {

    /** test counter to illustrate RetryTestListener. */
    private int counter;

    // Configuration methods to illustrate LoggingTestListener

    @BeforeClass
    protected void beforeClass() {
        Logger.info("My before class message");
    }

    @BeforeMethod
    protected void beforeMethod() {
        Logger.info("My before method message");
    }

    @AfterMethod
    protected void afterMethod() {
        Logger.info("My after method message");
    }

    @AfterClass
    protected void afterClass() {
        Logger.info("My after class message");
    }

    // Actual test methods

    @Test(priority = 1)
    public final void tryAgain() {
        this.counter++;
        assertThat(this.counter).isEqualTo(2);
    }

    @Test(priority = 2)
    public final void helloWorld() {
        Logger.info("Hello world!");
    }

    @Test(priority = 3)
    @UnsupportedBrowser("ie")
    public final void unsupportedBrowser() {
        Logger.info("I can't run on my favorite browser !!");
    }

}
