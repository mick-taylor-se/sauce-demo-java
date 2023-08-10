package com.saucedemo.selenium.testng.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**

/**
 * Example of running a TestNG test of the White House website without using Sauce Bindings.
 */
public class WhiteHouseWebsiteTest {
    protected RemoteWebDriver driver;

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        sauceOptions.setCapability("name", method.getName());
        sauceOptions.setCapability("browserVersion", "latest");

        ChromeOptions options = new ChromeOptions();
        options.setCapability("sauce:options", sauceOptions);
        URL url = new URL("https://ondemand.us-west-1.saucelabs.com/wd/hub");

        driver = new RemoteWebDriver(url, options);
    }

    @Test
    public void testWhiteHouseBriefingRoomLink() {
        driver.navigate().to("https://www.whitehouse.gov/");

        // You can verify the title of the website if required.
        // Assert.assertEquals("White House", driver.getTitle());

        driver.findElement(By.linkText("Briefing Room")).click();

        // Wait for a few seconds for visual confirmation
        // (usually, explicit or implicit waits are used in real-world scenarios)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Additional TestNG assertions can be added here as required.
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        String status = result.isSuccess() ? "passed" : "failed";
        driver.executeScript("sauce:job-result=" + status);
        driver.quit();
    }
}
