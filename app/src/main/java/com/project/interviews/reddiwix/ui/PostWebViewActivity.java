package com.project.interviews.reddiwix.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.project.interviews.reddiwix.R;
import com.project.interviews.reddiwix.Utils.SharedPrefsManager;
import com.project.interviews.reddiwix.Utils.network.NetworkManager;
import com.project.interviews.reddiwix.datamodel.T3post;

public class PostWebViewActivity extends AppCompatActivity {

    //region Data Members
    private WebView mMainView;
    private T3post mPost;
    private FloatingActionButton favoriteFloatingActionButton;
    private boolean mIsPostFav = false;
    //endregion

    //region Protected / Public Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_post_web_view);
        initUiRefs();

        getPostFromIntent();

        setupFavButton();
        setupWebView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Private Methods
    private void initUiRefs() {
        mMainView = (WebView) findViewById(R.id.post_web_view);
        favoriteFloatingActionButton = (FloatingActionButton) findViewById(R.id.post_fav_btn);
    }

    private void getPostFromIntent() {
        mPost = getIntent().getParcelableExtra(T3post.POST_ITEM);
    }

    private void setupFavButton() {
        //set initial state of fav btn
        mIsPostFav = SharedPrefsManager.getInstance(this).isFavPost(mPost.getId());
        if (mIsPostFav) {
            favoriteFloatingActionButton.setSelected(true);
        } else {
            favoriteFloatingActionButton.setSelected(false);
        }

        favoriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteFloatingActionButton.setSelected(!favoriteFloatingActionButton.isSelected());
                if (mIsPostFav) {
                    mIsPostFav = false;
                    SharedPrefsManager.getInstance(PostWebViewActivity.this).removeFavPost(mPost.getId());
                } else {
                    mIsPostFav = true;
                    SharedPrefsManager.getInstance(PostWebViewActivity.this).insertNewFavPost(mPost);
                }
            }
        });
    }

    private void setupWebView() {
        mMainView.getSettings().setJavaScriptEnabled(true);

        mMainView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(PostWebViewActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        mMainView.loadUrl(NetworkManager.CLOUD_API_BASE_URL + mPost.getPermalink());
    }
    //endregion
}
