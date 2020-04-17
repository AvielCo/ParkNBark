package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.User;
import com.evan.parknbark.bulletinboard.BulletinBoardActivity;
import com.evan.parknbark.maps.MapActivity;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout textInputEmail, textInputPassword;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);
        setProgressBar(R.id.progressBar);

        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        findViewById(R.id.forget_password_link).setOnClickListener(this);
        findViewById(R.id.button_sign_in).setOnClickListener(this);
    }

    public void signIn(String email, String password) {

        if (EditTextValidator.isValidEditText(email, textInputEmail) & EditTextValidator.isValidEditText(password, textInputPassword)) {
            showProgressBar();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            updateUI(authResult.getUser());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    Toasty.error(LoginActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            hideProgressBar();
        }
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Toasty.info(this, "Hello " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, BulletinBoardActivity.class));
            //startActivity(new Intent(this,ChangePassActivity.class)); //Change password activity - will be attached to settings later
        } else
            Toasty.error(this, "Error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.forget_password_link:
                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));
                break;
            case R.id.button_sign_in:
                String emailInput = textInputEmail.getEditText().getText().toString().trim();
                String passwordInput = textInputPassword.getEditText().getText().toString().trim();
                signIn(emailInput, passwordInput);
                break;

        }
    }
}