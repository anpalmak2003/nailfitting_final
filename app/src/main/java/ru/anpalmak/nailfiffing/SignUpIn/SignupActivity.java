package ru.anpalmak.nailfiffing.SignUpIn;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.List;

import ru.anpalmak.nailfiffing.R;


public class SignupActivity extends AppCompatActivity {
    String email, username, photo=null;
    private static final String TAG = "SignUpActivity";
    public FirebaseAuth mAuth;
    Button signUpButton;
    Button logInButton;
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
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#232F34")));
        mAuth = FirebaseAuth.getInstance();
        logInButton = findViewById(R.id.haveanaccount);
        signUpButton = findViewById(R.id.signup);
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
            }
    }
    /**Метод, который позволяет обрезать изобжадение*/
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

    /**Проверка заполненности полей*/
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
    /**Регистрация пользователя*/
public void CreateRegistration(){
    mAuth.createUserWithEmailAndPassword(emailInput.getText().
            toString(), passwordInput.getText().toString()).
            addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userNameInput.getText().toString())
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

                                            alertDialogBuilder.setTitle(user.getDisplayName() + ", Please Verify Your EmailID");

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

                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();}
                                    }
                                });

                } catch (Exception e) {
                    errorView.setText(e.getMessage());
                }
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(SignupActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

                if (task.getException() != null) {
                    errorView.setText(task.getException().getMessage());
                }}}});
}

    /**Добавление пользователя в базу данных*/
public void addUserToDatabase(){
        username=userNameInput.getText().toString();
        email=emailInput.getText().toString();
        currentUser = mAuth.getCurrentUser();
    if(selectedImage!=null) uploadToPhotoFirebase(selectedImage);
    user = new User(username, email);
    userInformation.child(username).setValue(user);
}
    /**Загрузка фотографии в хранилище*/
    private void uploadToPhotoFirebase(Uri uri){

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userPhoto = uri.toString();
                        userInformation.child(username).child("photo").setValue(userPhoto);
                    }}
            );
        }});}

    /**Получить ссылку на фото*/
    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

}
