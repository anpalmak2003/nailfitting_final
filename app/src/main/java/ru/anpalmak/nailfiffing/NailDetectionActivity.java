package ru.anpalmak.nailfiffing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
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

public class NailDetectionActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {
    public static final int REQUEST_CODE_PERMISSION = 101;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private TextureView tv;
    private ImageView iv;
    FirebaseModelInterpreter interpreter;
    private static final String TAG = "RealTimeCustomObjectDetectionActivity";
    private int DIM_BATCH_SIZE = 1;
    private int DIM_PIXEL_SIZE = 3;
    private int DIM_IMG_SIZE_X = 300;
    private int DIM_IMG_SIZE_Y = 300;
    FirebaseModelInputOutputOptions inputOutputOptions;
    ObjectDetector objectDetector;
    LocalModel localModel;
    InputImage image;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint linePaint;
    private float widthScaleFactor = 1.0f;
    private float heightScaleFactor = 1.0f;

    public static CameraX.LensFacing lens = CameraX.LensFacing.BACK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_model_detection_video);
        tv = findViewById(R.id.od_texture_view);
        iv = findViewById(R.id.od_image_view);
        if (allPermissionsGranted()) {
            initDetector();
            tv.post(this::startCamera);
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
        }

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

    @SuppressLint({"RestrictedApi", "LongLogTag"})
    private void startCamera() {
        initCamera();
        ImageButton ibSwitch = findViewById(R.id.btn_switch_od);
        ibSwitch.setOnClickListener(v -> {
            if (lens == CameraX.LensFacing.FRONT)
                lens = CameraX.LensFacing.BACK;
            else
                lens = CameraX.LensFacing.FRONT;
            try {
                Log.i(TAG, "" + lens);
                CameraX.getCameraWithLensFacing(lens);
                initCamera();
            } catch (CameraInfoUnavailableException e) {
                Log.e(TAG, e.toString());
            }
        });
    }

    private void initCamera() {
        CameraX.unbindAll();
        PreviewConfig pc = new PreviewConfig
                .Builder()
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        Preview preview = new Preview(pc);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup vg = (ViewGroup) tv.getParent();
            vg.removeView(tv);
            vg.addView(tv, 0);
            tv.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig iac = new ImageAnalysisConfig
                .Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(iac);
        imageAnalysis.setAnalyzer(Runnable::run,
                new DemoAnalyzer(tv, iv));
        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }


    private void detectObjects() {

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

    @SuppressLint("LongLogTag")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                tv.post(this::startCamera);
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
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
            detectObjects();
    }
}
