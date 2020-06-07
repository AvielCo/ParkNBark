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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

        mTextInputEmail.getEditText().addTextChangedListener(new EditTextListener(mTextInputEmail, this));
        mTextInputFName.getEditText().addTextChangedListener(new EditTextListener(mTextInputFName, this));
        mTextInputLName.getEditText().addTextChangedListener(new EditTextListener(mTextInputLName, this));
        mTextInputPassword.getEditText().addTextChangedListener(new EditTextListener(mTextInputPassword, this));
    }

    /*
    Registration functionality summary
     */
    public boolean signUp(final String email, final String password, final String firstName, final String lastName, boolean test) {
        if (test) {
            return EditTextValidator.isValidLayoutEditText(email, mTextInputEmail, null) & EditTextValidator.isValidLayoutEditText(password, mTextInputPassword, null)
                    & EditTextValidator.isValidLayoutEditText(firstName, mTextInputFName, null) & EditTextValidator.isValidLayoutEditText(lastName, mTextInputLName, null);
        }
        if (!EditTextListener.hasErrorInText & EditTextValidator.isEmptyEditText(mTextInputEmail, this)
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
                                isFirebaseProcessRunning = false;
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
            Intent i = new Intent();
            i.putExtra("email_reg", email);
            i.putExtra("pass_reg", password);
            isFirebaseProcessRunning = false;
            setResult(RESULT_OK, i);
            mAuth.signOut();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_register) {
            isFirebaseProcessRunning = true;
            hideSoftKeyboard();
            String emailInput = mTextInputEmail.getEditText().getText().toString().trim().toLowerCase();
            String passwordInput = mTextInputPassword.getEditText().getText().toString().trim();
            String firstNameInput = mTextInputFName.getEditText().getText().toString().trim();
            String lastNameInput = mTextInputLName.getEditText().getText().toString().trim();
            signUp(emailInput, passwordInput, firstNameInput, lastNameInput, false);
        }
    }
}