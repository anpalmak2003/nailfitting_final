package ru.anpalmak.nailfiffing;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import static android.graphics.Color.RED;

public class Draw_design_avtivity extends AppCompatActivity {
    private float bottom, left, right, top;
    public ImageView nail; View n;
DrawNail draw;
public int height, width;
public ImageButton longNail;
    public ImageButton shortNail;
    int fingerHeight;
    int fingerWidth;
   /* Display display = getWindowManager().getDefaultDisplay();
    int width = display.getWidth();  // deprecated
    int height = display.getHeight();  // deprecated*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_design_avtivity);
        nail=findViewById(R.id.imageView5);
        DisplayMetrics displaymetrics = getResources(). getDisplayMetrics();
        height =displaymetrics.heightPixels;
        width =displaymetrics.widthPixels;
      //  n=findViewById(R.id.view2);
        draw=(DrawNail)this.findViewById(R.id.view2);
        longNail=(ImageButton)findViewById(R.id.longnail);
        shortNail=(ImageButton)findViewById(R.id.shortnail);

        longNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(top>bottom - fingerHeight)
         top=top-20;
         setLength();
            }
        });
        shortNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(top<height - fingerHeight)
                top=top+20;
                setLength();
            }
        });

    }
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

           fingerHeight = nail.getHeight();
           fingerWidth = nail.getWidth();

            bottom = height - fingerHeight / 3 * 2;
            top = bottom - fingerHeight / 2;
            left = width / 2 - fingerWidth / 3 ;
            right = width / 2 + fingerWidth / 3 ;

            draw.setNail( bottom, right, left, top, fingerHeight, fingerWidth);
            draw.invalidate();

        }
    }
    public void setLength(){
        draw.setNail( bottom, right, left, top, fingerHeight, fingerWidth);
        draw.invalidate();
    }
}