package com.evan.parknbark.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.evan.parknbark.R;
import com.evan.parknbark.RateUsActivity;
import com.evan.parknbark.contacts.EditContactActivity;
import com.evan.parknbark.emailpassword.ChangePassActivity;
import com.evan.parknbark.settings.admin.UsersListActivity;
import com.evan.parknbark.utilities.BaseActivity;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private String[] settingsArray = {"Change password", "Rate Us"}; //Settings menu items
    private String[] adminSettingsArray = {"Change password", "Edit contact", "Show all users"};
    private String currentUserPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUserPermission = getIntent().getStringExtra("current_user_permission");
        ArrayAdapter adapter;
        if (currentUserPermission.equals("admin"))
            adapter = new ArrayAdapter<>(this,
                    R.layout.setting_list, adminSettingsArray);
        else
            adapter = new ArrayAdapter<>(this,
                    R.layout.setting_list, settingsArray);

        ListView listView = findViewById(R.id.setting_listview); //Interactive list view
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    //On click implementation for items in the menu
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //if user is an admin give admin settings
        if (currentUserPermission.equals("admin")) {
            switch (position) {
                case 0:
                    //Change password
                    startActivity(new Intent(getApplicationContext(), ChangePassActivity.class));
                    break;

                case 1:
                    //Rate Us!
                    startActivity(new Intent(getApplicationContext(), EditContactActivity.class));
                    break;

                case 2:
                    startActivity(new Intent(getApplicationContext(), UsersListActivity.class));
                    break;
            }
        }
        else{
            switch (position) {
                case 0: {
                    //Change password
                    startActivity(new Intent(SettingsActivity.this, ChangePassActivity.class));
                    break;
                }
                case 2: {
                    //Rate Us!
                    startActivity(new Intent(SettingsActivity.this, RateUsActivity.class));
                    break;
                }
            }
        }

    }
}
