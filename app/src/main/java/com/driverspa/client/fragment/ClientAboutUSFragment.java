package com.driverspa.client.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AboutUSRequestEvent;
import com.driverspa.util.otto.ws.AboutUSResponseEvent;
import butterknife.BindView;

public class ClientAboutUSFragment extends ClientBaseFragment {
    private String aboutUs = UserPreferences.getAboutUs(BA.getContext());

    public interface ActivityActions {
    }

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.cover_view)
    View coverView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_about_us, container, false);
        ButterKnife.bind(this,view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void performClick() {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","info@washme.kz", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        }, "btnEmail");

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void performClick() {
                String url = "http://www.washme.kz";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }, "btnWebSite");

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void performClick() {
                String url = "https://play.google.com/store/apps/details?id=com.driverspa";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }, "btnPlayMarket");

        // Set the client BEFORE loading so onPageFinished reliably hides the cover.
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                coverView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                try{
                }catch (Exception e) {
                    // TODO: handle exception
                }
            }

        });

        renderAboutUs();

        // Cache may be empty (initial fetch at splash failed/hasn't run yet) —
        // fetch it now; onAboutUSReceived() re-renders when it arrives.
        if (TextUtils.isEmpty(aboutUs)) {
            BA.getEventBus().post(new AboutUSRequestEvent());
        }
        return view;

    }

    /**
     * Renders the cached About Us HTML. Uses loadDataWithBaseURL (not loadData):
     * the HTML is full of '#' hex colors, and plain loadData treats '#'/'%' as
     * data-URL syntax, truncating the document at the first '#' and leaving the
     * screen blank.
     */
    private void renderAboutUs() {
        if (TextUtils.isEmpty(aboutUs)) {
            return;
        }
        webView.loadDataWithBaseURL(null, aboutUs, "text/html", "utf-8", null);
    }

    @Subscribe
    public void onAboutUSReceived(AboutUSResponseEvent event) {
        if (event != null && event.getData() != null && event.getData().getResponse() != null
                && !TextUtils.isEmpty(event.getData().getResponse().getAboutUS())) {
            aboutUs = event.getData().getResponse().getAboutUS();
            renderAboutUs();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        BA.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BA.getEventBus().unregister(this);
    }
}
