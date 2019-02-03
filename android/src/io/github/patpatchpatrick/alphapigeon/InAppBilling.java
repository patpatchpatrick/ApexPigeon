package io.github.patpatchpatrick.alphapigeon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

public class InAppBilling implements PurchasesUpdatedListener {

    //Class to handle in-app billing

    private BillingClient mBillingClient;
    private Context mContext;
    private Activity mActivity;

    // In-app products. Currently only selling "ad removal"
    static final String ITEM_SKU_ADREMOVAL = "ad.removal";

    public InAppBilling(Context context, Activity activity){

        mContext =  context;
        mActivity = activity;


        // Establish connection to billing client
        mBillingClient = BillingClient.newBuilder(context).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO implement your own retry policy
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }



    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

        //Handle the responseCode for the purchase
        //If response code is OK,  handle the purchase
        //If user already owns the item, then indicate in the shared prefs that item is owned
        //If cancelled/other code, log the error

        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d("Billing", "User Canceled" + responseCode);
        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            //Set user to be ad free in their shared preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String adRemovalPurchasedPreference = mContext.getResources().getString(R.string.ad_removal_purchase_pref);
            sharedPreferences.edit().putBoolean(adRemovalPurchasedPreference, true).commit();
        } else {
            Log.d("Billing", "Other code" + responseCode);
            // Handle any other error codes.
        }

    }

    private void queryPurchases() {

        //Method not being used for now, but can be used if purchases ever need to be queried in the future
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if (purchasesResult != null) {
            List<Purchase> purchasesList = purchasesResult.getPurchasesList();
            if (purchasesList == null) {
                return;
            }
            if (!purchasesList.isEmpty()) {
                for (Purchase purchase : purchasesList) {
                    if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL)) {

                    }
                }
            }
        }

    }

    private void queryPrefPurchases() {

    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL)) {
            //Set user to be ad free in their shared preferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String adRemovalPurchasedPreference = mContext.getResources().getString(R.string.ad_removal_purchase_pref);
            sharedPreferences.edit().putBoolean(adRemovalPurchasedPreference, true).commit();
        }
    }

    public void launchAdRemovalPurchase(){

        // If user clicks the purchase ad removal button, launch the billing flow for an ad removal purchase
        // Response is handled using onPurchasesUpdated listener
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(ITEM_SKU_ADREMOVAL)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        int responseCode = mBillingClient.launchBillingFlow(mActivity, flowParams);

    }

    public boolean isAdRemovalPurchased(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String adRemovalPurchasedPreference = mContext.getResources().getString(R.string.ad_removal_purchase_pref);
        return sharedPreferences.getBoolean(adRemovalPurchasedPreference, false);


    }




}
