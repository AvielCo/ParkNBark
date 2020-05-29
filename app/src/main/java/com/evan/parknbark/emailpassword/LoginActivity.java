package com.evan.parknbark.emailpassword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.evan.parknbark.map_profile.maps.MapActivity;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.utilities.MainActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firestore.v1.UpdateDocumentRequest;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout textInputEmail, textInputPassword;

    private static final String TAG = "LoginActivity";

    private boolean isLoggedIn = false;

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

    public boolean signIn(String email, String password, boolean test) {
        if (test) {
            return EditTextValidator.isValidEditText(email, textInputEmail, null) && EditTextValidator.isValidEditText(password, textInputPassword, null);
        }
        if (EditTextValidator.isValidEditText(email, textInputEmail, getApplicationContext()) &
                EditTextValidator.isValidEditText(password, textInputPassword, getApplicationContext())) {
            showProgressBar();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                isLoggedIn = true;
                                updateUI(task.getResult().getUser());
                            } else {
                                Exception e = task.getException();
                                Log.d(TAG, "onFailure: " + e.getMessage());
                                showErrorToast();
                            }
                        }
                    });
        }
        return true;
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (!user.isBanned()) {
                            startActivity(new Intent(LoginActivity.this, MapActivity.class)
                                    .putExtra("current_user_permission", user.getPermission()));
                        }
                        else {
                            startActivity(new Intent(LoginActivity.this, BannedUserActivity.class));
                        }
                    }
                }
            });
            hideProgressBar();
        } else
            showErrorToast();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.forget_password_link:
                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));
                break;
            case R.id.button_sign_in:
                hideSoftKeyboard();
                String emailInput = textInputEmail.getEditText().getText().toString().trim();
                String passwordInput = textInputPassword.getEditText().getText().toString().trim();
                signIn(emailInput, passwordInput, false);
                break;
        }
    }

    @VisibleForTesting
    protected FirebaseAuth fireBaseAuthMock() {
        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth;
    }
}