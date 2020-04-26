package com.evan.parknbark.bulletinboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.evan.parknbark.utilis.BaseActivity;
import com.evan.parknbark.R;

public class NoteDescriptionActivity extends BaseActivity {
    TextView textViewTitle, textViewDescription, textViewDate;
    private static final String TAG = "NoteDescriptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_description);
        Intent i = getIntent();
        Note displayNote = (Note) i.getSerializableExtra("NOTE_TO_SEE");

        textViewTitle = findViewById(R.id.text_view_title_des);
        textViewTitle.setText(displayNote.getTitle());

        textViewDescription = findViewById(R.id.text_view_description_des);
        textViewDescription.setText(displayNote.getDescription());

        textViewDate = findViewById(R.id.text_view_date_des);
        textViewDate.setText(displayNote.getDate());
    }
}
