package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.validation.EditTextListener;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_email_password);

        mTextInputEmail = findViewById(R.id.text_input_email_reset_pass);

        mTextInputEmail.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputEmail.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (!EmailValidator.isValidEmail(completeNewText)) {
                    mTextInputEmail.setError(getString(R.string.email_not_valid));
                    hasErrorInText = true;
                } else {
                    mTextInputEmail.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });

        findViewById(R.id.button_send_reset_pass).setOnClickListener(this);
    }

    private void resetPassword(String email) {
        if (!hasErrorInText & !EditTextValidator.isEmptyEditText(mTextInputEmail, this)) {
            showProgressBar();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Success//
                        showSuccessToast(R.string.reset_pass_success);
                        startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                    } else
                        showErrorToast();
                    hideProgressBar();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_send_reset_pass) {
            hideSoftKeyboard();
            String email = mTextInputEmail.getEditText().getText().toString().trim();
            resetPassword(email);
        }
    }
}
