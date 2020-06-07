package com.evan.parknbark.settings.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.profile.Profile;
import com.evan.parknbark.utilities.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UsersListActivity extends BaseActivity implements ExpandableListView.OnChildClickListener {
    private ExpandableListView expandableListView;
    private List<UserItem> userList;
    private HashMap<UserItem, List<String>> userItemOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        expandableListView = findViewById(R.id.expandable_list_view_users);
        initData();
        expandableListView.setOnChildClickListener(this);
    }

    private void initData() {
        isFirebaseProcessRunning = true;
        userList = new ArrayList<>();
        userItemOptions = new HashMap<>();
        getUsersFromDB();
    }

    private void getUsersFromDB() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String firstName = (String) document.get("firstName");
                        String lastName = (String) document.get("lastName");
                        String email = (String) document.get("emailAddress");
                        boolean banned = (boolean) document.get("banned");
                        String uid = document.getId();
                        UserItem user = new UserItem(firstName + " " + lastName, email, uid, banned);
                        userList.add(user);
                    }
                    UsersListActivity.this.initOptionsHashMap();
                } else {
                    isFirebaseProcessRunning = false;
                    showErrorToast();
                }
            }
        });
    }

    private void initOptionsHashMap() {
        List<String> permittedUsersOptions = new ArrayList<>(); //options for unbanned users
        permittedUsersOptions.add(getString(R.string.watch_profile));
        permittedUsersOptions.add(getString(R.string.ban_user));

        List<String> bannedUsersOptions = new ArrayList<>(); //options for banned user
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
        isFirebaseProcessRunning = false;
    }

    private void getUserProfile(final UserItem user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Profile profile = document.toObject(Profile.class);
                    if (profile != null)
                        displayProfileDetail(profile);
                    else
                        Toasty.error(getApplicationContext(), user.getDisplayName() + getString(R.string.profile_not_set), Toasty.LENGTH_SHORT, true).show();
                } else {
                    showErrorToast();
                }
            }
        });
    }

    private void displayProfileDetail(final Profile profile) {
        Dialog profileDialog = new Dialog(UsersListActivity.this, R.style.AppTheme_NoActionBar);
        profileDialog.setContentView(R.layout.profile_details);
        profileDialog.setCanceledOnTouchOutside(true);

        ImageView imageViewProfilePicture = profileDialog.findViewById(R.id.image_view_profile_picture);
        TextView textViewDogName = profileDialog.findViewById(R.id.text_view_dog_name);
        TextView textViewDogBreed = profileDialog.findViewById(R.id.text_view_dog_breed);
        TextView textViewDogAge = profileDialog.findViewById(R.id.text_view_dog_age);

        if (profile.getProfilePicture() != null)
            Picasso.get().load(profile.getProfilePicture()).into(imageViewProfilePicture);
        textViewDogName.setText(profile.getDogName());
        textViewDogBreed.setText(profile.getDogBreed());
        textViewDogAge.setText(profile.getDogAge());

        //set dialog to center of parent
        Window window = profileDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        //set background to 30% brightness
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.7f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        profileDialog.getWindow().setAttributes(lp);
        profileDialog.show();
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
                        isFirebaseProcessRunning = false;
                    }
                });
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch (childPosition) {
            case 0:
                getUserProfile(userList.get(groupPosition));
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
                    if (isFirebaseProcessRunning) {
                        showInfoToast(R.string.please_wait);
                        return false;
                    }
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            isFirebaseProcessRunning = true;
                            permitUser(user);
                        }
                        dialog.dismiss();
                    };
                    new AlertDialog.Builder(UsersListActivity.this)
                            .setTitle(getString(R.string.permit_user))
                            .setMessage(getString(R.string.permit_user_ask) + user.getDisplayName())
                            .setPositiveButton(R.string.permit_text, dialogClickListener)
                            .setNegativeButton(R.string.cancel_text, dialogClickListener)
                            .setCancelable(false)
                            .show();
                }
                break;
        }
        return true;
    }
}
