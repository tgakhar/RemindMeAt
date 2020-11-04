package com.example.remindmeat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.remindmeat.R;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This java class is for {@link FeedbackActivity}
 */
public class FeedbackActivity extends AppCompatActivity {

    /**
     * Variable of send feedback button
     */
    Button btn_send;
    /**
     * Variable of edit text
     */
    EditText txt_message;

    /**
     * Variable of String type for storing message.
     */
    String message;

    /**
     * variable of toolbar
     */
    MaterialToolbar toolbar;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btn_send=findViewById(R.id.btn_feedBack);
        txt_message=findViewById(R.id.edt_feedBack);
        toolbar=findViewById(R.id.topAppBar_feedback);
        toolbar.setNavigationOnClickListener(toolNav);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=txt_message.getText().toString();
                sendFeedback();
            }
        });
    }

    /**
     * OnCLickListener for back button click in toolbar
     */
    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    /**
     * this method open E-mail application for sending feedback
     */
    private void sendFeedback() {
        String[] TO = {"dhruvj5418@gmail.com","karthik.bablu25@gmail.com","gakhartanvi@gmail.com","nishuuukaur12@gmail.com","akshay9varma@gmail.com","dankharachintan@gmail.com","saikamaljit181294@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Remind Me At");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            //Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            //Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}