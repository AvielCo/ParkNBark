package com.evan.parknbark.validation;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.evan.parknbark.R;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Text view listener which splits the update text event in four parts:
 * <ul>
 *     <li>The text placed <b>before</b> the updated part.</li>
 *     <li>The <b>old</b> text in the updated part.</li>
 *     <li>The <b>new</b> text in the updated part.</li>
 *     <li>The text placed <b>after</b> the updated part.</li>
 * </ul>
 * Created by Jeremy B.
 */
public class EditTextListener implements TextWatcher {

    private static final int EMAIL_ADDRESS_FIELD = 33;
    private static final int PASSWORD_FIELD = 129;
    private static final int NAME_FIELD = 8289;
    private static final int MULTILINE_FIELD = 131073;
    private static final int TITLE_FIELD = 16385;
    public static Boolean hasErrorInText = true;
    private TextInputLayout mTextInputLayout;
    private Context mContext;
    /**
     * Unchanged sequence which is placed before the updated sequence.
     */
    private String _before;
    /**
     * Updated sequence before the update.
     */
    private String _old;
    /**
     * Updated sequence after the update.
     */
    private String _new;
    /**
     * Unchanged sequence which is placed after the updated sequence.
     */
    private String _after;
    /**
     * Indicates when changes are made from within the listener, should be omitted.
     */
    private boolean _ignore = false;

    public EditTextListener(TextInputLayout mTextInputLayout, Context context) {
        this.mTextInputLayout = mTextInputLayout;
        this.mContext = context;
    }

    @Override
    public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
        _before = sequence.subSequence(0, start).toString();
        _old = sequence.subSequence(start, start + count).toString();
        _after = sequence.subSequence(start + count, sequence.length()).toString();
    }

    @Override
    public void onTextChanged(CharSequence sequence, int start, int before, int count) {
        _new = sequence.subSequence(start, start + count).toString();
    }

    @Override
    public void afterTextChanged(Editable sequence) {
        if (_ignore)
            return;
        onTextChanged(_before, _old, _new, _after);
    }

    /**
     * Triggered method when the text in the text view has changed.
     * <br/>
     * You can apply changes to the text view from this method
     * with the condition to call {@link #startUpdates()} before any update,
     * and to call {@link #endUpdates()} after them.
     *
     * @param before Unchanged part of the text placed before the updated part.
     * @param old    Old updated part of the text.
     * @param aNew   New updated part of the text?
     * @param after  Unchanged part of the text placed after the updated part.
     */
    protected void onTextChanged(String before, String old, String aNew, String after) {
        String completeNewText = before + aNew + after;
        int inputType = mTextInputLayout.getEditText().getInputType();
        startUpdates();
        if (completeNewText.isEmpty()) { //if generally is empty
            mTextInputLayout.setError(mContext.getString(R.string.empty_field));
            EditTextListener.hasErrorInText = true;
        } else if (inputType == EMAIL_ADDRESS_FIELD && !EmailValidator.isValidEmail(completeNewText)) { //if email and email is not valid
            mTextInputLayout.setError(mContext.getString(R.string.email_not_valid));
            EditTextListener.hasErrorInText = true;
        } else if (inputType == PASSWORD_FIELD &&
                (completeNewText.length() < 6 || completeNewText.length() > 15)) { //if is password and length is too short/long
            if (completeNewText.length() < 6) { //if input is below 6 chars
                mTextInputLayout.setError(mContext.getString(R.string.password_too_short));
            } else { //if input is above 15 chars
                mTextInputLayout.setError(mContext.getString(R.string.password_too_long));
            }
            EditTextListener.hasErrorInText = true;
        } else if (inputType == NAME_FIELD &&
                (completeNewText.length() < 2 || completeNewText.length() > 15)) { //if field is name and length too short/long
            if (completeNewText.length() < 2) { //if input is below 2 chars
                mTextInputLayout.setError(mContext.getString(R.string.input_too_short));
            } else { //if input is above 15 chars
                mTextInputLayout.setError(mContext.getString(R.string.input_too_long));
            }
            EditTextListener.hasErrorInText = true;
        } else if (inputType == TITLE_FIELD && completeNewText.length() > 30) {
            mTextInputLayout.setError(mContext.getString(R.string.input_too_long_30));
            EditTextListener.hasErrorInText = true;
        } else { //if everything is good
            mTextInputLayout.setError(null);
            EditTextListener.hasErrorInText = false;
        }
        endUpdates();
    }

    /**
     * Call this method when you start to update the text view, so it stops listening to it and then prevent an infinite loop.
     *
     * @see #endUpdates()
     */
    protected void startUpdates() {
        _ignore = true;
    }

    /**
     * Call this method when you finished to update the text view in order to restart to listen to it.
     *
     * @see #startUpdates()
     */
    protected void endUpdates() {
        _ignore = false;
    }
}

