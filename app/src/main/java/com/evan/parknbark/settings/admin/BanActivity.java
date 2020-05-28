package com.evan.parknbark.settings.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;

import es.dmoral.toasty.Toasty;

public class BanActivity extends BaseActivity implements View.OnClickListener {

    private FieldPath bannedField, reasonField;
    private EditText banReason;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        b = getIntent().getExtras();
        findViewById(R.id.ban_user_button).setOnClickListener(this);
        TextView name = findViewById(R.id.banned_user_name);
        TextView email = findViewById(R.id.banned_user_email);
        TextView uid = findViewById(R.id.banned_user_uid);
        banReason = findViewById(R.id.ban_reason_text_input);
        String n = getString(R.string.ban_user_name) + " " + b.getString("name"),
            e = getString(R.string.ban_user_email) + " " + b.getString("email"),
            u = getString(R.string.ban_user_uid) + " " + b.getString("uid");
        name.setText(n);
        email.setText(e);
        uid.setText(u);
        bannedField = FieldPath.of("banned");
        reasonField = FieldPath.of("banReason");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ban_user_button) {
            if (banReason.getText().toString().isEmpty()) {
                Toasty.info(BanActivity.this, ("You have to describe a reason for a ban."), Toasty.LENGTH_SHORT).show();
            } else {
                db.collection("users").document(b.getString("uid"))
                        .update(bannedField, "true", reasonField, banReason.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.info(BanActivity.this, ("THE USER WAS BANNED!"), Toasty.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toasty.info(BanActivity.this, ("Something went wrong!"), Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
