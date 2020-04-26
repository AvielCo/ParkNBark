package com.evan.parknbark;

import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ValidatorTest {
    @Test
    public void emailValidator_CorrectEmail_ReturnsTrue() {
        assertThat(EmailValidator.isValidEmail("name@email.com")).isTrue();
    }

    @Test
    public void fieldValidator_NotEmpty_ReturnsTrue() {
        assertThat(EditTextValidator.isValidString("SomeString")).isTrue();
    }

    @Test
    public void fieldValidatorS_NotEmpty_ReturnsFalse() {
        assertThat(EditTextValidator.isValidString("")).isFalse();
    }
}