package com.evan.parknbark.bulletinboard;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.evan.parknbark.utilities.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class BulletinBoardActivity extends BaseActivity implements NoteAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "BulletinBoardActivity";
    private CollectionReference mNoteRef;
    private NoteAdapter mAdapter;
    private FloatingActionButton buttonAddNote;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_board);

        mAuth = FirebaseAuth.getInstance();
        mNoteRef = db.collection("notes");

        buttonAddNote = findViewById(R.id.button_add_note);

        findViewById(R.id.button_add_note).setOnClickListener(this);

        String currentUserPermission = getIntent().getStringExtra("current_user_permission");
        setUpRecyclerView();
        if (currentUserPermission.equals("admin")) {
            findViewById(R.id.button_add_note).setVisibility(View.INVISIBLE);
            setItemTouchHelper();
        }
    }

    private void setItemTouchHelper(){
        buttonAddNote.setVisibility(View.VISIBLE);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setUpRecyclerView() {
        Query query = mNoteRef.orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        mAdapter = new NoteAdapter(options);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
        Note note = documentSnapshot.toObject(Note.class);
        openNoteDescriptionDialog(note);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.button_add_note)
            openNewNoteDialog();
    }

    private void openNoteDescriptionDialog(Note note) {
        Bundle bundle = new Bundle();
        bundle.putString("note_title", note.getTitle());
        bundle.putString("note_desc", note.getDescription());
        bundle.putString("note_date", note.getDate());
        NoteDescriptionDialog.display(getSupportFragmentManager()).setArguments(bundle);
    }

    private void openNewNoteDialog(){
        NewNoteDialog.display(getSupportFragmentManager());
    }
}
