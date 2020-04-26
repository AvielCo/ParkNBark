package com.evan.parknbark.validation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmailValidatorTest {

    String goodEmail;
    String badEmail1;
    String badEmail2;

    @Before
    public void setUp() throws Exception {
        goodEmail = "test@email.com";
        badEmail1 = "testemail.com";
        badEmail2 = "test@emailcom";
    }

    @Test
    public void isValidEmail_Return_True() {
        assertTrue(EmailValidator.isValidEmail(goodEmail));
    }
    @Test
    public void isValidEmail_Return_False_1() {
        assertFalse(EmailValidator.isValidEmail(badEmail1));
    }
    @Test
    public void isValidEmail_Return_False_2() {
        assertFalse(EmailValidator.isValidEmail(badEmail2));
    }
}