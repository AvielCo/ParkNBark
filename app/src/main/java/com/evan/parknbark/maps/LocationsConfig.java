package com.evan.parknbark.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.evan.parknbark.R;
import java.util.List;

public class LocationsConfig {
    /**
     * gets the context and inputs each item from the db into the view
     */
    private Context mContext;
    private ParkAdapter mParkAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Park> parks){
        mContext = context;
        mParkAdapter = new ParkAdapter(parks);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mParkAdapter);
    }
    class ParkItemView extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mStreet;

        public ParkItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.parks_items_lists ,parent, false));

            mName = (TextView) itemView.findViewById(R.id.park_name);
            mStreet = (TextView) itemView.findViewById(R.id.street);
        }

        public void bind(Park park){
           mName.setText(park.getName());
           mStreet.setText(String.valueOf(park.getStreet()));
        }

    }
    class ParkAdapter extends RecyclerView.Adapter<ParkItemView>{
        private List<Park> mParkList;

        public ParkAdapter(List<Park> mParkList) {
            this.mParkList = mParkList;
        }

        @NonNull
        @Override
        public ParkItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ParkItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ParkItemView holder, int position) {
            /**
             * binding each value from db into the proper view
             */
            holder.bind(mParkList.get(position));
        }

        @Override
        public int getItemCount() {
            return mParkList.size();
        }
    }
}
