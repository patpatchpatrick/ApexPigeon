package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;

public class AppleGameCenterManager {

    public IGameServiceListener gsListener;
    public IGameServiceClient gsClient;
    public AlphaPigeon game;
    public IFetchLeaderBoardEntriesResponseListener gsLeaderboardListener;

    //Handle calls to Apple Game Center
    public AppleGameCenterManager(AlphaPigeon game, IGameServiceClient client){
        this.game = game;
        this.gsClient = client;
        gsLeaderboardListener = new IFetchLeaderBoardEntriesResponseListener() {
            @Override
            public void onLeaderBoardResponse(Array<ILeaderBoardEntry> leaderBoard) {
                for(int i = 0; i < leaderBoard.size; i++){
                    Gdx.app.log(leaderBoard.get(i).getUserId(), leaderBoard.get(i).getFormattedValue());
                }
            }
        };

        gsListener = new IGameServiceListener() {
            @Override
            public void gsOnSessionActive() {
                Gdx.app.log("SESSION: ", "ACTIVE");
                gsClient.logIn();
                Gdx.app.log("LOGGED IN: ",  gsClient.getPlayerDisplayName());
                gsClient.isSessionActive();


            }

            @Override
            public void gsOnSessionInactive() {
                Gdx.app.log("SESSION: ", "INACTIVE");

            }

            @Override
            public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
                Gdx.app.log("SESSION ERROR: ", msg);
            }
        };

        // for getting callbacks from the client
        gsClient.setListener(gsListener);

        // establish a connection to the game service without error messages or login screens
        gsClient.resumeSession();


    }

    public void submitScoreToLeaderboard(long score){

        //Post high score to leaderboard
        Gdx.app.log(gsClient.getPlayerDisplayName(), gsClient.getGameServiceId());
        gsClient.submitToLeaderboard("apx1", score, "Test");

    }

    public void showHighScoresUI(){

        //Show Apple GameCenter High Scores UI
        //If the UI does not load, show an alert message to the user

        try {
            Gdx.app.log(gsClient.getPlayerDisplayName(), gsClient.getGameServiceId());
            gsClient.showLeaderboards("apx1");
        } catch (GameServiceException e) {
            Gdx.app.log("LEADERBOARD EXCEPTION: ", e.getMessage());
            GDXButtonDialog bDialog = game.dialogs.newDialog(GDXButtonDialog.class);
            bDialog.setTitle("Not Logged In To Game Center");
            bDialog.setMessage("Please log in to Game Center to see Leaderboards");

            bDialog.addButton("Ok");

            bDialog.build().show();
        }
    }



}
