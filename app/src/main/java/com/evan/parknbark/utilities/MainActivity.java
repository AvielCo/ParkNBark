package com.evan.parknbark.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.evan.parknbark.R;
import com.evan.parknbark.emailpassword.*;
import com.evan.parknbark.google.GoogleAuthActivity;
import com.evan.parknbark.maps.MapActivity;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    GoogleAuthActivity gaa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadLocale(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.button_sign_in_main).setOnClickListener(this);
        findViewById(R.id.button_sign_up_main).setOnClickListener(this);
       //findViewById(R.id.button_google_sign_in).setOnClickListener(this);

        gaa = new GoogleAuthActivity();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null)
            startActivity(new Intent(this, MapActivity.class));
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
//            case R.id.button_google_sign_in:
//                GoogleSignInAccount googleAccount = gaa.signInWithGoogle();
//                if (googleAccount != null)
//                    updateUI(mAuth.getCurrentUser());
//                break;

        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}