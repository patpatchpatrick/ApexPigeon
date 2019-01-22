package io.github.patpatchpatrick.alphapigeon.resources;

public interface PlayServices {

    //Interface to interact with google play services

    public void onStartMethod();
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement(String str);
    public void submitScore(long highScore);
    public void submitLevel(int highLevel);
    public void showAchievement();
    public void showScore(String LeaderBoard);
    public void showLevel();
    public void showLeaderboard();
    public void getPlayerCenteredScores();
    public boolean isSignedIn();
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks);


}
