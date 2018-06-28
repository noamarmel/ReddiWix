package com.project.interviews.reddiwix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.project.interviews.reddiwix.R;
import com.project.interviews.reddiwix.Utils.network.NetworkManager;
import com.project.interviews.reddiwix.datamodel.ListingData;
import com.project.interviews.reddiwix.datamodel.T3post;
import com.project.interviews.reddiwix.ui.adapters.PostsRVAdapter;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;

public class PostsListFragment extends Fragment {

    //region Consts
    private static final String TAG = PostsListFragment.class.getSimpleName();
    //endregion

    //region Data Members
    private String mNextListingPageName = null;
    private boolean mIsFetchRequestForPagination = false;
    private boolean mIsRefreshingData = false;

    private SearchView mSearchView;

    private SwipeRefreshLayout mSwipeToRefresh;
    private RecyclerView mRecyclerView;
    private PostsRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    //endregion

    //region Data Members - Callbacks
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!mIsRefreshingData) {
                mSearchView.setIconified(true);
                mIsRefreshingData = true;
                NetworkManager.getInstance().getMostRecentPosts(mNetworkResponseCallback);
            }
        }
    };

    private OnScrollListener mScrollDownListener = (new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!mIsFetchRequestForPagination) {
                super.onScrolled(recyclerView, dx, dy);
            }

            if (isScrollForPagination(dy)) {
                mIsRefreshingData = true;
                mIsFetchRequestForPagination = true;
                mSwipeToRefresh.setRefreshing(true);
                NetworkManager.getInstance().getPostsAfter(mNetworkResponseCallback, mNextListingPageName);
            }

        }
    });

    private NetworkManager.NetworkResponse mNetworkResponseCallback = new NetworkManager.NetworkResponse() {
        @Override
        public void onResponse(ListingData data) {
            mNextListingPageName = data.getNextListing();

            if (!mIsFetchRequestForPagination) {
                mAdapter.replaceData(data.getPosts());
            } else {
                mAdapter.insertNewData(data.getPosts());
                mIsFetchRequestForPagination = false;
            }

            mSwipeToRefresh.setRefreshing(false);
            mIsRefreshingData = false;
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

            Log.e(TAG, "Fetching new posts encountered an error", t);
            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            mSwipeToRefresh.setRefreshing(false);
            mIsRefreshingData = false;
        }
    };
    //endregion

    //region C'tor
    public PostsListFragment() {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeToRefresh.setRefreshing(true);
        mIsRefreshingData = true;
        NetworkManager.getInstance().getMostRecentPosts(mNetworkResponseCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posts_list, menu);
        setupSearchView(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean selectionConsumed;
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                mSwipeToRefresh.setRefreshing(true);
                mIsRefreshingData = true;
                NetworkManager.getInstance().getMostRecentPosts(mNetworkResponseCallback);
                selectionConsumed = true;
                break;
            }
            case R.id.action_search: {
                selectionConsumed = true;
                break;
            }
            default: {
                selectionConsumed = super.onOptionsItemSelected(item);
                break;
            }
        }

        return selectionConsumed;
    }
    //endregion

    //region Private Methods
    private void initUi(@NotNull View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.posts_recycler_view);
        mSwipeToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.posts_swipe_refresh_layout);

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

        mSwipeToRefresh.setOnRefreshListener(mRefreshListener);
        mRecyclerView.addOnScrollListener(mScrollDownListener);
    }

    private void setupSearchView(Menu menu) {
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }

    private boolean isScrollForPagination(int scrollDirection) {
        boolean isScrollForPagination = false;

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

        if (!mIsRefreshingData && !TextUtils.isEmpty(mNextListingPageName)) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= NetworkManager.FETCH_RESULTS_DEFAULT_LIMIT
                    && scrollDirection > 0) {
                isScrollForPagination = true;
            }
        }

        return isScrollForPagination;
    }
    //endregion
}