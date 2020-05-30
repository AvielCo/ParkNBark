package com.evan.parknbark.emailpassword;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BannedUserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned_user);
        TextView name = findViewById(R.id.ban_detail_name),
                email = findViewById(R.id.ban_detail_email),
                reason = findViewById(R.id.ban_detail_reason);
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot docSnap, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    String n = getString(R.string.ban_user_name) + " " + docSnap.getString("firstName") + " " + docSnap.getString("lastName"),
                            m = getString(R.string.ban_user_email) + " " + docSnap.getString("emailAddress"),
                            r = docSnap.getString("banReason");
                    name.setText(n);
                    email.setText(m);
                    reason.setText(r);
                }
            }
        });
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
