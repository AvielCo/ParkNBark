package com.evan.parknbark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.evan.parknbark.emailpassword.ChangePassActivity;
import com.evan.parknbark.maps.LocationsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected DocumentReference docRef;
    protected volatile User user;
    protected DrawerLayout drawer;

    protected final String SITE_KEY = "6LfyN-wUAAAAALV11XA__SU7kXTkL_3O_LGcB0Zw";
    private static final String TAG = "BaseActivity";

    @VisibleForTesting
    public ProgressBar mProgressBar;

   /* @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //[START:set toolbar]
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //[END:set toolbar]

        //[START:set drawer]
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //[END:set drawer]

    }

    protected void setNavDrawer() {

        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        navView.bringToFront();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    case R.id.nav_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String text = "Come and join ParkN'Bark at <input some link>";
                        intent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(intent, "Share with"));
                        break;
                    case R.id.nav_locations:
                        startActivity(new Intent(getApplicationContext(), LocationsActivity.class));
                        break;
                    case R.id.nav_changepass:
                        startActivity(new Intent(getApplicationContext(), ChangePassActivity.class));
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }*/

    public void setProgressBar(int resId) {
        mProgressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }

}
