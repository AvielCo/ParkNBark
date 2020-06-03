package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextListener;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Register";
    private TextInputLayout mTextInputFName, mTextInputLName, mTextInputEmail, mTextInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email_password);
        setProgressBar(R.id.progressBar);

        initElements();

        findViewById(R.id.button_register).setOnClickListener(this);
    }

    private void initElements() {
        mTextInputEmail = findViewById(R.id.text_input_email);
        mTextInputPassword = findViewById(R.id.text_input_password);
        mTextInputLName = findViewById(R.id.text_input_lname);
        mTextInputFName = findViewById(R.id.text_input_fname);

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

        mTextInputFName.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputFName.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (completeNewText.length() < 2) {
                    mTextInputPassword.setError(getString(R.string.fname) + " " + getString(R.string.input_too_short_2));
                    hasErrorInText = true;
                } else if (completeNewText.length() > 15) {
                    mTextInputFName.setError(getString(R.string.fname) + " " + getString(R.string.input_too_long));
                    hasErrorInText = true;
                } else {
                    mTextInputFName.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });

        mTextInputLName.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputLName.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (completeNewText.length() < 2) {
                    mTextInputPassword.setError(getString(R.string.password_too_short));
                    hasErrorInText = true;
                } else if (completeNewText.length() > 15) {
                    mTextInputLName.setError(getString(R.string.password_too_long));
                    hasErrorInText = true;
                } else {
                    mTextInputLName.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });

        mTextInputPassword.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputPassword.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (completeNewText.length() < 6) {
                    mTextInputPassword.setError(getString(R.string.password_too_short));
                    hasErrorInText = true;
                } else if (completeNewText.length() > 15) {
                    mTextInputPassword.setError(getString(R.string.password_too_long));
                    hasErrorInText = true;
                } else {
                    mTextInputPassword.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });
    }

    /*
    Registration functionality summary
     */
    public boolean signUp(final String email, final String password, final String firstName, final String lastName, boolean test) {
        if (test) {
            return EditTextValidator.isValidLayoutEditText(email, mTextInputEmail, null) & EditTextValidator.isValidLayoutEditText(password, mTextInputPassword, null)
                    & EditTextValidator.isValidLayoutEditText(firstName, mTextInputFName, null) & EditTextValidator.isValidLayoutEditText(lastName, mTextInputLName, null);
        }
        if (!hasErrorInText & EditTextValidator.isEmptyEditText(mTextInputEmail, this)
                & EditTextValidator.isEmptyEditText(mTextInputFName, this) & EditTextValidator.isEmptyEditText(mTextInputLName, this)) {
            showProgressBar();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //There is no user with the same email address
                                FirebaseUser mAuthCurrentUser = mAuth.getCurrentUser();
                                User newUser = new User(firstName, lastName, "user", email);

                                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .build();
                                mAuthCurrentUser.updateProfile(update);
                                db.collection("users").document(mAuthCurrentUser.getUid()).set(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) { //No error on firebase side.
                                                    updateUI(mAuthCurrentUser, email, password);
                                                } else
                                                    Log.d(TAG, "db.collection: onComplete: ERROR!!! " + task.getException().getMessage());
                                            }
                                        });
                            } else {
                                Log.d(TAG, "createUserWithEmailAndPassword: onComplete: ERROR!!! " + task.getException().getMessage());
                                showErrorToast();
                            }
                            hideProgressBar();
                        }
                    });
        }
        return true;
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.email_verification_sent) + " " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            showErrorToast(R.string.email_verification_failed);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user, String email, String password) {
        if (user != null) {
            sendEmailVerification(user);
            Toasty.success(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_LONG, true).show();
            Intent i = new Intent();
            i.putExtra("email_reg", email);
            i.putExtra("pass_reg", password);
            setResult(RESULT_OK, i);
            mAuth.signOut();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_register) {
            hideSoftKeyboard();
            String emailInput = mTextInputEmail.getEditText().getText().toString().trim().toLowerCase();
            String passwordInput = mTextInputPassword.getEditText().getText().toString().trim();
            String firstNameInput = mTextInputFName.getEditText().getText().toString().trim();
            String lastNameInput = mTextInputLName.getEditText().getText().toString().trim();
            signUp(emailInput, passwordInput, firstNameInput, lastNameInput, false);
        }
    }
}