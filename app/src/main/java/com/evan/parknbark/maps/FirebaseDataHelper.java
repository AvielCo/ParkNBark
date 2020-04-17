package com.evan.parknbark.maps;
import android.widget.ArrayAdapter;

import com.evan.parknbark.BaseActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Park> parksArray = Arrays.asList(new Park("Park Kaplan", "Bazel Street", 31.248640, 34.790501),
                new Park("Park Ofira", "Ofira Street", 31.245387, 34.770759),
                new Park("Park Shomron", "Shomron Street", 31.246992, 34.765799));
        ArrayList<Park> parksArrayList = new ArrayList<>();

        parks.addAll(parksArray);

        dataStatus.DataIsLoaded(parks);
    }

}