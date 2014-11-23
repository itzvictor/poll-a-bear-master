package com.calhacks.pollabear;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.calhacks.pollabear.models.UserModel;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.TestSession;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;


public class LoginActivity extends Activity {

    private static final Collection<String> FB_PERMISSIONS = new HashSet<String>(Arrays.asList(new String[] {
        "public_profile", "email", "user_friends"
    }));

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainLoginFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainLoginFragment extends Fragment {

        private boolean profileLoaded = false;
        private boolean friendsLoaded = false;

        public MainLoginFragment() {
        }

        private void launchMainActivity() {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        private void profileLoaded() {
            profileLoaded = true;
            if (friendsLoaded) {
                launchMainActivity();
            }
        }

        private void friendsLoaded() {
            friendsLoaded = true;
            if (profileLoaded) {
                launchMainActivity();
            }
        }

        private void loadProfile() {
            Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser graphUser, Response response) {
                    if (graphUser != null) {
                        PollApplication.loggedInUser.setFbId(graphUser.getId());
                        PollApplication.loggedInUser.setFullName(graphUser.getName());
                        PollApplication.loggedInUser.setFirstName(graphUser.getFirstName());
                        PollApplication.loggedInUser.setLastName(graphUser.getFirstName());
                        PollApplication.loggedInUser.saveInBackground();
                    }
                    profileLoaded();
                }
            }).executeAsync();
        }

        private void loadFriends() {
            Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {
                @Override
                public void onCompleted(List<GraphUser> users, Response response) {
                    long stime = System.currentTimeMillis();
                    // For now, look up by fbId - won't catch friends who sign up by email
                    final Semaphore sem = new Semaphore(0);
                    for (final GraphUser user : users) {
                        Log.d(TAG, "Friend: " + user.getName() + " (id: " + user.getId() + ")");
                        try {
                            ParseUser parseUser = ParseUser.getQuery().whereEqualTo("fbId", user.getId()).getFirst();
                            if (parseUser != null) {
                                Log.d(TAG, "Adding user " + user.getName() + " as friend");
                                synchronized (PollApplication.loggedInUser) {
                                    PollApplication.loggedInUser.addFriendByUsername(parseUser.getUsername());
                                }
                            }
                        } catch (ParseException e) {}
                    }
                    Log.d(TAG, "Time: " + (System.currentTimeMillis() - stime));
                    PollApplication.loggedInUser.saveInBackground();
                    friendsLoaded();
                }
            }).executeAsync();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            final View loadingPanel = rootView.findViewById(R.id.loadingPanel);

            Button facebookLoginBtn = (Button)rootView.findViewById(R.id.facebook_btn);
            facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingPanel.setVisibility(View.VISIBLE);
                    ParseFacebookUtils.logIn(FB_PERMISSIONS, getActivity(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            PollApplication.loggedInUser = user == null ? null : new UserModel(user);
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                            } else if (user.isNew()) {
                                loadProfile();
                                loadFriends();
                            } else {
                                launchMainActivity();
                            }
                        }
                    });
                }
            });

            Button emailSignupButton = (Button)rootView.findViewById(R.id.email_signup_btn);
            emailSignupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SignUpActivity.class);
                    startActivity(intent);
                }
            });

            TextView signinLink = (TextView)rootView.findViewById(R.id.signinLink);
            signinLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }
}
