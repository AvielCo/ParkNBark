package com.evan.parknbark.validation;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.evan.parknbark.R;
import com.google.android.material.textfield.TextInputLayout;

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

    public static boolean isValidLayoutEditText(CharSequence string, TextInputLayout mTextInputLayout, Context context) {
        if (mTextInputLayout != null && context != null) {
            if (!isValidString(string)) {
                mTextInputLayout.setError(context.getString(R.string.empty_field));
                return false;
            }
            int inputType = mTextInputLayout.getEditText().getInputType();
            if (inputType == EMAIL_ADDRESS)
                if (!EmailValidator.isValidEmail(string)) {
                    mTextInputLayout.setError(context.getString(R.string.illegal_email));
                    return false;
                }
                /**
                 *  if the input type is of type text we check if the phone or fax are input correctly with PhoneFaxValidator.
                 */
                else if (inputType == TEXT) {
                    if (!PhoneFaxValidator.isValidMobileOrFax(string, mTextInputLayout)) {
                        return false;
                    }
                }
            mTextInputLayout.setError(null);
            return true;
        } else
            return isValidString(string);
    }



    public static boolean isValidEditText(CharSequence string, TextInputLayout mTextInputLayout, Context context) {
        if (mTextInputLayout != null && context != null) {
            if (!isValidString(string)) {
                mTextInputLayout.setError(context.getString(R.string.empty_field));
                return false;
            }
            int inputType = mTextInputLayout.getEditText().getInputType();
            if (inputType == EMAIL_ADDRESS)
                if (!EmailValidator.isValidEmail(string)) {
                    mTextInputLayout.setError(context.getString(R.string.illegal_email));
                    return false;
                }
                /**
                 *  if the input type is of type text we check if the phone or fax are input correctly with PhoneFaxValidator.
                 */
                else if (inputType == TEXT) {
                    if (!PhoneFaxValidator.isValidMobileOrFax(string, mTextInputLayout)) {
                        return false;
                    }
                }
            mTextInputLayout.setError(null);
            return true;
        } else
            return isValidString(string);
    }

    public static boolean isValidMobileOrFax(String num) {
        return num.length() == 10;

    }

    public static boolean isEmptyEditText(TextInputLayout mTextInputLayout, Context context) {
        if (mTextInputLayout.getEditText().getText().toString().trim().isEmpty()) {
            mTextInputLayout.setError(context.getString(R.string.empty_field));
            return true;
        }
        return false;
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
