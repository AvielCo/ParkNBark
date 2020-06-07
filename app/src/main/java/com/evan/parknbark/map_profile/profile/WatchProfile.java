package com.evan.parknbark.map_profile.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class WatchProfile extends BaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "WatchProfile";
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText mEditTextDogName, mEditTextDogAge, mEditTextDogBreed;
    private ImageView mImageViewDogPic;
    private Button mButtonUploadPic;
    private boolean hiddenItem = false;
    private Toolbar toolbar;
    private Uri mImageUri;
    private boolean isUploadedImage = false;

    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private User user;

    private String dogPicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_profile);

        mEditTextDogAge = findViewById(R.id.editText_dogAge);
        mEditTextDogName = findViewById(R.id.editText_dogName);
        mEditTextDogBreed = findViewById(R.id.editText_dogBreed);

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles");
        currentUser = mAuth.getCurrentUser();

        mImageViewDogPic = findViewById(R.id.imageView_dog_pic);
        mButtonUploadPic = findViewById(R.id.button_uploadDogPic);
        mButtonUploadPic.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.edit_menu);
        toolbar.setOnMenuItemClickListener(this);

        hideItemInsideToolbar();
        getInfoFromFirebase();
    }

    public void hideItemInsideToolbar() {
        Menu m = toolbar.getMenu();
        MenuItem edit_item = m.findItem(R.id.edit_icon);
        MenuItem save_item = m.findItem(R.id.save_icon);
        if (hiddenItem) {
            edit_item.setVisible(false);
            save_item.setVisible(true);
            return;
        }
        edit_item.setVisible(true);
        save_item.setVisible(false);
    }

    private void getInfoFromFirebase() {
        DocumentReference usersDocRef = db.collection("profiles").document(mAuth.getCurrentUser().getUid());
        usersDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName"),
                                    lastName = documentSnapshot.getString("lastName"),
                                    dogName = documentSnapshot.getString("dogName"),
                                    dogAge = documentSnapshot.getString("dogAge"),
                                    dogBreed = documentSnapshot.getString("dogBreed");
                            dogPicUri = documentSnapshot.getString("profilePicture");
                            toolbar.setTitle(firstName + " " + lastName);
                            toolbar.setTitleTextColor(Color.WHITE);

                            mEditTextDogName.setText(dogName);
                            int positionDN = mEditTextDogName.length();
                            mEditTextDogName.setSelection(positionDN);

                            mEditTextDogBreed.setText(dogBreed);
                            int positionDB = mEditTextDogBreed.length();
                            mEditTextDogBreed.setSelection(positionDB);

                            mEditTextDogAge.setText(dogAge);
                            int positionDA = mEditTextDogAge.length();
                            mEditTextDogAge.setSelection(positionDA);

                            Picasso.get().load(dogPicUri).into(mImageViewDogPic);
                        } else {
                            Toast.makeText(WatchProfile.this, "data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        if (imageUri == null) {
            imageUri = dogPicUri;
        }
        Profile profile = new Profile(user.getFirstName(), user.getLastName(), dogNameInput, dogBreedInput, dogAgeInput, imageUri);
        db.collection("profiles").document(currentUser.getUid()).set(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSuccessToast(R.string.profile_saved);
                            setResult(RESULT_OK);
                            user.setBuiltProfile(true);
                            hideProgressBar();
                        } else showErrorToast();
                    }
                });

    }

    public void saveProfile(String dogNameInput, String dogBreedInput, String dogAgeInput) {
        if (EditTextValidator.isValidString(dogNameInput) & EditTextValidator.isValidString(dogBreedInput) &
                EditTextValidator.isValidString(dogAgeInput)) {
            showProgressBar();
            DocumentReference usersDocRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            usersDocRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        user = Objects.requireNonNull(task.getResult()).toObject(User.class);
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
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        hideSoftKeyboard();
        if (i == R.id.button_uploadDogPic) {
            uploadImage();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        hideSoftKeyboard();
        switch (i) {
            case R.id.edit_icon:
                hiddenItem = true;
                hideItemInsideToolbar();
                mButtonUploadPic.setVisibility(View.VISIBLE);
                break;
            case R.id.save_icon:
                String name = mEditTextDogName.getText().toString();
                String age = mEditTextDogAge.getText().toString();
                String breed = mEditTextDogBreed.getText().toString();
                if(name == "name")
                    name = "UNKNOWN";
                else if(age == "age")
                    age="UNKNOWN";
                else if(breed=="breed")
                    breed="UNKNOWN";
                saveProfile(name, breed, age);
                hiddenItem = false;
                hideItemInsideToolbar();
                mButtonUploadPic.setVisibility(View.INVISIBLE);
                break;
        }
        return false;
    }
}
