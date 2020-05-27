package com.evan.parknbark.map_profile.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;


public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private TextInputLayout mTextInputDogName, mTextInputDogAge, mTextInputDogBreed;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private ImageView mImageViewDogPic;
    private boolean isUploadedImage = false;

    //getting the current user
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mTextInputDogName = findViewById(R.id.text_input_dog_name);
        mTextInputDogBreed = findViewById(R.id.text_input_dog_breed);
        mTextInputDogAge = findViewById(R.id.text_input_dog_age);

        mImageViewDogPic = findViewById(R.id.image_view_dog_pic);

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles");
        currentUser = mAuth.getCurrentUser();

        findViewById(R.id.button_save_profile).setOnClickListener(this);
        findViewById(R.id.button_upload_image).setOnClickListener(this);

        setProgressBar(R.id.progressBar);
    }


    public boolean saveProfile(String dogNameInput, String dogBreedInput, String dogAgeInput, boolean test) {
        if (test) {
            return EditTextValidator.isValidEditText(dogNameInput, mTextInputDogName, null) && EditTextValidator.isValidEditText(dogBreedInput, mTextInputDogBreed, null) &&
                    EditTextValidator.isValidEditText(dogAgeInput, mTextInputDogAge, null);
        }
        if (EditTextValidator.isValidEditText(dogNameInput, mTextInputDogName, getApplicationContext()) & EditTextValidator.isValidEditText(dogBreedInput, mTextInputDogBreed, getApplicationContext()) &
                EditTextValidator.isValidEditText(dogAgeInput, mTextInputDogAge, getApplicationContext())) {
            showProgressBar();
            DocumentReference usersDocRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            usersDocRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        user = task.getResult().toObject(User.class);
                        if (mUploadTask != null && mUploadTask.isInProgress())
                            Toasty.info(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                        else if (isUploadedImage)
                            uploadImageToFirebase(dogNameInput, dogBreedInput, dogAgeInput);
                        else uploadProfile(dogNameInput, dogBreedInput, dogAgeInput, null);
                    } else
                        showErrorToast();
                }
            });
        }
        return true;
    }

    //opens the option to pick a picture from phone`s gallery/google drive/downloads etc.
    public void uploadImage() {
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
            isUploadedImage = true;
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageViewDogPic);
        }
    }

    //mainly to get the 'xxxx.jpg' extension
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //upload the picture to firebase storage
    public void uploadImageToFirebase(String dogNameInput, String dogBreedInput, String dogAgeInput) {
        if (mImageUri != null) {
            final String fileName = currentUser.getUid()
                    + "." + getFileExtension(mImageUri);
            StorageReference fileReference = mStorageRef.child(fileName);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                UploadTask.TaskSnapshot taskSnapshot = task.getResult();
                                if (taskSnapshot != null) {
                                    //get uploaded image uri
                                    mStorageRef.child(fileName).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String mImageDownloadUri = task.getResult().toString();
                                                uploadProfile(dogNameInput, dogBreedInput, dogAgeInput, mImageDownloadUri);
                                            } else showErrorToast();
                                        }
                                    });
                                }
                            } else
                                showErrorToast();
                        }
                    });
        }
    }

    private void uploadProfile(String dogNameInput, String dogBreedInput, String dogAgeInput, String imageUri) {
        Profile profile = new Profile(user.getFirstName(), user.getLastName(), dogNameInput, dogBreedInput, dogAgeInput, imageUri);
        db.collection("profiles").document(currentUser.getUid()).set(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSuccessToast(R.string.profile_saved);
                            hideProgressBar();
                        } else showErrorToast();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        hideSoftKeyboard();
        switch (i) {
            case R.id.button_save_profile:
                String dogNameInput = mTextInputDogName.getEditText().getText().toString().trim();
                String dogBreedInput = mTextInputDogBreed.getEditText().getText().toString().trim();
                String dogAgeInput = mTextInputDogAge.getEditText().getText().toString().trim();
                saveProfile(dogNameInput, dogBreedInput, dogAgeInput, false);
                break;
            case R.id.button_upload_image:
                uploadImage();
                break;
        }
    }
}
