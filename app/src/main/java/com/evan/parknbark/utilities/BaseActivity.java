package com.evan.parknbark.utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import android.content.res.Resources;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    private final String KEY_LANGUAGE = "Language";

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    protected void loadLocale() {
        changeLang(null);
    }

    protected String getPrefLanguage() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String prefLang;
        if (prefs.contains(KEY_LANGUAGE))
            prefLang = prefs.getString(KEY_LANGUAGE, Locale.getDefault().getLanguage());
        else {
            prefLang = new Locale("en", "US").getLanguage();
            saveLocale(prefLang);
        }
        return prefLang;
    }

    protected boolean requestChangeLang(String lang) {
        final String prefLang = getPrefLanguage();
        if (lang.equalsIgnoreCase(prefLang))
            return false;
        return changeLang(lang);
    }

    private boolean changeLang(String lang){
        String country;
        if(lang == null){
            lang = getPrefLanguage();
        }
        switch (lang) {
            case "iw":
                country = "IL";
                break;
            case "ru":
                country = "RU";
                break;
            default:
                country = "US";
        }
        Locale myLocale = new Locale(lang, country);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        Resources res = getBaseContext().getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(myLocale);
        res.updateConfiguration(config, res.getDisplayMetrics());
        return true;
    }

    private void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }
}
