package io.github.patpatchpatrick.alphapigeon.resources;

public interface PlayServices {

    //Interface to interact with google play services

    //--Set the mobile callbacks interface on the device.  This interface is used for the core class
    // -- to receive callbacks from the mobile device
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks);

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



}
