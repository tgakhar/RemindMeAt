package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Location.SingleShotLocationProvider;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dhankara Chintan
 * @author Karthik Modubowna
 * Java class for Reminderdialog {@link ReminderdialogActivity}
 */

public class ReminderdialogActivity extends AppCompatActivity {
    /**
     * Object of FirebaseFirestore
     */
    FirebaseFirestore db;
    /**
     * Object of FirebaseAuth
     */
    FirebaseAuth auth;
    /**
     * Object of FirebaseUser
     */
    FirebaseUser user;
    /**
     * Object of reminder
     */
    Reminder reminder;
    /**
     * Variables of Text views
     */
    TextView txt_tite,txt_location,txt_distance;
    /**
     * Variable of Image Views
     */
    ImageView img_delete,img_location,img_edit;
    /**
     * object of Toggle Switch
     */
    SwitchMaterial switchMaterial;
    /**
     * Object of dialog
     */
    Dialog dialog1;

    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderdialog);
    }

    /**
     * showDialog Method used to display the dialog requested
     * @param activity
     * @param reminder
     */
    public void showDialog(final FragmentActivity activity, final Reminder reminder) {
        this.reminder=reminder;
        final Dialog dialog = new Dialog(activity);
        dialog1=dialog;
        dialog.requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_reminderdialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width= WindowManager.LayoutParams.MATCH_PARENT;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.y=150;
        window.setAttributes(wlp);
        //ImageView next = (ImageView) dialog.findViewById(R.id.btn_dialog);

        showReminderDetail(dialog,reminder);
/**
 * setOnClickListener method for when the title of the reminder is clicked
 */
        txt_tite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity, ReminderdetailsActivity.class) ;
                i.putExtra("Reminder",reminder);
                activity.startActivity(i);
            }
        });
/**
 * setOnClickListener method for when the image of the reminder is clicked
 */
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(activity, EditreminderActivity.class) ;
                i.putExtra("Reminder",reminder);
                activity.startActivity(i);
            }
        });
        dialog.show();

    }

    /**
     * Method to show the details of the reminder
     * @param dialog
     * @param reminder
     */

    private void showReminderDetail(final Dialog dialog, final Reminder reminder) {

        txt_tite= dialog.findViewById(R.id.txt_dialogTitle);
        txt_location= dialog.findViewById(R.id.txt_dialogLocation);
        img_delete=dialog.findViewById(R.id.img_dialogDelete);
        txt_distance=dialog.findViewById(R.id.txt_dialogDistance);
        img_location=dialog.findViewById(R.id.img_dialogLocation);
        img_edit=dialog.findViewById(R.id.img_dialogEdit);
        switchMaterial=dialog.findViewById(R.id.switch_dialogStatus);
        txt_tite.setText(reminder.getReminderTitle());
        txt_location.setText(reminder.getReminderLocation());
        if (reminder.getReminderStatus()==0){
            switchMaterial.setChecked(false);
        }
        final LatLng target=new LatLng(reminder.getReminderLat(),reminder.getReminderLong());
        SingleShotLocationProvider.requestSingleUpdate(dialog.getContext(),
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        final Location loc1 = new Location("");
                        loc1.setLatitude(target.latitude);
                        loc1.setLongitude(target.longitude);
                        final Location loc2 = new Location("");
                        loc2.setLatitude(location.latitude);
                        loc2.setLongitude(location.longitude);
                        float d= BigDecimal.valueOf((loc1.distanceTo(loc2))/1000).setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                        txt_distance.setText(""+d+"Km");
                    }
                });
        /**
         * setOnCheckedChangeListener to switch the active/inactive status of the reminder
         */
        switchMaterial.setOnCheckedChangeListener(updateStatus);
        /**
         * Image View onClickListeners to Open Map
         */
        img_location.setOnClickListener(openMap);
        /**
         * Text View onClickListener to Open Map
         */
        txt_distance.setOnClickListener(openMap);
        /**
         * Image View onClickListener to delete reminder
         */
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth=FirebaseAuth.getInstance();
                db=FirebaseFirestore.getInstance();
                user=auth.getCurrentUser();
                DocumentReference docRef=db.collection("Users").document(user.getUid()).collection("Reminder").document(reminder.getReminderId());

                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * Delete reminder onSuccess method
                     * @param aVoid
                     */
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            //Intent intent=new Intent(this,LocationService.class);
                            dialog.getContext().stopService(new Intent(dialog.getContext(), LocationService.class));
                            dialog.getContext().startForegroundService(new Intent(dialog.getContext(), LocationService.class));
                        }
                        else{
                            // Intent intent=new Intent(this,LocationService.class);
                            dialog.getContext().stopService(new Intent(dialog.getContext(), LocationService.class));
                            dialog.getContext().startService(new Intent(dialog.getContext(), LocationService.class));
                        }
                    Intent intent=new Intent(dialog.getContext(), DashActivity.class);
                    dialog.getContext().startActivity(intent);
                    }
                });
            }
        });
    }




    SwitchMaterial.OnCheckedChangeListener updateStatus=new CompoundButton.OnCheckedChangeListener() {
        /**
         * onCheckedChanged method to set active/inactive reminders
         * @param compoundButton
         * @param b
         */
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            auth=FirebaseAuth.getInstance();
            db=FirebaseFirestore.getInstance();
            user=auth.getCurrentUser();
            DocumentReference docRef=db.collection("Users").document(user.getUid()).collection("Reminder").document(reminder.getReminderId());
            int s;
            if (b){
                //Toast.makeText(getActivity().getApplicationContext(), "Status changed to active", Toast.LENGTH_SHORT).show();
                s=1;
            }else {
                //Toast.makeText(getActivity().getApplicationContext(), "Status changed to inactive", Toast.LENGTH_SHORT).show();
                s=0;
            }
            Map<String,Object> status=new HashMap<>();
            status.put("Status",s);
            docRef.update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        //Intent intent=new Intent(this,LocationService.class);
                        dialog1.getContext().stopService(new Intent(dialog1.getContext(), LocationService.class));
                        dialog1.getContext().startForegroundService(new Intent(dialog1.getContext(), LocationService.class));
                    }
                    else{
                        // Intent intent=new Intent(this,LocationService.class);
                        dialog1.getContext().stopService(new Intent(dialog1.getContext(), LocationService.class));
                        dialog1.getContext().startService(new Intent(dialog1.getContext(), LocationService.class));
                    }
                    //Toast.makeText(getActivity().getApplicationContext(), "Status Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    /**
     * onClickListener to open Map for navigation
     */
    View.OnClickListener openMap =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+reminder.getReminderLat()+","+reminder.getReminderLong()));
            startActivity(intent);
        }
    };

}