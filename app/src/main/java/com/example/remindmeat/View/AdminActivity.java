package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.remindmeat.Location.LocationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This java class is for {@link AdminActivity}
 */
public class AdminActivity extends AppCompatActivity {
    /**
     * Variable of {@link ArrayList Admin type}
     */
    List<Admin> adminList=new ArrayList<>();

    /**
     * Object of {@link FirebaseAuth}
     */
    FirebaseAuth auth;

    /**
     * Object of {@link FirebaseFirestore}
     */
    FirebaseFirestore db;

    /**
     * Recyclerview variable
     */
    RecyclerView recyclerView;
    /**
     * Object of {@link AdminAdapter}
     */
    AdminAdapter adminAdapter;
    /**
     * Object of {@link MaterialToolbar}
     */
    MaterialToolbar toolbar;

    /**
     * Object of {@link EditText for search bar}
     */
    EditText searchAdmin;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.topAdminAppBar);
        searchAdmin=findViewById(R.id.edt_searchList_admin);
        searchAdmin.addTextChangedListener(searchAdminAdapter);

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


        searchAdminList();



        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setMessage("Do you want to Sign out?")
                        .setCancelable(false)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                auth.signOut();
                                Intent intent=new Intent(AdminActivity.this,MainActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Sign Out!!!");
                alert.show();

            }
        });
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycler_admin);
        loadUser();
    }


    /**
     * This method set design of search edit text
     */
    private void searchAdminList() {

        searchAdmin.setBackgroundResource(R.drawable.search_input_style);

    }

    /**
     * {@link TextWatcher} method for getting text value on change
     */
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


    /**
     * This method is used to get all User details from database
     */
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
                            Integer userDisable = ((Long) document.getData().get("Disabled")).intValue();
                            addToList(userId,userEmail,userDisable);
                        }

                    }

                }
            }
        });

    }

    /**
     * This method is used to add object of {@link Admin} in {@link ArrayList}.
     * @param userId
     * @param userEmail
     * @param userDisable
     */
    private void addToList(String userId, String userEmail, Integer userDisable) {

        adminList.add(new Admin(userEmail,userId,userDisable));

        setAdminRecycler(adminList);
    }

    /**
     * This method is setting adapter to recycler view using object of {@link AdminAdapter}
     * @param adminList
     */
    private void setAdminRecycler(final List<Admin> adminList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
                , RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adminAdapter = new AdminAdapter(adminList, AdminActivity.this);
        recyclerView.setAdapter(adminAdapter);

        adminAdapter.setOnClickListner(adapterClick);
    }

    /**
     * Click Listener for adapter
     */
    View.OnClickListener adapterClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_delete:
                    deleteUser(view);
                    break;
                case R.id.img_disable:
                    updateUser(view);
                    break;
                case R.id.img_reset:
                    resetPass(view);
                    break;
            }
        }
    };

    /**
     * This method is used to send password reset link to user's email
     * @param view
     */
    private void resetPass(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        Admin admin=adminAdapter.getItem(position);
        auth.sendPasswordResetEmail(admin.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AdminActivity.this, "Password reset link sent successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * This method is used to disable/enable user from application.
     * @param view
     */
    private void updateUser(View view) {
        final int d;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        String url;
        final Admin admin=adminAdapter.getItem(position);
        if (admin.getDisabled()==0){
            d=1;
             url ="http://10.123.157.102:3000/disable/"+admin.getUId();
        }else {
            d=0;
            url ="http://10.123.157.102:3000/enable/"+admin.getUId();
        }
        Log.d("AdminActivity","Link"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Map<String,Object>map=new HashMap<>();
                        map.put("Disabled",d);

                        db.collection("Users").document(admin.getUId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AdminActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                    adminList.clear();
                                    loadUser();
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

    /**
     * This method is used to delete user from application as well as user data from database.
     * @param view
     */
    private void deleteUser(View view) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        final Admin admin=adminAdapter.getItem(position);
        String url ="http://10.123.157.102:3000/delete/"+admin.getUId();
        Log.d("AdminActivity","Link"+url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        db.collection("Users").document(admin.getUId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AdminActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    adminList.clear();
                                    loadUser();
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