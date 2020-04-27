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
        getDataFromFirebase(ContactActivity.this);

    }

    /**
     * if successful, for each iteration it pulls from the document the data needed to supply the contact class.
     * with the contact class contactList is created with the data from the document and then added to the Hashmap.
     * after the iterations are done the hasshmap, the list and the context are used to show on view the data as expandable list
     *
     * @param context gets the context ContactActivity so it can get updated in the listener
     */
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
                                List<String> contactList = new ArrayList<>();
                                contactList.add(PHONE_NUM + String.valueOf(contact.getPhoneNum()));
                                contactList.add(FAX + String.valueOf(contact.getFax()));
                                listDataheader.add(document.getId());
                                Log.d(TAG, "onSuccess: contact " + document.getId() + " " + contact.getFax() + " " + contact.getPhoneNum());

                                listHashMap.put(document.getId(), contactList);
                            }
                            expandableListView = (ExpandableListView) findViewById(R.id.contactList);
                            expandableadapter = new ExpandableListAdapter(context, listDataheader, listHashMap);
                            expandableListView.setAdapter(expandableadapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
