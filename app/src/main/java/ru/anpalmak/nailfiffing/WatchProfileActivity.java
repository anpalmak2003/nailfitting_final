package ru.anpalmak.nailfiffing;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.anpalmak.nailfiffing.DesignView.UserDesignFragment;

public class WatchProfileActivity extends AppCompatActivity {
    DatabaseReference mDataReference;
    TextView username;
    ImageView userPhoto;
    String name;
    Button follow;
    TextView followers;
    TextView following;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_profile);
        Bundle user = getIntent().getExtras();
        name = user.get("username").toString();
        mDataReference = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(name);
        username=findViewById(R.id.userUsername);
        userPhoto=findViewById(R.id.userPhoto);
        follow=findViewById(R.id.follow);
        addNavHeaderProfile();
        Fragment userDesign= new UserDesignFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user", name);
        userDesign.setArguments(bundle);
        setFollow();
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#232F34")));
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.followings);
        setFollowers();setFollowing();
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataReference.child("Follower").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                DatabaseReference dataReference= FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                        getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                dataReference.child("Followings").child(name).setValue(name);
                follow.setText("You follow this user");
            }
        });
    }

     /** Проверка подписан ли уже пользователь на этот аккаунт */
   public void setFollow()
    {DatabaseReference mDataReference = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Followings");
 mDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                if(data.getValue()!=null){
                    if (data.getValue().toString().equals(name)) {
                        follow.setText("You follow this user");
                    }}}}
             @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }


    /**Установка количества подписчиков */
     public void setFollowers(){
       final int[] size = new int[1];
         mDataReference.child("Followings")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    size[0] = (int) dataSnapshot.getChildrenCount();
                    following.setText(String.valueOf(size[0])+" Followings");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });}
    /**Установка количество аккаунтов, на которые подписан пользователь*/
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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /**Получение имени пользователя*/
    public String getUsernameString() {
        Bundle user = getIntent().getExtras();
        name = user.get("username").toString();
        return name;
    }
    /**Установка поля профиля*/
    public void addNavHeaderProfile()
    {mDataReference.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String usernameString = String.valueOf(task.getResult().getValue());
                    username.setText(usernameString);}}
        });mDataReference.child("photo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String photoPath = String.valueOf(task.getResult().getValue());
                    new DownloadImageTask(userPhoto)
                            .execute(photoPath);}}});}
}
