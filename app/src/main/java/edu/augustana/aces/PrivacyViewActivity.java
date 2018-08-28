package edu.augustana.aces;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PrivacyViewActivity extends Activity {
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_view);

        wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setSupportMultipleWindows(true);
        wv.loadUrl("file:///android_asset/privacy_policy.html");

    }

    public void onBackPressed() {
        if (wv.isFocused() && wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
