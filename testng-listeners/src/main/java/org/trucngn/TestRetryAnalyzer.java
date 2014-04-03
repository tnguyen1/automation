package org.trucngn;

import org.pmw.tinylog.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * A simple retry analyzer which re-run a flakey test immediately.
 * 
 * @author Truc Nguyen
 */
public class TestRetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = Integer.valueOf(PropertyLoader.loadTestProperty("testng.retry.count"));

    /** Current number of retry attempts left. */
    private int nbRetryLeft = MAX_RETRY_COUNT;

    /**
     * Retry maxRetryCount times to fight against flakey tests!
     * 
     * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
     */
    @Override
    public final boolean retry(final ITestResult pTestResult) {

        final String testFullName = pTestResult.getTestClass().getName() + "." + pTestResult.getName();

        if (hasRetryAttemptLeft()) {
            this.nbRetryLeft--;
            Logger.warn("|++++++++++ RETRY @Test {0} / {1} attempt left ++++++++++|", testFullName, this.nbRetryLeft);
            return true;
        }

        Logger.warn("Reached maximum retry count ({0}) for test [{1}]", MAX_RETRY_COUNT, testFullName);
        return false;
    }

    /**
     * Are there retry attempts left?
     * 
     * @return
     */
    public final boolean hasRetryAttemptLeft() {
        return this.nbRetryLeft > 0;
    }

}
