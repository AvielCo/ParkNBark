package com.evan.parknbark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.emailpassword.*;
import com.evan.parknbark.profile.ProfileActivity;
import com.evan.parknbark.google.GoogleAuthActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    GoogleAuthActivity gaa = new GoogleAuthActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressBar(R.id.progressBar);

        //login to firebase and get instance
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            mAuth.signOut();

        findViewById(R.id.button_sign_in_main).setOnClickListener(this);
        findViewById(R.id.button_sign_up_main).setOnClickListener(this);
        findViewById(R.id.button_google_sign_in).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toasty.info(this, "Hello " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.button_sign_in_main:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.button_sign_up_main:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.button_google_sign_in:
                GoogleSignInAccount googleAccount = gaa.signInWithGoogle();
                if(googleAccount != null)
                    updateUI(mAuth.getCurrentUser());
                break;
        }
    }
}