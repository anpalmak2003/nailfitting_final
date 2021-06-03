package ru.anpalmak.nailfiffing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.NavigationUI;

import ru.anpalmak.nailfiffing.NailDetection.CameraActivity;
import ru.anpalmak.nailfiffing.NailDetection.DetectorActivity;
import ru.anpalmak.nailfiffing.SignUpIn.SignupActivity;
import ru.anpalmak.nailfiffing.Draw.Draw_design_avtivity;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    StorageReference reference;
    DatabaseReference userInformation;
    TextView username;
    String photoPath;
    ImageView userPhoto;
    View headerView;
    Button following;
    Button followers;
    DatabaseReference mDataReference;
    public FirebaseAuth mAuth;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_view);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_liked, R.id.nav_myDesigns)
                        .setDrawerLayout(drawer)
                        .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration); //Setup toolbar with back button and drawer icon according to appBarConfiguration
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        userInformation = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        headerView = navigationView.getHeaderView(0);
        addNavHeaderProfile();
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#232F34")));

    }
    /**Установка кнопок нижней навигации*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId()) {
                case R.id.navigation_drawing:
                    startActivity(new Intent(MainPageActivity.this, Draw_design_avtivity.class));
                    break;
                case R.id.navigation_camera:
                    startActivity(new Intent(MainPageActivity.this, DetectorActivity.class));
                    break;
            }

            return true;}
    };
    /**Установка бококвой навигации*/
    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.HomePage) {
            if(navController.getCurrentDestination().getId()==R.id.nav_liked)
                navController.navigate(R.id.action_nav_liked_to_nav_home);
            if(navController.getCurrentDestination().getId()==R.id.nav_myDesigns)
                navController.navigate(R.id.action_nav_myDesigns_to_nav_home);

        } else if (item.getItemId() == R.id.liked) {
            if(navController.getCurrentDestination().getId()==R.id.nav_home)
                navController.navigate(R.id.action_nav_home_to_nav_liked);
            if(navController.getCurrentDestination().getId()==R.id.nav_myDesigns)
                navController.navigate(R.id.action_nav_myDesigns_to_nav_liked);
        }
        else if (item.getItemId() == R.id.MyDesigns) {
            if(navController.getCurrentDestination().getId()==R.id.nav_home)
                navController.navigate(R.id.action_nav_home_to_nav_myDesigns);
            if(navController.getCurrentDestination().getId()==R.id.nav_liked)
                navController.navigate(R.id.action_nav_liked_to_nav_myDesigns);
        }
        else if(item.getItemId() == R.id.LogOut)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainPageActivity.this, SignupActivity.class));
        }
        else if(item.getItemId() == R.id.navigation_drawing)
        {
            startActivity(new Intent(MainPageActivity.this,Draw_design_avtivity.class));
        }
        else if(item.getItemId() == R.id.navigation_camera)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainPageActivity.this, CameraActivity.class));
        }
        drawer.close();
        return super.onOptionsItemSelected(item);

    }
    /**Установка поля профиля в боковом меню*/
    public void addNavHeaderProfile(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        following=headerView.findViewById(R.id.followings);
        followers=headerView.findViewById(R.id.followers);
        username=(TextView) headerView.findViewById(R.id.userUsername);
        username.setText(user.getDisplayName());
        userPhoto=(ImageView)headerView.findViewById(R.id.userPhoto);
        reference = FirebaseStorage.getInstance("gs://nails-90d66.appspot.com").getReference();
        String usernameString=user.getDisplayName();
        mDataReference = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(user.getDisplayName());
        setFollowers();setFollowing();
        userInformation.child(usernameString).child("photo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    photoPath = String.valueOf(task.getResult().getValue());
                    new DownloadImageTask(userPhoto)
                            .execute(photoPath);}}});}

    /**Установка количества подписчиков*/
    public void setFollowers(){
        final int[] size = new int[1];
        if(mDataReference.child("Followings")!=null){
            mDataReference.child("Followings")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            size[0] = (int) dataSnapshot.getChildrenCount();
                            following.setText(String.valueOf(size[0])+" Followings");
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });}}

    /**Установка количества аккаунтов, на которые подписан пользователь */
    public void setFollowing(){
        final int[] size = new int[1];
        mDataReference.child("Follower")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        size[0] = (int) dataSnapshot.getChildrenCount();
                        followers.setText(String.valueOf(size[0])+" Followers");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

}
