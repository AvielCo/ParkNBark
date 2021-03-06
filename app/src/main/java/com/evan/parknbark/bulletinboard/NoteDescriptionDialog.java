package com.evan.parknbark.bulletinboard;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseDialogFragment;

import es.dmoral.toasty.Toasty;

public class NoteDescriptionDialog extends BaseDialogFragment {
    private TextView textViewDescription, textViewDate;
    private static final String TAG = "NoteDescriptionDialog";
    private Toolbar toolbar;

    static NoteDescriptionDialog display(FragmentManager fragmentManager) {
        NoteDescriptionDialog noteDescriptionDialog = new NoteDescriptionDialog();
        noteDescriptionDialog.show(fragmentManager, TAG);
        return noteDescriptionDialog;
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
        View v = inflater.inflate(R.layout.dialog_note_description, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        textViewDate = v.findViewById(R.id.text_view_date_des);
        textViewDescription = v.findViewById(R.id.text_view_description_des);
        setNote(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //when clicking on X inside toolbar
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
    }

    private void setNote(View v){
        Bundle bundle = this.getArguments();
        toolbar.setTitle(bundle.getString("note_title"));
        textViewDescription.setText(bundle.getString("note_desc"));
        textViewDate.setText(bundle.getString("note_date"));
        v.findViewById(R.id.button_copy_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("description", bundle.getString("note_desc")));
                Toasty.success(getContext(), getContext().getString(R.string.copy_success), Toasty.LENGTH_SHORT).show();
            }
        });
    }
}
