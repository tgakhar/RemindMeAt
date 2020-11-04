package com.example.remindmeat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeat.Model.Admin;
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
 * This adapter class is used for showing all Reminders in the Reminder History as list in recyclerview {@link ReminderHistoryAdapter}.
 */
public class ReminderHistoryAdapter extends RecyclerView.Adapter<ReminderHistoryAdapter.ViewHolder> implements Filterable {

    /**
     * ArrayList of Reminder type for saving all Reminder's details
     */
    List<Reminder> reminderList = new ArrayList<>();

    /**
     * ArrayList of Admin type for saving all filtered user's details
     */
    List<Reminder> reminderFiltered = new ArrayList<>();


    /**
     * Variable of a context
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
        this.reminderFiltered = reminderList;
    }


    /**
     * onCreate method which returns new object of {@ReminderHistoryAdapter ViewHolder}
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
        holder.txt_title.setText(reminderFiltered.get(position).getReminderTitle());
        holder.txt_address.setText(reminderFiltered.get(position).getReminderLocation());
    }

    /**
     * this method is to return the size of Reminderlist
     * @return
     */
    @Override
    public int getItemCount() {
        return reminderFiltered.size();
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
     * This method filtered the {@link ArrayList reminderFiltered} using search value
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    reminderFiltered = reminderList;
                } else {
                    List<Reminder> filteredList = new ArrayList<>();
                    for (Reminder row : reminderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getReminderTitle().toLowerCase().contains(charString.toLowerCase()) || row.getReminderLocation().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    reminderFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reminderFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                reminderFiltered = (ArrayList<Reminder>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }


    /**
     * This method returns position of item in {@link ArrayList reminderFiltered}
     * @param position
     * @return
     */
    public Reminder getItem (int position) {
        return reminderFiltered.get(position);
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
