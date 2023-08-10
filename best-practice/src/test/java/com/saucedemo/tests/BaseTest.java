package com.saucedemo.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.Parameterized;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import static com.saucedemo.Constants.*;

public class BaseTest {
    protected RemoteWebDriver driver;
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    protected String WEB_URL = "https://www.saucedemo.com/";

    @Parameterized.Parameter
    public String platform;
    @Parameterized.Parameter(1)
    public String browserDeviceName;
    @Parameterized.Parameter(2)
    public String browserPlatformVersion;
    @Parameterized.Parameter(3)
    public String platformName;

    @Parameterized.Parameters()
    public static Collection<Object[]> crossPlatformData() {
        return Arrays.asList(new Object[][]{
                {"desktop", "safari", "latest", "macOS 11.00"},
                {"desktop", "chrome", "latest-1", "macOS 13"},
                {"desktop", "firefox", "latest", "Windows 11"},
                {"desktop", "chrome", "latest", "Windows 10"}
        });
    }

    @Rule
    public TestName name = new TestName();

    @Before
    public void setup() throws MalformedURLException {
        LOGGER.info("Setting up the test");
        URL url = getSauceURL();

        MutableCapabilities caps = initializeCapabilities();
        try {
            driver = new RemoteWebDriver(url, caps);
        } catch (Exception e) {
            LOGGER.severe("Problem to create the driver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private URL getSauceURL() throws MalformedURLException {
        switch (region) {
            case "us":
                return new URL(SAUCE_US_URL);
            case "eu":
            default:
                return new URL(SAUCE_EU_URL);
        }
    }

    private MutableCapabilities initializeCapabilities() {
        MutableCapabilities caps = new MutableCapabilities();
        switch (platform) {
            case "desktop":
                initializeDesktopCapabilities(caps);
                break;
            case "android":
                initializeAndroidCapabilities(caps);
                break;
            case "ios":
                initializeIOSCapabilities(caps);
                break;
            default:
                throw new IllegalStateException("Unexpected platform: " + platform);
        }
        setSauceOptions(caps);
        return caps;
    }

    private void initializeDesktopCapabilities(MutableCapabilities caps) {
        caps.setCapability("browserName", browserDeviceName);
        caps.setCapability("browserVersion", browserPlatformVersion);
        caps.setCapability("platformName", platformName);
    }

    private void initializeAndroidCapabilities(MutableCapabilities caps) {
        caps.setCapability("platformName", "android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("browserName", "chrome");
        caps.setCapability("appium:deviceName", browserDeviceName);
        caps.setCapability("appium:platformVersion", browserPlatformVersion);
    }

    private void initializeIOSCapabilities(MutableCapabilities caps) {
        caps.setCapability("platformName", "iOS");
        caps.setCapability("appium:automationName", "XCuiTest");
        caps.setCapability("browserName", "safari");
        caps.setCapability("appium:deviceName", browserDeviceName);
        caps.setCapability("appium:platformVersion", browserPlatformVersion);
    }

    private void setSauceOptions(MutableCapabilities caps) {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        sauceOptions.setCapability("name", name.getMethodName());
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH");
        String buildLocal = "sauceDemo-" + dateTime.format(formatter);
        String buildVal = System.getenv("BUILD_TAG");
        sauceOptions.setCapability("build", buildVal == null ? buildLocal : buildVal);
        caps.setCapability("sauce:options", sauceOptions);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            if (driver != null) {
                LOGGER.info("Test Passed!");
                driver.executeScript("sauce:job-result=passed");
                driver.quit();
            }
        }

        @Override
        public void failed(Throwable e, Description description) {
            if (driver != null) {
                LOGGER.severe("Test Failed: " + e.getMessage());
                driver.executeScript("sauce:job-result=failed");
                driver.executeScript("sauce:context=" + e.getMessage());
                driver.quit();
            }
        }
    };
}
