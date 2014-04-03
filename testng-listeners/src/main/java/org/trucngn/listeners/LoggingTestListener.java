package org.trucngn.listeners;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 * TestNG test listener which logs test execution.
 * 
 * @author Truc Nguyen
 */
public class LoggingTestListener extends TestListenerAdapter {

    private static final char TEST_CHAR = '#';

    private static final char CONFIGURATION_CHAR = '-';

    private static final String TEST = "Test";

    private static final String CONFIGURATION = "Configuration";

    private static final String START = "START";

    private static final String END = "END";

    private static final String SKIPPED = "Skipped";

    private static final String FAILURE = "Failure";

    private static final String SUCCESS = "Success";

    /**
     * Common logging method.
     * 
     * @param pResult
     * @param pPhaseString
     *            eg. START, END
     * @param pStatusString
     *            eg. Succeeded, Failed, Skipped,
     * @param pCharacter
     *            character to be repeated in log message
     */
    private void logResult(final ITestResult pResult, final String pPhaseString, final String pStatusString, final char pCharacter) {
        // log exception in case of failure
        if (FAILURE.equals(pStatusString)) {
            Logger.error(pResult.getThrowable());
        }

        // log step
        final String paddedString = StringUtils.repeat(pCharacter, 20);
        String className = pResult.getTestClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        final Object[] params = {
                paddedString, pPhaseString, pStatusString, className, pResult.getName(), paddedString
        };
        Logger.info("|{0}> {1} @{2} {3}.{4} <{5}|", params);

    }

    /**
     * Log to TestNG reporter.
     * 
     * @param pResult
     * @param pSuffix
     */
    private void logToReporter(final ITestResult pResult, final String pSuffix) {
        final Date startDate = new Date(pResult.getStartMillis());
        final Date endDate = new Date(pResult.getEndMillis());
        final String testFullName = pResult.getTestClass().getName() + "." + pResult.getName();

        Reporter.log("[" + startDate + "] Start test: " + testFullName);
        Reporter.log("[" + endDate + "] Test " + pSuffix);
    }

    @Override
    public final void beforeConfiguration(final ITestResult pResult) {
        logResult(pResult, START, CONFIGURATION, CONFIGURATION_CHAR);
    }

    @Override
    public final void onConfigurationSuccess(final ITestResult pResult) {
        logResult(pResult, END, SUCCESS, CONFIGURATION_CHAR);
    }

    @Override
    public final void onConfigurationFailure(final ITestResult pResult) {
        logResult(pResult, END, FAILURE, CONFIGURATION_CHAR);
    }

    @Override
    public final void onConfigurationSkip(final ITestResult pResult) {
        logResult(pResult, END, SKIPPED, CONFIGURATION_CHAR);
    }

    @Override
    public final void onTestStart(final ITestResult pResult) {
        logResult(pResult, START, TEST, TEST_CHAR);
    }

    @Override
    public final void onTestSuccess(final ITestResult pResult) {
        logResult(pResult, END, SUCCESS, TEST_CHAR);
        logToReporter(pResult, SUCCESS);
    }

    @Override
    public final void onTestFailure(final ITestResult pResult) {
        logResult(pResult, END, FAILURE, TEST_CHAR);
        logToReporter(pResult, FAILURE);
    }

    @Override
    public final void onTestSkipped(final ITestResult pResult) {
        logResult(pResult, END, SKIPPED, TEST_CHAR);
        logToReporter(pResult, SKIPPED);
    }

}
