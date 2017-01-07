package org.catchmycode.automation.harness;


import org.catchmycode.automation.common.TestRecord;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

public interface DataDriven {

    /**
     * Requires each test to author a verification step
     *
     * @param data from the data set as a string array via ellipses
     */
    @Test(dataProvider = "testData")
    void verifyData(TestRecord data);

    /**
     * Requires each test case to implement a data set
     *
     * @return the data set
     */
    @DataProvider(name = "testData")
    Iterator<Object[]> createData();
}
