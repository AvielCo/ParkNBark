package com.evan.parknbark.settings.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersListActivity extends BaseActivity {
    private UsersListAdapter adapter;
    private ExpandableListView expandableListView;
    private List<UserItem> userList;
    private HashMap<UserItem, List<String>> userItemOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        expandableListView = findViewById(R.id.expandable_list_view_users);
        initData();
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Bundle b = new Bundle();
                b.putString("uid",userList.get(groupPosition).getUid());
                b.putString("name",userList.get(groupPosition).getDisplayName());
                b.putString("email",userList.get(groupPosition).getEmail());
                startActivity(new Intent(UsersListActivity.this,BanActivity.class).putExtras(b));
                return false;
            }
        });
    }

    private void initData() {
        userList = new ArrayList<>();
        userItemOptions = new HashMap<>();
        getUsersFromDB();
    }

    private void getUsersFromDB() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String firstName = (String) document.get("firstName");
                    String lastName = (String) document.get("lastName");
                    String email = (String) document.get("emailAddress");
                    String uid = document.getId();
                    UserItem user = new UserItem(firstName + " " + lastName, email, uid);
                    userList.add(user);
                }
                initOptionsHashMap();
            }
        });
    }

    private void initOptionsHashMap() {
        List<String> options = new ArrayList<>(); //add options to drop down of every user.
        options.add(getString(R.string.ban_user)); //option 0 - ban user
        options.add(getString(R.string.watch_profile)); //option 1 - watch user profile
        for (int i = 0; i < userList.size(); i++) {
            userItemOptions.put(userList.get(i), options);
        }

        initAdapter();
    }

    private void initAdapter() {
        adapter = new UsersListAdapter(this, userItemOptions, userList);
        expandableListView.setAdapter(adapter);
    }
}
