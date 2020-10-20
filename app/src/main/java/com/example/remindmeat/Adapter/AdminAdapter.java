package com.example.remindmeat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindmeat.Model.Admin;
import com.example.remindmeat.R;

import java.util.ArrayList;
import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder>{

    List<Admin> adminList=new ArrayList<>();
    Context context;

    private View.OnClickListener OnClick;

    public AdminAdapter(List<Admin> adminList, Context context) {
        this.adminList = adminList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_email.setText(""+adminList.get(position).getEmail());
    }

    public void setOnClickListner(View.OnClickListener onClickListner)
    {
        OnClick = onClickListner;
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_email;
        ImageView img_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_email=itemView.findViewById(R.id.txt_adminEmail);
            img_delete=itemView.findViewById(R.id.img_delete);

            img_delete.setTag(this);
            img_delete.setOnClickListener(OnClick);
        }
    }
}
