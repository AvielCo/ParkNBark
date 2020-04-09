package com.evan.parknbark.validation;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.regex.Pattern;

public class EditTextValidator implements TextWatcher {

    private boolean mIsValid = false;

    public boolean isValid() {
        return mIsValid;
    }

    public static boolean isValidString(CharSequence str){
        return str.toString().isEmpty();
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
