package com.evan.parknbark.maps;
import android.widget.ArrayAdapter;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FirebaseDataHelper extends BaseActivity {
    /**
     * creates instance and gets references for the db.
     */
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ArrayList<Park> parks = new ArrayList<>();
    public interface  DataStatus {
        void DataIsLoaded(List<Park> parks);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }
    public FirebaseDataHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
    }

    public void readParks(final DataStatus dataStatus){
        Park p1 = new Park("Park Kaplan", 31.248819663000063, 34.79040811400006);
        Park p2 = new Park("Park Ofira", 31.247142073000077, 34.76597492600007);
        Park p3 = new Park("Park Shomron", 31.247142073000077, 34.76597492600007);
        parks.add(p1);
        parks.add(p2);
        parks.add(p3);
        dataStatus.DataIsLoaded(parks);
    }

}
