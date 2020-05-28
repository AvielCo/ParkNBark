package com.evan.parknbark.settings.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;

import es.dmoral.toasty.Toasty;

public class BanActivity extends BaseActivity implements View.OnClickListener {

    private TextView name, email, uid;
    private FieldPath field;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        b = getIntent().getExtras();
        findViewById(R.id.ban_user_button).setOnClickListener(this);
        name = findViewById(R.id.banned_user_name);
        email = findViewById(R.id.banned_user_email);
        uid = findViewById(R.id.banned_user_uid);
        name.setText("Name: " + b.getString("name"));
        email.setText("E-Mail: " + b.getString("email"));
        uid.setText("UID: " + b.getString("uid"));
        field = FieldPath.of("banned");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ban_user_button) {
            db.collection("users").document(b.getString("uid")).update(field,"true").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toasty.info(BanActivity.this, ("THE USER WAS BANNED!") ,Toasty.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toasty.info(BanActivity.this, ("Something went wrong!") ,Toasty.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
