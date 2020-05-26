package com.evan.parknbark.utilities;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.evan.parknbark.R;
import com.evan.parknbark.RateUsActivity;
import com.evan.parknbark.bulletinboard.BulletinBoardActivity;
import com.evan.parknbark.contacts.ContactActivity;
import com.evan.parknbark.contacts.EditContactActivity;
import com.evan.parknbark.map_profile.maps.LocationsActivity;
import com.evan.parknbark.map_profile.maps.MapActivity;
import com.evan.parknbark.map_profile.profile.ProfileActivity;
import com.evan.parknbark.map_profile.profile.WatchProfile;
import com.evan.parknbark.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;


public abstract class BaseNavDrawerActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    protected NavigationView navView;

    public static final String TAG = "BaseNavDrawer";
    public static final String PARK_CHECKIN = "parkcheckin";
    public static final String CHECKIN_FIELD = "currentProfilesInPark";
    public static final String INVITE_TXT = "Come and join ParkN'Bark at <input some link>";
    public static final String SHARE_WITH_TXT = "Share with";
    public static final String WRONG_PERMISSION = "You don't have the right permission";
    private String userCheckinPark;

    protected String currentUserPermission;


    protected void onCreateDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        navView = findViewById(R.id.nav_view);
        navView.bringToFront();
        navView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View header = navView.getHeaderView(0);

        TextView textViewFirstLastName = header.findViewById(R.id.nav_header_user_fname);
        ImageView imageViewCountryFlag = header.findViewById(R.id.country_flag_menu);

        //Set flag for current language
        String prefLanguage = getPrefLanguage();
        if (prefLanguage.equalsIgnoreCase("en"))
            imageViewCountryFlag.setImageResource(R.drawable.ic_usa);
        else if (prefLanguage.equalsIgnoreCase("iw"))
            imageViewCountryFlag.setImageResource(R.drawable.ic_israel);
        else if (prefLanguage.equalsIgnoreCase("ru"))
            imageViewCountryFlag.setImageResource(R.drawable.ic_russia);

        //Set name for current user
        textViewFirstLastName.setText(mAuth.getCurrentUser().getDisplayName());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showPopupLanguageSelection(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.language_selection_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String lang;
        switch (item.getItemId()) {
            case R.id.lang_english:
                lang = "en";
                break;
            case R.id.lang_hebrew:
                lang = "iw";
                break;
            case R.id.lang_russian:
                lang = "ru";
                break;
            default:
                return false;
        }
        changeToNewLocale(lang, this);
        finish();
        startActivity(getIntent());
        return true;
    }

    /**
     * @return the name of the park current user has checked in into.
     */
    public String getUserCheckinPark() {
        return userCheckinPark;
    }

    /**
     * @param userCheckinPark gets text and sets it to the variable
     */
    public void setUserCheckinPark(String userCheckinPark) {
        this.userCheckinPark = userCheckinPark;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                if (getUserCheckinPark() != null)
                    db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, INVITE_TXT);
                startActivity(Intent.createChooser(intent, SHARE_WITH_TXT));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                break;
            case R.id.nav_locations:
                startActivity(new Intent(getApplicationContext(), LocationsActivity.class));
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(getApplicationContext(), RateUsActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class).putExtra("current_user_permission", currentUserPermission));
                break;
            case R.id.nav_bulletin:
                //starting activity and sending the user details to check if he is admin or nah
                startActivity(new Intent(getApplicationContext(), BulletinBoardActivity.class).putExtra("current_user_permission", currentUserPermission));
                break;
            case R.id.nav_watch_profile:
                startActivity(new Intent(getApplicationContext(), WatchProfile.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.nav_map:
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
