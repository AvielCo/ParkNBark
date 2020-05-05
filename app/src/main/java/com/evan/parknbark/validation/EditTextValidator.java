package com.evan.parknbark.validation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class EditTextValidator implements TextWatcher {

    private static final int EMAIL_ADDRESS = 33;
    private static final int TEXT = 1;

    private boolean mIsValid = false;

    public boolean isValid() {
        return mIsValid;
    }

    public static boolean isValidString(CharSequence str) {
        return !str.toString().isEmpty();
    }

    public static boolean isValidEditText(CharSequence string, TextInputLayout mTextInputLayout) {
        if (!isValidString(string)) {
            mTextInputLayout.setError("Field cannot be empty.");
            return false;
        }
        int inputType = mTextInputLayout.getEditText().getInputType();
        if (inputType == EMAIL_ADDRESS)
            if (!EmailValidator.isValidEmail(string)) {
                mTextInputLayout.setError("Illegal email address.");
                return false;
            }
            /**
             *  if the input type is of type text we check if the phone or fax are input correctly with PhoneFaxValidator.
             */
        else if(inputType == TEXT){
            if(!PhoneFaxValidator.isValidMobileOrFax(string, mTextInputLayout)){
                return false;
            }
        }
        mTextInputLayout.setError(null);
        return true;
    }

    public static boolean isValidMobileOrFax(String num) {
        return num.length() == 10;

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mIsValid = isValidString(s);
    }
}
