package com.evan.parknbark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputEmail, textInputPassword;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //login to firebase and get instance
        mAuth = FirebaseAuth.getInstance();

        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

    }

    public void register(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void login(View v){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        String passwordInput = textInputEmail.getEditText().getText().toString().trim();

        if(!validateEmail(emailInput) | !validatePassword(passwordInput)){
            return;
        }
        firebaseEmailAuth(emailInput, passwordInput);

    }

    private boolean validateEmail(String emailInput){
        if(EditTextValidator.isValidString(emailInput)) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (EmailValidator.isValidEmail(emailInput)) {
            textInputEmail.setError("Invalid email address");
            return false;
        } else{
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String passwordInput){
        if(EditTextValidator.isValidString(passwordInput)) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else{
            textInputPassword.setError(null);
            return true;
        }
    }


    private void firebaseEmailAuth(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    /*Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

     private void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(this, MapActivity.class));
        }
        else
            Toast.makeText(this,"Error!",Toast.LENGTH_SHORT).show();
     }

}
