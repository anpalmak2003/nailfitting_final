package ru.anpalmak.nailfiffing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ru.anpalmak.nailfiffing.ui.login.LoginActivity;

public class Signup extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        final Button haveaccount = findViewById(R.id.haveanaccount);
        final Button signup = findViewById(R.id.signup);
        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(Signup.this, LoginActivity.class);
                startActivity(myIntent);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(Signup.this, MainPageActivity.class);
                startActivity(myIntent);

            }
        });
    }
    }

