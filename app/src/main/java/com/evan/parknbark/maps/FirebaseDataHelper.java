package com.evan.parknbark.maps;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FirebaseDataHelper {
    /**
     * creates instance and gets references for the db.
     */
    public static final String TAG = "FirebaseDataHelper";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Park> parks = new ArrayList<>();
    public interface  DataStatus {
        void DataIsLoaded(List<Park> parks, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }
    public FirebaseDataHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
    }

    public void readParks(final DataStatus dataStatus){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                parks.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
//                     keys.add(keyNode.getKey());
//                     Park park = keyNode.getValue(Park.class);
//                     parks.add(park);
                }
                dataStatus.DataIsLoaded(parks, keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mReference.addValueEventListener(postListener);
//        mReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                /**
//                 * pulls the data from the db into list of parks.
//                 */
//                parks.clear();
//                List<String> keys = new ArrayList<>();
//                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
//                     keys.add(keyNode.getKey());
//                     Park park = keyNode.getValue(Park.class);
//                     parks.add(park);
//                }
//                dataStatus.DataIsLoaded(parks, keys);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

}
