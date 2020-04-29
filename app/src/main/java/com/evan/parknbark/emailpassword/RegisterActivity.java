package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private TextInputLayout textInputFName, textInputLName, textInputEmail, textInputPassword;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email_password);
        setProgressBar(R.id.progressBar);

        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputLName = findViewById(R.id.text_input_lname);
        textInputFName = findViewById(R.id.text_input_fname);
        findViewById(R.id.button_sign_up).setOnClickListener(this);
    }

    /*
    Registration functionality summary
     */
    public void signUp(final String email, final String password, final String firstName, final String lastName) {
        if (EditTextValidator.isValidEditText(email, textInputEmail) & EditTextValidator.isValidEditText(password, textInputPassword)
                & EditTextValidator.isValidEditText(firstName, textInputFName) & EditTextValidator.isValidEditText(lastName, textInputLName)) {
            showProgressBar();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //There is no user with the same email address
                                FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();
                                User newUser = new User(firstName, lastName, "user");

                                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .build();
                                mAuthCurrentUser.updateProfile(update);
                                db.collection("users").document(mAuthCurrentUser.getUid()).set(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) { //No error on firebase side.
                                                    updateUI(mAuthCurrentUser);
                                                } else
                                                    Log.d(TAG, "db.collection: onComplete: ERROR!!! " + task.getException().getMessage());
                                                hideProgressBar();
                                            }
                                        });
                            } else {
                                Log.d(TAG, "createUserWithEmailAndPassword: onComplete: ERROR!!! " + task.getException().getMessage());
                                Toasty.error(RegisterActivity.this, task.getException().getMessage(), Toasty.LENGTH_SHORT).show();
                                hideProgressBar();
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toasty.success(RegisterActivity.this, "Registered successfully.\nPlease log in.", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_up) {
            String emailInput = textInputEmail.getEditText().getText().toString().trim();
            String passwordInput = textInputPassword.getEditText().getText().toString().trim();
            String firstNameInput = textInputFName.getEditText().getText().toString().trim();
            String lastNameInput = textInputLName.getEditText().getText().toString().trim();
            signUp(emailInput, passwordInput, firstNameInput, lastNameInput);
        }
    }
}

/**
Just an example on how to add reCAPTCHA to the registration

SafetyNet.getClient(getApplicationContext()).verifyWithRecaptcha(SITE_KEY)
            .addOnSuccessListener((Executor) RegisterActivity.this,
                    new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                        @Override
                        public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                            // Indicates communication with reCAPTCHA service was
                            // successful.
                            String userResponseToken = response.getTokenResult();
                            if (!userResponseToken.isEmpty()) {
                                // Validate the user response token using the
                                // reCAPTCHA siteverify API.
                            }
                        }
                    })
            .addOnFailureListener((Executor) RegisterActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ApiException) {
                        // An error occurred when communicating with the
                        // reCAPTCHA service. Refer to the status code to
                        // handle the error appropriately.
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        Log.d(TAG, "Error: " + CommonStatusCodes
                                .getStatusCodeString(statusCode));
                    } else {
                        // A different, unknown type of error occurred.
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });

*/
