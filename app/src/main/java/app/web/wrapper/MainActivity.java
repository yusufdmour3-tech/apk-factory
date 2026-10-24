package app.web.wrapper;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends Activity {
  private WebView web;
  private SwipeRefreshLayout swipe;
  private boolean fullscreen;

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    String o = getString(R.string.cfg_orientation);
    if (o.equals("Portrait")) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    else if (o.equals("Landscape")) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    fullscreen = getString(R.string.cfg_display).equals("Fullscreen");
    if (getResources().getBoolean(R.bool.cfg_keep_on)) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    web = new WebView(this);
    web.setBackgroundColor(Color.parseColor(getString(R.string.cfg_splash)));
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setDatabaseEnabled(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    s.setLoadWithOverviewMode(true);
    s.setUseWideViewPort(true);
    s.setAllowFileAccess(true);
    web.setWebChromeClient(new WebChromeClient());
    web.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView v, String u) { swipe.setRefreshing(false); }
    });

    swipe = new SwipeRefreshLayout(this);
    swipe.addView(web);
    swipe.setEnabled(getResources().getBoolean(R.bool.cfg_pull_refresh));
    swipe.setOnRefreshListener(() -> web.reload());
    setContentView(swipe);
    applyFullscreen();

    if (state != null) web.restoreState(state);
    else web.loadUrl(getString(R.string.cfg_url));
  }

  private void applyFullscreen() {
    if (!fullscreen) return;
    getWindow().getDecorView().setSystemUiVisibility(
      View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
      | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) { super.onWindowFocusChanged(hasFocus); if (hasFocus) applyFullscreen(); }
  @Override protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); web.saveState(out); }
  @Override public void onBackPressed() { if (web.canGoBack()) web.goBack(); else super.onBackPressed(); }
}
