package com.evan.parknbark.emailpassword;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
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
    public boolean signUp(final String email, final String password, final String firstName, final String lastName, boolean test) {
        if(test){
            return EditTextValidator.isValidLayoutEditText(email, textInputEmail, null) & EditTextValidator.isValidLayoutEditText(password, textInputPassword, null)
                    & EditTextValidator.isValidLayoutEditText(firstName, textInputFName, null) & EditTextValidator.isValidLayoutEditText(lastName, textInputLName, null);
        }
        if (EditTextValidator.isValidLayoutEditText(email, textInputEmail, getApplicationContext()) & EditTextValidator.isValidLayoutEditText(password, textInputPassword, getApplicationContext())
                & EditTextValidator.isValidLayoutEditText(firstName, textInputFName, getApplicationContext()) & EditTextValidator.isValidLayoutEditText(lastName, textInputLName, getApplicationContext())) {
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
                                                    updateUI(mAuthCurrentUser);
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

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toasty.success(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_LONG, true).show();
            mAuth.signOut();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_up) {
            hideSoftKeyboard();
            String emailInput = textInputEmail.getEditText().getText().toString().trim().toLowerCase();
            String passwordInput = textInputPassword.getEditText().getText().toString().trim();
            String firstNameInput = textInputFName.getEditText().getText().toString().trim();
            String lastNameInput = textInputLName.getEditText().getText().toString().trim();
            signUp(emailInput, passwordInput, firstNameInput, lastNameInput, false);
        }
    }
}