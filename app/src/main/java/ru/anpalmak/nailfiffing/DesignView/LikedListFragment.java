package ru.anpalmak.nailfiffing.DesignView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.net.URL;

//import ru.anpalmak.nailfiffing.dummy.DummyContent;

import ru.anpalmak.nailfiffing.Draw.ImageNailInfo;
import ru.anpalmak.nailfiffing.R;
import ru.anpalmak.nailfiffing.WatchProfileActivity;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Фрагмент дизайнов, которым пользователь поставил лайк
 */
public class LikedListFragment extends Fragment {

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

    public LikedListFragment() {
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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_design_list_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);


        return view;
    }
/**Загрузка дизайнов*/
    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
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
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            firebaseRecyclerAdapter.startListening();
        @SuppressLint("RestrictedApi")
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static LikedListFragment newInstance() {
       LikedListFragment fragment = new LikedListFragment();

        return fragment;
    }
}