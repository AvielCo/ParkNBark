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

     protected void loadLocale(Context context){
        changeLang(getPrefLanguage(), context);
    }
    
    protected void changeToNewLocale(String newLanguage, Context context){
        changeLang(newLanguage, context);
        saveLocale(newLanguage);
    }

    protected String getPrefLanguage() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String prefLang;
        prefLang = prefs.getString(KEY_LANGUAGE, "");
        prefLang = prefLang.isEmpty() ? Locale.getDefault().getLanguage() : prefLang;
        return prefLang;
    }
    
     private void changeLang(String lang, Context context) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LANGUAGE, lang);
        editor.apply();
    }
    
    protected void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    
}
