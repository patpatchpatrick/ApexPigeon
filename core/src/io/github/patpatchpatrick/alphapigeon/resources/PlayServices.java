package io.github.patpatchpatrick.alphapigeon.resources;

public interface PlayServices {

    //Interface to interact with google play services

    //--Set the mobile callbacks interface on the device.  This interface is used for the core class
    // -- to receive callbacks from the mobile device
    public void setMobileCallbacks(MobileCallbacks mobileCallbacks);

    //Ads
    public void showBannerAds(boolean show); //Enable or disable banner ads in mobile device
    public void showOrLoadInterstitialAd(); //Show or load interstitial ad

    //Billing
    public void purchaseAdRemoval();


}
