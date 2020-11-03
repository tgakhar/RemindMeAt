package com.example.remindmeat.Adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeat.Location.SingleShotLocationProvider;
import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This adapter class is used for showing user's reminders
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> implements Filterable {

    /**
     * Arraylist of Reminder type
     */
    List<Reminder> reminderList = new ArrayList<>();

    /**
     * FilteredArraylist of Reminder type
     */
    List<Reminder> reminderListFiltered=new ArrayList<>();

    /**
     * Variable of Context
     */
    Context context;

    /**
     * Variable to get the current latitude/longitude of user
     */
    LatLng current;

    private View.OnClickListener OnClick;
    SwitchMaterial.OnCheckedChangeListener statusChangeListener;

    /**
     * constructor
     * @param context
     * @param reminderList
     */
    public ReminderAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
        this.reminderListFiltered= reminderList;
    }

    /**
     * onCreateViewHolder of adapter
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reminder, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder of adapter
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.txt_title.setText(reminderListFiltered.get(position).getReminderTitle());
        holder.txt_address.setText(reminderListFiltered.get(position).getReminderLocation());
        if (reminderListFiltered.get(position).getReminderStatus() == 1) {
            //holder.switchStatus.setChecked(true);
        } else {
            holder.switchStatus.setChecked(false);
        }

        final LatLng target=new LatLng(reminderListFiltered.get(position).getReminderLat(),reminderList.get(position).getReminderLong());
        Log.d("Adapter","Taget="+target);
        Log.d("Adapter","Current="+current);
        //holder.txt_distance.setText(""+checkDistance(target));
        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        final Location loc1 = new Location("");
                        loc1.setLatitude(target.latitude);
                        loc1.setLongitude(target.longitude);
                        final Location loc2 = new Location("");
                        loc2.setLatitude(location.latitude);
                        loc2.setLongitude(location.longitude);
                        float d= BigDecimal.valueOf((loc1.distanceTo(loc2))/1000).setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                        holder.txt_distance.setText(""+d+" Km");
                        Log.d("Location", "my location " +loc1.distanceTo(loc2) );
                        Log.d("Location", "my location is " + location.latitude +location.longitude);
                    }
                });

    }

    /**
     * Method to search the list according to user's input and get the filtered result
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    reminderListFiltered = reminderList;
                } else {
                    List<Reminder> filteredList = new ArrayList<>();
                    for (Reminder row : reminderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getReminderTitle().toLowerCase().contains(charString.toLowerCase()) || row.getReminderLocation().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    reminderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reminderListFiltered;
                //updatelist(reminderListFiltered);
                return filterResults;
            }
            /**
             *Method to publish the filtered results
             */
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                reminderListFiltered = (ArrayList<Reminder>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    /**
     * return item from filteredList
     * @return
     */
    public Reminder getItem (int position) {
        return reminderListFiltered.get(position);
    }

    /**
     * return size of arrayList
     * @return
     */
    @Override
    public int getItemCount() {
        return reminderListFiltered.size();
    }

    /**
     *
     * @param onClickListner
     */
    public void setOnClickListner(View.OnClickListener onClickListner)
    {
        OnClick = onClickListner;
    }

    /**
     *clickListener to check for switch material status change
     */
    public void setStatusChangeListener(SwitchMaterial.OnCheckedChangeListener onCheckedChangeListener){
        statusChangeListener=onCheckedChangeListener;
    }

    /**
     * item class
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title,txt_address,txt_distance;
        ImageView img_delete,img_edit,img_distance;
        SwitchMaterial switchStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_address=itemView.findViewById(R.id.txt_layoutLocation);
            txt_distance=itemView.findViewById(R.id.txt_layoutDistance);
            txt_title=itemView.findViewById(R.id.txt_layoutTitle);
            switchStatus=itemView.findViewById(R.id.switch_status);
            img_delete=itemView.findViewById(R.id.img_delete);
            img_edit=itemView.findViewById(R.id.img_edit);
            img_distance=itemView.findViewById(R.id.img_location);
            img_delete.setTag(this);
            img_edit.setTag(this);
            txt_title.setTag(this);
            txt_address.setTag(this);
            img_distance.setTag(this);
            txt_distance.setTag(this);
            switchStatus.setTag(this);
            txt_distance.setOnClickListener(OnClick);
            img_distance.setOnClickListener(OnClick);
            txt_address.setOnClickListener(OnClick);
            txt_title.setOnClickListener(OnClick);
            img_delete.setOnClickListener(OnClick);
            img_edit.setOnClickListener(OnClick);
            switchStatus.setOnCheckedChangeListener(statusChangeListener);
        }
    }
}
