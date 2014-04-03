package org.trucngn.listeners;

import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;
import org.trucngn.PropertyLoader;
import org.trucngn.TestRetryAnalyzer;

/**
 * A test listener to abort all subsequent tests uppon first failure.
 * 
 * @author Truc Nguyen
 */
public class AbortTestListener extends TestListenerAdapter {

    private static final boolean ABORT_ON_FAILURE =
            Boolean.parseBoolean(PropertyLoader.loadTestProperty("testng.abort.on.failure"));

    /** Encountered failure if any so far. */
    private static ITestResult failedTestResult = null;

    /** Shall we skip configuration methods? */
    private static boolean skipConfiguration = false;

    @Override
    public final void onConfigurationFailure(final ITestResult pTestResult) {

        super.onConfigurationFailure(pTestResult);

        // record the failure
        final TestRetryAnalyzer retryAnalyzer = (TestRetryAnalyzer) pTestResult.getMethod().getRetryAnalyzer();
        if (retryAnalyzer != null && !retryAnalyzer.hasRetryAttemptLeft()) {
            failedTestResult = pTestResult;
        }
    }

    @Override
    public final void onTestFailure(final ITestResult pTestResult) {

        super.onTestFailure(pTestResult);

        // record the failure
        final TestRetryAnalyzer retryAnalyzer = (TestRetryAnalyzer) pTestResult.getMethod().getRetryAnalyzer();
        if (!retryAnalyzer.hasRetryAttemptLeft()) {
            failedTestResult = pTestResult;
        }
    }

    @Override
    public final void beforeConfiguration(final ITestResult pTestResult) {

        super.beforeConfiguration(pTestResult);

        // skip configuration for all subsequent test classes
        if (pTestResult.getMethod().isBeforeClassConfiguration() && abortTest()) {
            skipConfiguration = true;
        }

        if (skipConfiguration) {
            throw new SkipException("Configuration skipped due to previous failure " + failedTestResult);
        }
    }

    @Override
    public final void onTestStart(final ITestResult pTestResult) {

        super.onTestStart(pTestResult);

        // skip test if a previous failure has been encountered
        if (abortTest()) {
            throw new SkipException("Test skipped due to previous failure " + failedTestResult);
        }
    }

    /**
     * Shall we abort subsequent tests?
     */
    private static boolean abortTest() {
        if (ABORT_ON_FAILURE) {
            return failedTestResult != null;
        } else {
            return false;
        }
    }

}
