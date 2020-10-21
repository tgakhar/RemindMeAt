package com.example.remindmeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.DashActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EditreminderActivity extends AppCompatActivity {

    AutocompleteSupportFragment autocompleteFragmentLocation;
    LatLng latLng;
    Slider slider;
    TextView txt_rangeValue;
    SwitchMaterial repeatSwitch;
    TextInputLayout edt_date,edt_title,edt_description;
    Button btn_editReminder;
    String title,address,description,date;
    int repeatMode,range;
    int status = 1;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser curUser;

    Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editreminder);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        reminder=getIntent().getExtras().getParcelable("Reminder");

        slider=findViewById(R.id.slider_edit);
        txt_rangeValue=findViewById(R.id.txt_editRangeSelected);
        edt_date=findViewById(R.id.edt_editDate);
        edt_title=findViewById(R.id.edt_editTitle);
        edt_description=findViewById(R.id.edt_editDescription);
        repeatSwitch=findViewById(R.id.switch_editRepeat);
        repeatSwitch.setOnCheckedChangeListener(repeat);
        btn_editReminder=findViewById(R.id.btn_editReminder);
        btn_editReminder.setOnClickListener(updateReminder);
        slider.addOnChangeListener(updateSlider);
        edt_date.getEditText().setOnClickListener(datePicker);
        autocompleteFragmentLocation= (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.location_editFragment);
        setReminderData();

        autocompleteFragmentLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
        Places.initialize(getApplicationContext(), "AIzaSyD65EBPYQAG0ITBPgmZHk_vNMKsexCEVcA");
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragmentLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("AutoComplete", "Place=" + place.getAddressComponents());
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

    private void setReminderData() {
        edt_title.getEditText().setText(reminder.getReminderTitle());
        edt_description.getEditText().setText(reminder.getReminderDescription());
        edt_date.getEditText().setText(reminder.getReminderDate());
        slider.setValue(reminder.getReminderRange());
        txt_rangeValue.setText(reminder.getReminderRange()+" m");

        if (reminder.getReminderRepeat()==0){
            repeatSwitch.setChecked(false);
            if (reminder.getReminderDate().length()<2){

            }else {
                edt_date.getEditText().setText(reminder.getReminderDate());
            }

        }else{
            repeatSwitch.setChecked(true);
        }
        address=reminder.getReminderLocation();
        latLng=new LatLng(reminder.getReminderLat(),reminder.getReminderLong());
        View fView = autocompleteFragmentLocation.getView();
        EditText etTextInput = fView.findViewById(R.id.places_autocomplete_search_input);
        etTextInput.setTextColor(Color.BLACK);
        etTextInput.setHintTextColor(Color.GRAY);
        etTextInput.setTextSize(14.5f);
        etTextInput.setHint(reminder.getReminderLocation());
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

    View.OnClickListener updateReminder=new View.OnClickListener() {
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
            reminderMap.put("UniqueId",reminder.getUid());

            curUser=auth.getCurrentUser();

            db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminder.getReminderId()).update(reminderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EditreminderActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(EditreminderActivity.this, DashActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    };


}