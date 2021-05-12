package ru.anpalmak.nailfiffing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.anpalmak.nailfiffing.NailDetection.DetectorActivity;

import static android.graphics.Color.RED;
import static androidx.core.content.ContextCompat.startActivity;

public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView nailDesign;
    Button username;
    TextView designName;
    TextView access;
    Button like;
    String url;
    public static Bitmap image;
    Button tryOn;

    public ViewHolder(View itemView) {
        super(itemView);
        tryOn = itemView.findViewById(R.id.try_design);
        nailDesign = itemView.findViewById(R.id.nail_design);
        username = itemView.findViewById(R.id.user_design);
        designName = itemView.findViewById(R.id.design_name);
        access=itemView.findViewById(R.id.access);
        like=itemView.findViewById(R.id.like);
        if(like!=null) setLike();


    }

public void setLike()
{DatabaseReference mDataReference = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
        getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Liked");
    mDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot data: dataSnapshot.getChildren()){
                if (data.child("url").getValue().equals(url)) {
                    like.setBackgroundColor(RED);
                } else {
                    //do something if not exists
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError firebaseError) {

        }
    });
    like.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            String key = mDataReference.push().getKey();
            like.setBackgroundColor(RED);
            FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                    getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Liked").
                    child(designName.getText().toString()+username.getText().toString()).setValue(new ImageNailInfo(url, username.getText().toString(), designName.getText().toString(), null ))
            ;
        }
    });}
    public void setUsername(String text)
    {
        username.setText(text);
    }
    public void setAccess(String text)
    {
        access.setText(text);
    }
    public void setDesignName(String text)
    {
        designName.setText(text);
    }
    public void setImage(String text)
    {   url=text;

    DownloadImageTask download= new DownloadImageTask(nailDesign);
      download.execute(text);
     // image=download.getBitmapDesign();
        tryOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image=download.getBitmapDesign();
                Intent intent = new Intent(v.getContext(), DetectorActivity.class);
                v.getContext().startActivity(intent);

                ;
            }
        });


    }
}
