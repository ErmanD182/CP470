package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "LoginActivity";
    private Button loginButton;
    private EditText editLoginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Read from SharedPreferences
        editLoginText = (EditText)findViewById(R.id.LoginEditText);
        SharedPreferences getSP = getSharedPreferences("DefaultEmail", Context.MODE_PRIVATE);
        String defaultEmail = getResources().getString(R.string.DefaultEmailText);
        String emailText = getSP.getString(String.valueOf(R.string.SavedEmailText),defaultEmail);
        editLoginText.setText(emailText);

        loginButton = (Button)findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Write to SharedPreferences
                String emailText = editLoginText.getText().toString();
                SharedPreferences setSP = getSharedPreferences("DefaultEmail",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setSP.edit();
                editor.putString(String.valueOf(R.string.SavedEmailText),emailText);
                editor.commit();


                Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
