package com.evan.parknbark.map_profile.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class WatchProfile extends BaseActivity {

    private TextView dogDetailes,fullName;
    private ImageView dogPic;
    private static final String TAG  = "WatchProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_profile);

        dogDetailes = findViewById(R.id.textView_dogDetails);
        fullName = findViewById(R.id.textView_fullName);
        dogPic = findViewById(R.id.imageView_dog_pic);

        getInfoFromFirebase();
    }


    void getInfoFromFirebase(){
        DocumentReference usersDocRef = db.collection("profiles").document(mAuth.getCurrentUser().getUid());
        usersDocRef.
                get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String mfirstName = documentSnapshot.getString("firstName");
                            String mlastName = documentSnapshot.getString("lastName");
                            String mdogName = documentSnapshot.getString("dogName");
                            String mdogAge = documentSnapshot.getString("dogAge");
                            String mdogBreed = documentSnapshot.getString("dogBreed");
                            String mgodPic = documentSnapshot.getString("profilePicture");

                            fullName.setText(mfirstName + " "+ mlastName);
                            dogDetailes.setText(mdogName + ", " + mdogAge + ", " + mdogBreed);
                            Picasso.get().load(mgodPic).into(dogPic);
                        }else{
                            Toast.makeText(WatchProfile.this,"data does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,e.toString());
                    }
                });

    }

}
