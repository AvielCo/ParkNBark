package com.evan.parknbark.utilis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.drawerlayout.widget.DrawerLayout;


import com.evan.parknbark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected DocumentReference docRef;
    protected volatile User user;
    protected DrawerLayout drawer;

    protected final String SITE_KEY = "6LfyN-wUAAAAALV11XA__SU7kXTkL_3O_LGcB0Zw";
    private static final String TAG = "BaseActivity";

    @VisibleForTesting
    public ProgressBar mProgressBar;

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

    protected void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    protected void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    private void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

    public void showPopupLanguageSelection(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.language_selection_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lang_english:
                changeLang("en");
                finish();
                startActivity(getIntent());
                return true;
            case R.id.lang_hebrew:
                changeLang("iw");
                finish();
                startActivity(getIntent());
                return true;
            case R.id.lang_russian:
                changeLang("ru");
                finish();
                startActivity(getIntent());
                return true;
            default:
                return false;
        }
    }
}
