package ru.anpalmak.nailfiffing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DrawNail extends View {
float bottom; float right; float left; float top; float height; float widht;
    public DrawNail(Context context, AttributeSet attrs) {
        super(context, attrs);
        invalidate();

    }
  /*  public DrawNail(Context context, float bottom, float right, float left, float top, float height, float wight) {
        super(context);
        this.bottom=bottom;
        this.right=right;
        this.left=left;
        this.top=top;
        this.height=height;
        this.widht=wight;
    }*/
    public void setNail(float bottom, float right, float left, float top, float height, float wight) {

        this.bottom=bottom;
        this.right=right;
        this.left=left;
        this.top=top;
        this.height=height;
        this.widht=wight;
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

       Bitmap nail= BitmapFactory.decodeResource(getResources(), R.drawable.balelerina);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

     // canvas.drawColor(Color.WHITE);
       /* top=getMeasuredHeight()/2-nail.getHeight()/2;
        bottom=getMeasuredHeight()/2+nail.getHeight()/2;
        left=getMeasuredWidth()/2-nail.getWidth()/2;
        right=getMeasuredWidth()/2+nail.getWidth()/2;*/
        RectF rect = new RectF(left, top, right, bottom);
      canvas.drawBitmap(nail, null, rect, null);
     //  drawing(canvas);*/

    }


}
