package com.example.remindmeat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeat.Model.Reminder;
import com.example.remindmeat.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderHistoryAdapter extends RecyclerView.Adapter<ReminderHistoryAdapter.ViewHolder>{

    List<Reminder> reminderList = new ArrayList<>();
    Context context;
    private View.OnClickListener OnClick;
    public ReminderHistoryAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reminderhistory, parent, false);
        return new ReminderHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText(reminderList.get(position).getReminderTitle());
        holder.txt_address.setText(reminderList.get(position).getReminderLocation());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
    public void setOnClickListner(View.OnClickListener onClickListner)
    {
        OnClick = onClickListner;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title,txt_address;
        ImageView img_add,img_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_address=itemView.findViewById(R.id.txt_historyLocation);
            txt_title=itemView.findViewById(R.id.txt_historyTitle);
            img_add=itemView.findViewById(R.id.img_addReminder);
            img_delete=itemView.findViewById(R.id.img_historyDelete);
            img_delete.setTag(this);
            img_add.setTag(this);
            img_delete.setOnClickListener(OnClick);
            img_add.setOnClickListener(OnClick);
        }
    }



}
