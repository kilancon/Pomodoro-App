package com.pomodoro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pomodoro.R;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.AppInfo;
import com.pomodoro.model.BlockedApp;

import java.util.ArrayList;

public class AppBlockAdapter extends RecyclerView.Adapter<AppBlockAdapter.AppBlockHolder> {
    private Context context;
    private ArrayList<AppInfo> ds;

    public AppBlockAdapter(Context context, ArrayList<AppInfo> ds) {
        this.context = context;
        this.ds = ds;
    }

    @NonNull
    @Override
    public AppBlockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blockapp, parent, false);
        return new AppBlockHolder(view);
    }

    private boolean isBlocked;

    @Override
    public void onBindViewHolder(@NonNull AppBlockHolder holder, int position) {
        AppInfo app = ds.get(position);
        holder.appicon.setImageDrawable(app.getIcon());
        holder.appname.setText(app.getAppName());
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        BlockedApp blockedApp = databaseHelper.getBlockedApp(app.getPackageName());
        isBlocked = false;
        if(blockedApp != null)
            isBlocked = blockedApp.isLocked();
        holder.btnLock.setImageResource(isBlocked?R.drawable.ic_lock:R.drawable.ic_unlock);

        holder.btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBlocked = !isBlocked;
                BlockedApp blockedApp2 = databaseHelper.getBlockedApp(app.getPackageName());
                databaseHelper.addOrUpdBlockedApp(new BlockedApp(app.getPackageName(), isBlocked), blockedApp2==null);
                holder.btnLock.setImageResource(isBlocked?R.drawable.ic_lock:R.drawable.ic_unlock);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ds.size();
    }

    public class AppBlockHolder extends RecyclerView.ViewHolder {
        private ImageView appicon;
        private TextView appname;
        private ImageButton btnLock;

        public AppBlockHolder(@NonNull View itemView) {
            super(itemView);
            appicon = itemView.findViewById(R.id.imgAppIcon);
            appname = itemView.findViewById(R.id.tvAppLabel);
            btnLock = itemView.findViewById(R.id.btnLock);
        }
    }
}
