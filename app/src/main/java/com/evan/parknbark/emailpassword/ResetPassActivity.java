package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import es.dmoral.toasty.Toasty;

public class ResetPassActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout textInputResetPassEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_email_password);

        textInputResetPassEmail = findViewById(R.id.text_input_email_reset_pass);
        findViewById(R.id.button_send_reset_pass).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();

    }

    private void resetPassword(String email){
        if (validateEmail(email)) {
            showProgressBar();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Success
                        Toasty.info(ResetPassActivity.this, "Sent! Please check your E-Mail.", Toasty.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                    } else {
                        //Failure
                        String errorMSG = task.getException().getMessage();
                        Toasty.error(ResetPassActivity.this, "Error occurred. " + errorMSG, Toasty.LENGTH_SHORT).show();
                    }
                }
            });
            hideProgressBar();
        }
    }

    /*
    E-Mail validation
     */
    private boolean validateEmail(String emailInput) {
        if (!EditTextValidator.isValidString(emailInput)) {
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.button_send_reset_pass){
            String email = textInputResetPassEmail.getEditText().getText().toString().trim();
            resetPassword(email);
        }
    }
}
