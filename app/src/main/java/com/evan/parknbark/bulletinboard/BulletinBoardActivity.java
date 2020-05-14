package com.evan.parknbark.bulletinboard;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    private volatile User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_board);

        mAuth = FirebaseAuth.getInstance();
        mNoteRef = db.collection("notes");

        buttonAddNote = findViewById(R.id.button_add_note);

        findViewById(R.id.button_add_note).setOnClickListener(this);
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);
                    if (user.getPermission().equals("admin")) {
                        findViewById(R.id.button_add_note).setVisibility(View.INVISIBLE);
                        setItemTouchHelper();
                    }
                } else {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }
            }
        });
        setUpRecyclerView();
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

    private void goToNoteDescription(Note note) {
        Intent intent = new Intent(this, NoteDescriptionActivity.class);
        intent.putExtra("NOTE_TO_SEE", note);
        startActivity(intent);
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
        goToNoteDescription(note);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.button_add_note)
            BulletinBoardActivity.this.startActivity(new Intent(BulletinBoardActivity.this, NewNoteActivity.class));
    }
}
