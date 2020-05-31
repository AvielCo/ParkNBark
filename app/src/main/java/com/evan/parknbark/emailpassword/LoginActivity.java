package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.maps.MapActivity;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;


public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "LoginActivity";
    private static final int REGISTER_REQUEST = 0;
    private TextInputLayout mTextInputEmail, mTextInputPassword;
    private CheckBox mCheckBoxRememberMe;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);
        setProgressBar(R.id.progressBar);

        mTextInputEmail = findViewById(R.id.text_input_email);
        mTextInputPassword = findViewById(R.id.text_input_password);
        mCheckBoxRememberMe = findViewById(R.id.checkbox_remember_me);

        findViewById(R.id.forgot_password_link).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
        mCheckBoxRememberMe.setOnCheckedChangeListener(this);

        Bundle bundle = getIntent().getExtras();
        User currentUser = (User) bundle.getSerializable("current_user");

        SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {
            updateUI(mAuth.getCurrentUser(), currentUser);
        }
    }

    public boolean signIn(String email, String password, boolean test) {
        if (test) {
            return EditTextValidator.isValidEditText(email, mTextInputEmail, null) && EditTextValidator.isValidEditText(password, mTextInputPassword, null);
        }
        if (EditTextValidator.isValidEditText(email, mTextInputEmail, getApplicationContext()) &
                EditTextValidator.isValidEditText(password, mTextInputPassword, getApplicationContext())) {
            showProgressBar();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getUserDetails(mAuth.getCurrentUser());
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

    private void getUserDetails(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        updateUI(firebaseUser, user);
                        hideProgressBar();
                    }
                }
            });
        } else
            showErrorToast();
    }

    private void updateUI(FirebaseUser firebaseUser, User currentUser) {
        if (firebaseUser != null && currentUser != null) {
            if (!currentUser.isBanned()) {
                if (currentUser.isBuiltProfile()) {
                    startActivity(new Intent(LoginActivity.this, MapActivity.class)
                            .putExtra("current_user_permission", currentUser.getPermission()));
                } else {
                    //TODO: if user didn't build profile yet
                    //TODO: delete the line below when above is finished
                    startActivity(new Intent(LoginActivity.this, MapActivity.class)
                            .putExtra("current_user_permission", currentUser.getPermission()));
                }
            } else { //user is banned
                startActivity(new Intent(LoginActivity.this, BannedUserActivity.class));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.button_register:
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class),
                        REGISTER_REQUEST);
                break;
            case R.id.forgot_password_link:
                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));
                break;
            case R.id.button_login:
                hideSoftKeyboard();
                String emailInput = mTextInputEmail.getEditText().getText().toString().trim();
                String passwordInput = mTextInputPassword.getEditText().getText().toString().trim();
                signIn(emailInput, passwordInput, false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra("email_reg"),
                        password = data.getStringExtra("pass_reg");
                mTextInputEmail.getEditText().setText(email);
                mTextInputPassword.getEditText().setText(password);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "true");
            editor.apply();
        } else {
            SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
        }
    }
}