package io.github.patpatchpatrick.alphapigeon.resources;

public interface PlayServices {

    //Interface to interact with google play services

    //--Set the mobile callbacks interface on the device.  This interface is used for the core class
    // -- to receive callbacks from the mobile device
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks);
    public void signIn();
    public void signOut();
    public void submitScore(long highScore);
    public void showLeaderboard();
    public void getPlayerCenteredScores();
    public void getTopScores(int scoreType);
    public boolean isSignedIn();
    public void showAds(boolean show); //Enable or disable ads in mobile device



}
