package com.evan.parknbark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestoreException;

import es.dmoral.toasty.Toasty;

public class RateUsActivity extends BaseActivity implements View.OnClickListener {

    private RatingBar rating;
    private FirebaseUser fireBaseUser;
    private FieldPath field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);
        findViewById(R.id.rate_us_submit_button).setOnClickListener(this);

        fireBaseUser = mAuth.getCurrentUser();
        field = FieldPath.of("appRate");
        rating = findViewById(R.id.rate_us_stars_bar);
        DocumentReference docRef = db.collection("users").document(fireBaseUser.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) rating.setRating((documentSnapshot.getDouble(field.toString()).floatValue()));
                else rating.setRating(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rate_us_submit_button){
            db.collection("users").document(fireBaseUser.getUid()).update(field,rating.getRating()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toasty.info(RateUsActivity.this, ("Your rating submitted!\nThank you!") ,Toasty.LENGTH_SHORT).show();
                    }
                    else {
                        Toasty.info(RateUsActivity.this, ("Something went wrong!") ,Toasty.LENGTH_SHORT).show();
                    }
                }
            });
            this.finish();
        }
    }
}
