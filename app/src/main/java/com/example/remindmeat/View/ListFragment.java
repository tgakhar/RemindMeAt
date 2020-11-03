package com.example.remindmeat.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindmeat.Adapter.ReminderAdapter;
import com.example.remindmeat.Location.LocationService;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
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

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This java class is for showing items in the listview using fragment
 */
public class ListFragment extends Fragment {

    /**
     * Object of FirebaseUser
     */
    FirebaseUser curUser;

    /**
     * Object of FirebaseAuth
     */
    FirebaseAuth auth;

    /**
     * Object of Firestore
     */
    FirebaseFirestore db;

    /**
     * Arraylist of Reminder type
     */
    List<Reminder> reminderList = new ArrayList<>();

    /**
     * Variable for recyclerView
     */
    RecyclerView recyclerView_listReminder;

    /**
     * object of Reminder Adapter
     */
    ReminderAdapter reminderAdapter;
    /**
     * EditText variable
     */
    EditText search;
    //For exapndable Floating menu
    private FloatingActionButton fab_main, fab_all, fab_inactive,fab_active;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_all,textview_mail, textview_active;
    int status=0;
    Boolean isOpen = false;

    /**
     * default constructor for the class
     */
    public ListFragment() {
        // Required empty public constructor

    }

    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * onCreateView Method
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    /**
     * onViewCreated method
     * @param view
     * @param savedInstanceState
     */
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
        search=view.findViewById(R.id.edt_searchList);
        search.addTextChangedListener(searchAdpter);
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        search.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });
        searchList();
    }

    /**
     * For setting the drawable resource file for search list
     */
    private void searchList() {
        search.setBackgroundResource(R.drawable.search_input_style);



    }

    /**
     * {@link TextWatcher} method
     */
    TextWatcher searchAdpter=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            reminderAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * {@link android.view.View.OnClickListener} for filter button
     */
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


    /**
     * OnclickListener for active filter button
     */
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

    /**
     * OnClickListener for inactive filter button
     */
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

    /**
     * OnClickListener for showing all reminders
     */
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

    /**
     * For getting reminder details
     */
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
                        Integer reminderUid= ((Long) document.getData().get("UniqueId")).intValue();
                        addToList(reminderId,reminderTitle,reminderLocation,reminderDescription,reminderDate,reminderRepeat,reminderRange,reminderStatus,reminderLat,reminderLong, reminderUid);

                    }
                }
                setReminderRecycler(reminderList);
            }
        });

    }

    /**
     * Method to fetch reminder data from database
     */
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
                        Integer reminderUid= ((Long) document.getData().get("UniqueId")).intValue();
                        addToList(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong,reminderUid);

                    }

                    setReminderRecycler(reminderList);
                }

            }
        });


    }

    /**
     * Method to add all reminder details into the list
     */
    private void addToList(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong, Integer reminderUid) {

        reminderList.add(new Reminder(reminderId, reminderTitle, reminderLocation, reminderDescription, reminderDate, reminderRepeat, reminderRange, reminderStatus, reminderLat, reminderLong,reminderUid));

        setReminderRecycler(reminderList);
    }

    /**
     * Method for setting the reminder array list to recycler view
     */
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            //Intent intent=new Intent(this,LocationService.class);
                            getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                            getActivity().getApplicationContext().startForegroundService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                        }
                        else{
                            // Intent intent=new Intent(this,LocationService.class);
                            getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                            getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                        }
                        Log.d("ListFragment","Status Updated");
                    }
                });
            }
        });
    }

    /**
     * OnClickListener method for adapter
     */
    View.OnClickListener adapterClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_delete:
                    deleteReminder(view);

                    break;
                case R.id.img_edit:
                    editReminder(view);
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

    /**
     * Method to edit reminder detail by getting the item position in the adapter
     */
    private void editReminder(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        Intent intent=new Intent(getActivity().getApplicationContext(), EditreminderActivity.class);
        intent.putExtra("Reminder",reminderAdapter.getItem(position));
        startActivity(intent);

    }

    /**
     * method to opem google maps for navigation
     */
    private void openMap(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+reminderAdapter.getItem(position).getReminderLat()+","+reminderAdapter.getItem(position).getReminderLong()));
        startActivity(intent);

    }

    /**
     * For showing reminder details
     */
    private void showReminderDetails(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();

        Intent intent=new Intent(getActivity().getApplicationContext(), ReminderdetailsActivity.class);
        intent.putExtra("Reminder",reminderAdapter.getItem(position));
        startActivity(intent);
    }

    /**
     * Method to delete reminder from the listview and database
     */
    private void deleteReminder(View view) {
        RecyclerView.ViewHolder viewHolder=(RecyclerView.ViewHolder) view.getTag();
        final int position = viewHolder.getAdapterPosition();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete the reminder?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        curUser=auth.getCurrentUser();
                        DocumentReference docRef=db.collection("Users").document(curUser.getUid()).collection("Reminder").document(reminderAdapter.getItem(position).getReminderId());

                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity().getApplicationContext(),"Reminder deleted successfully!!!",
                                        Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    //Intent intent=new Intent(this,LocationService.class);
                                    getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                                    getActivity().getApplicationContext().startForegroundService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                                }
                                else{
                                    // Intent intent=new Intent(this,LocationService.class);
                                    getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                                    getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(), LocationService.class));
                                }
                                  loadData();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getActivity().getApplicationContext(),"Reminder is still available!!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete!!!");
        alert.show();


    }

}