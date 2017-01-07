package org.catchmycode.automation.harness;

import org.catchmycode.automation.common.Assertions;
import org.catchmycode.automation.common.IOUtils;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

public abstract class AbstractBaseTestHarness implements Assertions, IOUtils {

    private ITestContext testContext;
    private String path = null;

    /**
     * Adds the test context to each and every test if needed
     *
     * @param testContext the {@link org.testng.ITestContext}
     *
     */
    @BeforeClass
    public void initializeTestContext(ITestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * Get the test context for this test
     *
     * @return the {@link org.testng.ITestContext}
     */
    public ITestContext getTestContext() {
        return testContext;
    }

    /**
     * Getter for the application path
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Fluent API call to allow for clients to pass in an optional application path like /doSomething
     *
     * @param path the path
     * @return the {@link AbstractBaseTestHarness}
     */
    public AbstractBaseTestHarness withPath(String path) {
        this.path = path;
        return this;
    }

    public <T extends AbstractBaseTestHarness> T withPath(final String path, Class<T> clazz) {
        this.path = path;
        return clazz.cast(this);
    }

}
