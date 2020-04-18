package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
import com.evan.parknbark.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            User newUser = new User(firstName, lastName, "user");

                            UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName + " " + lastName)
                                    .build();
                            user.updateProfile(update);

                            db.collection("users").document(user.getUid()).set(newUser)
                                    .addOnSuccessListener(aVoid -> RegisterActivity.this.updateUI(mAuth.getCurrentUser()))
                                    .addOnFailureListener(e -> {
                                        Toasty.error(RegisterActivity.this, "An error has been occurred\nPlease try again later", Toasty.LENGTH_LONG).show();
                                        Log.d(TAG, "onSuccess: onFailure: " + e.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "createUserWithEmail:failure", e.getCause());
                            Toasty.error(RegisterActivity.this, e.getMessage(), Toasty.LENGTH_SHORT).show();
                        }
                    });
            hideProgressBar();
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
