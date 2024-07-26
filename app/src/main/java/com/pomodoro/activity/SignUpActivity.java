package com.pomodoro.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import com.pomodoro.model.User;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //change status bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        window.setNavigationBarColor(getColor(R.color.light_brown));
        //
        EditText eemail = findViewById(R.id.emailInput);
        EditText epwd = findViewById(R.id.pwdInput);
        Button btnSignup = findViewById(R.id.btnSignup);
        ImageButton btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                try {
                    String mail = eemail.getText().toString();
                    String pwd = epwd.getText().toString();
                    User user = new User();
                    user.setEmail(mail);
                    user.setPassword(pwd);
                    dbHelper.addUser(user);
                    setResult(RESULT_OK, null);
                    finish();
                }catch (Exception e){
//                    Toast.makeText(SignUpActivity.this, "Hihihi", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}