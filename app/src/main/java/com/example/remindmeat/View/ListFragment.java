package com.example.remindmeat.View;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Adapter.ReminderAdapter;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.example.remindmeat.ReminderdetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListFragment extends Fragment {
    FirebaseUser curUser;
    FirebaseAuth auth;
    FirebaseFirestore db;
    List<Reminder> reminderList = new ArrayList<>();
    RecyclerView recyclerView_listReminder;
    ReminderAdapter reminderAdapter;

    //For exapndable Floating menu
    private FloatingActionButton fab_main, fab_all, fab_inactive,fab_active;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_all,textview_mail, textview_active;
    int status=0;
    Boolean isOpen = false;
    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView_listReminder = view.findViewById(R.id.recycler_listReminder);
        loadData();

        fab_main = view.findViewById(R.id.fab);
        fab_all = view.findViewById(R.id.fab_all);
        fab_inactive = view.findViewById(R.id.fab_inactive);
        fab_active=view.findViewById(R.id.fab_active);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_mail = (TextView) view.findViewById(R.id.textview_inactive);
        textview_active = (TextView) view.findViewById(R.id.textview_active);
        textview_all=(TextView) view.findViewById(R.id.textview_all) ;

        fab_main.setOnClickListener(fabMain);
        fab_active.setOnClickListener(fabActive);
        fab_inactive.setOnClickListener(fabInactive);
        fab_all.setOnClickListener(fabAll);
    }

    View.OnClickListener fabMain=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpen) {

                textview_mail.setVisibility(View.INVISIBLE);
                textview_active.setVisibility(View.INVISIBLE);
                textview_all.setVisibility(View.INVISIBLE);
                fab_inactive.startAnimation(fab_close);
                fab_all.startAnimation(fab_close);
                fab_active.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab_inactive.setClickable(false);
                fab_all.setClickable(false);
                fab_active.setClickable(false);
                isOpen = false;
            } else {
                textview_mail.setVisibility(View.VISIBLE);
                textview_active.setVisibility(View.VISIBLE);
                textview_all.setVisibility(View.VISIBLE);
                fab_inactive.startAnimation(fab_open);
                fab_all.startAnimation(fab_open);
                fab_active.startAnimation(fab_open);
                fab_main.startAnimation(fab_clock);
                fab_inactive.setClickable(true);
                fab_all.setClickable(true);
                fab_active.setClickable(true);
                isOpen = true;
            }
        }
    };


    View.OnClickListener fabActive=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fab_active.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Gray)));
            fab_inactive.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            fab_all.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            status=1;
            filterData();
        }
    };

    View.OnClickListener fabInactive=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fab_inactive.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Gray)));
            fab_active.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            fab_all.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            status=0;
            filterData();
        }
    };

    View.OnClickListener fabAll=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //fab_all.setBackground(getResources().getDrawable(R.drawable.clicked_floating));
            fab_all.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Gray)));
            fab_inactive.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            fab_active.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.White)));
            loadData();
        }
    };



    private void filterData(){
        reminderList.clear();
        curUser=auth.getCurrentUser();
        CollectionReference collectionReference=db.collection("Users").document(curUser.getUid()).collection("Reminder");
        collectionReference.whereEqualTo("Status",status).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:task.getResult()){

                        String reminderId=(String) document.getId();
                        String reminderTitle=(String) document.getData().get("Title");
                        String reminderLocation=(String) document.getData().get("Address");
                        String reminderDescription=(String) document.getData().get("Description");
                        String reminderDate=(String) document.getData().get("Date");
                        Integer reminderRepeat= ((Long) document.getData().get("Repeat")).intValue();
                        Integer reminderRange= ((Long) document.getData().get("Range")).intValue();
                        Integer reminderStatus= ((Long) document.getData().get("Status")).intValue();
                        Double reminderLat= (Double) document.getData().get("Latitude");
                        Double reminderLong= (Double) document.getData().get("Longitude");

                        addToList(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong);

                    }
                }
                setReminderRecycler(reminderList);
            }
        });

    }

    private void loadData() {
        reminderList.clear();
        curUser = auth.getCurrentUser();
        CollectionReference collectionReference = db.collection("Users").document(curUser.getUid()).collection("Reminder");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String reminderId = (String) document.getId();
                        String reminderTitle = (String) document.getData().get("Title");
                        String reminderLocation = (String) document.getData().get("Address");
                        String reminderDescription = (String) document.getData().get("Description");
                        String reminderDate = (String) document.getData().get("Date");
                        Integer reminderRepeat = ((Long) document.getData().get("Repeat")).intValue();
                        Integer reminderRange = ((Long) document.getData().get("Range")).intValue();
                        Integer reminderStatus = ((Long) document.getData().get("Status")).intValue();
                        Double reminderLat = (Double) document.getData().get("Latitude");
                        Double reminderLong = (Double) document.getData().get("Longitude");

                        addToList(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong);

                    }

                    setReminderRecycler(reminderList);
                }

            }
        });


    }

    private void addToList(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong) {

        reminderList.add(new Reminder(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong));

        setReminderRecycler(reminderList);
    }

    private void setReminderRecycler(final List<Reminder> reminderList) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext()
                , RecyclerView.VERTICAL, false);
        recyclerView_listReminder.setLayoutManager(layoutManager);
        reminderAdapter = new ReminderAdapter(reminderList, getActivity());
        recyclerView_listReminder.setAdapter(reminderAdapter);
        reminderAdapter.setOnClickListner(adapterClick);
        reminderAdapter.setStatusChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) compoundButton.getTag();
                final int position = viewHolder.getAdapterPosition();
                curUser=auth.getCurrentUser();
                DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminderList.get(position).getReminderId());
                int s=0;
                if (b){
                    s=1;
                }else {
                    s=0;
                }
                Map<String,Object> status=new HashMap<>();
                status.put("Status",s);
                docRef.update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ListFragment","Status Updated");
                    }
                });
            }
        });
    }

    View.OnClickListener adapterClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_delete:
                    deleteReminder(view);

                    break;
                case R.id.img_edit:
                    Toast.makeText(getActivity().getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txt_layoutTitle:
                case R.id.txt_layoutLocation:
                    showReminderDetails(view);
                    break;
                case R.id.txt_layoutDistance:
                case R.id.img_location:
                    openMap(view);
                    break;

            }


        }
    };

    private void openMap(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+reminderList.get(position).getReminderLat()+","+reminderList.get(position).getReminderLong()));
        startActivity(intent);

    }

    private void showReminderDetails(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();

        Intent intent=new Intent(getActivity().getApplicationContext(), ReminderdetailsActivity.class);
        intent.putExtra("Reminder",reminderList.get(position));
        startActivity(intent);
    }

    private void deleteReminder(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        curUser=auth.getCurrentUser();
        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminderList.get(position).getReminderId());

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                reminderList.remove(position);
                setReminderRecycler(reminderList);
            }
        });

    }

}