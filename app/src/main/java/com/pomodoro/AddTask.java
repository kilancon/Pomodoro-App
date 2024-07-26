package com.pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.Task;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //change status bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        //
        int userid = getSharedPreferences("My_pref", Context.MODE_PRIVATE).getInt("User id", 0);
        
        EditText ename = findViewById(R.id.editTaskname);
        EditText eest = findViewById(R.id.editEst);
        Button btnAdd = findViewById(R.id.btnAdd);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                task.setState(false);
                task.setName(ename.getText().toString());
                task.setEst(Integer.parseInt(eest.getText().toString()));
                task.setUserid(userid);
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.addTask(task);
                Toast.makeText(getBaseContext(),"Add success",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}