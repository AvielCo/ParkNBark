package com.evan.parknbark.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditContactActivity extends BaseActivity implements View.OnClickListener{
    //strings
    public static final String CONTACTS = "contacts";
    public static final String SUCCESS_UPDATE = "Contact updated";
    public static final String FAILED_UPDATE = "Couldn't update contact";

    //view varaiables
    private Spinner contactSpinner, fieldSpinner;
    private TextInputLayout updatedContactFieldTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        setUpViews();
        setUpSpinners();
    }

    /**
     * setting up the variable for each of the views in the xml file.
     */
    private void setUpViews() {
        updatedContactFieldTxt = findViewById(R.id.edit_text_contact_field);
        contactSpinner = findViewById(R.id.contact_document_spinner);
        fieldSpinner = findViewById(R.id.contact_field_spinner);
        findViewById(R.id.button_update_contact_field).setOnClickListener(this);

    }

    /**
     * pulls from the contacts collection the name of the documents and the name of the fields and sets them up in the spinner view.
     *
     */
    private void setUpSpinners() {
        db.collection(CONTACTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> listTitle = new ArrayList<>();
                        List<String> listField = new ArrayList<>();
                        boolean gotFields = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listTitle.add(document.getId());
                                if(!gotFields){
                                    Map<String, Object> map = document.getData();
                                    if (map != null) {
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            listField.add(entry.getKey());
                                        }
                                    }
                                    gotFields = true;
                                }
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EditContactActivity.this,R.layout.spinner_item,listTitle);
                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            contactSpinner.setAdapter(spinnerArrayAdapter);

                            spinnerArrayAdapter = new ArrayAdapter<String>(EditContactActivity.this,R.layout.spinner_item,listField);
                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                            fieldSpinner.setAdapter(spinnerArrayAdapter);
                        }
                    }
                });
    }

    /**
     * when the button is clicked if the text from the edittext passes the validtion, updatecontact is called.
     * if the input is wrong, an error is showed to the user.
     */
    @Override
    public void onClick(View v) {
        hideSoftKeyboard();
        String updateText = updatedContactFieldTxt.getEditText().getText().toString();
        if(EditTextValidator.isValidLayoutEditText(updateText, updatedContactFieldTxt, getApplicationContext()) ){
            updateContact(updateText, contactSpinner.getSelectedItem().toString(), fieldSpinner.getSelectedItem().toString());
        }
    }

    /**
     *  the method pulls the desired document from the db and tries updating the value.
     *  proper message is shown if the action succeeds or fails.
     * @param updateText the new value the user wants to input into the field in db.
     * @param doc doc represents the document in the db the user chose to update.
     * @param field field represents the field in the db the user chose to update.
     */
    private void updateContact(String updateText, String doc, String field) {
        db.collection(CONTACTS).document(doc).update(field, updateText).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditContactActivity.this, SUCCESS_UPDATE, Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditContactActivity.this, FAILED_UPDATE, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
