package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AddreminderActivity extends AppCompatActivity {
    AutocompleteSupportFragment autocompleteFragmentLocation;
    LatLng latLng;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminder);



        autocompleteFragmentLocation= (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.location_fragment);
        autoCompleteFragment();
    }

    void autoCompleteFragment(){
        autocompleteFragmentLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
        Places.initialize(getApplicationContext(), "AIzaSyD65EBPYQAG0ITBPgmZHk_vNMKsexCEVcA");
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.getAddressComponents());
                setSearchUI();
                latLng=place.getLatLng();
                address=place.getName();
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });

    }


    private void setSearchUI() {
        View fView = autocompleteFragmentLocation.getView();
        EditText etTextInput = fView.findViewById(R.id.places_autocomplete_search_input);
        etTextInput.setTextColor(Color.BLACK);
        etTextInput.setHintTextColor(Color.GRAY);
        etTextInput.setTextSize(14.5f);
        etTextInput.setHint("Address");
        ImageButton close = fView.findViewById(R.id.places_autocomplete_clear_button);
        close.setVisibility(View.GONE);
    }
}