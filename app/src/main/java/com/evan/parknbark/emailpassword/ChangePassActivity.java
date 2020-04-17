package com.evan.parknbark.emailpassword;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import es.dmoral.toasty.Toasty;

public class ChangePassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputCurrentPassword;
    private TextInputLayout mTextInputNewPassword;
    private String userEmail;
    private FirebaseUser user;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        mTextInputCurrentPassword = findViewById(R.id.text_input_change_pass_enter_current);
        mTextInputNewPassword = findViewById(R.id.text_input_change_pass_enter_new);

        findViewById(R.id.button_change_pass_confirm).setOnClickListener(this);
    }

    private void changePassword() {
        final String currentPassword = mTextInputCurrentPassword.getEditText().getText().toString().trim();
        final String newPassword = mTextInputNewPassword.getEditText().getText().toString().trim();

        if (EditTextValidator.isValidEditText(currentPassword, mTextInputCurrentPassword) &
                EditTextValidator.isValidEditText(newPassword, mTextInputNewPassword) && !currentPassword.equals(newPassword)) {
            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toasty.info(ChangePassActivity.this, "Password changed successfully", Toasty.LENGTH_SHORT).show();
                                    ChangePassActivity.this.startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                                } else
                                    Toasty.info(ChangePassActivity.this, "Password change failed", Toasty.LENGTH_SHORT).show();
                            });
                        } else
                            Toasty.info(ChangePassActivity.this, "The current password you enter is invalid!", Toasty.LENGTH_SHORT).show();
                    });
        }
        else Toasty.info(ChangePassActivity.this, "New password cannot be the same as the current password", Toasty.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_change_pass_confirm)
            changePassword();
    }
}
