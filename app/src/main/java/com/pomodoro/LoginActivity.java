package com.pomodoro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //change status bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        window.setNavigationBarColor(getColor(R.color.light_brown));
        //
        EditText eemail = findViewById(R.id.emailInput);
        EditText epwd = findViewById(R.id.pwdInput);
        Button btnSignin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        ImageButton btnClose = findViewById(R.id.btnClose);
//
        //luu user vao pref
        SharedPreferences preferences = getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                try {
                    String mail = eemail.getText().toString();
                    String pwd = epwd.getText().toString();
                    User user = dbHelper.getUser(mail, pwd);
                    editor.putInt("User id", user.getId());
                    editor.putString("User email", user.getEmail());
                    editor.apply();
                    Log.e("ID", preferences.getInt("User id", 0) + "");
                    Log.e("Email", preferences.getString("User email", "empty"));
                    setResult(RESULT_OK, null);
                    finish();
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intentSignup = new Intent(this, SignUpActivity.class);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intentSignup, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
            Toast.makeText(LoginActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
    }
}