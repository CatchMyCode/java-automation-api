package org.catchmycode.automation.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import java.io.File;

public class Driver {

    private static WebDriver driver;
    private Driver() {}

    public static WebDriver getFirefoxDriver() {
        if(driver == null) {

            ProfilesIni profile = new ProfilesIni();
            FirefoxProfile ffprofile = profile.getProfile("Selenium");
            return driver = new FirefoxDriver();

        } else {
            return driver;
        }
    }

    public static WebDriver getChromeDriverForWin() {

        File file = new File(Driver.class.getResource("chromedriver.exe").getFile());
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

        if(driver == null) {
            return driver = new ChromeDriver();
        } else {
            return driver;
        }
    }

    public static WebDriver getChromeDriverForLinux() {

        File file = new File(Driver.class.getResource("chromedriver").getFile());
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

        if(driver == null) {
            return driver = new ChromeDriver();
        } else {
            return driver;
        }
    }

    public static WebDriver getChromeDriverForMac() {

        File file = new File(Driver.class.getResource("chromedriver_mac").getFile());
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

        if(driver == null) {
            return driver = new ChromeDriver();
        } else {
            return driver;
        }
    }

    public static WebDriver getInternetExplorerDriver() {

        File file = new File(Driver.class.getResource("IEDriverServer.exe").getFile());
        System.setProperty("webdriver.ie.driver", file.getAbsolutePath());

        if(driver == null) {
            return driver = new InternetExplorerDriver();
        } else {
            return driver;
        }
    }

    public static void close() {
        driver.close();
    }
}
