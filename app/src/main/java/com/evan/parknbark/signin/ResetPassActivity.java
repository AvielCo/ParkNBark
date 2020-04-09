package com.evan.parknbark.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.evan.parknbark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import es.dmoral.toasty.Toasty;

public class ResetPassActivity extends AppCompatActivity {

    private Button resetPassSendEmailButton;
    private EditText resetPassEmailTextInput;
    private FirebaseAuth resetPassAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        resetPassSendEmailButton = (Button) findViewById(R.id.resetPassSendButton);
        resetPassEmailTextInput = (EditText) findViewById(R.id.resetPassMailText);
        resetPassAuth = FirebaseAuth.getInstance();

        /*
        Send button action on click
         */
        resetPassSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = resetPassEmailTextInput.getText().toString();
                if (TextUtils.isEmpty(userEmail)){
                    Toasty.info(ResetPassActivity.this, "Please enter the E-Mail", Toasty.LENGTH_SHORT).show();
                }
                else {
                    resetPassAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Success
                                Toasty.info(ResetPassActivity.this, "Sent! Please check your E-Mail.", Toasty.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                            }
                            else {
                                //Failure
                                String errorMSG = task.getException().getMessage();
                                Toasty.info(ResetPassActivity.this, "Error occured. " + errorMSG, Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
