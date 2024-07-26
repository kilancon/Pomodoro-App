package com.pomodoro.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pomodoro.R;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.Task;

public class UpdateTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        //change status bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        //
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnDelete = findViewById(R.id.btnDelete);
        EditText ename = findViewById(R.id.editTaskname);
        EditText eest = findViewById(R.id.editEst);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        Intent intent = this.getIntent();
        int idTask = Integer.valueOf(intent.getStringExtra("idTask"));
        String nameTask = intent.getStringExtra("nameTask");
        String estPomo = intent.getStringExtra("estTask");
        ename.setText(nameTask);
        eest.setText(estPomo);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = dbHelper.getTaskById(idTask);
                task.setName(ename.getText().toString());
                task.setEst(Integer.valueOf(eest.getText().toString()));
                int row_affected = dbHelper.updateTask(task);
                Log.e("Row affected", row_affected+"");
                Toast.makeText(getBaseContext(),"Update success",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateTaskActivity.this);
                alert.setTitle("Delete task");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue
                        dbHelper.deleteTaskById(idTask);
                        Toast.makeText(getBaseContext(),"Delete success",Toast.LENGTH_SHORT).show();
                        setResult(2, null);
                        finish();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }
}