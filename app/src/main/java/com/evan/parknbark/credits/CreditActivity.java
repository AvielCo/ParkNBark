package com.evan.parknbark.credits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class CreditActivity extends BaseActivity {
    private static final String TAG = "BaseActivity";
    protected FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element adsElement = new Element();
        adsElement.setTitle("Advertise here");
        loadConnections();

    }

    private void loadConnections() {
//        View aboutPage = new AboutPage(CreditActivity.this)
//                                    .isRTL(false)
//                                    .setImage(R.drawable.app_dog_logo_background)
//                                    .setDescription("This should hold string about us")
//                                    .addItem(new Element().setTitle("Version 1.0"))
//                                    .addGroup("Connect with us")
//                                    .addEmail("email")
//                                    .addFacebook("facebook")
//                                    .addPlayStore("playstore")
//                                    .create();
//        setContentView(aboutPage);
        this.database = FirebaseFirestore.getInstance();
        this.database.collection("credits")
                .document("options")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onSuccess: got in");
                            DocumentSnapshot  document = task.getResult();
                            View aboutPage = new AboutPage(CreditActivity.this)
                                    .isRTL(false)
                                    .setImage(R.drawable.app_dog_logo_background)
                                    .setDescription(document.getString("aboutus"))
                                    .addItem(new Element().setTitle("Version 1.0"))
                                    .addGroup("Connect with us")
                                    .addEmail(document.getString("email"))
                                    .addFacebook(document.getString("facebook"))
                                    .addPlayStore(document.getString("playstore"))
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
