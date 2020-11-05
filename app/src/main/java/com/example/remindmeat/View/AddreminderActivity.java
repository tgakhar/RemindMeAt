package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * AddReminder class is used for adding all the reminders to the list {@link AddreminderActivity}.
 */
public class AddreminderActivity extends AppCompatActivity {
    /**
     * variable of AutoCompleteFragmentLocation
     */
    AutocompleteSupportFragment autocompleteFragmentLocation;
    /**
     * variable of LatLng
     */
    LatLng latLng;
    /**
     * Variable of Slider
     */
    Slider slider;
    /**
     * variable of TextView
     */
    TextView txt_rangeValue;
    /**
     * Variable of SwitchMaterial
     */
    SwitchMaterial repeatSwitch;
    /**
     * All the variables of TextInputLayout
     */
    TextInputLayout edt_date,edt_title,edt_description;
    /**
     * Variable of Button
     */
    Button btn_addReminder;
    /**
     * All the variable of String
     */
    String title,address,description,date;
    /**
     * Variables of type int for repeatMode and Range of the Reminder
     */
    int repeatMode,range;
    /**
     * Default int value for status is 1
     */
    int status = 1;
    /**
     * variable of FirebaseAuth
     */
    FirebaseAuth auth;
    /**
     * Variable of FirebaseFirestore
     */
    FirebaseFirestore db;
    /**
     * Variable of FirebaseUser
     */
    FirebaseUser curUser;
    /**
     * Variable of MaterialToolbar
     */
    MaterialToolbar toolbar;
    /**
     * variable of reminder
     */
    Reminder reminder;
    /**
     * Default boolean value for reAdding is false
     */
    Boolean reAdding=false;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminder);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        Intent intent = getIntent();

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
        if (intent.hasExtra("Reminder")) {
            reminder=intent.getExtras().getParcelable("Reminder");
            setReminderData();
        }
    }

    /**
     * for setting the Reminder data
     */
    private void setReminderData() {
        edt_title.getEditText().setText(reminder.getReminderTitle());
        edt_description.getEditText().setText(reminder.getReminderDescription());
        slider.setValue(reminder.getReminderRange());
        txt_rangeValue.setText(reminder.getReminderRange()+" m");
        reAdding=true;
        if (reminder.getReminderRepeat()==0){
            repeatSwitch.setChecked(false);
            if (reminder.getReminderDate().equals("0")){

            }else {
                edt_date.getEditText().setError("Please, Select new date.");
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


    /**
     * onClickListener for back press button
     */
    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    /**
     * autoCompleteFragment for selecting address and the latitude and longitude for the place
     */
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


    /**
     *  onChangeListener for updating the vale for the slider for specifying range
     */
    Slider.OnChangeListener updateSlider=new Slider.OnChangeListener() {
        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            txt_rangeValue.setText(((int) value)+" m");

        }
    };


    /**
     * onClickListener for date picker for selecting date
     */
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    Date date = null;
                    try {
                         date = sdf.parse(materialDatePicker.getHeaderText());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sdf.applyPattern("dd-MM-yyyy");
                    edt_date.getEditText().setText(sdf.format(date));
                    Log.d("Add","Long="+selection);
                }
            });
        }
    };


    /**
     * onCheckedChangeListener for selecting repeat mode of reminder
     */
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

    /**
     * onClickListener for addReminder button
     */
    View.OnClickListener addReminder= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            title=edt_title.getEditText().getText().toString().trim();
            description=edt_description.getEditText().getText().toString().trim();
            range= (int) slider.getValue();

            if (TextUtils.isEmpty(title)){
                edt_title.setError("Title Required!");
                return;
            }else if (latLng==null||address.isEmpty()){
                edt_title.setError(null);
                Toast.makeText(AddreminderActivity.this, "Please select location", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (TextUtils.isEmpty(description)){
                edt_description.setError("Description Required!");
                return;
            }

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
                    if (reAdding){
                        deleteFromHistory();
                    }

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

    /**
     * for deleting reminder from history
     */
    private void deleteFromHistory() {
        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder History").document(reminder.getReminderId());

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    /**
     * for searching the location
     */
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