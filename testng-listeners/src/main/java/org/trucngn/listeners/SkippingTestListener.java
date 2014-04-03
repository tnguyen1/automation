package org.trucngn.listeners;

import java.text.MessageFormat;

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;
import org.trucngn.PropertyLoader;
import org.trucngn.UnsupportedBrowser;

/**
 * A test listener to skip tests when requirements are not met.
 * 
 * @author Truc Nguyen
 */
public class SkippingTestListener extends TestListenerAdapter {

    /**
     * Skip tests at class level (ie. skip all methods of this class).
     */
    @Override
    public void beforeConfiguration(final ITestResult tr) {

        super.beforeConfiguration(tr);

        // skip execution for unsupported browser
        final UnsupportedBrowser browserAnnotation = tr.getMethod().getConstructorOrMethod().getDeclaringClass().getAnnotation(UnsupportedBrowser.class);
        if (browserAnnotation != null) {
            unsupportedBrowserSkipExecution(tr.getMethod().getConstructorOrMethod().getDeclaringClass().getName(), browserAnnotation.value());
        }
    }

    /**
     * Skip tests at method level.
     */
    @Override
    public void onTestStart(final ITestResult result) {

        super.onTestStart(result);

        // skip execution for unsupported browser
        final UnsupportedBrowser browserAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(UnsupportedBrowser.class);
        if (browserAnnotation != null) {
            unsupportedBrowserSkipExecution(result.getMethod().getMethodName(), browserAnnotation.value());
        }
    }

    /**
     * Skip test execution if browser does not match requirement.
     * 
     * @param result
     * @param unsupportedBrowserName
     */
    private void unsupportedBrowserSkipExecution(final String testName, final String unsupportedBrowserName) {

        if (unsupportedBrowserName.equals(PropertyLoader.loadTestProperty("browser"))) {
            final String skipMessage = MessageFormat.format("Skipping <{0}> test due to unsupported browser <{1}>",
                    testName, unsupportedBrowserName);

            throw new SkipException(skipMessage);
        }
    }

}
