package org.trucngn.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.trucngn.TestRetryAnalyzer;

/**
 * Test listener to remove duplicate retried attempts (retry analyzer).
 * 
 * @author Truc Nguyen
 * @see http://stackoverflow.com/questions/13131912/testng-retrying-failed-tests-doesnt-output-the-correct-test-results
 */
public class RetryTestListener extends TestListenerAdapter {

    /**
     * Add our retry analyzer to all test methods.
     */
    @Override
    public final void onStart(final ITestContext pTestContext) {

        super.onStart(pTestContext);

        // add retry analyzer to all test methods
        for (final ITestNGMethod m : pTestContext.getAllTestMethods()) {
            m.setRetryAnalyzer(new TestRetryAnalyzer());
        }
    }

    /**
     * Remove failed attempts (retry analyzer).
     * 
     * 1. Collect all passed test. If i encounter a failed test with at least one passed test i remove the failed test (That would cover case 2 and 3 from
     * above)
     * 2. Iterate over all failed test. If i encounter a failed test which previously failed i remove the current failed result. (That would cover case 3 and 4
     * actually). That also means i will only keep the first failed result if there are several failed results.
     * 
     * @see http://stackoverflow.com/questions/13131912/testng-retrying-failed-tests-doesnt-output-the-correct-test-results
     */
    @Override
    public final void onFinish(final ITestContext pTestContext) {

        super.onFinish(pTestContext);

        // List of test results which we will delete later
        final List<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();

        // collect all id's from passed test
        final Set<Integer> passedTestIds = new HashSet<Integer>();
        for (final ITestResult passedTest : pTestContext.getPassedTests().getAllResults()) {
            passedTestIds.add(getId(passedTest));
        }

        final Set<Integer> failedTestIds = new HashSet<Integer>();
        for (final ITestResult failedTest : pTestContext.getFailedTests().getAllResults()) {

            // id = class + method + dataprovider
            final int failedTestId = getId(failedTest);

            // if we saw this test as a failed test before we mark as to be deleted
            // or delete this failed test if there is at least one passed version
            if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestIds.add(failedTestId);
            }
        }

        // finally delete all tests that are marked
        for (final Iterator<ITestResult> iterator = pTestContext.getFailedTests().getAllResults().iterator(); iterator.hasNext();) {
            final ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                iterator.remove();
            }
        }

        // TNG: force SKIP state for skipped tests
        for (final ITestResult r : pTestContext.getSkippedTests().getAllResults()) {
            if (r.getStatus() != ITestResult.SKIP) {
                r.setStatus(ITestResult.SKIP);
            }
        }
    }

    /**
     * Hash function to identify a test result.
     * 
     * @param pTestResult
     * @return
     */
    private static int getId(final ITestResult pTestResult) {
        int id = pTestResult.getTestClass().getName().hashCode();
        id = 31 * id + pTestResult.getMethod().getMethodName().hashCode();
        id = 31 * id + (pTestResult.getParameters() != null ? Arrays.hashCode(pTestResult.getParameters()) : 0);
        return id;
    }

    /**
     * Set test result to SKIP as long as there are more retry attempts left.
     */
    @Override
    public final void onTestFailure(final ITestResult pTestResult) {

        super.onTestFailure(pTestResult);

        final TestRetryAnalyzer retryAnalyzer = (TestRetryAnalyzer) pTestResult.getMethod().getRetryAnalyzer();
        if (retryAnalyzer != null && retryAnalyzer.hasRetryAttemptLeft()) {
            // set test as Skipped
            pTestResult.setStatus(ITestResult.SKIP);

            // print stack trace
            final Throwable t = pTestResult.getThrowable();
            if (t != null) {
                t.printStackTrace();
            }
        }
    }
}
