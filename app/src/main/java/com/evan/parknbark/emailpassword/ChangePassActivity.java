package com.evan.parknbark.emailpassword;

import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import es.dmoral.toasty.Toasty;

public class ChangePassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputCurrentPassword;
    private TextInputLayout mTextInputNewPassword;
    private String userEmail;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        setProgressBar(R.id.progressBar);

        firebaseUser = mAuth.getCurrentUser();
        userEmail = firebaseUser.getEmail();
        mTextInputCurrentPassword = findViewById(R.id.text_input_change_pass_enter_current);
        mTextInputNewPassword = findViewById(R.id.text_input_change_pass_enter_new);

        findViewById(R.id.button_change_pass_confirm).setOnClickListener(this);
    }

    private void changePassword() {
        final String currentPassword = mTextInputCurrentPassword.getEditText().getText().toString().trim();
        final String newPassword = mTextInputNewPassword.getEditText().getText().toString().trim();

        if (EditTextValidator.isValidEditText(currentPassword, mTextInputCurrentPassword) &
                EditTextValidator.isValidEditText(newPassword, mTextInputNewPassword) && !currentPassword.equals(newPassword)) {
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
                                            Toasty.info(ChangePassActivity.this, "Password changed successfully", Toasty.LENGTH_SHORT).show();
                                            ChangePassActivity.this.startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                                        } else
                                            Toasty.info(ChangePassActivity.this, "Password change failed", Toasty.LENGTH_SHORT).show();
                                        hideProgressBar();
                                    }
                                });
                            } else
                                Toasty.info(ChangePassActivity.this, "The current password you enter is invalid!", Toasty.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toasty.info(ChangePassActivity.this, "New password cannot be the same as the current password", Toasty.LENGTH_SHORT).show();
            hideProgressBar();
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_change_pass_confirm) {
            hideSoftKeyboard();
            changePassword();
        }
    }
}
