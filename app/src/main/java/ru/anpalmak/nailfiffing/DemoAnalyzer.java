package ru.anpalmak.nailfiffing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.common.model.CustomRemoteModel;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.linkfirebase.FirebaseModelSource;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import java.util.Arrays;
import java.util.List;

public class DemoAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "DemoAnalyzer";
    InputImage image;
    ObjectDetector objectDetector;
    LocalModel localModel;

TextureView tv;
ImageView iv;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint linePaint;
    private float widthScaleFactor = 1.0f;
    private float heightScaleFactor = 1.0f;

    public static CameraX.LensFacing lens = CameraX.LensFacing.BACK;

    public DemoAnalyzer(TextureView tv, ImageView iv) {
        this.tv = tv;
        this.iv = iv;
    }

    @Override
    public void analyze(ImageProxy imageProxy, int rotationDegrees) {

        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            image = InputImage.fromMediaImage(mediaImage, rotationDegrees);
            // Pass image to an ML Kit Vision API
            // ...
        }

        initDrawingUtils();
        initDetector();
        detectObjects(image);
    }


    private void detectObjects(InputImage image) {

        objectDetector.process(image).addOnSuccessListener(DetectedObjects -> {
            if (!DetectedObjects.isEmpty()) {
                processObjects(DetectedObjects);
            } else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
            }
        }).addOnFailureListener(e -> {

        });
    }

    private void initDrawingUtils() {
        bitmap = Bitmap.createBitmap(tv.getWidth(), tv.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        linePaint = new Paint();
        linePaint.setColor(Color.CYAN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3f);
        linePaint.setTextSize(40);
        widthScaleFactor = canvas.getWidth() / (image.getWidth() * 1.0f);
        heightScaleFactor = canvas.getHeight() / (image.getHeight() * 1.0f);
    }

    private void initDetector(){
        CustomRemoteModel remoteModel = new CustomRemoteModel.Builder(new FirebaseModelSource.Builder("nails").build()).build();
        localModel= new LocalModel.Builder().setAssetFilePath("nails_detect_fp16.tflite").build();
        CustomObjectDetectorOptions customObjectDetectorOptions =
                new CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                        .enableClassification()
                        .setClassificationConfidenceThreshold(0.5f)
                        .setMaxPerObjectLabelCount(3)
                        .build();
        objectDetector =
                ObjectDetection.getClient(customObjectDetectorOptions);
    }
    private float translateY(float y) {
        return y * heightScaleFactor;
    }

    private float translateX(float x) {
        float scaledX = x * widthScaleFactor;
        if (lens == CameraX.LensFacing.FRONT) {
            return canvas.getWidth() - scaledX;
        } else {
            return scaledX;
        }
    }
    private void processObjects(List<DetectedObject> DetectedObjects) {
        List<String> classes = Arrays.asList("NAILS", "???"
        );
        Log.i(TAG, "Size: " + DetectedObjects.size());
        for (DetectedObject object : DetectedObjects) {
            Log.i(TAG, object.getLabels() + "");
            Rect box = new Rect((int) translateX(object.getBoundingBox().left),
                    (int) translateY(object.getBoundingBox().top),
                    (int) translateX(object.getBoundingBox().right),
                    (int) translateY(object.getBoundingBox().bottom));
            canvas.drawRect(box, linePaint);
            canvas.drawText(String.format("%s %.2f",
                    object.getLabels(),
                    object.getLabels() == null ? 0 : object.getLabels()),
                    translateX(object.getBoundingBox().centerX()),
                    translateY(object.getBoundingBox().centerY()),
                    linePaint);
        }
        iv.setImageBitmap(bitmap);
    }




}