package com.evan.parknbark.contacts;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import com.evan.parknbark.utilis.BaseActivity;
import com.evan.parknbark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactActivity extends BaseActivity {
    private static final String TAG = "ContactActivity";
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableadapter;

    private List<String> listDataheader;
    private HashMap<String, List<String>> listHashMap;

    public static final String PHONE_NUM = "Phone Number: ";
    public static final String FAX = "Fax: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        listHashMap = new HashMap<String, List<String>>();
        listDataheader = new ArrayList<>();

        getDataFromFirebase( ContactActivity.this);

        expandableListView  = (ExpandableListView) findViewById(R.id.contactList);


        expandableadapter = new ExpandableListAdapter(this, listDataheader, listHashMap);
        expandableListView.setAdapter(expandableadapter);
        expandableadapter.notifyDataSetChanged();


    }


    private void getDataFromFirebase(Context context) {
        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> listDataheader = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contact contact = document.toObject(Contact.class);
                                List<String> list = new ArrayList<>();
                                list.add(PHONE_NUM + String.valueOf(contact.getPhoneNum()));
                                list.add(FAX + String.valueOf(contact.getFax()));
                                listDataheader.add(document.getId());
                                Log.d(TAG, "onSuccess: contact " + document.getId() + " " + contact.getFax() + " " + contact.getPhoneNum());

                                listHashMap.put(document.getId(), list );
                            }
                            expandableListView  = (ExpandableListView) findViewById(R.id.contactList);


                            expandableadapter = new ExpandableListAdapter(context, listDataheader, listHashMap);
                            expandableListView.setAdapter(expandableadapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
