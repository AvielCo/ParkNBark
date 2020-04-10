package com.evan.parknbark.maps;
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
        mReference = mDatabase.getReference("parks");
    }

    public void readParks(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**
                 * pulls the data from the db into list of parks.
                 */
                parks.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                     keys.add(keyNode.getKey());
                     Park park = keyNode.getValue(Park.class);
                     parks.add(park);
                }
                dataStatus.DataIsLoaded(parks, keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
