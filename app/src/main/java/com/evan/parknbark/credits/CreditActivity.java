package com.evan.parknbark.credits;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class CreditActivity extends BaseActivity {
    private static final String TAG = "CreditActivity";
    private String CREDITS_COLLECTION = "credits";
    private String OPTION_DOC = "options";
    private String ABOUTUS = "aboutus";

    private String EMAIL = "Email";
    private String FACEBOOK = "Facebook";
    private String FACEBOOK_ID = "FacebookId";
    private String FACEBOOK_URL = "FacebookUrl";
    private String PLAYSTORE = "Playstore";
    private String EN = "en", EN_US = "en_US";
    private String IW = "iw", IW_IL = "iw_IL";

    private String lang;
    protected FirebaseFirestore database;

    /**
     *  gets the system language and loads the proper values in loadConnections
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lang = getPrefLanguage();
        loadConnections();
    }

    /**
     * brings the document from firebase and loads the elements needed for the about page.
     */
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
                                    .setDescription(getAboutUs(document, lang))
                                    .addGroup(getConnectText())
                                    .addItem(addEmail(document, lang))
                                    .addItem(addFacebook(document, lang))
                                    .addItem(addPlayStore(document,  lang))
                                    .create();
                            setContentView(aboutPage);
                        }
                        else{
                            Toasty.info(CreditActivity.this, "An error occurred while opening this page.", Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     *  gets the language and loads the value for "about us" section.

     * @return string relevant to language
     */
    private String getAboutUs(DocumentSnapshot document, String lang) {
        if(lang.equals(EN) || lang.equals(EN_US))
            return document.getString(ABOUTUS);
        if(lang.equals(IW) || lang.equals(IW_IL))
                return document.getString(ABOUTUS + "-he");
       else
            return document.getString(ABOUTUS + "-ru");

    }


    /**
     * gets the language and loads the value for the mail section with an option to send an email to the devs.
     * @param document document from firebase
     * @param lang app language chosen by user
     * @return email element
     */
    private Element addEmail(DocumentSnapshot document, String lang){
        Element email = new Element();
        email.setTitle(setEmailTitle( lang));
        email.setIconDrawable(R.drawable.about_icon_email);
        email.setIconTint(R.color.about_item_icon_color);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{document.getString(EMAIL)});
        email.setIntent(emailIntent);
        return email;
    }


    /**
     * gets the language and loads the value for the playstore section with an option to open dev's playstore page.
     * @param lang app language chosen by user
     * @return playstore element
     */
    private Element addPlayStore(DocumentSnapshot document, String lang){
        Element playstore = new Element();
        playstore.setTitle(setPlaystoreTitle(lang));
        playstore.setIconDrawable(R.drawable.about_icon_google_play);
        playstore.setIconTint(R.color.about_play_store_color);
        Intent psIntent = new Intent();
        psIntent.setAction(Intent.ACTION_VIEW);
        psIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        psIntent.setData(Uri.parse(
                document.getString(PLAYSTORE)));
        psIntent.setPackage("com.android.vending");

        playstore.setIntent(psIntent);
        return playstore;
    }

    /**
     * gets the language and loads the value for the facebook section with an option to open dev's facebook page.
     * @param document document from firebase
     * @param lang app language chosen by user
     * @return facebook element
     */

    private Element addFacebook(DocumentSnapshot document, String lang){
        Element facebook = new Element();
        facebook.setTitle(setFacebookTitle(lang));
        facebook.setIconDrawable(R.drawable.about_icon_facebook);
        facebook.setIconTint(R.color.about_facebook_color);

        Intent facebookIntent = new Intent();
        facebookIntent.setAction(Intent.ACTION_VIEW);
        facebookIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        String url = getFacebookUrl(CreditActivity.this, document, facebookIntent);
        facebookIntent.setData(Uri.parse(url));
        facebook.setIntent(facebookIntent);

        return facebook;
    }

    /**
     * checks if there is a facebook app installed on phone. if there's an installed app
     * checks for its version and returns  fb reference in app format.
     * else, if no app is installed - returns url format.
     * @param context get apps current context
     * @param document document from firebase
     * @return the proper form of url to the dev's page/
     */
    private String getFacebookUrl(Context context, DocumentSnapshot document, Intent facebookIntent) {
        PackageManager packageManager = context.getPackageManager();
        if (isAppInstalled(context, "com.facebook.katana")) {
            facebookIntent.setPackage("com.facebook.katana");
            int versionCode = 0;
            try {
                versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if(versionCode != 0){
                return "fb://page/" + document.getString(FACEBOOK_ID);
            }
        }
        return document.getString(FACEBOOK_URL);

    }

    static Boolean isAppInstalled(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(appName)) {
                installed = true;
                break;
            }
        }

        return installed;
    }


    /**
     * sets title for email section
     * @param lang app language chosen by user
     * @return the string related to the language chosen by user for the app
     */
    private String setEmailTitle( String lang) {
        if(lang.equals(EN) || lang.equals(EN_US))
            return "Contact us";
        if(lang.equals(IW)|| lang.equals(IW_IL))
            return "צור קשר";
        else
            return "Связаться с нами";
    }


    /**
     * sets title for email section
     * @param lang app language chosen by user
     * @return the string related to the language chosen by user for the app
     */
    private String setFacebookTitle( String lang) {
        if(lang.equals(EN) || lang.equals(EN_US))
            return "Like us on Facebook";
        if(lang.equals(IW)|| lang.equals(IW_IL))
            return "תנו לנו לייק בפייסבוק";
        else
            return "Поставьте нам лайк на фейсбуке";
    }


    /**
     * sets title for playstore section
     * @param lang app language chosen by user
     * @return the string related to the language chosen by user for the app
     */
    private String setPlaystoreTitle( String lang) {
        if(lang.equals("en")|| lang.equals("en_US"))
            return "Rate us on the Play Store";
        if(lang.equals(IW)|| lang.equals(IW_IL))
            return "דרגו אותנו בפלייסטור";
        else
            return "Оцените нас в магазине Play";
    }


    /**
     * sets text for connecting section
     */
    private String getConnectText() {
        if(lang.equals(EN)|| lang.equals(EN_US))
            return "Connecting options";
        if(lang.equals(IW)|| lang.equals(IW_IL))
            return "אפשרויות התקשרות";
        else
            return "Варианты подключения";
    }
}
