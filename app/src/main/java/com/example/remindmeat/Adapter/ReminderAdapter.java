package com.example.remindmeat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder>{

    List<Reminder> reminderList = new ArrayList<>();
    Context context;
    double currLat;
    double currLong;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txt_title.setText(reminderList.get(position).getReminderTitle());
        holder.txt_address.setText(reminderList.get(position).getReminderLocation());
        if (reminderList.get(position).getReminderStatus() == 1) {
            holder.switchStatus.setChecked(true);
        } else {
            holder.switchStatus.setChecked(false);
        }


    }
    @Override
    public int getItemCount() {
        return reminderList.size();
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

        }
    }
}
