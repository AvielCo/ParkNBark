package com.evan.parknbark.bulletinboard;

import java.text.DateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;


public class NewNoteActivity extends BaseActivity {
    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        setTitle("Add Note");

        textInputTitle = findViewById(R.id.text_input_title);
        textInputDescription = findViewById(R.id.text_input_description);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                String title = textInputTitle.getEditText().getText().toString();
                String description = textInputDescription.getEditText().getText().toString();
                hideSoftKeyboard();
                saveNote(title, description, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean saveNote(String title, String description, boolean test) {
        if(test){
            return EditTextValidator.isValidEditText(title, textInputTitle) || !EditTextValidator.isValidEditText(description, textInputDescription);
        }
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        if (EditTextValidator.isValidEditText(title, textInputTitle) | !EditTextValidator.isValidEditText(description, textInputDescription)) {
            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("notes");
            Note note = new Note(title, description, currentDate);
            notebookRef.add(note);
            Toasty.info(this, "Note added", Toast.LENGTH_SHORT).show();
            finish();
        }
        return true;
    }
}