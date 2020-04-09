package com.evan.parknbark.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import es.dmoral.toasty.Toasty;

public class ResetPassActivity extends AppCompatActivity {

    private Button buttonResetPassSendEmail;
    private TextInputLayout textInputResetPassEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        buttonResetPassSendEmail = findViewById(R.id.button_send_reset_pass);
        textInputResetPassEmail = findViewById(R.id.text_input_email_reset_pass);
        mAuth = FirebaseAuth.getInstance();

        /*
        Send button action on click
         */
        buttonResetPassSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = textInputResetPassEmail.getEditText().getText().toString().trim();
                if (validateEmail(userEmail)) {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Success
                                Toasty.info(ResetPassActivity.this, "Sent! Please check your E-Mail.", Toasty.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                            } else {
                                //Failure
                                String errorMSG = task.getException().getMessage();
                                Toasty.info(ResetPassActivity.this, "Error occurred. " + errorMSG, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    E-Mail validation
     */
    private boolean validateEmail(String emailInput) {
        if (EditTextValidator.isValidString(emailInput)) {
            textInputResetPassEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputResetPassEmail.setError("Invalid email address");
            return false;
        } else {
            textInputResetPassEmail.setError(null);
            return true;
        }
    }
}
