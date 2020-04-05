package com.evan.parknbark;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
/*
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
*/

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
        final String passwordInput = textInputEmail.getEditText().getText().toString().trim();
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
        if (emailInput.isEmpty()) {
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
        if (passwordInput.isEmpty()) {
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
        if (fnameInput.isEmpty()) {
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
        if (lnameInput.isEmpty()) {
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

    private void firebaseEmailRegister(final String fname, final String lname, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;

                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put(KEY_FNAME, fname);
                            newUser.put(KEY_LNAME, lname);
                            newUser.put(KEY_PERMISSION, "user");

                            db.collection("users").document(user.getUid()).set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Successfully registered.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            /*Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(RegisterActivity.this, "REGISTERED", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            Toast.makeText(RegisterActivity.this, "ALREADY USER", Toast.LENGTH_LONG).show();
        }
    }
}
