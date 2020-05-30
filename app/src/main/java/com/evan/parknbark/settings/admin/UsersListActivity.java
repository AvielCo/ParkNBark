package com.evan.parknbark.settings.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UsersListActivity extends BaseActivity {
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
                switch (childPosition) {
                    case 0:
                        //watch profile
                        break;
                    case 1:
                        final UserItem user = userList.get(groupPosition);
                        if (!user.isBanned()) {
                            Bundle b = new Bundle();
                            b.putString("uid", userList.get(groupPosition).getUid());
                            b.putString("name", userList.get(groupPosition).getDisplayName());
                            b.putString("email", userList.get(groupPosition).getEmail());
                            startActivity(new Intent(UsersListActivity.this, BanActivity.class).putExtras(b));
                            finish();
                        } else {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                if (which == DialogInterface.BUTTON_POSITIVE)
                                    permitUser(user);
                                dialog.dismiss();
                            };
                            new AlertDialog.Builder(UsersListActivity.this)
                                    .setTitle(getString(R.string.permit_user))
                                    .setMessage(getString(R.string.permit_user_ask) + user.getDisplayName())
                                    .setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener)
                                    .setCancelable(false)
                                    .show();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void permitUser(UserItem userItem) {
        FieldPath bannedField = FieldPath.of("banned"),
                reasonField = FieldPath.of("banReason");
        db.collection("users").document(userItem.getUid())
                .update(bannedField, false, reasonField, "")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.info(getBaseContext(), getString(R.string.permit_user_success) + userItem.getDisplayName(), Toasty.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getBaseContext(), UsersListActivity.class));
                        } else {
                            showErrorToast();
                        }
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
                    boolean banned = (boolean) document.get("banned");
                    String uid = document.getId();
                    UserItem user = new UserItem(firstName + " " + lastName, email, uid, banned);
                    userList.add(user);
                }
                initOptionsHashMap();
            }
        });
    }

    private void initOptionsHashMap() {
        List<String> permittedUsersOptions = new ArrayList<>(); //add options to drop down of every user.
        permittedUsersOptions.add(getString(R.string.watch_profile));
        permittedUsersOptions.add(getString(R.string.ban_user));

        List<String> bannedUsersOptions = new ArrayList<>();
        bannedUsersOptions.add(getString(R.string.watch_profile));
        bannedUsersOptions.add(getString(R.string.permit_user));
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).isBanned())
                userItemOptions.put(userList.get(i), bannedUsersOptions);
            else userItemOptions.put(userList.get(i), permittedUsersOptions);

        }

        initAdapter();
    }

    private void initAdapter() {
        UsersListAdapter adapter = new UsersListAdapter(this, userItemOptions, userList);
        expandableListView.setAdapter(adapter);
    }
}
