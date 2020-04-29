package com.evan.parknbark.utilities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.evan.parknbark.R;
import com.evan.parknbark.RateUsActivity;
import com.evan.parknbark.bulletinboard.BulletinBoardActivity;
import com.evan.parknbark.contacts.ContactActivity;
import com.evan.parknbark.maps.LocationsActivity;
import com.evan.parknbark.maps.MapActivity;
import com.evan.parknbark.profile.ProfileActivity;
import com.evan.parknbark.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BaseNavDrawerActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener  {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private TextView textViewFirstLastName;
    private ImageView imageViewCountryFlag;

    protected void onCreateDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.bringToFront();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;
                    case R.id.nav_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String text = "Come and join ParkN'Bark at <input some link>";
                        intent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(intent, "Share with"));
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
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    case R.id.nav_bulletin:
                        startActivity(new Intent(getApplicationContext(), BulletinBoardActivity.class));
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
        });

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View header = navView.getHeaderView(0);

        textViewFirstLastName = header.findViewById(R.id.nav_header_user_fname);
        imageViewCountryFlag = header.findViewById(R.id.country_flag_menu);

        //Set flag for current language
        String prefLanguage = getPrefLanguage();
        if(prefLanguage.equalsIgnoreCase("en"))
            imageViewCountryFlag.setImageResource(R.drawable.ic_usa);
        else if(prefLanguage.equalsIgnoreCase("iw"))
            imageViewCountryFlag.setImageResource(R.drawable.ic_israel);
        else if(prefLanguage.equalsIgnoreCase("ru"))
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
