package org.catchmycode.automation.selenium;

import org.openqa.selenium.WebDriver;
import java.io.IOException;
import java.util.Properties;

public interface PageUtils extends WebDriverUtils{

    Properties pageElements = new Properties();

    default void loadPageElements(String property) {
        try {

            pageElements.load(PageUtils.class.getResourceAsStream(property));

        } catch (IOException ioe) {
            System.out.println("Could not load page properties file: " + ioe.getMessage());
        }
    }

    boolean hasPageOpened(WebDriver driver);
}
