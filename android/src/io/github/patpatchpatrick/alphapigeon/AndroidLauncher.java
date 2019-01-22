package io.github.patpatchpatrick.alphapigeon;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.DatabaseManager;
import io.github.patpatchpatrick.alphapigeon.resources.MobileCallbacks;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_PUBLIC;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_ALL_TIME;

public class AndroidLauncher extends AndroidApplication implements PlayServices, DatabaseManager {

    // Google Play Services Variables
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    private GoogleSignInAccount signedInAccount;
    // -- Leaderboard variables
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final String leaderboard = "CgkIyYyG7qMKEAIQAQ";
    private String playerCenteredScoresString = ""; //String of high scores (player centered)

    // Interface to send callbacks back to libgdx from mobile device
    private MobileCallbacks mobileCallbacks;

    //Database tools
    protected static ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useCompass = false;
        initialize(new AlphaPigeon(this, this), config);
        contentResolver = getContentResolver();



        // Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

    }

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
                            int exceptionStatusCode  = signInFailException.getStatusCode();
                            if (exceptionStatusCode == SIGN_IN_REQUIRED){
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
                int  failCode = result.getStatus().getStatusCode();
                Log.d("FAILCODE", "" + failCode);
                Log.d("RESULT",  "" + result.getStatus());
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
        if (!isSignedIn()){
        signInSilently();}
    }

    @Override
    protected void onStart() {
        super.onStart();
        //gameHelper.onStart(this); // You will be logged in to google play services as soon as you open app , i,e on start
    }

    @Override
    public void onStartMethod() {
        super.onStart();
        //gameHelper.onStart(this); // This is similar method but I am using this if i wish to login to google play services
        // from any other screen and not from splash screen of my code
    }

    @Override
    public void signIn() {
        signInSilently();
        if (signedInAccount != null){
        GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, signedInAccount);
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());}
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
    public void rateGame() {

    }

    @Override
    public void unlockAchievement(String str) {

    }

    @Override
    public void submitScore(long highScore) {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore((leaderboard), highScore);

    }

    @Override
    public void submitLevel(int highLevel) {

    }

    @Override
    public void showAchievement() {

    }

    @Override
    public void showScore(String LeaderBoard) {

    }

    @Override
    public void showLevel() {

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

        //Get a list of player centered high scores and return it in string format

        playerCenteredScoresString = "";

        Task<AnnotatedData<LeaderboardsClient.LeaderboardScores>> playerCenteredScoresTask =
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .loadPlayerCenteredScores(leaderboard, TIME_SPAN_ALL_TIME, COLLECTION_PUBLIC, 10, false );

        playerCenteredScoresTask.addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores> leaderboardScoresAnnotatedData) {

                LeaderboardsClient.LeaderboardScores leaderboardScores = leaderboardScoresAnnotatedData.get();
                LeaderboardScoreBuffer leaderboardScoreBuffer = leaderboardScores.getScores();
                int count = 0;
                if (leaderboardScoreBuffer != null) {
                    count = leaderboardScoreBuffer.getCount();
                }
                for (int i = 0; i < count; i++) {
                    LeaderboardScore score = leaderboardScoreBuffer.get(i);
                    playerCenteredScoresString += "Name: " + score.getScoreHolderDisplayName() +
                            " Rank: " + score.getDisplayRank() + " Score: " + score.getDisplayScore();
                }
                mobileCallbacks.setPlayerCenteredHighScores(playerCenteredScoresString);
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
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks) {
        this.mobileCallbacks = mobileCallbacks;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //gameHelper.onStop();
    }


    @Override
    public void insert(float highScore,  float lastScore) {

        DatabaseHandler.insert(this, highScore, lastScore);

    }

    @Override
    public void query() {

        DatabaseHandler.query();
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

}
