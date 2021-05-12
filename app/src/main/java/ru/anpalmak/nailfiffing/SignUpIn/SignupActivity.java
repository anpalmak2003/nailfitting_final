package ru.anpalmak.nailfiffing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.schema.Model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import ru.anpalmak.nailfiffing.ui.login.LoginActivity;

public class SignupActivity extends AppCompatActivity {
    String email, username, bio=null, photo=null;
    private static final String TAG = "SignUpActivity";
    public FirebaseAuth mAuth;
    Button signUpButton;
    Button logInButton;
    Button signUpGoogleButton;
    Button choosePhotoButton;
    EditText userNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText bioInput;
    TextView errorView;
    Bundle extras;
    User user;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
     StorageReference reference;
    DatabaseReference userInformation;
    public static final int RESULT_LOAD_IMAGE =1;
    Uri selectedImage=null;
    String userPhoto;
    private static final int CROP_FROM_CAMERA = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mAuth = FirebaseAuth.getInstance();
        logInButton = findViewById(R.id.haveanaccount);
        signUpButton = findViewById(R.id.signup);
        //signUpGoogleButton=findViewById(R.id.google);
        choosePhotoButton = findViewById(R.id.photo);
        userNameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.mail);
        passwordInput = findViewById(R.id.password);

        errorView = findViewById(R.id.errorView);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        reference = FirebaseStorage.getInstance("gs://nails-90d66.appspot.com").getReference();
        userInformation = FirebaseDatabase.getInstance("https://nails-90d66-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        choosePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(SignupActivity.this, SignInActivity.class);
                startActivity(myIntent);

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  Intent myIntent = new Intent(SignupActivity.this, MainPageActivity.class);
                startActivity(myIntent);*/

                if (CheckEditTextIsEmptyOrNot()) {
                    CreateRegistration();
                    addUserToDatabase();}

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
           selectedImage = data.getData();
           if(selectedImage!=null) doCrop();
           extras = data.getExtras();
            if (extras != null)
                 photo = extras.getParcelable("data");
           /* if (data != null) {

                Bundle bundle = data.getExtras();

                Bitmap bitmap = bundle.getParcelable("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
                selectedImage= Uri.parse(path);
            }*/
                //mImageView.setImageBitmap(photo);

          /*  String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

        }
    }
    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(selectedImage);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

         //   selectedImage=intent.getData();
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (selectedImage!= null) {
                            getContentResolver().delete(selectedImage, null, null);
                            selectedImage = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();

            }
        }
    }
public boolean CheckEditTextIsEmptyOrNot(){

    if (userNameInput.getText().toString().contentEquals("")) {
        errorView.setText("Username cannot be empty");
        return false;
    } else if (emailInput.getText().toString().contentEquals("")) {
        errorView.setText("Email cannot be empty");
        return false;
    } else if (passwordInput.getText().toString().contentEquals("")) {
        errorView.setText("Password cannot be empty");
        return false;
    }
    else return true;
}
public void CreateRegistration(){
    mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString()).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userNameInput.getText().toString())
                        //  .setPhotoUri(selectedImage)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
                try {
                    if (user != null)
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");

                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                    SignupActivity.this);

                                            // set title
                                            alertDialogBuilder.setTitle(user.getDisplayName() + ", Please Verify Your EmailID");

                                            // set dialog message
                                            alertDialogBuilder
                                                    .setMessage("A verification Email Is Sent To Your Registered EmailID, please click on the link and Sign in again!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            Intent signInIntent = new Intent(SignupActivity.this, SignInActivity.class);
                                                            startActivity(signInIntent);
                                                            SignupActivity.this.finish();
                                                        }
                                                    });

                                            // create alert dialog
                                            AlertDialog alertDialog = alertDialogBuilder.create();

                                            // show it
                                            alertDialog.show();


                                        }
                                    }
                                });

                } catch (Exception e) {
                    errorView.setText(e.getMessage());
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(SignupActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

                if (task.getException() != null) {
                    errorView.setText(task.getException().getMessage());
                }

            }

        }
    });

}


public void addUserToDatabase(){
        username=userNameInput.getText().toString();
        email=emailInput.getText().toString();

    currentUser = mAuth.getCurrentUser();
    if(selectedImage!=null) uploadToPhotoFirebase(selectedImage);

    user = new User(username, email);

        userInformation.child(username).setValue(user);


  //  myRef.child("users").child(username).setValue(user);
   // myRef.child(email).child("username").setValue(username);
}
    private void uploadToPhotoFirebase(Uri uri){

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        //fileRef.putFile(uri);

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userPhoto = uri.toString();
                        userInformation.child(username).child("photo").setValue(userPhoto);
                        //Do what you want with the url
                    }
       // Toast.(SignupActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
                }
            );
        }});}



             /*   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {*/
            //   uri=fileRef.getDownloadUrl();
                        /*.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
*/
                    //    userPhoto = fileRef.getDownloadUrl().toString();
                     /*   user = new User(username, email, bio, userPhoto);
                        if (currentUser != null) {
                            userInformation.child(currentUser.getUid()).setValue(user);
                        }*/
                       // String modelId = root.push().getKey();
                       // user.addPhoto(model);
                       /* progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);*/
   /*                 }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SignupActivity.this, "Uploading photo Failed !!", Toast.LENGTH_SHORT).show();
            }
        });*/

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

}
