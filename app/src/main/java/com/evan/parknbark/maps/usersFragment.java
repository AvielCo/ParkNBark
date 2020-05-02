package com.evan.parknbark.maps;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evan.parknbark.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class usersFragment extends Fragment {

    TextView usersTextView;

    public usersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_users, container, false);
        usersTextView = v.findViewById(R.id.users_Text_view);
        Bundle bundle = getArguments();
        String data = bundle.getString("users");
        usersTextView.setText(data);
        return v;
    }
}
