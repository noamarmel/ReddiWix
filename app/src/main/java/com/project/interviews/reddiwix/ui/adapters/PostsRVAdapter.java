package com.project.interviews.reddiwix.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.interviews.reddiwix.R;
import com.project.interviews.reddiwix.datamodel.T3post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsRVAdapter extends RecyclerView.Adapter<PostsRVAdapter.PostViewHolder> implements Filterable {

    //region Data Members
    private ArrayList<T3post> mData = new ArrayList<>();
    private ArrayList<T3post> mFilteredData = new ArrayList<>();
    //endregion

    //region Data Members - Callback
    private OnItemClickListener mItemClickListener = null;
    //endregion

    //region C'tor
    public PostsRVAdapter() {

    }

    public PostsRVAdapter(ArrayList<T3post> data) {
        mData.addAll(data);
        mFilteredData.addAll(data);
    }
    //endregion

    //region Public Methods
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate the custom view into new ViewHolder
        PostViewHolder holder = new PostViewHolder(inflater.inflate(R.layout.recyclerview_post_layout, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final T3post currPost = mFilteredData.get(position);
        holder.postTitle.setText(currPost.getTitle());
        if (TextUtils.isEmpty(currPost.getThumbnail())) {
            Picasso.get().cancelRequest(holder.postThumbnail);
            holder.postThumbnail.setImageResource(R.drawable.thumbnail_placeholder);
        } else {
            Picasso.get().load(currPost.getThumbnail()).placeholder(R.drawable.thumbnail_placeholder).error(R.drawable.thumbnail_placeholder).fit().into(holder.postThumbnail);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(currPost);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    public void insertNewData(ArrayList<T3post> newData) {
        //TODO add handling for adding data when filter is applied
        int preInsertDataSize = mData.size();
        mData.addAll(newData);
        mFilteredData.addAll(newData);
        this.notifyItemRangeInserted(preInsertDataSize, newData.size());
    }

    public void replaceData(ArrayList<T3post> newData) {
        mData.clear();
        mFilteredData.clear();
        mData.addAll(newData);
        mFilteredData.addAll(newData);
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterString = charSequence.toString();
                ArrayList<T3post> filteredList = new ArrayList<>();
                if (filterString.isEmpty()) {
                    filteredList.addAll(mData);
                } else {

                    for (T3post post : mData) {
                        if (post.getTitle().toLowerCase().contains(filterString.toLowerCase())) {
                            filteredList.add(post);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredData.clear();
                mFilteredData.addAll((ArrayList<T3post>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }
    //endregion

    //region Custom Classes / interfaces
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postTitle;
        public ImageView postThumbnail;

        public PostViewHolder(View itemView) {
            super(itemView);

            postThumbnail = (ImageView) itemView.findViewById(R.id.rv_post_cell_thumbnail);
            postTitle = (TextView) itemView.findViewById(R.id.rv_post_cell_title);

            postThumbnail.setClipToOutline(true);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(T3post item);
    }
    //endregion
}
