package com.evan.parknbark.credits;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class CreditActivity extends BaseActivity {
    private static final String TAG = "BaseActivity";
    private String CREDITS_COLLECTION = "credits";
    private String OPTION_DOC = "options";
    private String ABOUTUS = "aboutus";
    private String CONNECT = "Connect with us";
    private String EMAIL = "email";
    private String FACEBOOK = "facebook";
    private String PLAYSTORE = "playstore";
    protected FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadConnections();
    }

    private void loadConnections() {
        this.database = FirebaseFirestore.getInstance();
        this.database.collection(CREDITS_COLLECTION)
                .document(OPTION_DOC)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot  document = task.getResult();
                            View aboutPage = new AboutPage(CreditActivity.this)
                                    .isRTL(false)
                                    .setImage(R.drawable.app_dog_logo_background)
                                    .setDescription(document.getString(ABOUTUS))
                                    .addItem(new Element().setTitle("Version 1.0"))
                                    .addGroup(CONNECT)
                                    .addEmail(document.getString(EMAIL))
                                    .addFacebook(document.getString(FACEBOOK))
                                    .addPlayStore(document.getString(PLAYSTORE))
                                    .create();
                            setContentView(aboutPage);
                        }
                        else{
                            Log.d(TAG, "onFailure: not  got in");

                        }
                    }
                });
    }
}
