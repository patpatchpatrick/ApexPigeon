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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_PUBLIC;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_ALL_TIME;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_DAILY;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_WEEKLY;

public class AndroidLauncher extends AndroidApplication implements PlayServices, DatabaseAndPreferenceManager {

    // Google Play Services Variables
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    private GoogleSignInAccount signedInAccount;

    // -- Leaderboard variables
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final String leaderboard = "CgkIyYyG7qMKEAIQAQ";

    // Interface to send callbacks back to libgdx from mobile device
    private MobileCallbacks mobileCallbacks;

    //Database tools
    protected static ContentResolver contentResolver;

    //Google ads
    private AdView adView;
    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        //Set up config for Android App
        config.useCompass = false;
        config.useImmersiveMode = true;
        useImmersiveMode(true);

        // Create the layout for the game/ads to share
        RelativeLayout layout = new RelativeLayout(this);

        // Set up Android for app initialization
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create view for libgdx
        View gameView = initializeForView(new AlphaPigeon(this, this), config);

        // Initialize mobile ads
        MobileAds.initialize(this, getString(R.string.app_ad_id));

        // Create and setup the AdMob view for the MainMenu Screen
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

        //TODO replace with real ad!
        adView.setAdUnitId(getString(R.string.app_ad_testad_id)); // Initialize main menu banner ad

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Add the libgdx view
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        layout.addView(adView, adParams);

        // Hook it all up
        setContentView(layout);

        // Get the content resolver for database
        contentResolver = getContentResolver();

        // Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

    }

    protected Handler handler = new Handler()
    {

        //Handler to handle enabling and disabling ads on the UI thread
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    //Set ads to visible and resume ad processes
                    adView.setVisibility(View.VISIBLE);
                    adView.resume();
                    break;
                }
                case HIDE_ADS:
                {
                    //Set ads to invisible and pause ad processes
                    adView.setVisibility(View.GONE);
                    adView.pause();
                    break;
                }
            }
        }
    };


    private void signInSilently() {
        //GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
        // GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            signedInAccount = task.getResult();
                            Log.d("SILENTSIGNIN", "SUCCESSFUL");
                        } else {
                            // Player will need to sign-in explicitly using via UI if the silent sign-in fails
                            // with exception code of SIGN_IN_REQUIRED
                            ApiException signInFailException = (ApiException) task.getException();
                            int exceptionStatusCode = signInFailException.getStatusCode();
                            if (exceptionStatusCode == SIGN_IN_REQUIRED) {
                                startSignInIntent();
                            }
                        }
                    }
                });
    }

    private void startSignInIntent() {
        //Manually sign in if silent sign-in fails
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Activity result for startSignInIntent method
        //If signed in successfully, get signed in account, otherwise log result fail code
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                signedInAccount = result.getSignInAccount();
                Log.d("ManualSignIn", "Success");
            } else {
                int failCode = result.getStatus().getStatusCode();
                Log.d("FAILCODE", "" + failCode);
                Log.d("RESULT", "" + result.getStatus());
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //When the app resumes, sign back in
        if (!isSignedIn()) {
            signInSilently();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //gameHelper.onStart(this); // You will be logged in to google play services as soon as you open app , i,e on start
    }

    @Override
    public void signIn() {
        signInSilently();
        if (signedInAccount != null) {
            GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, signedInAccount);
            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());
        }
    }

    @Override
    public void signOut() {

        //Figure out when/if you need to allow the user to sign out

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                    }
                });

    }

    @Override
    public void submitScore(long highScore) {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore((leaderboard), highScore);

    }

    @Override
    public void showLeaderboard() {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getLeaderboardIntent(leaderboard)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });


    }

    @Override
    public void getPlayerCenteredScores() {

        //Get a list of player centered high scores and return it in ArrayList<String> format

        Task<AnnotatedData<LeaderboardsClient.LeaderboardScores>> playerCenteredScoresTask =
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .loadPlayerCenteredScores(leaderboard, TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC, 10, false);

        playerCenteredScoresTask.addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores> leaderboardScoresAnnotatedData) {

                LeaderboardsClient.LeaderboardScores leaderboardScores = leaderboardScoresAnnotatedData.get();
                LeaderboardScoreBuffer leaderboardScoreBuffer = leaderboardScores.getScores();
                int count = 0;
                if (leaderboardScoreBuffer != null) {
                    count = leaderboardScoreBuffer.getCount();
                }
                ArrayList<String> playerCenteredHighScores = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    LeaderboardScore score = leaderboardScoreBuffer.get(i);
                    String scoreString = "";
                    scoreString += "Name: " + score.getScoreHolderDisplayName() +
                            " Rank: " + score.getDisplayRank() + " Score: " + score.getDisplayScore();
                    playerCenteredHighScores.add(scoreString);
                }
                leaderboardScoreBuffer.release();
                mobileCallbacks.requestedHighScoresReceived(playerCenteredHighScores);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, getString(R.string.leaderboards_exception));
            }
        });


    }

    @Override
    public void getTopScores(int scoreType) {

        //Get a list of top high scores and return it in ArrayList<String> format

        int timeSpan;

        //Determine time span to look up scores based on score type selected by user
        switch (scoreType) {
            case HighScoreScreen.GLOBAL_BUTTON_TOP_DAY:
                timeSpan = TIME_SPAN_DAILY;
                break;
            case HighScoreScreen.GLOBAL_BUTTON_TOP_WEEK:
                timeSpan = TIME_SPAN_WEEKLY;
                break;
            case HighScoreScreen.GLOBAL_BUTTON_TOP_ALLTIME:
                timeSpan = TIME_SPAN_ALL_TIME;
                break;
            default:
                timeSpan = TIME_SPAN_ALL_TIME;
                break;
        }


        Task<AnnotatedData<LeaderboardsClient.LeaderboardScores>> topScoresTask =
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .loadTopScores(leaderboard, timeSpan, COLLECTION_PUBLIC, 20, false);

        topScoresTask.addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores> leaderboardScoresAnnotatedData) {

                LeaderboardsClient.LeaderboardScores leaderboardScores = leaderboardScoresAnnotatedData.get();
                LeaderboardScoreBuffer leaderboardScoreBuffer = leaderboardScores.getScores();
                int count = 0;
                if (leaderboardScoreBuffer != null) {
                    count = leaderboardScoreBuffer.getCount();
                }
                ArrayList<String> topHighScores = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    LeaderboardScore score = leaderboardScoreBuffer.get(i);
                    String scoreString = "";
                    scoreString += "Name: " + score.getScoreHolderDisplayName() +
                            " Rank: " + score.getDisplayRank() + " Score: " + score.getDisplayScore();
                    topHighScores.add(scoreString);
                }
                leaderboardScoreBuffer.release();
                //Send callback to mobile device with scores requested
                mobileCallbacks.requestedHighScoresReceived(topHighScores);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, getString(R.string.leaderboards_exception));
            }
        });


    }

    @Override
    public boolean isSignedIn() {
        //Return false if the last signed in account is null
        return GoogleSignIn.getLastSignedInAccount(this) != null;

    }

    @Override
    public void showAds(boolean show) {
        //Send message to handler to show or hide ads
        handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }

    @Override
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks) {
        this.mobileCallbacks = mobileCallbacks;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //gameHelper.onStop();
    }


    @Override
    public void insert(float highScore, float lastScore) {

        DatabaseHandler.insert(this, highScore, lastScore);

    }

    @Override
    public void queryHighScores() {

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

    private void handleException(Exception e, String details) {
        int status = 0;

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    @Override
    public void toggleMusicOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicSettingPreference = getResources().getString(R.string.music_setting_pref);
        sharedPreferences.edit().putBoolean(musicSettingPreference, isOn).commit();

    }

    @Override
    public void toggleGameSoundsOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameSoundsSettingPreference = getResources().getString(R.string.game_sounds_setting_pref);
        sharedPreferences.edit().putBoolean(gameSoundsSettingPreference, isOn).commit();

    }

    @Override
    public void toggleTouchControlsOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSettingPreference = getResources().getString(R.string.touch_setting_pref);
        sharedPreferences.edit().putBoolean(touchSettingPreference, isOn).commit();

    }

    @Override
    public void toggleAccelButtonOnOff(Boolean isOn) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSettingPreference = getResources().getString(R.string.accel_setting_pref);
        sharedPreferences.edit().putBoolean(accelSettingPreference, isOn).commit();

    }

    @Override
    public boolean isMusicOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicPreference = getResources().getString(R.string.music_setting_pref);
        return sharedPreferences.getBoolean(musicPreference, true);
    }

    @Override
    public boolean isGameSoundsOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameSoundsPreference = getResources().getString(R.string.game_sounds_setting_pref);
        return sharedPreferences.getBoolean(gameSoundsPreference, true);
    }

    @Override
    public boolean isTouchControlsOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchPreference = getResources().getString(R.string.touch_setting_pref);
        return sharedPreferences.getBoolean(touchPreference, true);
    }

    @Override
    public boolean isAccelButtonOn() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelPreference = getResources().getString(R.string.accel_setting_pref);
        return sharedPreferences.getBoolean(accelPreference, true);
    }

    @Override
    public void toggleMusicVolumeSlider(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicVolumeSettingPreference = getResources().getString(R.string.music_volume_setting_pref);
        sharedPreferences.edit().putFloat(musicVolumeSettingPreference, value).commit();

    }

    @Override
    public void toggleGameVolumeSlider(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameVolumeSettingPreference = getResources().getString(R.string.game_volume_setting_pref);
        sharedPreferences.edit().putFloat(gameVolumeSettingPreference, value).commit();

    }

    @Override
    public void toggleTouchSensitivity(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSensitivitySettingPreference = getResources().getString(R.string.touch_sensitivity_setting_pref);
        sharedPreferences.edit().putFloat(touchSensitivitySettingPreference, value).commit();

    }

    @Override
    public void toggleAccelSensitivity(float value) {

        // Update the settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSensitivitySettingPreference = getResources().getString(R.string.accel_sensitivity_setting_pref);
        sharedPreferences.edit().putFloat(accelSensitivitySettingPreference, value).commit();

    }

    @Override
    public float getMusicVolumeSliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String musicVolPreference = getResources().getString(R.string.music_volume_setting_pref);
        return sharedPreferences.getFloat(musicVolPreference, 0.5f);
    }

    @Override
    public float getGameVolumeSliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gameVolPreference = getResources().getString(R.string.game_volume_setting_pref);
        return sharedPreferences.getFloat(gameVolPreference, 0.5f);
    }

    @Override
    public float getTouchSensitivitySliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String touchSensitivityPreference = getResources().getString(R.string.touch_sensitivity_setting_pref);
        return sharedPreferences.getFloat(touchSensitivityPreference, 0.5f);
    }

    @Override
    public float getAccelSensitivitySliderValue() {
        //Return settings preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accelSensitivityPreference = getResources().getString(R.string.accel_sensitivity_setting_pref);
        return sharedPreferences.getFloat(accelSensitivityPreference, 0.5f);
    }
}
