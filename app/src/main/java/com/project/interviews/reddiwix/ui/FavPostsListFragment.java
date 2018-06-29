package com.project.interviews.reddiwix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.interviews.reddiwix.R;
import com.project.interviews.reddiwix.utils.FavoritesManager;
import com.project.interviews.reddiwix.datamodel.T3post;
import com.project.interviews.reddiwix.ui.adapters.PostsRVAdapter;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class FavPostsListFragment extends Fragment {
    //region Consts
    private static final String TAG = FavPostsListFragment.class.getSimpleName();
    //endregion

    //region Data Members
    private RecyclerView mRecyclerView;
    private PostsRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private TextView mEmptyListMsg;
    //endregion

    //region C'tor
    public FavPostsListFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Public Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_posts_list, container, false);

        initUi(root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.replaceData(FavoritesManager.getInstance(getActivity()).getFavPosts());
    }
    //endregion

    //region Private Methods
    private void initUi(@NotNull View root) {
        (root.findViewById(R.id.posts_swipe_refresh_layout)).setEnabled(false);

        mEmptyListMsg = root.findViewById(R.id.post_empty_list);
        mRecyclerView = root.findViewById(R.id.posts_recycler_view);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // improve performance , content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new PostsRVAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PostsRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(T3post item) {
                Intent openPostViewIntent = new Intent(getActivity(), PostWebViewActivity.class);
                openPostViewIntent.putExtra(T3post.POST_ITEM, item);
                startActivity(openPostViewIntent);
            }
        });
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mAdapter.getItemCount() == 0)
                {
                    mEmptyListMsg.setVisibility(View.VISIBLE);
                }
                else
                {
                    mEmptyListMsg.setVisibility(View.GONE);
                }
            }
        });
    }
    //endregion
}
