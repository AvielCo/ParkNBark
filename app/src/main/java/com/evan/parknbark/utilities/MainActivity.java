package com.evan.parknbark.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.evan.parknbark.R;
import com.evan.parknbark.emailpassword.BannedUserActivity;
import com.evan.parknbark.emailpassword.LoginActivity;
import com.evan.parknbark.emailpassword.RegisterActivity;
import com.evan.parknbark.google.GoogleAuthActivity;
import com.evan.parknbark.map_profile.maps.MapActivity;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    GoogleAuthActivity gaa;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadLocale(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        findViewById(R.id.button_sign_in_main).setOnClickListener(this);
        findViewById(R.id.button_sign_up_main).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        currentUser = (User) bundle.getSerializable("current_user");

        updateUI(mAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null && currentUser != null) {
            if (!currentUser.isBanned()) {
                startActivity(new Intent(MainActivity.this, MapActivity.class)
                        .putExtra("current_user_permission", currentUser.getPermission()));
            } else {
                startActivity(new Intent(MainActivity.this, BannedUserActivity.class));
            }

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
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}