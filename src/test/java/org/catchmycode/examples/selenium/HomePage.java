package org.catchmycode.examples.selenium;

import org.catchmycode.automation.selenium.Driver;
import org.catchmycode.automation.selenium.PageUtils;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 *  WebElement flight = Driver.getDriver().findElement(By.id("selectValue1"));
 *  WebElement hotel = Driver.getDriver().findElement(By.id("selectValue3"));
 *  WebElement flightHotel = Driver.getDriver().findElement(By.id("selectValue2"));
 *  WebElement car =  Driver.getDriver().findElement(By.id("selectValue4"));
 *  WebElement returnTrip = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactView_RoundTrip"));
 *  WebElement oneWay = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactView_OneWay"));
 *  WebElement multiCity = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactView_OpenJaw"));
 *  WebElement origin = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactVieworiginStationMultiColumn1_1"));
 *  WebElement destination = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactViewdestinationStationMultiColumn1_1"));
 *  WebElement departDate = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactViewdate_picker_display_id_1"));
 *  WebElement returnDate = Driver.getDriver().findElement(By.id("ControlGroupCompactView_AvailabilitySearchInputCompactViewdate_picker_display_id_2"));
 */
public class HomePage implements PageUtils{

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

    public HomePage searchFlight(String from, String to, String departure, String arrival){
        enterTextById(driver, "ControlGroupCompactView_AvailabilitySearchInputCompactVieworiginStationMultiColumn1_1", from);
        enterTextById(driver, "ControlGroupCompactView_AvailabilitySearchInputCompactViewdestinationStationMultiColumn1_1", to);
        enterTextById(driver, "ControlGroupCompactView_AvailabilitySearchInputCompactViewdate_picker_display_id_1", departure);
        enterTextById(driver, "ControlGroupCompactView_AvailabilitySearchInputCompactViewdate_picker_display_id_2", arrival);
        search(driver, "ControlGroupCompactView_ButtonSubmit");

        return this;
    }

    public HomePage bookHotel(String destination, String checkIn, String checkOut){
        verifyObject(driver, "selectValue3").click();
        enterTextById(driver, "ControlGroupCompactViewHotel_AvailabilitySearchInputCompactViewHoteloriginStationMultiColumn1_1", destination);
        enterTextById(driver, "ControlGroupCompactViewHotel_AvailabilitySearchInputCompactViewHoteldate_picker_display_id_1", checkIn);
        enterTextById(driver, "ControlGroupCompactViewHotel_AvailabilitySearchInputCompactViewHoteldate_picker_display_id_2", checkOut);
        search(driver, "ControlGroupCompactViewHotel_ButtonSubmit");

        return this;
    }

    private void search(WebDriver driver, String webId){
        verifyObject(driver, webId).click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }
}
