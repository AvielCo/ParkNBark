package com.evan.parknbark.bulletinboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evan.parknbark.BaseActivity;
import com.evan.parknbark.R;
import com.evan.parknbark.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class BulletinBoardActivity extends BaseActivity implements NoteAdapter.OnItemClickListener {

    private static final String TAG = "BulletinBoardActivity";
    private CollectionReference noteRef;
    private NoteAdapter adapter;
    private User user;
    private FloatingActionButton buttonAddNote;
    private FireStoreResults mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_board);

        mAuth = FirebaseAuth.getInstance();
        noteRef = db.collection("notes");

        buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BulletinBoardActivity.this, NewNoteActivity.class));
            }
        });

        //if (user.getPermission().equals("user"))
            //buttonAddNote.setVisibility(View.INVISIBLE);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = noteRef.orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new NoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /*if (user.getPermission().equals("admin")) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                }
            }).attachToRecyclerView(recyclerView);
        }*/

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                //String id = documentSnapshot.getId();
                //documentSnapshot.getReference();
                goToNoteDescription(note);
            }
        });
    }

    private void goToNoteDescription(Note note) {
        Intent intent = new Intent(this, NoteDescriptionActivity.class);
        intent.putExtra("NOTE_TO_SEE", note);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }

    public interface FireStoreResults {
        public void onResultGet();
    }

    public void readData(final FireStoreResults a) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}
