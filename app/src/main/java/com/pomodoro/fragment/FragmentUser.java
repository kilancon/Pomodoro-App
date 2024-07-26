package com.pomodoro.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pomodoro.AddTask;
import com.pomodoro.LoginActivity;
import com.pomodoro.R;
import com.pomodoro.UpdateTask;
import com.pomodoro.adapter.TaskAdapter;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.Task;

import java.util.ArrayList;

public class FragmentUser extends Fragment implements TaskAdapter.TaskItemListener {
    private RecyclerView rv, rv2;
    private TaskAdapter adapter, adapter2;
    private ImageView ava;
    private TextView tvName, tvHi2;
    private Button btnSignin, btnNewTask;
    private SharedPreferences preferences;
    private ArrayList<Task> ds;
    private DatabaseHelper dbHelper;
    private int userid;
    public FragmentUser() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = getActivity().getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        initView(view);
        //id = 0 ~ !loged in
        userid = preferences.getInt("User id", 0);
        if(userid == 0){
            btnSignin.setVisibility(View.VISIBLE);
            tvHi2.setVisibility(View.INVISIBLE);
            tvName.setVisibility(View.INVISIBLE);
        }
        else{//already log in
            btnSignin.setVisibility(View.INVISIBLE);
            tvHi2.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            String name = preferences.getString("User email", "").split("@")[0];
            tvName.setText("Hi "+ name);
            ava.setImageResource(R.drawable.avatar);
            //log out
            ava.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putInt("User id", 0);
                    editor.putString("User email", "");
                    editor.apply();
                    Log.e("Click", "....");
                    btnSignin.setVisibility(View.VISIBLE);
                    tvHi2.setVisibility(View.INVISIBLE);
                    tvName.setVisibility(View.INVISIBLE);
                    ava.setImageResource(R.drawable.avatar2);
                    userid = 0;
                    updateDsView();
                }
            });
        }

        dbHelper = new DatabaseHelper(view.getContext());
        adapter = new TaskAdapter(getContext(), dbHelper.getAllTaskByState(0, userid)); //unfinshed task
        adapter2 = new TaskAdapter(getContext(), dbHelper.getAllTaskByState(1, userid)); //completed task
        adapter.setAdapter2(adapter2);
        adapter2.setAdapter2(adapter);
        adapter.setTil(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv2.setAdapter(adapter2);
        rv2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        Intent intentAdd = new Intent(getActivity(), AddTask.class);
        btnNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intentAdd, 1);
            }
        });
        Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivityForResult(intentLogin, 3);}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1)  // new task
            updateDsView();
        if(resultCode == RESULT_OK && requestCode == 2)  //update task
            updateDsView();
        if(resultCode == 2 && requestCode == 2) //delete task
            updateDsView();
        if(requestCode == 3 && resultCode == RESULT_OK){//sign in
            btnSignin.setVisibility(View.INVISIBLE);
            tvHi2.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            ava.setImageResource(R.drawable.avatar);
            String name = preferences.getString("User email", "").split("@")[0];
            tvName.setText("Hi "+ name);
            userid = preferences.getInt("User id", 0);
            updateDsView();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    //click task item event
    @Override
    public void onItemClick(View view, int pos) {
        Task task = adapter.getDs().get(pos);
        Intent intentUpdate = new Intent(getActivity(), UpdateTask.class);
        intentUpdate.putExtra("idTask", Integer.toString(task.getId()));
        intentUpdate.putExtra("nameTask", task.getName());
        intentUpdate.putExtra("estTask", Integer.toString(task.getEst()));
        startActivityForResult(intentUpdate, 2);
    }

    private void updateDsView(){
        adapter.setDs(dbHelper.getAllTaskByState(0, userid));
        adapter2.setDs(dbHelper.getAllTaskByState(1, userid));
    }

    private void initView(View view){
        ava = view.findViewById(R.id.imgAva);
        tvName = view.findViewById(R.id.tvHi);
        tvHi2 = view.findViewById(R.id.tvHi2);
        rv = view.findViewById(R.id.rvTasks);
        rv2 = view.findViewById(R.id.rvCompletedTasks);
        btnNewTask = view.findViewById(R.id.btnNewTask);
        btnSignin = view.findViewById(R.id.btnSignin);
    }
    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
