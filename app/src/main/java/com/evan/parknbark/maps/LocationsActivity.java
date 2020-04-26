package com.evan.parknbark.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.evan.parknbark.R;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * on creation a recycler view is created with the data loaded from the db.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_locations);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_parks);
        new FirebaseDataHelper().readParks(new FirebaseDataHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Park> parks) {
                new LocationsConfig().setConfig(mRecyclerView, LocationsActivity.this, parks);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

}
