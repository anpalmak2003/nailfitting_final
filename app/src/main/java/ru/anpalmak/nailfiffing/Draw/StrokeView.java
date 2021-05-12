package ru.anpalmak.nailfiffing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class StrokeView extends View {
    Paint p = new Paint();
    int color= Color.RED;
    int wight=5;
    public StrokeView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStrokeWight(int wight)
    {
        this.wight=wight;
        invalidate();
    }

    public void setStrokeColor(int color)
    {
       this.color=color;
        invalidate();
    }

    public void saveStroke()
    {

    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        p.setStrokeWidth(wight);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);

        canvas.drawColor(Color.WHITE);
        canvas.drawLine(0, 150, 500, 150, p);


    }

}
