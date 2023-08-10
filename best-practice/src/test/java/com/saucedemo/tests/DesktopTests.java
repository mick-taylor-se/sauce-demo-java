package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductsPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Desktop Tests for SauceDemo website.
 */
@RunWith(Parameterized.class)
public class DesktopTests extends BaseTest {

    private static final String STANDARD_USER = "standard_user";
    private static final String LOCKED_OUT_USER = "locked_out_user";
    private static final String INVALID_USER = "foo_bar_user";

    private LoginPage loginPage;

    @Before
    public void setUp() {
        // Create a new LoginPage instance before each test
        loginPage = new LoginPage(driver);
        loginPage.visit();
    }

    /**
     * Validates that a standard user can log in successfully.
     */
    @Test
    public void loginWorks() {
        loginPage.login(STANDARD_USER);
        assertTrue(new ProductsPage(driver).isDisplayed());
    }

    /**
     * Validates that a locked out user cannot log in.
     */
    @Test(expected = TimeoutException.class)
    public void lockedOutUser() {
        loginPage.login(LOCKED_OUT_USER);
        assertFalse(new ProductsPage(driver).isDisplayed());
    }

    /**
     * Validates that invalid credentials prevent login.
     */
    @Test(expected = TimeoutException.class)
    public void invalidCredentials() {
        loginPage.login(INVALID_USER);
        assertFalse(new ProductsPage(driver).isDisplayed());
    }
}
