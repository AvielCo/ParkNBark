package com.evan.parknbark.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.*;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends BaseActivity{
    private TextInputLayout textInputDogName, textInputDogAge, textInputDogBreed;
    private static final String KEY_DOG_NAME = "dogName";
    private static final String KEY_DOG_BREED = "dogBreed";
    private static final String KEY_DOG_AGE = "dogAge";

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageViewDogPic;

    //getting the current user
    private FirebaseUser user = mAuth.getCurrentUser();
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask nUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textInputDogName = findViewById(R.id.text_input_dog_name);
        textInputDogBreed = findViewById(R.id.text_input_dog_breed);
        textInputDogAge = findViewById(R.id.text_input_dog_age);

        imageViewDogPic = findViewById(R.id.image_view_dog_pic);

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("profiles");
    }


    public void saveProfile(View view) {
        String dogNameInput = textInputDogName.getEditText().getText().toString().trim();
        String dogBreedInput = textInputDogBreed.getEditText().getText().toString().trim();
        String dogAgeInput = textInputDogAge.getEditText().getText().toString().trim();
        if(EditTextValidator.isValidEditText(dogNameInput, textInputDogName) | EditTextValidator.isValidEditText(dogBreedInput, textInputDogBreed) |
                EditTextValidator.isValidEditText(dogAgeInput, textInputDogAge)) {

            Profile profile = new Profile(dogNameInput, dogBreedInput, dogAgeInput);

            db.collection("profiles").document(user.getUid()).set(profile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "profile saved", Toast.LENGTH_SHORT).show();
                            if (nUploadTask != null && nUploadTask.isInProgress()) {
                                Toast.makeText(ProfileActivity.this, "upload in progress", Toast.LENGTH_SHORT).show();
                            } else {
                                uploadImageToFirebase();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Error!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //opens the option to pick a picture from phone`s gallery/google drive/downloads etc.
    public void uploadImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //after choosing a picture (UploadImage function) these validations are being checked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewDogPic.setImageURI(imageUri);
        }
    }

    //mainly to get the 'xxxx.jpg' extension
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //upload the picture to firebase storage
    public void uploadImageToFirebase() {
        if (imageUri != null) {
            //reducing the image size
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //choosing the quality of the image
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();
            //uploading the image
            StorageReference fileReference = mStorageRef.child(user.getUid()
                    + "." + getFileExtension(imageUri));
            nUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(textInputDogName.getEditText().getText().toString().trim(),
                                    taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else Toast.makeText(this, "no file selected", Toast.LENGTH_SHORT).show();
    }

    //showing other user`s profiles
    /*public void showProfiles(View view) {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }*/
}
