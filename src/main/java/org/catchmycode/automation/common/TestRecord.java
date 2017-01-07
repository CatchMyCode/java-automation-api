package org.catchmycode.automation.common;

import java.util.Arrays;

/**
 * Class used by testng to wrap a list of String parameters to test cases
 * <p>
 * The driver for this is so testng can call toString() and we can see the message
 */
public class TestRecord {

    String[] data;

    /**
     * Initializing constructor
     *
     * @param data the data
     */
    public TestRecord(String[] data) {
        this.data = data;
    }

    /**
     * Get the list of data parameters passed into the test
     *
     * @return the data
     */
    public String[] getData() {
        return data;
    }

    @Override
    public String toString() {
        if (data != null) {
            return Arrays.toString(data);
        }
        return "n/a";
    }
}
