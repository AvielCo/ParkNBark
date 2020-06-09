package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.evan.parknbark.validation.EditTextListener;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import net.steamcrafted.loadtoast.LoadToast;


public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "LoginActivity";
    private static final int REGISTER_REQUEST = 0;
    private static final int REMEMBER_REQUEST = 1;
    private static String LOG_IN_LOAD;
    private TextInputLayout mTextInputEmail, mTextInputPassword;
    private long mBackPressedTime;
    private LoadToast mLoadToast;
    private Bundle bundle;
    private String checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);
        setProgressBar(R.id.progressBar);

        initElements();

        bundle = getIntent().getExtras();
        User currentUser = (User) bundle.getSerializable("current_user");

        SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
        checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {
            updateUI(mAuth.getCurrentUser(), currentUser);
        } else mAuth.signOut();
    }

    private void initElements() {
        mTextInputEmail = findViewById(R.id.text_input_email);
        mTextInputPassword = findViewById(R.id.text_input_password);
        CheckBox mCheckBoxRememberMe = findViewById(R.id.checkbox_remember_me);

        LOG_IN_LOAD = getString(R.string.login_in_t);
        mLoadToast = new LoadToast(LoginActivity.this);

        findViewById(R.id.forgot_password_link).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
        mCheckBoxRememberMe.setOnCheckedChangeListener(this);

        mTextInputPassword.getEditText().addTextChangedListener(new EditTextListener(mTextInputPassword, this));
        mTextInputEmail.getEditText().addTextChangedListener(new EditTextListener(mTextInputEmail, this));
    }

    public boolean signIn(String email, String password, boolean test) {
        if (test) {
            return EditTextValidator.isValidEditText(email, mTextInputEmail, null) && EditTextValidator.isValidEditText(password, mTextInputPassword, null);
        }
        if (!EditTextListener.hasErrorInText & EditTextValidator.isEmptyEditText(mTextInputEmail, this) &
                EditTextValidator.isEmptyEditText(mTextInputPassword, this)) {
            loadToastCreator();
            mLoadToast.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        getUserDetails(mAuth.getCurrentUser());
                    } else {
                        Exception e = task.getException();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        mLoadToast.error();
                        showErrorToast(R.string.wrong_email_pass);
                        isFirebaseProcessRunning = false;
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
                    }
                }
            });
        } else
            showErrorToast();
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.email_verification_sent) + " " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            showErrorToast(R.string.email_verification_failed);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser, User currentUser) {
        if (firebaseUser != null && currentUser != null)
            if (firebaseUser.isEmailVerified()) {
                mLoadToast.success();
                if (!currentUser.isBanned()) {
                    startActivityForResult(new Intent(LoginActivity.this, MapActivity.class)
                            .putExtras(bundle)
                            .putExtra("check_box", checkbox.equals("true")), REMEMBER_REQUEST);
                } else { //user is banned
                    startActivity(new Intent(LoginActivity.this, BannedUserActivity.class));
                }
            } else {
                mLoadToast.error();
                sendEmailVerification(firebaseUser);
                mAuth.signOut();
            }
        isFirebaseProcessRunning = false;
    }

    @Override
    public void onClick(View v) {
        if (isFirebaseProcessRunning) {
            showInfoToast(R.string.please_wait);
            return;
        }
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
                isFirebaseProcessRunning = true;
                hideSoftKeyboard();
                String emailInput = mTextInputEmail.getEditText().getText().toString().trim().toLowerCase();
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
        if (requestCode == REMEMBER_REQUEST) {
            //if user is checked the remember me checkbox
            if (resultCode == RESULT_OK) {
                //if user is pressing back button
                //we want the app to terminate
                finishAffinity();
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isFirebaseProcessRunning) {
            showInfoToast(R.string.please_wait);
            return;
        }
        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            showInfoToast(R.string.press_back_again);
        }
        mBackPressedTime = System.currentTimeMillis();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "true");
            editor.apply();
            checkbox = "true";
        } else {
            SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            checkbox = "false";
        }
    }

    public void loadToastCreator() {
        int loadToastYLocation = 300;
        mLoadToast.setText(LOG_IN_LOAD)
                .setBorderColor(Color.BLACK)
                .setTranslationY(loadToastYLocation)
                .setTextColor(Color.BLACK)
                .setBackgroundColor(Color.GREEN)
                .setProgressColor(Color.BLUE);
    }
}