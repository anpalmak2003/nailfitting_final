package ru.anpalmak.nailfiffing.Draw;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import ru.anpalmak.nailfiffing.R;
/**Настройка ногтя*/
public class DrawNail extends View {
float bottom; float right; float left; float top; float height; float widht;
    Bitmap form= BitmapFactory.decodeResource(getResources(), R.drawable.balelerinaex);
    Rect clip;RectF rect=null;
    Paint paint = new Paint();
    Bitmap canvasBitmap;
    Canvas drawCanvas;
    Paint canvasPaint;
    public DrawNail(Context context, AttributeSet attrs) {
        super(context, attrs);
        invalidate();


    }

    public void setNail(float bottom, float right, float left, float top, float height, float wight) {

        this.bottom=bottom;
        this.right=right;
        this.left=left;
        this.top=top;
        this.height=height;
        this.widht=wight;
        rect = new RectF(left, top, right, bottom);
        invalidate();

    }
    public void setForm(Bitmap form)
    {
        this.form=form;
        invalidate();
    }
    public void mainColor(int color)
    {

        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        invalidate();
    }



    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        paint.setStyle(Paint.Style.FILL);
        drawCanvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

      //  RectF rect = new RectF(left, top, right, bottom);
if(rect!=null)
        drawCanvas.drawBitmap(form, null, rect, paint);
canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        clip = new Rect((int)left, (int) top,(int) right,(int) bottom);

    }
    public Rect getClip()
    {
        return clip;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }


public Bitmap getBitmap(){
        return canvasBitmap;
}



}
