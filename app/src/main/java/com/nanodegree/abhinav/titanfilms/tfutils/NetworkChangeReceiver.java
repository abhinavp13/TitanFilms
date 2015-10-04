package com.nanodegree.abhinav.titanfilms.tfutils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.nanodegree.abhinav.titanfilms.highestrated.activities.HighestRatedMoviesDetailActivity;
import com.nanodegree.abhinav.titanfilms.highestrated.activities.HighestRatedMoviesGridActivity;
import com.nanodegree.abhinav.titanfilms.mostpopular.activities.MostPopularMoviesDetailActivity;
import com.nanodegree.abhinav.titanfilms.mostpopular.activities.MostPopularMoviesGridActivity;

/**
 * Class receives broadcast when network connection is changed.
 * It gets triggered when either no internet is available
 * Or, network is back available.
 *
 * Created by Abhinav Puri
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // TODO : Start Fetching again...
        } else {
            if((Activity)context instanceof HighestRatedMoviesDetailActivity || (Activity)context instanceof MostPopularMoviesDetailActivity || (Activity)context instanceof HighestRatedMoviesGridActivity || (Activity)context instanceof MostPopularMoviesGridActivity){
                // TODO : exitable pop up...
            }
        }
    }
}
