package com.example.remindmeat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.remindmeat.Adapter.AdminAdapter;
import com.example.remindmeat.Model.Admin;
import com.example.remindmeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.topAdminAppBar);

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

    }


}