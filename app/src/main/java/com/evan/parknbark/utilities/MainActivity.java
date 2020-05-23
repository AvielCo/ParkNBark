package com.evan.parknbark.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.emailpassword.*;
import com.evan.parknbark.google.GoogleAuthActivity;
import com.evan.parknbark.map_profile.maps.MapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    GoogleAuthActivity gaa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadLocale(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        findViewById(R.id.button_sign_in_main).setOnClickListener(this);
        findViewById(R.id.button_sign_up_main).setOnClickListener(this);
        //findViewById(R.id.button_google_sign_in).setOnClickListener(this);

        gaa = new GoogleAuthActivity();
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        startActivity(new Intent(MainActivity.this, MapActivity.class)
                                .putExtra("current_user_permission", user.getPermission()));
                    }
                }
            });

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