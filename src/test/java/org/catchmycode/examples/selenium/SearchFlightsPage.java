package org.catchmycode.examples.selenium;

import org.catchmycode.automation.selenium.PageUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchFlightsPage implements PageUtils {

//    private WebElement summary = verifyObject("taxAndFeeInclusiveDivHeader");


    @Override
    public boolean hasPageOpened(WebDriver driver) {
        boolean flag = false;
        if(verifyObject(driver, pageElements.getProperty("page.header")).isDisplayed() &&
                verifyObject(driver, pageElements.getProperty("page.header")).isEnabled()){
            flag = true;
        }
        return flag;
    }

    public static boolean isOpen() {
        return true;
    }
}
