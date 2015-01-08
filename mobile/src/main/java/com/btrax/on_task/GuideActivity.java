package com.btrax.on_task;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crittercism.app.Crittercism;
import com.flurry.android.FlurryAgent;

public class GuideActivity extends BaseActivity {

    private WebView mWebView;

    private final static String TAG = GuideActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Crittercism.initialize(getApplicationContext(), AppConsts.CRITTERCISM_APP_ID);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new GuideWebViewClient());
        setWebSettings();

        mWebView.loadUrl(AppConsts.GUIDE_URL);
        FlurryAgent.logEvent("guide page open");

    }

    private void setWebSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class GuideWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // open only GUIDE PAGE in webvidw
            if (url.equals(AppConsts.GUIDE_URL)) {
                return false;
            }

            // launch browser
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
