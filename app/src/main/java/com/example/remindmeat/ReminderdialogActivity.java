package com.example.remindmeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.View.ReminderdetailsActivity;

public class ReminderdialogActivity extends AppCompatActivity {

    Reminder reminder;
    TextView txt_tite,txt_location;
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

    private void showReminderDetail(Dialog dialog, Reminder reminder) {

        txt_tite= dialog.findViewById(R.id.txt_dialogTitle);
        txt_location= dialog.findViewById(R.id.txt_dialogLocation);

        txt_tite.setText(reminder.getReminderTitle());
        txt_location.setText(reminder.getReminderLocation());

    }



}