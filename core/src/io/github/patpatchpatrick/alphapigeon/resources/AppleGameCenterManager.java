package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class AppleGameCenterManager {

    public IGameServiceListener gsListener;
    public IGameServiceClient gsClient;
    public IFetchLeaderBoardEntriesResponseListener gsLeaderboardListener;

    public AppleGameCenterManager(IGameServiceClient client){
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

        Gdx.app.log(gsClient.getPlayerDisplayName(), gsClient.getGameServiceId());
        gsClient.submitToLeaderboard("apx1", score, "Test");

    }

    public void showHighScoresUI(){

        try {
            Gdx.app.log(gsClient.getPlayerDisplayName(), gsClient.getGameServiceId());
            gsClient.showLeaderboards("apx1");
            gsClient.fetchLeaderboardEntries("apx1", 100,false,gsLeaderboardListener);
        } catch (GameServiceException e) {
            Gdx.app.log("LEADERBOARD EXCEPTION: ", e.getMessage());
        }
    }



}
