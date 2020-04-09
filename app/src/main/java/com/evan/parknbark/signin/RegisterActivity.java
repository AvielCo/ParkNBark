package com.evan.parknbark.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout textInputFName, textInputLName, textInputEmail, textInputPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "Register";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_LNAME = "lname";
    private static final String KEY_PERMISSION = "permission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance(); //Firebase Authorization

        /*
        Text inputs integration with variables
         */
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputLName = findViewById(R.id.text_input_lname);
        textInputFName = findViewById(R.id.text_input_fname);

    }

    /*
    Registration functionality summary
     */
    public void register(View v) {
        final String emailInput = textInputEmail.getEditText().getText().toString().trim();
        final String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        final String fnameInput = textInputFName.getEditText().getText().toString().trim();
        final String lnameInput = textInputLName.getEditText().getText().toString().trim();

        if (!validateEmail(emailInput) | !validatePassword(passwordInput) |
                !validateFName(fnameInput) | !validateLName(lnameInput)) {
            return;
        }
        firebaseEmailRegister(fnameInput, lnameInput, emailInput, passwordInput);
    }

    /*
    E-Mail validation
     */
    private boolean validateEmail(String emailInput) {
        if (EditTextValidator.isValidString(emailInput)) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Invalid email address");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    /*
    Password validation
     */
    private boolean validatePassword(String passwordInput) {
        if (EditTextValidator.isValidString(passwordInput)) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    /*
    First name validation
     */
    private boolean validateFName(String fnameInput) {
        if (EditTextValidator.isValidString(fnameInput)) {
            textInputFName.setError("Field can't be empty");
            return false;
        } else if (fnameInput.length() > 15) {
            textInputFName.setError("Field too long");
            return false;
        } else {
            textInputFName.setError(null);
            return true;
        }
    }

    /*
    Last name validation
     */
    private boolean validateLName(String lnameInput) {
        if (EditTextValidator.isValidString(lnameInput)) {
            textInputLName.setError("Field can't be empty");
            return false;
        } else if (lnameInput.length() > 15) {
            textInputLName.setError("Field too long");
            return false;
        } else {
            textInputLName.setError(null);
            return true;
        }
    }

    private void firebaseEmailRegister(final String fname, final String lname, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fname + lname)
                                .build();
                        user.updateProfile(update);

                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put(KEY_FNAME, fname);
                        newUser.put(KEY_LNAME, lname);
                        newUser.put(KEY_PERMISSION, "user");

                        db.collection("users").document(email).set(newUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updateUI(mAuth.getCurrentUser());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(RegisterActivity.this, "An error has been occurred\nPlease try again later", Toasty.LENGTH_LONG).show();
                                        Log.d(TAG, "onSuccess: onFailure: " + e.getMessage());
                                    }
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

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toasty.success(RegisterActivity.this, "Registered successfully.\nPlease log in.", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            finish();
        }
    }
}
