package com.evan.parknbark.bulletinboard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.evan.parknbark.R;
import com.evan.parknbark.validation.EditTextValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class NewNoteDialog extends DialogFragment {
    private static final String TAG = "NewNoteDialog";
    private Toolbar toolbar;
    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;

    static void display(FragmentManager fragmentManager) {
        NewNoteDialog exampleDialog = new NewNoteDialog();
        exampleDialog.show(fragmentManager, TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_FromToPointAnimation);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_new_note, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        textInputDescription = v.findViewById(R.id.text_input_description);
        textInputTitle = v.findViewById(R.id.text_input_title);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //when clicking on X inside toolbar
        toolbar.setNavigationOnClickListener(v -> dismiss());

        //title for toolbar
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        toolbar.setTitle(getResources().getString(R.string.new_note));
        toolbar.inflateMenu(R.menu.save_menu);

        //when clicking on save icon inside toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            String title = textInputTitle.getEditText().getText().toString().trim();
            String description = textInputDescription.getEditText().getText().toString().trim();
            saveNote(title, description, false);
            return true;
        });
    }

    public boolean saveNote(String title, String description, boolean test) {
        if (test) {
            return EditTextValidator.isValidLayoutEditText(title, textInputTitle, null) && EditTextValidator.isValidLayoutEditText(description, textInputDescription, null);
        }
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        if (EditTextValidator.isValidLayoutEditText(title, textInputTitle, getContext()) & EditTextValidator.isValidLayoutEditText(description, textInputDescription, getContext())) {
            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("notes");
            Note note = new Note(title, description, currentDate);
            notebookRef.add(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(getContext(), getResources().getString(R.string.new_note_success), Toasty.LENGTH_SHORT, true).show();
                    dismiss();
                }
                else
                    Toasty.error(getContext(), getResources().getString(R.string.error_message), Toasty.LENGTH_SHORT, true).show();
            });
        }
        return true;
    }
}
