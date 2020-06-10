package com.evan.parknbark.emailpassword;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputCurrentPassword;
    private TextInputLayout mTextInputNewPassword;
    private String userEmail;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        if (getIntent() != null && firebaseUser != null) {//if not testing
            firebaseUser = mAuth.getCurrentUser();
            userEmail = firebaseUser.getEmail();
            initElements();
        }
    }

    private void initElements() {
        setProgressBar(R.id.progressBar);
        mTextInputCurrentPassword = findViewById(R.id.text_input_change_pass_enter_current);
        mTextInputNewPassword = findViewById(R.id.text_input_change_pass_enter_new);

        mTextInputCurrentPassword.getEditText().addTextChangedListener(new EditTextListener(mTextInputCurrentPassword, this));
        mTextInputNewPassword.getEditText().addTextChangedListener(new EditTextListener(mTextInputNewPassword, this));

        findViewById(R.id.button_change_pass_confirm).setOnClickListener(this);
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!EditTextListener.hasErrorInText & EditTextValidator.isEmptyEditText(mTextInputNewPassword, this) &
                !currentPassword.equals(newPassword)) {
            showProgressBar();
            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if (task1.isSuccessful()) {
                                            showSuccessToast(R.string.change_password_success);
                                        } else
                                            showErrorToast();
                                        isFirebaseProcessRunning = false;
                                        hideProgressBar();
                                    }
                                });
                            } else {
                                isFirebaseProcessRunning = false;
                                showErrorToast(R.string.change_password_current_invalid);
                            }
                        }
                    });
        } else {
            showErrorToast(R.string.change_pass_same_old_new);
            hideProgressBar();
            isFirebaseProcessRunning = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (isFirebaseProcessRunning) {
            showInfoToast(R.string.please_wait);
            return;
        }
        int i = v.getId();
        if (i == R.id.button_change_pass_confirm) {
            isFirebaseProcessRunning = true;
            final String currentPassword = mTextInputCurrentPassword.getEditText().getText().toString().trim();
            final String newPassword = mTextInputNewPassword.getEditText().getText().toString().trim();
            hideSoftKeyboard();
            changePassword(currentPassword, newPassword);
        }
    }
}
