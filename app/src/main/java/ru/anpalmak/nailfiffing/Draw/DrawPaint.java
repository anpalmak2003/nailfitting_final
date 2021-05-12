package ru.anpalmak.nailfiffing;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import ru.anpalmak.nailfiffing.draw.FingerPath;


public class DrawPaint extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap=null;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    int color= Color.RED;
    int width_brush=5;
    Rect rect;
Context context;
    public ImageButton palette;
    public ImageButton longNail;
    public Button choseForm;
    public ImageButton shortNail;
    public ImageButton drawing;
float right, top, left, bottom;
Bitmap canvasBitmap;
Canvas drawCanvas;
Paint canvasPaint;
    public DrawPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);


    }

    public DrawPaint(Context context) {
        super(context);

    }

    public void setColorWidth(int color, int width){
        this.currentColor=color;
        this.strokeWidth=width;
    }

    public void init(int heightPixels, int widthPixels) {
        int height = heightPixels;
        int width = widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = color;
        strokeWidth = width_brush;

    }

    public void normal() {
        emboss = false;
        blur = false;
    }
    public void size_normal() {
        strokeWidth = 10;
    }
    public void size_big() {
        strokeWidth = 15;
    }
    public void size_small() {
        strokeWidth = 5;
    }
    public void color_green() {
        currentColor = Color.GREEN;
    }
    public void color_red() {
        currentColor = Color.RED;
    }
    public void color_black() {
        currentColor = Color.BLACK;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }
public void setClip(Rect clip, float left, float right, float bottom, float top)
{
    this.rect=clip;
    this.left=left;
    this.right=right;
    this.bottom=bottom;
    this.top=top;
}
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
       // mCanvas.drawColor(backgroundColor);
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawCanvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        if(rect!=null)
        canvas.clipRect(rect);
        drawCanvas.clipRect(rect);
        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

if(mBitmap!=null) {           mCanvas.drawPath(fp.path, mPaint);
                              drawCanvas.drawPath(fp.path, mPaint);}

        }

if(mBitmap!=null)      {  canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    drawCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);}
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        if (x > left & x < right & y > top & y < bottom) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    return true;

            }
            return true;
        }
else return false;

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
