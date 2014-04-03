package org.trucngn;

import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Listeners;
import org.trucngn.listeners.AbortTestListener;
import org.trucngn.listeners.LoggingTestListener;
import org.trucngn.listeners.RetryTestListener;
import org.trucngn.listeners.SkippingTestListener;

/**
 * Common base test class.
 * 
 * @author Truc Nguyen
 */
@Listeners({ AbortTestListener.class, LoggingTestListener.class, RetryTestListener.class, SkippingTestListener.class })
public class TestBase {

    /** test.properties */
    protected final Properties testProperties = new Properties();

    /**
     * Default Constructor.
     */
    public TestBase() {

        // load test.properties resource
        loadTestProperties();
    }

    /**
     * Load test.properties resource.
     * System property overrides configuration file.
     */
    private void loadTestProperties() {
        try {
            this.testProperties.load(getClass().getResourceAsStream("/test.properties"));
        } catch (final IOException ioe) {
            throw new Error(ioe);
        }
        String key, value;
        for (final Object keyObject : this.testProperties.keySet()) {
            key = (String) keyObject;
            value = System.getProperty(key);
            if (value != null) {
                this.testProperties.setProperty(key, value);
            }
        }
    }

}
