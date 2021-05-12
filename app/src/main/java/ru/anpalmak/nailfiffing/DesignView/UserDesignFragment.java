package ru.anpalmak.nailfiffing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.net.URL;

//import ru.anpalmak.nailfiffing.dummy.DummyContent;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A fragment representing a list of Items.
 */
public class UserDesignFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    DatabaseReference mDataReference;
    private FirebaseRecyclerAdapter mAdapter;
    ListView DesignListView;
    Query query;
    String username;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDesignFragment() {
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
        //DesignListView = (ListView) view.findViewById(R.id.list);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        WatchProfileActivity activity = (WatchProfileActivity) getActivity();
       username = activity.getUsernameString();
     /*   Bundle bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("user");
        }*/
        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set the adapter
      /*  if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS));
        }*/

        return view;
    }

    private void populateList(String designName, String username, URL nailPhotoDesign) {
        // ArrayAdapter<NailDesign> saveAdapter = new NailDesignAdapter(this, new NailDesign(designName,username, nailPhotoDesign));
        //  DesignListView.setAdapter(saveAdapter);
    }
    @Override
    public void onStart() {

       super.onStart();
          if(username!=null){
        query = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("images").child(username).child("public");
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

            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.nail_design_item, parent, false);
                //  progressDialog.dismiss();

                return new ViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        @SuppressLint("RestrictedApi")
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        // recyclerView.setLayoutManager(gridLayoutManager);
        //  recyclerView.smoothScrollToPosition(0);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }}


}