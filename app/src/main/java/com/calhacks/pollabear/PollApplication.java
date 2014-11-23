package com.calhacks.pollabear;

import android.app.Application;
import android.content.res.Resources;

import com.calhacks.pollabear.models.PollModel;
import com.calhacks.pollabear.models.UserModel;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by samuel on 04/10/14.
 */
public class PollApplication extends Application {

    // Hacks at the hackathon
    public static UserModel loggedInUser;
    public static PollModel newPoll;

    @Override
    public void onCreate() {
        super.onCreate();
        Resources res = getResources();
        Parse.initialize(this, res.getString(R.string.parse_app_id),
                res.getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(res.getString(R.string.facebook_app_id));
    }

}
