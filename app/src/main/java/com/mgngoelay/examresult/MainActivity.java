package com.mgngoelay.examresult;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    CheckInternet checkInternet;
    SwipeRefreshLayout swipe;
    public static String ok = null;
    Typeface typeface;
    String message="";
    AdView adView;
    InterstitialAd interstitialAd;
    LinearLayout adLayout;

    FloatingActionMenu fmenu;
    FloatingActionButton share,exit,about,refresh;
    String download_link = null;
    String mainSite = "https://mmrexamresult.blogspot.com/";
    String HOST = "mmrexamresult.blogspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fmenu = findViewById(R.id.fmenu);

        AudienceNetworkAds.initialize(this);
        //Banner
        adLayout = findViewById(R.id.adView);
        adView = new AdView(this,Constants.getBanner(), AdSize.BANNER_HEIGHT_50);
        adLayout.addView(adView);
        adView.loadAd();

        //Interstitial
        interstitialAd = new InterstitialAd(this,Constants.getInterstitial());
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (!interstitialAd.isAdLoaded()){
                    interstitialAd.loadAd();
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (!interstitialAd.isAdLoaded()){
                    interstitialAd.loadAd();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        interstitialAd.loadAd();

        typeface = Typeface.createFromAsset(getAssets(),"mm.ttf");
        swipe = findViewById(R.id.swipe);
        checkInternet = new CheckInternet(this);
        webView = findViewById(R.id.webView);
        setDesktopMode(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavascriptInterface(),"xLol");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!checkInternet()){
                    webView.setVisibility(View.GONE);
                }else {
                    webView.setVisibility(View.VISIBLE);
                }
                if (url.startsWith("https://drive.google.com/viewerng/viewer?url=") || url.startsWith("https://drive.google.com/viewerng/viewer?embedded=true&url=")){
                    swipe.setEnabled(false);
                }else {
                    swipe.setEnabled(true);
                }

                swipe.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (!webView.getUrl().contains(HOST)) {
                    inject();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!checkInternet()){
                    swipe.setRefreshing(false);
                    return true;
                }
                swipe.setRefreshing(true);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimetype, long contentLength) {
                download_link = url;
                final boolean isPDF = mimetype.contains("pdf");
                swipe.setRefreshing(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setTitle(MyanmarString.get(isPDF ? "ရွေးချယ်ပါ" : "အသိပေးချက်",typeface,Color.BLACK))
                        .setMessage(MyanmarString.get(isPDF ? "Download လုပ်မလား\n" + "တိုက်ရိုက်ဖွင့်ကြည့်မလား ?" : URLUtil.guessFileName(url,contentDisposition,mimetype) +" ကို Download ပြုလုပ်မှာလား ?",typeface,Color.BLACK))
                        .setPositiveButton(isPDF ? "Download" : "လုပ်မယ်", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showAdz();
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(download_link)));
                            }
                        })
                        .setNegativeButton(isPDF ? "ဖွင့်မည်" : "မလုပ်ပါ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPDF) {
                                    if (checkInternet()) {
                                        swipe.setRefreshing(false);
                                        String readURL = "https://drive.google.com/viewerng/viewer?url=" + download_link;
                                        startActivityForResult(new Intent(MainActivity.this, ViewerActivity.class).putExtra("url", readURL), 101);
                                    }
                                }
                                showAdz();
                            }
                        });
                if (isPDF) {
                        builder.setNeutralButton("ဘာမှမလုပ်ပါ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showAdz();
                        }
                    });
                }
                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
                        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTypeface(typeface);
                    }
                });

                dialog.show();
            }
        });

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                swipe.setRefreshing(true);
            }
        });

        addMenu();

        if (checkInternet()) {
            webView.loadUrl(mainSite);
            swipe.setRefreshing(true);
        }
    }

    private void addMenu() {
        Animation to = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.jump_from_down);
        Animation down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.jump_to_down);

        share = new FloatingActionButton(this);
        share.setLabelText("Share");
        share.setImageDrawable(getResources().getDrawable(R.drawable.share));
        share.setButtonSize(FloatingActionButton.SIZE_MINI);
        share.setShowAnimation(to);
        share.setHideAnimation(down);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        fmenu.addMenuButton(share);

        refresh = new FloatingActionButton(this);
        refresh.setLabelText("Reload");
        refresh.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        refresh.setButtonSize(FloatingActionButton.SIZE_MINI);
        refresh.setShowAnimation(to);
        refresh.setHideAnimation(down);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternet()){
                    webView.reload();
                    swipe.setRefreshing(true);
                }
            }
        });
        fmenu.addMenuButton(refresh);

        about = new FloatingActionButton(this);
        about.setLabelText("About");
        about.setImageDrawable(getResources().getDrawable(R.drawable.help));
        about.setButtonSize(FloatingActionButton.SIZE_MINI);
        about.setShowAnimation(to);
        about.setHideAnimation(down);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbout();
            }
        });
        fmenu.addMenuButton(about);

        exit = new FloatingActionButton(this);
        exit.setLabelText("Exit");
        exit.setImageDrawable(getResources().getDrawable(R.drawable.exit));
        exit.setButtonSize(FloatingActionButton.SIZE_MINI);
        exit.setShowAnimation(to);
        exit.setHideAnimation(down);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letExit();
            }
        });
        fmenu.addMenuButton(exit);
    }

    class MyJavascriptInterface {

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showAds() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAdz();
                }
            });
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void openBrowser(final String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
                }
            });
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void closeApp() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void setHostName(final String hostName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HOST = hostName;
                }
            });
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void canExit(final boolean exit) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (exit){
                        letExit();
                    }
                }
            });
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void ok(String string){
            ok = string;
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showAlert(final String title, final String message){
            if (!MainActivity.this.message.equals(message)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(MyanmarString.get(title, typeface, Color.BLACK))
                                .setMessage(MyanmarString.get(message, typeface, Color.BLACK))
                                .setPositiveButton("ဟုတ်ပြီ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showAds();
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface d) {
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
                            }
                        });
                        dialog.show();
                    }
                });
            }
            MainActivity.this.message = message;
        }
    }

    private void inject() {
        if (ok!=null){
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('script');" +
                    "style.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + ok + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101){
            showAdz();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            webView.loadUrl("javascript:(function() {letExit();})()");
        }
    }

    private boolean checkInternet(){
        if (checkInternet.isInternetOn()){
            return true;
        }
        noInternet();
        return false;
    }


    private void noInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(MyanmarString.get("အသိပေးချက်", typeface, Color.BLACK))
                .setMessage(MyanmarString.get("အင်တာနက်လိုင်းရှိရန်လိုအပ်ပါသည်။", typeface, Color.BLACK))
                .setPositiveButton("ထပ်လုပ်မည်", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkInternet()){
                            webView.setVisibility(View.VISIBLE);
                            webView.reload();
                            swipe.setRefreshing(true);
                            showAdz();
                        }
                    }
                })
                .setNegativeButton("ပိတ်မည်", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAdz();
                        letExit();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
            }
        });
        dialog.show();
    }


    private void letExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(MyanmarString.get("အသိပေးချက်",typeface,Color.BLACK))
                .setMessage(MyanmarString.get("ထွက်တော့မှာလား ?",typeface,Color.BLACK))
                .setPositiveButton("ထွက်မည်", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("မထွက်သေးပါ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAdz();
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
            }
        });

        dialog.show();
    }


    private void showAbout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(MyanmarString.get("အသိပေးချက်!",typeface,Color.BLACK))
                .setMessage(MyanmarString.get("ယခု Application သည်\n" +
                        "ပညာရေးဝန်ကြီးဌာနမှ\n" +
                        "တရားဝင်ထုတ်ထားခြင်းမဟုတ်ပါ။\n" +
                        "မူရင်း Website ကို\nApplication တစ်ခုအနေနဲ့\n" +
                        "အသုံးပြုနိုင်အောင်\nဖန်တီးထားခြင်းသာဖြစ်ပါသည်။",typeface,Color.BLACK))
                .setPositiveButton("ဟုတ်ပြီ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAdz();
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
            }
        });

        dialog.show();
    }

    private void share(){
        String message = "တကၠသိုလ္ဝင္တန္းစာေမးပြဲကို\n" +
                "ဖုန္းထဲကေနအလြယ္တကူၾကည့္ႏိုင္မယ့္ေဆာ့ဝဲေလးပါ။\n" +
                "Play Store ကေနေဒါင္းယူရန္ => http://bit.ly/2Fqvhqw\n" +
                "APK တိုက္႐ိုက္ေဒါင္းယူရန္ => http://bit.ly/2HOoCcE\n" +
                "#mmExamResult #ExamResult #ေအာင္စာရင္း\n\n" +
                "#Unicode\n" +
                "တက္ကသိုလ်ဝင်တန်းစာမေးပွဲကို\n" +
                "ဖုန်းထဲကနေအလွယ်တကူကြည့်နိုင်မယ့်ဆော့ဝဲလေးပါ။\n" +
                "Play Store ကနေဒေါင်းယူရန် => http://bit.ly/2Fqvhqw\n" +
                "APK တိုက်ရိုက်ဒေါင်းယူရန် => http://bit.ly/2HOoCcE\n" +
                "#mmExamResult #ExamResult #အောင်စာရင်း";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,message);
        startActivity(Intent.createChooser(intent,"Share App..."));
    }

    public void setDesktopMode(boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")"));
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, androidOSString + "; "+getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        System.out.println(newUserAgent);
        webView.getSettings().setUserAgentString(newUserAgent);
    }

    private void showAdz(){
        if (interstitialAd.isAdLoaded()){
            interstitialAd.show();
        }else interstitialAd.loadAd();
    }
}
