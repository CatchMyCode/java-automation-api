package org.catchmycode.examples.selenium;

import org.catchmycode.automation.selenium.Driver;
import org.catchmycode.automation.selenium.PageUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HotelSearchPage implements PageUtils{

    WebDriver driver = Driver.getFirefoxDriver();

    @Override
    public boolean hasPageOpened(WebDriver driver) {
        boolean flag = false;
        if(verifyObject(driver, pageElements.getProperty("page.header")).isDisplayed() &&
                verifyObject(driver, pageElements.getProperty("page.header")).isEnabled()){
            flag = true;
        }
        return flag;
    }

    public boolean bookRoom(){
//        WebElement room = driver.findElement(By.xpath("//*[@id=\"hotel0\"]/a/div[2]/ul[1]/li[3]/span/strong"));
        WebElement room = driver.findElement(By.xpath(pageElements.getProperty("search.room.xpath")));
        room.click();
        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
        }
//        WebElement obj = verifyObject(driver, pageElements.getProperty("mock-book-button"));
        WebElement bookButton = verifyObject(driver, pageElements.getProperty("search.book.button"));
        return (bookButton.isDisplayed() && bookButton.isEnabled());
    }
}
