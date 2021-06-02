package ru.anpalmak.nailfiffing.Draw;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ru.anpalmak.nailfiffing.R;
import ru.anpalmak.nailfiffing.RequestPermissionHandler;
import top.defaults.colorpicker.ColorPickerPopup;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Draw_design_avtivity extends AppCompatActivity {
    private float bottom, left, right, top=0;
    public ImageView nail; View n;
    public DrawNail draw;
    public int height, width;
    public ImageButton longNail;
    public ImageButton shortNail;
    int fingerHeight;
    int fingerWidth;
    Bitmap saveNail;
    public Button choseForm;
    public Point p;
    public Bitmap form;
    public ImageButton palette;
    public ImageButton drawing;
    int strokeWight=5;
    private DrawPaint paintView;
    int strokeColor= Color.RED;
    ImageButton save;
    StorageReference fileRef;

    RequestPermissionHandler mRequestPermissionHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#232F34")));


        mRequestPermissionHandler = new RequestPermissionHandler();


        setContentView(R.layout.activity_draw_design_avtivity);
        nail=findViewById(R.id.imageView5);
        DisplayMetrics displaymetrics = getResources(). getDisplayMetrics();
        height =displaymetrics.heightPixels;
        width =displaymetrics.widthPixels;

        save=(ImageButton) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(paintView.getBitmap() != null)
                    saveNail=combineBitmap(draw.getBitmap(), paintView.getBitmap());
                else saveNail=draw.getBitmap();
                saveNail = Bitmap.createBitmap(saveNail,(int) left, (int)top, (int)right-(int)left, (int)bottom-(int)top);
               showSaveDialog();



            }
        });

        paintView =(DrawPaint) this.findViewById(R.id.view);

        palette=(ImageButton) findViewById(R.id.palette);
        draw=(DrawNail)this.findViewById(R.id.nailform);
        longNail=(ImageButton)findViewById(R.id.longnail);

        shortNail=(ImageButton)findViewById(R.id.shortnail);
        choseForm=(Button)findViewById(R.id.form);
        drawing=(ImageButton) findViewById(R.id.draw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        choseForm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (p != null)
                    showPopup(Draw_design_avtivity.this, p);
            }
                                            });
        longNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(top>bottom - fingerHeight)
         top=top-40;
         setLength();
            }
        });
        shortNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(top<height - fingerHeight)
                top=top+40;
                setLength();
            }
        });
        palette.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(getApplicationContext())
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                v.setBackgroundColor(color);
                                draw.mainColor(color);
                            }


                        });
            }
        });

        drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paintView.init(height, width);
                paintView.setVisibility(View.VISIBLE);
                paintView.setClip(draw.getClip(), left, right, bottom, top);

            ImageButton stroke=(ImageButton)findViewById(R.id.stroke);
            stroke.setVisibility(View.VISIBLE);
                stroke.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        if(p!=null)
                       showPopupStroke(Draw_design_avtivity.this, p );
                    }
                });




            }
        });
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

           fingerHeight = nail.getHeight();
           fingerWidth = nail.getWidth();
