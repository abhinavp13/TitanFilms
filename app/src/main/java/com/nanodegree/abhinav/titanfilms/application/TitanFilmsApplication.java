package com.nanodegree.abhinav.titanfilms.application;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.FetchFavourite;
import com.nanodegree.abhinav.titanfilms.tfutils.AppConfig;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * This is Application Class for whole app.
 * ACRA initiation is most important task here.
 * ACRA is an error reporting app.
 * Currently, I don't have a web domain where I can point the stack trace of the
 * error. So I linked to my registered email.
 *
 * Created by Abhinav Puri.
 */
@ReportsCrashes(
        mailTo = "pabhinav@iitrpr.ac.in",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast
)
public class TitanFilmsApplication extends Application {

    /**
     * The saved data for application running.
     */
    public ApplicationSavedData applicationSavedData;

    /**
     * Singleton instance for application
     */
    protected static TitanFilmsApplication titanFilmsApplication;

    /**
     * Bitmap used for saved selected image
     * across activities.
     */
    public Drawable bitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * The following line triggers the initialization of ACRA.
         * Right now, ACRA simply ask user to send mail to developer, becoz its just a debugging stage.
         * Can be changed to send crash messages to a specific server, later in release stage.
         */
        ACRA.init(this);

        /*
         * The following stores data for most popular and highest rating films
         * This saving is on at application level,ie., available as long as application is open.
         * We do not hard copy this data.
         */
        applicationSavedData = new ApplicationSavedData();

        /*
         * Instantiation for singleton application.
         */
        titanFilmsApplication = this;

        /*
         * Initializing AppConfig Instance.
         */
        try {
            AppConfig.Instance();
        } catch (Exception e){
            Log.e("AppConfig Failure : ",e.getMessage());
        }

        /**
         * This is used to fetch data from database and load them into Application Saved Data Instance
         * Important, as these are movies saved by user.
         */
        FetchFavourite fetchFavourite = new FetchFavourite(titanFilmsApplication);
        applicationSavedData.setFavouriteMoviesSavedData(fetchFavourite.fetchData());

    }

    /**
     * This function is used to get the resources
     * initialized during application initiation.
     *
     * @return Resources object.
     */
    public static Resources getResource(){
        return titanFilmsApplication.getResources();
    }
}
