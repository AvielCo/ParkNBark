package com.evan.parknbark.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.evan.parknbark.R;
import com.evan.parknbark.RateUsActivity;
import com.evan.parknbark.bulletinboard.NoteAdapter;
import com.evan.parknbark.emailpassword.ChangePassActivity;
import com.evan.parknbark.utilis.BaseActivity;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private String[] settingsArray = {"Change password","Light/Dark Mode","Rate Us"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.setting_list, settingsArray);

        ListView listView = (ListView) findViewById(R.id.setting_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                startActivity(new Intent(SettingsActivity.this, ChangePassActivity.class));
                break;
            }
            case 2: {
                startActivity(new Intent(SettingsActivity.this, RateUsActivity.class));
                break;
            }
        }

    }
}
