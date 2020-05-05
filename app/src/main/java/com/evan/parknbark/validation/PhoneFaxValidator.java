package com.evan.parknbark.validation;

import com.google.android.material.textfield.TextInputLayout;

public class PhoneFaxValidator {

    /**
     * method checks if the input includes non number characters and if the length is equal to 10.
     *
     * @param string input from user
     * @param mTextInputLayout the text input from the EditContactActivity
     * @return true if proper input else false.
     */
    public static boolean isValidMobileOrFax(CharSequence string, TextInputLayout mTextInputLayout) {
        if(string.toString().matches("[a-zA-Z]+"))
        {
            mTextInputLayout.setError("Enter only numbers.");
            return false;
        }
        else if(string.length() != 10){
            mTextInputLayout.setError("Input should include 10 numbers.");
            return false;
        }

        return true;
    }
}


