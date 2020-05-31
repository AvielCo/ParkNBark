package com.evan.parknbark.credits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.evan.parknbark.R;
import com.evan.parknbark.contacts.EditContactActivity;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EditCreditActivity extends BaseActivity implements View.OnClickListener{
    private Spinner creditSpinner;
    private TextInputLayout updatedCreditFieldTxt;
    private FirebaseFirestore database;
    private String CREDITS_COLLECTION = "credits";
    private String OPTION_DOC = "options";
    public static final String SUCCESS_UPDATE = "Contact updated";
    public static final String FAILED_UPDATE = "Couldn't update contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_credit);

        String currentUserPermission = getIntent().getStringExtra("current_user_permission");

        setUpViews();
        setUpSpinners();
    }

    private void setUpSpinners() {
       String[] listTitle = new String[]{"Email", "Facebook", "Playstore"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EditCreditActivity.this,R.layout.spinner_item,listTitle);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        creditSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void setUpViews() {
        updatedCreditFieldTxt = findViewById(R.id.edit_text_credit_field);
        creditSpinner = findViewById(R.id.credits_field_spinner);
        findViewById(R.id.button_update_credit_field).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard();
        String updateText = updatedCreditFieldTxt.getEditText().getText().toString();
        if(EditTextValidator.isValidLayoutEditText(updateText, updatedCreditFieldTxt, getApplicationContext()) ){
            updateContact(updateText, creditSpinner.getSelectedItem().toString());
        }
    }

    private void updateContact(String updateText, String field) {
        this.database = FirebaseFirestore.getInstance();
        this.database.collection(CREDITS_COLLECTION)
                .document(OPTION_DOC)
                .update(field, updateText).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditCreditActivity.this, SUCCESS_UPDATE, Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCreditActivity.this, FAILED_UPDATE, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
