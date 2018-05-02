package com.mta.gpspunchcard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mta.gpspunchcard.model.GeofenceEvent;

import java.util.ArrayList;
import java.util.List;

class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {
        private List<GeofenceEvent> mDataset;

        // Provide a suitable constructor (depends on the kind of dataset)
    public StatsAdapter(ArrayList<GeofenceEvent> data) {
            mDataset = data;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
            // create a new view
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stats_row, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.setCurrentItem(mDataset.get(position));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    public void setData(List<GeofenceEvent> data) {
        this.mDataset = data;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder  {
            // each data item is just a string in this case
            public TextView mInTv;
            public TextView mOutTv;
            private GeofenceEvent currentItem;

            public ViewHolder(ViewGroup v) {
                super(v);
                mInTv = v.findViewById(R.id.entrance);
                mOutTv = v.findViewById(R.id.exit);
            }

            public void setCurrentItem(GeofenceEvent currentItem) {
                this.currentItem = currentItem;
                mInTv.setText(currentItem.getInTimeString());
                mOutTv.setText(currentItem.getOutTimeString());
            }
        }

//        /**
//         * LiveData is allowed to be declared static without danger of memory leak
//         * It actually holds a static reference to the activity inside
//         * But it's lifecycle aware, so it does all the work for us (keeping a weak reference,
//         * replacing it when a new activity is created), so the new activity gets the most current data.
//         *
//         * -au
//         */
//        private static MutableLiveData<GeofenceEvent> sLiveCurrentItem;
//
//        public LiveData<GeofenceEvent> getCurrentItemLive() {
//            if (sLiveCurrentItem == null) {
//                sLiveCurrentItem = new MutableLiveData<>();
//            }
//            return sLiveCurrentItem;
//        }

    }
