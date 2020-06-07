package com.evan.parknbark.map_profile.maps;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.MapProfileBottomSheetDialog;
import com.evan.parknbark.map_profile.profile.Profile;
import com.evan.parknbark.map_profile.profile.WatchProfile;
import com.evan.parknbark.settings.admin.UsersListActivity;
import com.evan.parknbark.utilities.BaseNavDrawerActivity;
import com.evan.parknbark.utilities.User;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class MapActivity extends BaseNavDrawerActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, MapProfileBottomSheetDialog.BottomSheetListener {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MapActivity";
    private static final String PARK_LOCATIONS_DB = "parklocations";
    private static final String LATLNG_FIELD = "latlng";
    private static final String PARK_CHECKIN = "parkcheckin";
    private static final String CHECKIN_FIELD = "currentProfilesInPark";
    private static final String PROFILES = "profiles";
    private static final String POPUP_WINDOW_TITLE = "Set your profile!";
    private static final String POPUP_WINDOW_BODY = "you must set your profile for further use.";
    private static final double defaultLan = 31.249927;
    private static final double defaultLon = 34.791930;
    //permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private String CHECKIN_MSG;
    private String CHECKOUT_MSG;
    private String GPS_NOT_ENABLE;
    private String ENABLE_GPS_MSG;
    private boolean check_in_flag;
    private final int REQUEST_PROFILE_BUILD = 0;
    //Map variables
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    //profiles
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference profilesRef = db.collection(PROFILES);
    private Marker currentClickedMarker;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationsPermissions();
        ENABLE_GPS_MSG = getString(R.string.yes);
        GPS_NOT_ENABLE = getString(R.string.gps_not_enabled);
        CHECKOUT_MSG = getString(R.string.checkout_msg);
        CHECKIN_MSG = getString(R.string.checkin_msg);

        bundle = getIntent().getExtras();
        User currentUser = (User) bundle.getSerializable("current_user");
        currentUserPermission = currentUser.getPermission();

        if (!currentUser.isBuiltProfile()) {
            displayPopupWindow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
    }

    @Override
    public void onBackPressed() {
        finish();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROFILE_BUILD) {
            if (resultCode != RESULT_OK) {
                displayPopupWindow();
            } else {
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .update("builtProfile", true);
            }
        }
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
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * if permission is granted the app proceeds to get user's current locations and is sent to update the map with that location.
     */
    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //give user the park locations even if the permission for the
        //current location has not been granted.
        if (userLocation == null || !gps_enabled || !mLocationPermissionsGranted) {
            //tell the user to turn on the gps only if permission to location has been granted.
            //else, don't ask to turn on GPS.
            if (!gps_enabled && mLocationPermissionsGranted)
                enableGps();
            LatLng defaultLoc = new LatLng(defaultLan, defaultLon);
            float defaultZoom = 13f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, defaultZoom));
        } else { //the user has gps enabled and permission is granted.
            LatLng userLatLon = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            float zoom = 14.5f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLon, zoom));
        }
    }

    /**
     * If gps is disabled, prompt a snack bar message to tell the user that his GPS
     * is turned off and a button to turn it on.
     */
    private void enableGps() {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.map_coordinator_layout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, GPS_NOT_ENABLE, Snackbar.LENGTH_SHORT)
                .setAction(ENABLE_GPS_MSG, v -> { //clicked YES
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
        snackbar.show();
    }

    /**
     * comment this
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        check_in_flag = marker.getSnippet().equals(CHECKIN_MSG);
        getCurrentUsersInPark(marker.getTitle());
        currentClickedMarker = marker;
    }

    /**
     * retrieving users that stay currently in parks
     */
    private void getCurrentUsersInPark(String parkName) {
        db.collection(PARK_CHECKIN).document(parkName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //cast always going to be successful
                                @SuppressWarnings("unchecked")
                                ArrayList<String> currentUsersInParkUID = (ArrayList<String>) document.get(CHECKIN_FIELD);
                                getCurrentUsersInParkDetails(currentUsersInParkUID);
                            }
                        }
                    }
                });
    }

    /**
     * get users detail
     *
     * @param usersUID Users in the selected park as arraylist of UID.
     */
    private void getCurrentUsersInParkDetails(ArrayList<String> usersUID) {
        profilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Profile> currentUsersInParkDetails = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (usersUID.contains(documentSnapshot.getId()) && !documentSnapshot.getId().equals(mAuth.getCurrentUser().getUid())) { //Found user details
                            currentUsersInParkDetails.add(documentSnapshot.toObject(Profile.class));
                        }
                    }
                    Collections.sort(currentUsersInParkDetails, (o1, o2) -> o1.getLastName().compareTo(o2.getLastName())); //sort by last name
                    setUpRecyclerView(currentUsersInParkDetails);
                } else Log.d(TAG, "onComplete: Failure" + task.getException().getMessage());
            }
        });
    }

    private void setUpRecyclerView(ArrayList<Profile> profiles) {
        MapProfileBottomSheetDialog bottomSheetDialog = new MapProfileBottomSheetDialog(profiles, currentClickedMarker, CHECKIN_MSG);
        bottomSheetDialog.show(getSupportFragmentManager(), "idk");
    }

    @Override
    public void onButtonClickedInsideBottomSheet() {
        if (currentClickedMarker.getSnippet().equals(CHECKIN_MSG)) {
            checkIn(currentClickedMarker.getTitle());
            currentClickedMarker.setSnippet(CHECKOUT_MSG);
        } else {
            db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
            currentClickedMarker.setSnippet(CHECKIN_MSG);
        }
    }

    /**
     * Check in the selected park.
     *
     * @param title name of the park
     */
    public void checkIn(String title) {
        Log.d(TAG, "checkIn: " + getUserCheckinPark());
        if (getUserCheckinPark() != null) {
            db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
        }
        db.collection(PARK_CHECKIN).document(title).update(CHECKIN_FIELD, FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
        setUserCheckinPark(title);
    }

    public void displayPopupWindow(){
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE)
                startActivityForResult(new Intent(MapActivity.this, WatchProfile.class),
                        REQUEST_PROFILE_BUILD);
            dialog.dismiss();
        };
        new AlertDialog.Builder(MapActivity.this)
                .setTitle(POPUP_WINDOW_TITLE)
                .setMessage(POPUP_WINDOW_BODY)
                .setPositiveButton("OK", dialogClickListener)
                .setCancelable(false)
                .show();
    }
}




