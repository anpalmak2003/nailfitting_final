package ru.anpalmak.nailfiffing;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import ru.anpalmak.nailfiffing.SignUpIn.SignupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       if(FirebaseAuth.getInstance().getCurrentUser()==null)
           startActivity(new Intent(MainActivity.this, SignupActivity.class));
       else startActivity(new Intent(MainActivity.this, MainPageActivity.class));

    }

}