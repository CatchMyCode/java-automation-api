package org.catchmycode.examples.selenium;

import org.catchmycode.automation.harness.AbstractSeleniumTestHarness;
import org.catchmycode.automation.selenium.Driver;
import org.catchmycode.automation.selenium.WebDriverUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;


public class AirAsiaReservationTest extends AbstractSeleniumTestHarness implements WebDriverUtils{

    WebDriver driver;
    Map<String, String> cities;

    @BeforeTest
    public void initializeWebDriver() {

        driver = Driver.getFirefoxDriver();
    }

    @BeforeTest
    public void initalizeData() {
        cities = getDestinationDataFromFile("src/test/resources/data/airports.txt");
    }

    @AfterTest
    public void closeWebDriver() {
        driver.close();
    }

    @Test
    public void performFlightSearch(){
        HomePage homePage = new HomePage();
        driver.get("http://www.airasia.com");
        homePage.searchFlight(cities.get("HKG"), cities.get("HYD"), "20/06/2015", "23/06/2015");
//        assertTrue(SearchFlightsPage.isOpen());
    }

//    @Test
//    public void bookHotelRoom(){
//        driver.get("http://www.airasia.com");
//        HomePage.bookHotel(cities.get("HND"), "20/06/2015", "27/06/2015");
//
//        if(HotelSearchPage.isOpen()){
//            assertTrue(HotelSearchPage.bookRoom());
//        }
//
//    }

    private static Map<String, String> getDestinationDataFromFile(String filePath){
        Map<String, String> data = new Hashtable<>();
        try {
            Path file = Paths.get(filePath);
            List<String> lines = Files.readAllLines(file);
            for(String line : lines){
                String[] tokens = line.split(",");
                data.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            System.out.println("Could not file " + filePath + ": " + e.getMessage());
        }

        return data;
    }
}
