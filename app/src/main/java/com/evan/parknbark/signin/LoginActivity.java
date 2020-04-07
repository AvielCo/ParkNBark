package com.evan.parknbark.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.MapActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout textInputEmail, textInputPassword;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

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
        if(!EmailValidator.isValidEmail(emailInput)) {
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
                            Toasty.error(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            Toasty.info(this, "Hello " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MapActivity.class));
        }
        else
            Toasty.error(this,"Error!",Toast.LENGTH_SHORT).show();
    }
}
