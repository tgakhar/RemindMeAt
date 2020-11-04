package com.example.remindmeat.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.ui.IconGenerator;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dhankara Chintan
 * @author Karthik Modubowna
 * Java class for MapFragment {@link MapFragment}
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final int REQUEST_LOCATION_PERMISSION = 1;
    /**
     * Object of GoogleMap Api
     */
    GoogleMap mGoogleMap;
    /**
     * Object of SupportMapFragment
     */
    SupportMapFragment mapFrag;
    /**
     * Variable for LocationRequest
     */
    LocationRequest mLocationRequest;
    /**
     * Object of Location
     */
    Location mLastLocation;
    /**
     * Object of marker for current location
     */
    Marker mCurrLocationMarker;
    /**
     * Object of FusedLocationProviderClient
     */
    FusedLocationProviderClient mFusedLocationClient;
    /**
     * Object of FirebaseAuth
     */
    FirebaseAuth auth;
    /**
     * Object of FirebaseFirestore
     */
    FirebaseFirestore db;
    /**
     * Object of FirebaseUser
     */
    FirebaseUser curUser;
    /**
     * Object of Reminder ArrayList
     */
    List<Reminder> reminderList=new ArrayList<>();
    ArrayList markerLocation= new ArrayList();
    private static final float COORDINATE_OFFSET = 0.000025f;
    private int offsetType = 0;
    private boolean connected;
    public MapFragment() {
        // Required empty public constructor
    }


    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateViewMethod
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * onViewCreated Method
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        requestLocationPermission();
        /**
         * Method to load the map
         */
        loadMap();
        if(checkInternetConnection()){
            loadData();
        }else {
            noInternetConnection();
        }

    }

    /**
     * Method to load data from the Firebase Firestore
     */
    private void loadData() {
        curUser=auth.getCurrentUser();
        CollectionReference collectionReference=db.collection("Users").document(curUser.getUid()).collection("Reminder");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:task.getResult()){

                        String reminderId=(String) document.getId();
                        String reminderTitle=(String) document.getData().get("Title");
                        String reminderLocation=(String) document.getData().get("Address");
                        String reminderDescription=(String) document.getData().get("Description");
                        String reminderDate=(String) document.getData().get("Date");
                        Integer reminderRepeat= ((Long) document.getData().get("Repeat")).intValue();
                        Integer reminderRange= ((Long) document.getData().get("Range")).intValue();
                        Integer reminderStatus= ((Long) document.getData().get("Status")).intValue();
                        Double reminderLat= (Double) document.getData().get("Latitude");
                        Double reminderLong= (Double) document.getData().get("Longitude");
                        Integer reminderUid= ((Long) document.getData().get("UniqueId")).intValue();
                        addToList(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong,reminderUid);

                    }
                    for (int i=0;i<reminderList.size();i++){
                        putReminderMarker(new LatLng(reminderList.get(i).getReminderLat(),reminderList.get(i).getReminderLong()),reminderList.get(i).getReminderId());
                    }
                    Log.d("ListView","List="+reminderList);

                }

            }
        });
    }

    /**
     * To store reminder's in {@link List} reminderList
      * @param reminderId
     * @param reminderTitle
     * @param reminderLocation
     * @param reminderDescription
     * @param reminderDate
     * @param reminderRepeat
     * @param reminderRange
     * @param reminderStatus
     * @param reminderLat
     * @param reminderLong
     * @param reminderUid
     */
    private void addToList(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong, Integer reminderUid) {
        reminderList.add(new Reminder(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong,reminderUid));

    }

    /**
     * Method to check the Internet connection on the device
     * @return
     */
    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    /**
     * Method to run when there is no internet connection
     */
    private void noInternetConnection() {
        final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.mapFrag),"No Internet Connection!!!",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Retry!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()){
                    snackbar.dismiss();
                    loadData();
                }else {
                    noInternetConnection();
                }
            }
        }).show();
    }


    /**
     * Method to receive results for request permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        loadMap();
    }

    /**
     * Method to request permission to access user's location
     */
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(getActivity(), perms)) {
            Toast.makeText(getActivity().getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();

        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    /**
     * Method to load the map
     */
    private void loadMap() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        Places.initialize(getActivity().getApplicationContext(), "AIzaSyC3V_fpRwWxPgiIrptG_Hi7VEEGl5Fd4d8");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getActivity());
        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.VIEWPORT, Place.Field.ADDRESS_COMPONENTS));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            /**
             * Method when a place is selected in the autocompleteFragment
             */
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("MapFragment", "Place= " + place.getAddressComponents());
                View fView = autocompleteFragment.getView();
                EditText etTextInput = fView.findViewById(R.id.places_autocomplete_search_input);
                etTextInput.setTextColor(Color.BLACK);
                etTextInput.setHintTextColor(Color.GRAY);
                etTextInput.setTextSize(14.5f);


                ImageView ivClear = fView.findViewById(R.id.places_autocomplete_search_button);
                ImageButton close = fView.findViewById(R.id.places_autocomplete_clear_button);
                ivClear.setVisibility(View.GONE);
                close.setVisibility(View.GONE);

                putmarker(place.getLatLng());

            }

            /**
             * Method when there is error in the fragment
             * @param status
             */
            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });
    }

    /**
     * Method to put markers on the map for the reminders
     * @param latLng
     * @param reminderId
     */
    void putReminderMarker(LatLng latLng, String reminderId){


        markerLocation.add(latLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(getLatLng(latLng));
        //Log.d("MapView","Marker="+i+" "+reminderList.get(i));
        //markerOptions.title("Current Position");
        markerOptions.alpha(1.0f);
        IconGenerator iconFactory = new IconGenerator(getActivity());
        iconFactory.setBackground(getResources().getDrawable(R.drawable.logo45));
        iconFactory.setTextAppearance(R.style.myStyleText);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()));
        markerOptions.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());


        Marker m = mGoogleMap.addMarker(markerOptions);
        m.setTag(reminderId);
        m.setTitle("vvv");

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag()!=null){
                    Reminder reminder = null;
                    for(Reminder r : reminderList){
                        if(r.getReminderId() != null && r.getReminderId().contains((String)marker.getTag())){
                            reminder=r;
                        }
                    }
                    ReminderdialogActivity reminderdialogActivity=new ReminderdialogActivity();
                    try {
                        reminderdialogActivity.showDialog(getActivity(),reminder);
                    }
                    catch (WindowManager.BadTokenException e) {
                        //use a log message
                    }
                    return true;
                }else {
                    return false;
                }
            }
        });


    }

    /**
     * This method is used for marker clustering
     * @param latLng
     * @return
     */
    private LatLng getLatLng(LatLng latLng) {

        LatLng updatedLatLng;

        if (markerLocation.contains(latLng)) {
            double latitude = 0;
            double longitude = 0;
            if (offsetType == 0) {
                latitude = latLng.latitude + COORDINATE_OFFSET;
                longitude = latLng.longitude;
            } else if (offsetType == 1) {
                latitude = latLng.latitude - COORDINATE_OFFSET;
                longitude = latLng.longitude;
            } else if (offsetType == 2) {
                latitude = latLng.latitude;
                longitude = latLng.longitude + COORDINATE_OFFSET;
            } else if (offsetType == 3) {
                latitude = latLng.latitude;
                longitude = latLng.longitude - COORDINATE_OFFSET;
            } else if (offsetType == 4) {
                latitude = latLng.latitude + COORDINATE_OFFSET;
                longitude = latLng.longitude + COORDINATE_OFFSET;
            }
            offsetType++;
            if (offsetType == 5) {
                offsetType = 0;
            }


            updatedLatLng = getLatLng(new LatLng(latitude, longitude));

        } else {
            markerLocation.add(latLng);
            updatedLatLng = latLng;
        }
        return updatedLatLng;
    }

    /**
     * onMapReadyMethod method is triggered when map is ready to be used
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setMaxZoomPreference(50);
        mGoogleMap.setMinZoomPreference(1);
        mLocationRequest = new LocationRequest();
        //  mLocationRequest.setInterval(30000); // two minute interval
        // mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);

                UiSettings mapsetting = googleMap.getUiSettings();
                mapsetting.setZoomControlsEnabled(true);
                mapsetting.setZoomGesturesEnabled(true);
                mapsetting.setAllGesturesEnabled(true);
                mapsetting.setScrollGesturesEnabled(true);


            } else {
                //Request Location Permission
                requestLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);

        }

    }


    /**
     * Method used to receive notifications when device location changes
     */

    LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsFragment", "Location= " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                putmarker(latLng);

            }
        }
    };

    /**
     * Place marker at current position
     * @param latLng
     */
    private void putmarker(LatLng latLng) {
        //Place current location marker
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        //markerOptions.title("Current Location");
        markerOptions.alpha(0.8f);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    }
}