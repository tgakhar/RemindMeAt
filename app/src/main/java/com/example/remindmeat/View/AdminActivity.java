package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.remindmeat.Adapter.AdminAdapter;
import com.example.remindmeat.Model.Admin;
import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    List<Admin> adminList=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    AdminAdapter adminAdapter;
    MaterialToolbar toolbar;
    EditText searchAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.topAdminAppBar);
        searchAdmin=findViewById(R.id.edt_searchList_admin);
        searchAdmin.addTextChangedListener(searchAdminAdapter);
         searchAdminList();
        searchAdmin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (searchAdmin.getRight() - searchAdmin.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        searchAdmin.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });






        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(AdminActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycler_admin);
        loadUser();
    }




    private void searchAdminList() {

        searchAdmin.setBackgroundResource(R.drawable.search_input_style);

    }

    TextWatcher searchAdminAdapter=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adminAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };






    private void loadUser() {

        CollectionReference collectionReference=db.collection("Users");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:task.getResult()){

                        if (document.getString("isAdmin")==null){
                            String userId=(String) document.getId();
                            String userEmail=(String) document.getData().get("Email");
                            addToList(userId,userEmail);
                        }

                    }

                }
            }
        });

    }

    private void addToList(String userId, String userEmail) {

        adminList.add(new Admin(userEmail,userId));

        setAdminRecycler(adminList);
    }

    private void setAdminRecycler(List<Admin> adminList) {
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext()
                , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adminAdapter=new AdminAdapter(adminList,AdminActivity.this);
        recyclerView.setAdapter(adminAdapter);
        adminAdapter.setOnClickListner(adapterClick);
    }

    View.OnClickListener adapterClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_delete:
                    deleteUser(view);
                    break;
            }
        }
    };

    private void deleteUser(View view) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        String url ="http://10.123.157.102:3000/delete/"+adminList.get(position).getUId();
        Log.d("AdminActivity","Link"+url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        db.collection("Users").document(adminList.get(position).getUId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AdminActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    adminList.remove(position);
                                    adminAdapter.notifyDataSetChanged();
                                }
                            }
                        });


                        //textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AdminActivity","Error="+error.getMessage());
                Toast.makeText(AdminActivity.this, "Didn't work"+error.getMessage(), Toast.LENGTH_SHORT).show();
                // textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}