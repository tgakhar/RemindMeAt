package com.example.remindmeat.Adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder>{

    List<Reminder> reminderList = new ArrayList<>();
    Context context;
    LatLng current;
    private View.OnClickListener deleteClickListener,editClickListener;

    SwitchMaterial.OnCheckedChangeListener statusChangeListener;
    public ReminderAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.txt_title.setText(reminderList.get(position).getReminderTitle());
        holder.txt_address.setText(reminderList.get(position).getReminderLocation());
        if (reminderList.get(position).getReminderStatus() == 1) {
            holder.switchStatus.setChecked(true);
        } else {
            holder.switchStatus.setChecked(false);
        }

        final LatLng target=new LatLng(reminderList.get(position).getReminderLat(),reminderList.get(position).getReminderLong());
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
    @Override
    public int getItemCount() {
        return reminderList.size();
    }


    public void setOnClickListner(View.OnClickListener onClickListner)
    {
        deleteClickListener = onClickListner;
        editClickListener=onClickListner;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title,txt_address,txt_distance;
        ImageView img_delete,img_edit;
        SwitchMaterial switchStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_address=itemView.findViewById(R.id.txt_layoutLocation);
            txt_distance=itemView.findViewById(R.id.txt_layoutDistance);
            txt_title=itemView.findViewById(R.id.txt_layoutTitle);
            switchStatus=itemView.findViewById(R.id.switch_status);
            img_delete=itemView.findViewById(R.id.img_delete);
            img_edit=itemView.findViewById(R.id.img_edit);
            img_delete.setTag(this);
            img_edit.setTag(this);
            img_delete.setOnClickListener(deleteClickListener);
            img_edit.setOnClickListener(editClickListener);
        }
    }
}
