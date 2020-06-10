package com.evan.parknbark.settings.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
        TextView name = findViewById(R.id.banned_user_name),
                email = findViewById(R.id.banned_user_email),
                uid = findViewById(R.id.banned_user_uid);
        banReason = findViewById(R.id.ban_reason_text_input);
        String n = getString(R.string.ban_user_name) + " " + b.getString("name"),
                e = getString(R.string.ban_user_email) + " " + b.getString("email"),
                u = getString(R.string.ban_user_uid) + " " + b.getString("uid");
        name.setText(n);
        email.setText(e);
        uid.setText(u);
        bannedField = FieldPath.of("banned");
        reasonField = FieldPath.of("banReason");
        setProgressBar(R.id.progressBar);
    }

    private void banUserWithUid(String uid) {
        db.collection("users").document(uid)
                .update(bannedField, true, reasonField, banReason.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.info(BanActivity.this, getString(R.string.banned_user_success), Toasty.LENGTH_SHORT).show();
                            finish();
                        } else {
                            showErrorToast();
                        }
                        isFirebaseProcessRunning = false;
                        hideProgressBar();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (isFirebaseProcessRunning) {
            showInfoToast(R.string.please_wait);
            return;
        }
        int i = v.getId();
        if (i == R.id.ban_user_button) {
            if (banReason.getText().toString().trim().isEmpty()) {
                Toasty.info(BanActivity.this, getString(R.string.empty_field), Toasty.LENGTH_SHORT).show();
            } else {
                isFirebaseProcessRunning = true;
                hideSoftKeyboard();
                showProgressBar();
                banUserWithUid(b.getString("uid"));
            }
        }
    }
}
