package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;
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

import es.dmoral.toasty.Toasty;

public class ChangePassActivity extends BaseActivity{

    private TextInputLayout currentPassEnter;
    private TextInputLayout newPassEnter;
    private String userEmail;
    private FirebaseUser user;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) userEmail = user.getEmail();
        currentPassEnter = findViewById(R.id.text_input_change_pass_enter_current);
        newPassEnter = findViewById(R.id.text_input_change_pass_enter_new);
        confirmButton = findViewById(R.id.change_pass_confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        final String currentPassword = currentPassEnter.getEditText().getText().toString().trim();
        final String newPassword = newPassEnter.getEditText().getText().toString().trim();

        if (validatePasswords(currentPassword, newPassword)) {
            AuthCredential credential = EmailAuthProvider.getCredential(userEmail,currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (!(newPassword.equals(currentPassword))) {
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toasty.info(ChangePassActivity.this, "Password changed successefully", Toasty.LENGTH_SHORT).show();
                                                startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                                            }
                                            else {
                                                Toasty.info(ChangePassActivity.this, "Password change failed", Toasty.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toasty.info(ChangePassActivity.this, "New password cannot be the same as the current password", Toasty.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toasty.info(ChangePassActivity.this,"The current password you enter is invalid!",Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Old and new passwords input validation
    private boolean validatePasswords(String currentPassword, String newPassword) {
        if (!EditTextValidator.isValidString(currentPassword) || !EditTextValidator.isValidString(newPassword)) {
            if (!EditTextValidator.isValidString(currentPassword)) currentPassEnter.setError("Field can't be empty");
            if (!EditTextValidator.isValidString(newPassword)) newPassEnter.setError("Field can't be empty");
        }
        else {
            currentPassEnter.setError(null);
            newPassEnter.setError(null);
            return true;
        }
        return false;
    }
}
