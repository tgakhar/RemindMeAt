package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.R;
import com.example.remindmeat.ViewnotificationActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class AddreminderActivity extends AppCompatActivity {
    AutocompleteSupportFragment autocompleteFragmentLocation;
    LatLng latLng;
    Slider slider;
    TextView txt_rangeValue;
    SwitchMaterial repeatSwitch;
    TextInputLayout edt_date,edt_title,edt_description;
    Button btn_addReminder;
    String title,address,description,date;
    int repeatMode,range;
    int status = 1;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser curUser;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminder);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        slider=findViewById(R.id.slider);
        txt_rangeValue=findViewById(R.id.txt_addRangeSelected);
        edt_date=findViewById(R.id.edt_addDate);
        edt_title=findViewById(R.id.edt_addTitle);
        edt_description=findViewById(R.id.edt_addDescription);
        repeatSwitch=findViewById(R.id.switch_addRepeat);
        btn_addReminder=findViewById(R.id.btn_addReminder);
        toolbar=findViewById(R.id.topbar_addReminder);
        btn_addReminder.setOnClickListener(addReminder);

        toolbar.setNavigationOnClickListener(toolNav);
        repeatSwitch.setOnCheckedChangeListener(repeat);
        edt_date.getEditText().setOnClickListener(datePicker);
        slider.addOnChangeListener(updateSlider);
        autocompleteFragmentLocation= (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.location_fragment);
        autoCompleteFragment();
    }

    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    void autoCompleteFragment(){
        autocompleteFragmentLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
        Places.initialize(getApplicationContext(), "AIzaSyC3V_fpRwWxPgiIrptG_Hi7VEEGl5Fd4d8");
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
    Slider.OnChangeListener updateSlider=new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            txt_rangeValue.setText(((int) value)+" m");

        }
    };

    View.OnClickListener datePicker=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calender.clear();

            Long today = MaterialDatePicker.todayInUtcMilliseconds();

            calender.setTimeInMillis(today);

            CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
            constraint.setValidator(DateValidatorPointForward.now());

            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Please select date");
            builder.setCalendarConstraints(constraint.build());
            //builder.setTheme(R.style.MaterialCalendarTheme);
            final MaterialDatePicker<Long> materialDatePicker = builder.build();
            builder.setSelection(today);

            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    edt_date.getEditText().setText(materialDatePicker.getHeaderText());

                }
            });
        }
    };

    SwitchMaterial.OnCheckedChangeListener repeat=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b){
                edt_date.getEditText().getText().clear();
                edt_date.setEnabled(false);
            }else {
                edt_date.setEnabled(true);
            }
        }
    };

    View.OnClickListener addReminder= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            title=edt_title.getEditText().getText().toString().trim();
            description=edt_description.getEditText().getText().toString().trim();
            range= (int) slider.getValue();
            if (repeatSwitch.isChecked()){
                repeatMode=1;
                date="0";
            }else{
                repeatMode=0;
                date=edt_date.getEditText().getText().toString();
                if (date.length()<2){
                    date="0";
                }
            }

            Log.d("Addreminder","Title"+title+", Description"+description+", Range"+range+", repeateMode"+repeatMode+", Date"+date+", Address="+address);

            Random random = new Random(System.nanoTime() % 100000);

            int randomInt = random.nextInt(1000000000);

            Map<String, Object> reminderMap = new HashMap<>();

            reminderMap.put("Title",title);
            reminderMap.put("Description",description);
            reminderMap.put("Address",address);
            reminderMap.put("Repeat",repeatMode);
            reminderMap.put("Date",date);
            reminderMap.put("Range",range);
            reminderMap.put("Latitude",latLng.latitude);
            reminderMap.put("Longitude",latLng.longitude);
            reminderMap.put("Status",status);
            reminderMap.put("UniqueId",randomInt);

            curUser=auth.getCurrentUser();

            db.collection("Users").document(curUser.getUid()).collection("Reminder").add(reminderMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(AddreminderActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        //Intent intent=new Intent(this,LocationService.class);
                        stopService(new Intent(AddreminderActivity.this, LocationService.class));
                        startForegroundService(new Intent(AddreminderActivity.this, LocationService.class));
                    }
                    else{
                        // Intent intent=new Intent(this,LocationService.class);
                        stopService(new Intent(AddreminderActivity.this, LocationService.class));
                        startService(new Intent(AddreminderActivity.this, LocationService.class));
                    }
                    Intent intent=new Intent(AddreminderActivity.this,DashActivity.class);
                    startActivity(intent);
                }
            });

        }
    };


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