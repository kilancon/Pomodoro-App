package com.pomodoro.adapter;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.pomodoro.MainActivity;
import com.pomodoro.R;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private Context context;
    private TaskItemListener til;
    private ArrayList<Task> ds;
    private TaskAdapter adapter2;


    public TaskAdapter(Context context, ArrayList<Task> ds) {
        this.context = context;
        this.ds = ds;
    }

    public void setDs(ArrayList<Task> ds) {
        this.ds = ds;
        notifyDataSetChanged();
    }

    public ArrayList<Task> getDs() {
        return ds;
    }

    public void setTil(TaskItemListener til){this.til=til;}

    public void setAdapter2(TaskAdapter adapter2) {
        this.adapter2 = adapter2;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task task = ds.get(position);
        // img dat theo state ....
        holder.btncheck.setImageResource(task.getState()?R.drawable.btncheck2:R.drawable.btncheck);
        holder.tvname.setText(task.getName());
        holder.tvname.setPaintFlags(task.getState()?holder.tvname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG:0);
        holder.tvname.setTextColor(task.getState()?context.getResources().getColor(R.color.light_brown):context.getResources().getColor(R.color.white));
        holder.tvest.setText(task.getCount()+"/"+task.getEst());
        holder.tvest.setTextColor(task.getState()?context.getResources().getColor(R.color.light_brown):context.getResources().getColor(R.color.white));
        holder.btnplay.setVisibility(task.getState()?View.GONE:View.VISIBLE);

        holder.btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).task_select = task;
                ViewPager vp = ((MainActivity)context).findViewById(R.id.viewP);
                vp.setCurrentItem(0,false);
            }
        });
        holder.btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setState(!task.getState());
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateTask(task);
                ds.remove(position);
                notifyDataSetChanged();
                ArrayList<Task> ds2 = adapter2.getDs();
                ds2.add(task);
                adapter2.setDs(ds2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ds.size();
    }

    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageButton btncheck, btnplay;
        private TextView tvname, tvest;
        private Button btnOption;
        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.tvTaskname);
            tvest = itemView.findViewById(R.id.tvTaskest);
            btncheck = itemView.findViewById(R.id.btnCheck);
            btnplay = itemView.findViewById(R.id.btnPlay);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(til!=null) til.onItemClick(view, getAdapterPosition());
        }
    }

    public interface TaskItemListener{
        public void onItemClick(View view, int pos);
    }
}