if(top==0){
            bottom = height - fingerHeight / 3 * 2;
            top = bottom - fingerHeight / 2;
            left = width / 2 - fingerWidth / 3 ;
            right = width / 2 + fingerWidth / 3 ;

            draw.setNail( bottom, right, left, top, fingerHeight, fingerWidth);}


        }
        int[] location = new int[2];



        choseForm.getLocationOnScreen(location);


        p = new Point();
        p.x = location[0];
        p.y = location[1];



    }

    public void setLength(){
        draw.setNail( bottom, right, left, top, fingerHeight, fingerWidth);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 500;
        int popupHeight = 1000;

        // Inflate the popup_layout.xml
        TableLayout viewGroup = (TableLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        ImageButton balerina=(ImageButton) layout.findViewById(R.id.balerinaform);
        balerina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.balelerinaex);
                draw.setForm(form);


            }
        });
        ImageButton extrasharp=(ImageButton) layout.findViewById(R.id.extrasharpform);
        extrasharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.extrasharp);
                draw.setForm(form);


            }
        });
        ImageButton round=(ImageButton) layout.findViewById(R.id.roundform);
        round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.round);
                draw.setForm(form);


            }
        });
        ImageButton oval=(ImageButton) layout.findViewById(R.id.ovalform);
        oval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.oval);
                draw.setForm(form);


            }
        });
        ImageButton square=(ImageButton) layout.findViewById(R.id.squareform);
        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.square);
                draw.setForm(form);


            }
        });
        ImageButton sharp=(ImageButton) layout.findViewById(R.id.sharpform);
        sharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form= BitmapFactory.decodeResource(getResources(), R.drawable.sharp);
                draw.setForm(form);


            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPopupStroke(final Activity context, Point p) {
        int popupWidth = 500;
        int popupHeight = 1000;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_stroke);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_setting_stroke, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        StrokeView stroke=(StrokeView) layout.findViewById(R.id.stroke_view);


        ImageButton plusStroke=(ImageButton)  layout.findViewById(R.id.plus_stroke);
        plusStroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWight=strokeWight+3;
                stroke.setStrokeWight(strokeWight);


            }
        });


        ImageButton minusStroke=(ImageButton)  layout.findViewById(R.id.minus_stroke);
        minusStroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWight=strokeWight-3;
                stroke.setStrokeWight(strokeWight);

            }
        });

        Button selectColor=(Button) layout.findViewById(R.id.select_color);
        selectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ColorPickerPopup.Builder(Draw_design_avtivity.this)
                        .initialColor(strokeColor) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(getCurrentFocus(), new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                            stroke.setStrokeColor(color);
                            strokeColor=color;
                            }


                        });


            }});

    ImageButton saveStroke=(ImageButton)layout.findViewById(R.id.save_stroke);
        saveStroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setColorWidth(strokeColor, strokeWight);
                popup.dismiss();
            }
        });
    ImageButton closeStroke=(ImageButton)layout.findViewById(R.id.close_stroke);
        closeStroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               popup.dismiss();

            }
        });
     Button clear=(Button)layout.findViewById(R.id.clear) ;
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
                popup.dismiss();

            }
        });

    }

 public void uploadImageToStorage(Bitmap bitmap, String imageName, String access)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference reference = FirebaseStorage.getInstance("gs://nails-90d66.appspot.com").getReference();

        fileRef = reference.child(System.currentTimeMillis() + ".png");
        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final String[] url = new String[1];
                addMetadata(imageName, access);
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url[0] = downloadUri.getResult().toString();
                        uploadImageToDatabase(url[0], FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), imageName, access);
                        if(access.equals("public")) Toast.makeText(Draw_design_avtivity.this, "Published", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(Draw_design_avtivity.this, "Saved as draft", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }
    public void uploadImageToDatabase(String url, String username, String imageName, String access)
    {
        DatabaseReference mDataReference = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").getReference("images");
        ImageNailInfo info=new ImageNailInfo(url, username, imageName, access);
        String key = mDataReference.push().getKey();
        if(access.equals("public"))
        { mDataReference.child("public").child(key).setValue(info);
            mDataReference.child(username).child("public").child(key).setValue(info);}
         mDataReference.child(username).child("draft").child(key).setValue(info);
    }
public void addMetadata(String imageName, String access)
{
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageMetadata metadata = new StorageMetadata.Builder()
            .setCustomMetadata("user", mAuth.getCurrentUser().getDisplayName())
              .setCustomMetadata("imagename", imageName)
            .setCustomMetadata("access", access)
            .build();
           fileRef.updateMetadata(metadata)
            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    // Updated metadata is in storageMetadata
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
}


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveCanvasImage() throws IOException {


        MediaStore.Images.Media.insertImage(getContentResolver(), saveNail, "nail" , "nails");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        Bitmap result;
        try {
            if (background == null) {
                return null;
            }
            int bgWidth = background.getWidth();
            int bgHeight = background.getHeight();
            int fgWidth = foreground.getWidth();
            int fgHeight = foreground.getHeight();
            result = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(result);
            cv.drawBitmap(background, 0, 0, null);
            cv.drawBitmap(foreground, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
            cv.save();
            cv.restore();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
public void showSaveDialog(){
    ViewGroup viewGroup = findViewById(android.R.id.content);

    //then we will inflate the custom alert dialog xml that we created
    View dialogView = LayoutInflater.from(this).inflate(R.layout.save_nail_dialog, viewGroup, false);


    //Now we need an AlertDialog.Builder object
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    //setting the view of the builder to our custom view that we already inflated
    builder.setView(dialogView);

    //finally creating the alert dialog and displaying it
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
    EditText enterDesignName=dialogView.findViewById(R.id.enter_designname);
    ImageView nailView=dialogView.findViewById(R.id.nailview);
    nailView.setImageBitmap(saveNail);
    Button cancel=dialogView.findViewById(R.id.cancel);
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         alertDialog.dismiss();
        }
    });
    Button draft=dialogView.findViewById(R.id.draft);
    draft.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if(enterDesignName.getText().toString().equals(""))
                Toast.makeText(Draw_design_avtivity.this,"Enter the design name", Toast.LENGTH_SHORT).show();
            else{
                uploadImageToStorage(saveNail, enterDesignName.getText().toString(), "draft");
                try {
                    saveCanvasImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
            }
        }
    });

    Button publish=dialogView.findViewById(R.id.publish);
    publish.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if(enterDesignName.getText().toString().equals(""))
                Toast.makeText(Draw_design_avtivity.this,"Enter the design name", Toast.LENGTH_SHORT).show();
            else{
                uploadImageToStorage(saveNail, enterDesignName.getText().toString(), "public");
                try {
                    saveCanvasImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
            }
        }
    });
}

}
