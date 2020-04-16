package com.evan.parknbark.bulletinboard;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;



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
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String title = textInputTitle.getEditText().getText().toString();
        String description = textInputDescription.getEditText().getText().toString();

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());


        if (!validateTitle(title) | !validateDescription(description)) return;

        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("Notes");
        notebookRef.add(new Note(title, description, currentDate));
        Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateTitle(String titleInput){
        if(titleInput.trim().isEmpty()) {
            textInputTitle.setError("Title can't be empty!");
            return false;
        } else {
            textInputTitle.setError(null);
            return true;
        }
    }

    private boolean validateDescription(String descriptionInput){
        if(descriptionInput.trim().isEmpty()) {
            textInputDescription.setError("Description can't be empty!");
            return false;
        } else {
            textInputDescription.setError(null);
            return true;
        }
    }
}