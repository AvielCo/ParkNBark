package com.evan.parknbark.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.evan.parknbark.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public abstract class BaseActivity extends AppCompatActivity {
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore db;

    private final String KEY_LANGUAGE = "Language";

    public ProgressBar mProgressBar;

    protected String ERROR_MSG;

    protected static Boolean hasErrorInText = true;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ERROR_MSG = getResources().getString(R.string.error_message);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

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

    protected void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showErrorToast() {
        Toasty.error(getApplicationContext(), ERROR_MSG, Toasty.LENGTH_LONG).show();
    }

    protected void showErrorToast(int resId) {
        Toasty.error(getApplicationContext(), getString(resId), Toasty.LENGTH_LONG).show();
    }

    protected void showSuccessToast(int resId) {
        Toasty.success(getApplicationContext(), getString(resId), Toasty.LENGTH_SHORT).show();
    }

    protected void showInfoToast(int resId) {
        Toasty.info(getApplicationContext(), getString(resId), Toasty.LENGTH_SHORT).show();
    }

    protected void loadLocale(Context context) {
        changeLang(getPrefLanguage(), context);
    }

    protected void changeToNewLocale(String newLanguage, Context context){
        if(getPrefLanguage().equals(newLanguage))
            return;
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
}
