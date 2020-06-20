package com.mgngoelay.examresult;

import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewerActivity extends AppCompatActivity {
    boolean SUCCESS = false;
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        if (!getIntent().hasExtra("url")){
            finish();
        }

        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setEnabled(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                System.out.println("onConsoleMessage => "+consoleMessage.message());
                if (!consoleMessage.message().contains("Uncaught TypeError:")){
                    SUCCESS = true;
                    swipeRefreshLayout.setRefreshing(false);
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("onPageFinished => "+url);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!SUCCESS){
                            System.out.println("Reload");
                            swipeRefreshLayout.setRefreshing(true);
                            webView.loadUrl(getIntent().getStringExtra("url"));
                        }else {
                            System.out.println("Loaded");swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 5000);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                swipeRefreshLayout.setRefreshing(true);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(true);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                inject();
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
        swipeRefreshLayout.setRefreshing(true);
    }

    private void inject() {
        if (MainActivity.ok!=null){
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('script');" +
                    "style.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + MainActivity.ok + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        }
    }
}
