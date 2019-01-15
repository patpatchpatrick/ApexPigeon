package io.github.patpatchpatrick.alphapigeon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.resources.PlayServices;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements PlayServices {

	private GameHelper gameHelper;
	private static final String leaderboard = "CgkIyYyG7qMKEAIQAQ";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useCompass = false;
		initialize(new AlphaPigeon(this), config);

		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(true);


		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInFailed() {
			}

			@Override
			public void onSignInSucceeded() {
			}
		};

		gameHelper.setup(gameHelperListener);

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
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void signIn() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					gameHelper.beginUserInitiatedSignIn();

				}
			});
		} catch (Exception e) {

		}
	}

	@Override
	public void signOut() {

		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					gameHelper.signOut();
				}
			});
		} catch (Exception e) {

		}

	}

	@Override
	public void rateGame() {

		String str = "Your PlayStore Link";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));

	}

	@Override
	public void unlockAchievement(String str) {

		Games.Achievements.unlock(gameHelper.getApiClient(), str);

	}

	@Override
	public void submitScore(String LeaderBoard, int highScore) {

		if (isSignedIn()) {

			Games.Leaderboards.submitScore(gameHelper.getApiClient(), LeaderBoard, highScore);
		}
		else{
			System.out.println(" Not signin Yet ");
		}

	}

	@Override
	public void submitLevel(int highLevel) {

		if (isSignedIn()) {
			Games.Leaderboards.submitScore(gameHelper.getApiClient(),leaderboard, highLevel);
		}

	}

	@Override
	public void showAchievement() {

		if (isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 1);
		} else {
			signIn();
		}

	}

	@Override
	public void showScore(String LeaderBoard) {

		if (isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), leaderboard), 1);
		} else {
			signIn();
		}

	}

	@Override
	public void showLevel() {

		if (isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), leaderboard), 1);
		} else {
			signIn();
		}

	}

	@Override
	public boolean isSignedIn() {
		return false;
	}
}
