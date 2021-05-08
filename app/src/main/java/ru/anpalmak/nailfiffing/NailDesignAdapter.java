package ru.anpalmak.nailfiffing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NailDesignAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private ArrayList<NailDesign> nailDesigns;
    String photoPath;
    public NailDesignAdapter(Context context, ArrayList<NailDesign> nailDesign)
    {
        this.context = context;
        this.nailDesigns = nailDesign;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return nailDesigns.size();
    }

    @Override
    public Object getItem(int position) {
        return nailDesigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.nail_design_item, parent, false);
        }

        NailDesign nailDesign = nailDesigns.get(position);

        TextView designName = (TextView) view.findViewById(R.id.design_name);
        designName.setText(nailDesign.getDesignName());

        Button userProfile = (Button) view.findViewById(R.id.user_design);
        userProfile.setText(nailDesign.getUsername());

        ImageView nailDesignPhoto = (ImageView) view.findViewById(R.id.nail_design);
        photoPath = String.valueOf(nailDesign.getNailPhotoDesign());
        new DownloadImageTask(nailDesignPhoto)
                .execute(photoPath);

        return view;
    }
}
