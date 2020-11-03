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

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This adapter class is used for showing the user's reminder history
 * all previously set reminders once completed would go into reminder history section
 */
public class ReminderHistoryAdapter extends RecyclerView.Adapter<ReminderHistoryAdapter.ViewHolder>{

    /**
     * Arraylist of Reminder type to save user's reminder details
     */
    List<Reminder> reminderList = new ArrayList<>();
    /**
     * Variable of context
     */
    Context context;
    /**
     * Variable of OnClickListener
     */
    private View.OnClickListener OnClick;

    /**
     * Constructor
     * @param reminderList
     * @param context
     */
    public ReminderHistoryAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    /**
     * onCreate method which returns new object of {@link ReminderHistoryAdapter.ViewHolder}
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reminderhistory, parent, false);
        return new ReminderHistoryAdapter.ViewHolder(view);
    }

    /**
     * onBind method for showing user details in recyclerview
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText(reminderList.get(position).getReminderTitle());
        holder.txt_address.setText(reminderList.get(position).getReminderLocation());
    }

    /**
     * return size of arrayList
     * @return
     */
    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    /**
     * @param onClickListner
     */
    public void setOnClickListner(View.OnClickListener onClickListner)
    {
        OnClick = onClickListner;
    }

    /**
     * Item class
     */
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
