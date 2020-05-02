package com.evan.parknbark.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseNavDrawerActivity;
import com.evan.parknbark.utilities.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.evan.parknbark.R.id.fragment_container;

public class MapActivity extends BaseNavDrawerActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "MapActivity";
    public static final String PARK_LOCATIONS_DB = "parklocations";
    public static final String LATLNG_FIELD = "latlng";
    public static final String PARK_CHECKIN = "parkcheckin";
    public static final String CHECKIN_FIELD = "currentProfilesInPark";
    public static final String CHECKIN_MSG = "Check in here?";
    public static final String CHECKOUT_MSG = "Check out?";
    public static final String GPS_NOT_ENABLE = "GPS is not enabled. Turn it on?";
    public static final String ENABLE_GPS_MSG = "Yes";
    public static final String NOT_ENABLE_GPS_MSG = "No";

    //Map variables
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final float zoom = 14.5f;
    private final float defaultZoom = 13f;
    private LatLng defaultLoc;
    private static final double defaultLan = 31.249927;
    private static final double defaultLon = 34.791930;
    private AlertDialog alert;
    private boolean gps_enabled = false;


    //permissions
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    //profiles
    private ArrayList<User> userLocations = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * permissions holds the types of permissions needed for the app.
     * this code requests the permissions from the user. if the user has granted those permissions -> the map is initiated
     * else, the app asks user for these permissions.
     */
    private void getLocationsPermissions() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
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
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
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
                                mMap.addMarker(new MarkerOptions().position(latLng).title(document.getId()).snippet(CHECKIN_MSG));
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
        //TODO (Noah) this whole section till the else should move to the getDeviceLocation method and gps_enabled should move to be a class activity variable. should fix this later.
        LocationManager lm = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        mMap.clear();
        setParksMarkers();

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled) {
            enableGps(alert);
            defaultLoc = new LatLng(defaultLan, defaultLon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, defaultZoom));
        } else {
            LatLng userLatLon = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLon, zoom));
        }
    }

    private void enableGps(AlertDialog alert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (alert == null) {
            builder.setMessage(GPS_NOT_ENABLE);
            builder.setPositiveButton(ENABLE_GPS_MSG,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            builder.setNegativeButton(NOT_ENABLE_GPS_MSG, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        alert = builder.create();
        alert.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationsPermissions();
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
        }

        mMap.setOnInfoWindowClickListener(this);
    }


    /*public void checkIn(String title) {
        Log.d(TAG, "checkIn: " + getUserCheckinPark());
        if (getUserCheckinPark() != null) {
            db.collection(PARK_CHECKIN).document(getUserCheckinPark()).update(CHECKIN_FIELD, FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
        }
        db.collection(PARK_CHECKIN).document(title)
                .update(CHECKIN_FIELD, FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
        setUserCheckinPark(title);
    }*/

    /**
     * comment this
     *
     * @param marker
     */
    @Override
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ATTENTION!!!!!!!!! uncomment "checkIn(marker.getTitle());" should be after an if section.!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void onInfoWindowClick(Marker marker) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setMessage(marker.getSnippet())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (marker.getSnippet().equals(CHECKIN_MSG)) {
                            //checkIn(marker.getTitle());
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
     * retrieving users that stay currently in parks from DB
     */
    public void getUserLocation(String parkName) {
        db.collection("parkcheckin").document(parkName).collection("currentProfilesInPark")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().toObjects(User.class) != null) {
                                userLocations = ((ArrayList<User>) task.getResult().toObjects(User.class));
                                System.out.println(userLocations);
                            }
                        } else {
                            Log.w(TAG, "Error getting users.", task.getException());
                        }
                    }
                });
    }

    /**
     * After a click on a marker this function will be activated
     */
    public boolean onMarkerClick(Marker marker) {
        //creating a fragment that holds the users that checked in
        usersFragment usersfragment = new usersFragment();
        if(usersfragment!=null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(fragment_container, usersfragment);

            getUserLocation(marker.getTitle());
            final Bundle bdl = new Bundle();
            //bdl.putString("users",userLocations );
            usersfragment.setArguments(bdl);
            ft.commit();
        }
        else {
            Log.e(TAG, "Error in creating usersfragment");
        }



        return false;
    }
}




