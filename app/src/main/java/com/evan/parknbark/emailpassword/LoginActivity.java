package com.evan.parknbark.emailpassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.maps.MapActivity;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextListener;
import com.evan.parknbark.validation.EditTextValidator;
import com.evan.parknbark.validation.EmailValidator;
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
    private static String LOG_IN_LOAD;
    private TextInputLayout mTextInputEmail, mTextInputPassword;
    private long mBackPressedTime;
    private LoadToast mLoadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);
        setProgressBar(R.id.progressBar);

        initElements();

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
        if (!hasErrorInText & !EditTextValidator.isEmptyEditText(mTextInputEmail, this) &
                !EditTextValidator.isEmptyEditText(mTextInputPassword, this)) {
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
                        mLoadToast.success();
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
        } else {
            SharedPreferences preferences = getSharedPreferences("remember_me", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
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

        mTextInputPassword.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputPassword.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (completeNewText.length() < 6) {
                    mTextInputPassword.setError(getString(R.string.password_not_enough));
                    hasErrorInText = true;
                } else if (completeNewText.length() > 15) {
                    mTextInputPassword.setError(getString(R.string.password_too_much));
                    hasErrorInText = true;
                } else {
                    mTextInputPassword.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });

        mTextInputEmail.getEditText().addTextChangedListener(new EditTextListener() {
            @Override
            protected void onTextChanged(String before, String old, String aNew, String after) {
                String completeNewText = before + aNew + after;
                startUpdates();
                if (completeNewText.isEmpty()) {
                    mTextInputEmail.setError(getString(R.string.empty_field));
                    hasErrorInText = true;
                } else if (!EmailValidator.isValidEmail(completeNewText)) {
                    mTextInputEmail.setError(getString(R.string.email_not_valid));
                    hasErrorInText = true;
                } else {
                    mTextInputEmail.setError(null);
                    hasErrorInText = false;
                }
                endUpdates();
            }
        });
    }
}