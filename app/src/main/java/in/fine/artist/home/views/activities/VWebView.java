package in.fine.artist.home.views.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.fine.artist.home.R;
import in.fine.artist.home.utils.networking.UploadManager;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class VWebView extends BaseActivity {

    private String mUrl;
    private String mTitle;
    private boolean loadingFinished = true;
    private boolean redirect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get("title") != null) {
                mTitle = bundle.getString("title");
            }

            if (bundle.get("url") != null) {
                mUrl = bundle.getString("url");
            }
        }

        if (mUrl != null && mUrl.contains("expertise")) {
            mUrl = mUrl + "?src=mob";
        }

        setUpActionBar();
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        findViewById(R.id.webView).setVisibility(View.GONE);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.loadUrl(mUrl, UploadManager.getInstance().getFeatureListHeaders());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    findViewById(R.id.loader).setVisibility(View.GONE);
                    findViewById(R.id.webView).setVisibility(View.VISIBLE);

                } else {
                    redirect = false;
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            }
        });
    }

    private void setUpActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle);
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

}