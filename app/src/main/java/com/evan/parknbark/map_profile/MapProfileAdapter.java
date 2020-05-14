package com.evan.parknbark.map_profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evan.parknbark.R;
import com.evan.parknbark.map_profile.profile.Profile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MapProfileAdapter extends RecyclerView.Adapter<MapProfileAdapter.ProfileViewHolder> {
    private ArrayList<Profile> mProfileList;
    private OnItemClickListener mListener;

    public MapProfileAdapter(ArrayList<Profile> profiles) {
        mProfileList = profiles;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewFirstName, mTextViewLastName;

        ProfileViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_profile_picture);
            mTextViewFirstName = itemView.findViewById(R.id.textView_user_firstName);
            mTextViewLastName = itemView.findViewById(R.id.textView_user_lastName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            listener.onItemClick(pos);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile item = mProfileList.get(position);
        holder.mTextViewFirstName.setText(item.getFirstName());
        holder.mTextViewLastName.setText(item.getLastName());
        if(item.getProfilePicture() != null)
            Picasso.get().load(item.getProfilePicture()).into(holder.mImageView);
        //TODO: get image from db
        //holder.mImageView
    }

    @Override
    public int getItemCount() {
        return mProfileList.size();
    }
}
