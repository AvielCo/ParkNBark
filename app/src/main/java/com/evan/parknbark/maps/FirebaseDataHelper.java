package com.evan.parknbark.maps;

import android.util.Log;

import androidx.annotation.NonNull;

import com.evan.parknbark.utilities.BaseActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirebaseDataHelper extends BaseActivity {
    public static final String TAG = "FirebaseDataHelper";

    public static final String PARK_LOCATIONS_DB = "parklocations";
    public static final String STREET_FIELD = "street";

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
        db.collection(PARK_LOCATIONS_DB)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Park> parkArray = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Park park = document.toObject(Park.class);
                                park.setName(document.getId());
                                parkArray.add(park);
                            }
                            parks.addAll(parkArray);
                            dataStatus.DataIsLoaded(parks);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}