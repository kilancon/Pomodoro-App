package com.pomodoro.adapter;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pomodoro.R;
import com.pomodoro.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
    private Context context;
    private ArrayList<AppInfo> ds;

    public ItemAdapter(Context context, ArrayList<AppInfo> ds) {
        this.context = context;
        this.ds = ds;
    }

    public void setDs(ArrayList<AppInfo> ds) {
        this.ds = ds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        AppInfo app = ds.get(position);
        holder.img.setImageDrawable(app.getIcon());
        holder.name.setText(app.getAppName());
        holder.total.setText(app.getTime());
    }

    @Override
    public int getItemCount() {
        return ds.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name, total;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgItem);
            name = itemView.findViewById(R.id.nameItem);
            total = itemView.findViewById(R.id.timeItem);
        }
    }
}
