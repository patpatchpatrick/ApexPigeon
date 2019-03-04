package io.github.patpatchpatrick.alphapigeon;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.github.patpatchpatrick.alphapigeon.Screens.HighScoreScreen;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseAndPreferenceManager;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.google.android.gms.ads.AdRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;



public class AndroidLauncher extends AndroidApplication implements PlayServices, DatabaseAndPreferenceManager {

    // Google Play Services Variables

    private int RC_SIGN_IN = 1;


    // -- Leaderboard variables
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final String leaderboard = "CgkIyYyG7qMKEAIQAQ";

    // Interface to send callbacks back to libgdx from mobile device
    private MobileCallbacks mobileCallbacks;

    //Database tools
    protected static ContentResolver contentResolver;

    //Google ads
    private final int GET_TOP_SCORES_FROM_NETWORK = 3;
    private final int SHOW_OR_LOAD_INTERSTITIAL_ADS = 2;
    private final int SHOW_BANNER_ADS = 1;
    private final int HIDE_BANNER_ADS = 0;

    //Google Billing
    private InAppBilling mInAppBilling;
    private final int PURCHASE_AD_REMOVAL = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        //Set up config for Android App
        config.useCompass = false;
        config.useImmersiveMode = true;
        useImmersiveMode(true);

        //Initialize In App Billing
        mInAppBilling = new InAppBilling(this, this);

        // Create the layout for the game/ads to share
        RelativeLayout layout = new RelativeLayout(this);

        // Set up Android for app initialization
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create view for libgdx
        View gameView = initializeForView(new AlphaPigeon(this, this), config);

        // Add the libgdx view
        layout.addView(gameView);

        // Add the Ad view
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


        // Hook it all up
        setContentView(layout);

        // Get the content resolver for database
        contentResolver = getContentResolver();

    }

    protected Handler handler = new Handler() {

        //Handler to handle enabling and disabling ads on the UI thread
        @Override
        public void handleMessage(Message msg) {

        }


    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    protected void onResume() {
        super.onResume();

        //Callback to libgdx game to let it know that the android app has resumed
        //The game will resize the screen appropriately, if this callback isn't used then there are
        //issues with the game screen scaling incorrectly and being stretched
        if (mobileCallbacks != null) {
            mobileCallbacks.appResumed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void showBannerAds(boolean show) {
        //Send message to handler to show or hide banner ads
        handler.sendEmptyMessage(show ? SHOW_BANNER_ADS : HIDE_BANNER_ADS);
    }

    @Override
    public void showOrLoadInterstitialAd() {
        //Send message to handler to show or load interstitial ads
        handler.sendEmptyMessage(SHOW_OR_LOAD_INTERSTITIAL_ADS);
    }

    @Override
    public void purchaseAdRemoval() {
        //Send message to handler to purchase ad removal
        handler.sendEmptyMessage(PURCHASE_AD_REMOVAL);
    }

    @Override
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks) {
        this.mobileCallbacks = mobileCallbacks;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void insert(float highScore, float lastScore) {

        //Insert a high score into the local sqlite database
        DatabaseHandler.insert(this, highScore, lastScore);

    }

    @Override
    public void queryHighScores() {

        //Query high scores from local sqlite database
        DatabaseHandler.query(mobileCallbacks);
    }

    @Override
    public float getHighScore() {
        return DatabaseHandler.getHighScore(this);
    }

    @Override
    public float getTotalNumGames() {
        return DatabaseHandler.getTotalNumberOfGames(this);
    }


}
