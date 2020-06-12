package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.validation.EditTextListener;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_email_password);

        if (getIntent() != null) //if not testing
            initElements();
    }

    private void initElements() {
        mTextInputEmail = findViewById(R.id.text_input_email_reset_pass);
        mTextInputEmail.getEditText().addTextChangedListener(new EditTextListener(mTextInputEmail, this));
        findViewById(R.id.button_send_reset_pass).setOnClickListener(this);
    }

    private void resetPassword(String email) {
        if (!EditTextListener.hasErrorInText & !EditTextValidator.isEmptyEditText(mTextInputEmail, this)) {
            showProgressBar();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Success//
                        showSuccessToast(R.string.reset_pass_success);
                        isFirebaseProcessRunning = false;
                        startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                    } else
                        showErrorToast();
                    isFirebaseProcessRunning = false;
                    hideProgressBar();
                }
            });
        } else isFirebaseProcessRunning = false;
    }

    @Override
    public void onClick(View v) {
        if (isFirebaseProcessRunning) {
            showInfoToast(R.string.please_wait);
            return;
        }
        int i = v.getId();
        if (i == R.id.button_send_reset_pass) {
            isFirebaseProcessRunning = true;
            hideSoftKeyboard();
            String email = mTextInputEmail.getEditText().getText().toString().trim();
            resetPassword(email);
        }
    }
}
