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
 * This adapter class is used for showing all registered user's as list in recyclerview.
 */
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> implements Filterable {

    /**
     * ArrayList of Admin type for saving all user's details
     */
    List<Admin> adminList = new ArrayList<>();

    /**
     * ArrayList of Admin type for saving all filtered user's details
     */
    List<Admin> adminListFiltered = new ArrayList<>();

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
     * @param adminList
     * @param context
     */
    public AdminAdapter(List<Admin> adminList, Context context) {
        this.adminList = adminList;
        this.context = context;
        this.adminListFiltered = adminList;
    }

    /**
     * onCreate method which returns new object of {@link ViewHolder}
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_admin, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBind method for showing user details in recyclerview
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_email.setText("" + adminListFiltered.get(position).getEmail());
        if (adminListFiltered.get(position).getDisabled()==1){
            holder.img_diasable.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_block_red_24));
        }
    }

    /**
     * @param onClickListner
     */
    public void setOnClickListner(View.OnClickListener onClickListner) {
        OnClick = onClickListner;
    }

    /**
     * This method returns size of {@link ArrayList adminListFiltered}
     * @return
     */
    @Override
    public int getItemCount() {
        return adminListFiltered.size();
    }

    /**
     * This method returns position of item in {@link ArrayList adminListFiltered}
     * @param position
     * @return
     */
    public Admin getItem (int position) {
        return adminListFiltered.get(position);
    }


    /**
     * This method filtered the {@link ArrayList adminListFiltered} using search value
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    adminListFiltered = adminList;
                } else {
                    List<Admin> filteredList = new ArrayList<>();
                    for (Admin row : adminList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUId().toLowerCase().contains(charString.toLowerCase()) || row.getEmail().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    adminListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = adminListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                adminListFiltered = (ArrayList<Admin>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }


    /**
     * Item class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_email;
        ImageView img_delete,img_diasable,img_reset;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_email = itemView.findViewById(R.id.txt_adminEmail);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_diasable=itemView.findViewById(R.id.img_disable);
            img_reset=itemView.findViewById(R.id.img_reset);
            img_delete.setTag(this);
            img_diasable.setTag(this);
            img_reset.setTag(this);
            img_diasable.setOnClickListener(OnClick);
            img_delete.setOnClickListener(OnClick);
            img_reset.setOnClickListener(OnClick);
            itemView.setTag(this);
        }
    }
}

