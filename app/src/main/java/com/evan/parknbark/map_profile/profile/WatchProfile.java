package com.evan.parknbark.map_profile.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import es.dmoral.toasty.Toasty;

public class WatchProfile extends BaseActivity implements View.OnClickListener{

    private TextView firstName, lastName;
    private EditText mEditTextdogName, mEditTextdogAge, mEditTextdogBreed;
    private ImageView dogPic;
    private Button uploadPic;

    private static final String TAG = "WatchProfile";

    private boolean hiddenItem = false;

    private Toolbar toolbar;

    private static final int PICK_IMAGE_REQUEST = 1;
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

        firstName = findViewById(R.id.TextView_firstName);
        lastName = findViewById(R.id.TextView_lastName);
        mEditTextdogAge = findViewById(R.id.editText_dogAge);
        mEditTextdogName = findViewById(R.id.editText_dogName);
        mEditTextdogBreed = findViewById(R.id.editText_dogBreed);

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles");
        currentUser = mAuth.getCurrentUser();

        dogPic = findViewById(R.id.imageView_dog_pic);
        uploadPic = findViewById(R.id.button_uploadDogPic);
        uploadPic.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.edit_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                hideSoftKeyboard();
                switch(i){
                    case R.id.edit_icon:
                        hiddenItem = true;
                        hideItemToolbar();
                        uploadPic.setVisibility(View.VISIBLE);
                        break;
                    case R.id.save_icon:
                        String name = mEditTextdogName.getText().toString();
                        String age = mEditTextdogAge.getText().toString();
                        String breed = mEditTextdogBreed.getText().toString();
                        saveProfile(name,breed,age);
                        hiddenItem = false;
                        hideItemToolbar();
                        uploadPic.setVisibility(View.INVISIBLE);
                        break;
                }
                return false;
            }
        });
        hideItemToolbar();
        getInfoFromFirebase();
    }


    public boolean hideItemToolbar() {
        Menu m = toolbar.getMenu();
        MenuItem edit_item = m.findItem(R.id.edit_icon);
        MenuItem save_item = m.findItem(R.id.save_icon);
        if(hiddenItem){
            edit_item.setVisible(false);
            save_item.setVisible(true);
            return true;
        }
        edit_item.setVisible(true);
        save_item.setVisible(false);
        return true;
    }

    void getInfoFromFirebase() {
        DocumentReference usersDocRef = db.collection("profiles").document(mAuth.getCurrentUser().getUid());
        usersDocRef.
                get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String mfirstName = documentSnapshot.getString("firstName");
                            String mlastName = documentSnapshot.getString("lastName");
                            String mdogName = documentSnapshot.getString("dogName");
                            String mdogAge = documentSnapshot.getString("dogAge");
                            String mdogBreed = documentSnapshot.getString("dogBreed");
                            dogPicUri = documentSnapshot.getString("profilePicture");

                            firstName.setText(mfirstName + " ");
                            lastName.setText(" " + mlastName);

                            mEditTextdogName.setText(mdogName);
                            int positionDN = mEditTextdogName.length();
                            mEditTextdogName.setSelection(positionDN);

                            mEditTextdogBreed.setText(mdogBreed);
                            int positionDB = mEditTextdogBreed.length();
                            mEditTextdogBreed.setSelection(positionDB);

                            mEditTextdogAge.setText(mdogAge);
                            int positionDA = mEditTextdogAge.length();
                            mEditTextdogAge.setSelection(positionDA);

                            Picasso.get().load(dogPicUri).into(dogPic);
                        } else {
                            Toast.makeText(WatchProfile.this, "data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
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
            Picasso.get().load(mImageUri).into(dogPic);
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
        if(imageUri==null){
            imageUri = dogPicUri;
        }
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

    public void saveProfile(String dogNameInput, String dogBreedInput, String dogAgeInput) {
        if (EditTextValidator.isValidString(dogNameInput) & EditTextValidator.isValidString(dogBreedInput) &
                EditTextValidator.isValidString(dogAgeInput)) {
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
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        hideSoftKeyboard();
        if (i == R.id.button_uploadDogPic) {
            uploadImage();
        }
    }

}
