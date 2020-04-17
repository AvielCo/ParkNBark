package com.evan.parknbark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.bulletinboard.BulletinBoardActivity;
import com.evan.parknbark.emailpassword.*;
import com.evan.parknbark.google.GoogleAuthActivity;
import com.evan.parknbark.maps.MapActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    GoogleAuthActivity gaa;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressBar(R.id.progressBar);

        findViewById(R.id.button_sign_in_main).setOnClickListener(this);
        findViewById(R.id.button_sign_up_main).setOnClickListener(this);
        findViewById(R.id.button_google_sign_in).setOnClickListener(this);

        gaa = new GoogleAuthActivity();

        //login to firebase and get instance
        if(mAuth.getCurrentUser() != null)
            mAuth.signOut();
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
            startActivity(new Intent(this, MapActivity.class));
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