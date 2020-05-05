package com.evan.parknbark;

import com.evan.parknbark.validation.EditTextValidator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EditTextValidatorTest {
    String notEmptyStr;
    String emptyStr;

    @Before
    public void setUp() throws Exception {
        notEmptyStr = "some not empty word";
        emptyStr = "";
    }

    @Test
    public void isValidString_Return_True() {
        assertTrue(EditTextValidator.isValidString(notEmptyStr));
    }

    @Test
    public void isValidString_Return_False() {
        assertFalse(EditTextValidator.isValidString(emptyStr));
    }
}