package com.evan.parknbark.maps;

import com.evan.parknbark.utilities.*;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.evan.parknbark.contacts.Contact;
import com.evan.parknbark.contacts.ExpandableListAdapter;
import com.evan.parknbark.utilities.BaseNavDrawerActivity;
import com.evan.parknbark.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends BaseNavDrawerActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "MapActivity";
    public static final String PARK_LOCATIONS_DB = "parklocations";
    public static final String LATLNG_FIELD = "latlng";
    public static final String PARK_CHECKIN = "parkcheckin";
    public static final String CHECKIN_FIELD = "currentProfilesInPark";
    public String CHECKIN_MSG;
    public String CHECKOUT_MSG;
    public String GPS_NOT_ENABLE;
    public String ENABLE_GPS_MSG;
    public String NOT_ENABLE_GPS_MSG;

    //Map variables
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final float zoom = 14.5f;
    private final float defaultZoom = 13f;
    private LatLng defaultLoc;
    private static final double defaultLan = 31.249927;
    private static final double defaultLon = 34.791930;
    private boolean gps_enabled = false;

    //permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    //profiles
    private ArrayList<User> currentUsers = new ArrayList<User>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationsPermissions();
        NOT_ENABLE_GPS_MSG = getString(R.string.no);
        ENABLE_GPS_MSG = getString(R.string.yes);
        GPS_NOT_ENABLE = getString(R.string.gps_not_enabled);
        CHECKOUT_MSG = getString(R.string.checkout_msg);
        CHECKIN_MSG = getString(R.string.checkin_msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceLocation();
    }

    /**
     * permissions holds the types of permissions needed for the app.
     * this code requests the permissions from the user. if the user has granted those permissions -> the map is initiated
     * else, the app asks user for these permissions.
     */
    private void getLocationsPermissions() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        initMap();
    }

    /**
     * if the case is LOCATION_PERMISSION_REQUEST_CODE, the code checks if both the permissions were granted by the user.
     * if not, mLocationPermissionsGranted is set to false and the code returns.
     * else, set to true and the map is initiated.
     *
     * @param permissions  string that hold all the permissions needed
     * @param grantResults string that holds all the permissions given by user.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        return;
                    }
                }
                mLocationPermissionsGranted = true;
                initMap();
            }
        }
    }

    /**
     * a map fragment is created with the id of the map from xml file/
     * then we set a callback for when the map is ready to be used. passes next to OnMapReady.
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     * if permission is granted the app proceeds to get user's current locations and is sent to update the map with that location.
     */
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            updateMap(currentLocation);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    /**
     * each iteration loads a document from the db and then
     * sets marker with an option to see their name if the marker is pressed.
     */
    public void setParksMarkers() {
        db.collection(PARK_LOCATIONS_DB)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint park = document.getGeoPoint(LATLNG_FIELD);
                                double lat = park.getLatitude();
                                double lng = park.getLongitude();
                                LatLng latLng = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(latLng)
                                        .title(document.getId())
                                        .snippet(CHECKIN_MSG)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_app_dog_logo)));
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * method gets users current location and zooms in on location
     * if the location is not enabled shows Beer Sheva and the park locations. updates again when the user enables the gps.
     *
     * @param userLocation variable that holds the users current location
     */
    public void updateMap(Location userLocation) {
        LocationManager lm = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        mMap.clear();
        setParksMarkers();
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //give user the park locations even if the permission for the
        //current location has not been granted.
        if (!gps_enabled || !mLocationPermissionsGranted) {
            //tell the user to turn on the gps only if permission to location has been granted.
            //else, don't ask to turn on GPS.
            if (!gps_enabled && mLocationPermissionsGranted)
                enableGps();
            defaultLoc = new LatLng(defaultLan, defaultLon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, defaultZoom));
        } else { //the user has gps enabled and permission is granted.
            LatLng userLatLon = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLon, zoom));
        }
    }

    private void enableGps() {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.map_coordinator_layout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, GPS_NOT_ENABLE, Snackbar.LENGTH_SHORT)
                .setAction(ENABLE_GPS_MSG, v -> { //clicked YES
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
        snackbar.show();
    }

    /**
     * onMapReady callback is triggered once the map is ready to be used.
     * it checks if permissions are given. if they are given - it gets users location and sets its location on map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
        } else updateMap(null);
        mMap.setOnInfoWindowClickListener(this);
    }


    public void checkIn(String title) {
        Log.d(TAG, "checkIn: " + getUserCheckinPark());
        if (getUserCheckinPark() != null) {
            db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
        }
        db.collection(PARK_CHECKIN).document(title)
                .update(CHECKIN_FIELD, FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
        setUserCheckinPark(title);
    }

    /**
     * comment this
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage(marker.getSnippet())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (marker.getSnippet().equals(CHECKIN_MSG)) {
                            checkIn(marker.getTitle());
                            dialog.dismiss();
                            marker.setSnippet(CHECKOUT_MSG);
                        } else if (marker.getSnippet().equals(CHECKOUT_MSG)) {
                            db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                            dialog.dismiss();
                            marker.setSnippet(CHECKIN_MSG);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * retrieving users that stay currently in parks
     */
    public void getCurrentUsersInPark(String parkName) {
        db.collection("parkcheckin").document(parkName).collection("currentProfilesInPark")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null)
                                currentUsers = (ArrayList<User>) task.getResult().toObjects(User.class);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void userListFragment() {
        Fragment fragment = new Fragment();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList();
        fragment.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {
        finish();
        FirebaseAuth.getInstance().signOut();
    }
}