package com.evan.parknbark.map_profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.profile.Profile;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MapProfileBottomSheetDialog extends BottomSheetDialogFragment {
    private ArrayList<Profile> profiles;
    private Marker marker;
    private BottomSheetListener mListener;

    private final String CHECK_IN;

    public MapProfileBottomSheetDialog(ArrayList<Profile> profiles, Marker marker, String CHECK_IN) {
        this.CHECK_IN = CHECK_IN;
        this.profiles = profiles;
        this.marker = marker;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MapProfileAdapter mAdapter = new MapProfileAdapter(profiles);
        View v = inflater.inflate(R.layout.map_profiles_bottom_sheet, container, false);
        RecyclerView mRecyclerView = v.findViewById(R.id.recycler_profile);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MapProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                displayProfileDetail(profiles.get(position));
            }
        });

        Button buttonCheckIn = v.findViewById(R.id.button_check_in);
        buttonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClickedInsideBottomSheet();
                dismiss();
            }
        });
        Button buttonCheckOut = v.findViewById(R.id.button_check_out);
        buttonCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClickedInsideBottomSheet();
                dismiss();
            }
        });

        if(marker.getSnippet().equals(CHECK_IN)) {
            buttonCheckIn.setVisibility(View.VISIBLE);
            buttonCheckOut.setVisibility(View.INVISIBLE);
        }
        else{
            buttonCheckIn.setVisibility(View.INVISIBLE);
            buttonCheckOut.setVisibility(View.VISIBLE);
        }
        TextView textViewParkName = v.findViewById(R.id.text_view_park_name);

        textViewParkName.setText(marker.getTitle());
        return v;
    }

    public interface BottomSheetListener {
        void onButtonClickedInsideBottomSheet();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    private void displayProfileDetail(Profile profile){
        Dialog profileDialog = new Dialog(getContext(), R.style.AppTheme_NoActionBar);
        profileDialog.setContentView(R.layout.profile_details);
        profileDialog.setCanceledOnTouchOutside(true);

        ImageView imageViewProfilePicture = profileDialog.findViewById(R.id.image_view_profile_picture);
        TextView textViewDogName = profileDialog.findViewById(R.id.text_view_dog_name);
        TextView textViewDogBreed = profileDialog.findViewById(R.id.text_view_dog_breed);
        TextView textViewDogAge = profileDialog.findViewById(R.id.text_view_dog_age);

        if(profile.getProfilePicture() != null)
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
        lp.dimAmount= 0.7f;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        profileDialog.getWindow().setAttributes(lp);
        profileDialog.show();
    }
}