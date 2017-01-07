package org.catchmycode.automation.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface WebDriverUtils {

    default WebDriverWait getWebDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, 10);
    }

    default void enterTextById(WebDriver driver, String element, String txt){
        getWebDriverWait(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(element)));
        WebElement obj = driver.findElement(By.id(element));
        obj.clear();
        obj.sendKeys(txt);
        obj.sendKeys(Keys.TAB);
        timeDelay(driver, 2);
    }

    default WebElement verifyObject(WebDriver driver, String webId){
        getWebDriverWait(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(webId)));
        WebElement obj = driver.findElement(By.id(webId));

        return obj;
    }

    default void clickElementById(WebDriver driver, String webId){
        getWebDriverWait(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(webId)));
        driver.findElement(By.id(webId)).click();
    }

    default void timeDelay(WebDriver driver, int i){
        driver.manage().timeouts().implicitlyWait(i, TimeUnit.SECONDS);
    }
}
