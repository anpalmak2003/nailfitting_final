package ru.anpalmak.nailfiffing.DesignView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

//import ru.anpalmak.nailfiffing.dummy.DummyContent;

import ru.anpalmak.nailfiffing.R;
import ru.anpalmak.nailfiffing.Draw.ImageNailInfo;

/**
 * A fragment representing a list of Items.
 */
public class LikedViewActivity extends AppCompatActivity {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    DatabaseReference mDataReference;
    private FirebaseRecyclerAdapter mAdapter;
    ListView DesignListView;
    Query query;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LikedViewActivity() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DesignListFragment newInstance(int columnCount) {
        DesignListFragment fragment = new DesignListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_design_list_list);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        query = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Liked");
        FirebaseRecyclerOptions<ImageNailInfo> options =
                new FirebaseRecyclerOptions.Builder<ImageNailInfo>()
                        .setQuery(query, ImageNailInfo.class)
                        .build();
        Log.d("Options"," data : "+options);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImageNailInfo, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull ImageNailInfo imageNailInfo) {
                viewHolder.username.setText(imageNailInfo.username);
                viewHolder.designName.setText(imageNailInfo.imageName);
                viewHolder.setImage(imageNailInfo.url);
               // viewHolder.setAccess(imageNailInfo.access);

            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.liked_item, parent, false);


                return new ViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        @SuppressLint("RestrictedApi")
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


}
