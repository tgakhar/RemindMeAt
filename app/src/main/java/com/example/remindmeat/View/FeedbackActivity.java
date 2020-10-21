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

public class FeedbackActivity extends AppCompatActivity {
    Button btn_send;
    EditText txt_message;
    String message;
    MaterialToolbar toolbar;
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

    View.OnClickListener toolNav=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

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