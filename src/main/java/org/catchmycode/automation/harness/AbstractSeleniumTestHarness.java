package org.catchmycode.automation.harness;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public abstract class AbstractSeleniumTestHarness extends AbstractBaseTestHarness {

    @BeforeTest
    public abstract void initializeWebDriver();

    @AfterTest
    public abstract void closeWebDriver();
}
