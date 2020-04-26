package com.evan.parknbark.contacts;


import android.os.Bundle;
import android.widget.ExpandableListView;

import com.evan.parknbark.utilis.BaseActivity;
import com.evan.parknbark.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactActivity extends BaseActivity {
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataheader;
    private HashMap<String, List<String>> listHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        listView = (ExpandableListView) findViewById(R.id.contactList);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataheader, listHashMap);
        listView.setAdapter(listAdapter);
    }

    private void initData() {
        listDataheader = new ArrayList<>();
        listHashMap = new HashMap<>();

        listDataheader.add("municipality");
        listDataheader.add("dog hound");
        listDataheader.add("vets");


        List<String> municipality = new ArrayList<>();
        municipality.add("phone number:");
        municipality.add("fax:");

        List<String> dogHound = new ArrayList<>();
        dogHound.add("phone number:");
        dogHound.add("fax:");

        List<String> vets = new ArrayList<>();
        vets.add("phone number:");
        vets.add("fax:");

        listHashMap.put(listDataheader.get(0),municipality);
        listHashMap.put(listDataheader.get(1),dogHound);
        listHashMap.put(listDataheader.get(2),vets);


    }

}
