package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Location.SingleShotLocationProvider;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.DashActivity;
import com.example.remindmeat.View.ReminderdetailsActivity;
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

public class ReminderdialogActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    Reminder reminder;
    TextView txt_tite,txt_location,txt_distance;
    ImageView img_delete,img_location;
    SwitchMaterial switchMaterial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminderdialog);
    }

    public void showDialog(final FragmentActivity activity, final Reminder reminder) {
        this.reminder=reminder;
        final Dialog dialog = new Dialog(activity);
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

        txt_tite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity, ReminderdetailsActivity.class) ;
                i.putExtra("Reminder",reminder);
                activity.startActivity(i);
            }
        });
        dialog.show();

    }

    private void showReminderDetail(final Dialog dialog, final Reminder reminder) {

        txt_tite= dialog.findViewById(R.id.txt_dialogTitle);
        txt_location= dialog.findViewById(R.id.txt_dialogLocation);
        img_delete=dialog.findViewById(R.id.img_dialogDelete);
        txt_distance=dialog.findViewById(R.id.txt_dialogDistance);
        img_location=dialog.findViewById(R.id.img_dialogLocation);
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
        switchMaterial.setOnCheckedChangeListener(updateStatus);
        img_location.setOnClickListener(openMap);
        txt_distance.setOnClickListener(openMap);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth=FirebaseAuth.getInstance();
                db=FirebaseFirestore.getInstance();
                user=auth.getCurrentUser();
                DocumentReference docRef=db.collection("Users").document(user.getUid()).collection("Reminder").document(reminder.getReminderId());

                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    Intent intent=new Intent(dialog.getContext(), DashActivity.class);
                    dialog.getContext().startActivity(intent);
                    }
                });
            }
        });
    }


    SwitchMaterial.OnCheckedChangeListener updateStatus=new CompoundButton.OnCheckedChangeListener() {
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
                    //Toast.makeText(getActivity().getApplicationContext(), "Status Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    View.OnClickListener openMap =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+reminder.getReminderLat()+","+reminder.getReminderLong()));
            startActivity(intent);
        }
    };

}