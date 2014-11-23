package com.calhacks.pollabear.models;

import android.util.Log;

import com.calhacks.pollabear.PollApplication;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 04/10/14.
 */
public class UserModel {

    private static final String FRIENDS_RELATION_KEY = "friends";
    private static final String TAG = "PollModel";
    private List<UserModel> friends;
    private ParseUser parseUser;

    /**
     * Constructor: from a parse user object/class
     * @param parseUser
     */
    public UserModel(ParseUser parseUser){
        this.parseUser = parseUser;
        this.friends   = new ArrayList<UserModel>();
    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public String getUsername(){
        return parseUser.getUsername();
    }

    public String getFbId() {
        return parseUser.getString("fbId");
    }

    public void setFbId(String fbId) {
        parseUser.put("fbId", fbId);
    }

    public String getFullName() {
        return parseUser.getString("fullName");
    }

    public void setFullName(String name) {
        parseUser.put("fullName", name);
    }

    public String getFirstName() {
        return parseUser.getString("firstName");
    }

    public void setFirstName(String firstName) {
        parseUser.put("firstName", firstName);
    }

    public String getLastName() {
        return parseUser.getString("lastName");
    }

    public void setLastName(String lastName) {
        parseUser.put("lastName", lastName);
    }

    public boolean checkPassword(String password){return parseUser.getString("password").equals(password);}

    public List<PollModel> getPolls() {
        return PollModel.getPollsByCreator(parseUser);
    }

    public void addFriendByUsername(String userName){
        UserModel userModel = PollApplication.loggedInUser;
        ParseUser friend = UserModel.findUserFromParse(userName, "username");
        ParseRelation<ParseObject> friendshipRelation = userModel.parseUser.getRelation(FRIENDS_RELATION_KEY);
        friendshipRelation.add(friend);
    }

    public List<UserModel> getFriends(){
        ParseRelation friendsRelation  = PollApplication.loggedInUser.parseUser.getRelation(FRIENDS_RELATION_KEY);
        ParseQuery    friendsQuery     = friendsRelation.getQuery();

        friendsQuery.whereNotEqualTo("fullName", null);

        try{
            this.friends.clear();
            List<ParseUser> friendsParse = friendsQuery.find();
            for (int i = 0, len = friendsParse.size(); i < len ; i++) {
                this.friends.add(new UserModel(friendsParse.get(i)));
            }
        }
        catch( ParseException e){
            System.err.println("Error getFriends "+ e.getMessage());
        }
        return this.friends;
    }

    /**
     * find a user by user name
     * @note probably be a good idea to use this inside AsyncTask
     * @param userName
     * @return
     */
    public static UserModel newUser(String firstname, String lastname, String email, String password){

        ParseUser newuser = new ParseUser();
        newuser.setEmail(email);
        newuser.setPassword(password);
        newuser.setUsername("Test2");
        UserModel newusermodel = new UserModel(newuser);
        newusermodel.setFirstName(firstname);
        newusermodel.setLastName(lastname);
        newusermodel.setFullName(firstname+" "+lastname);
        try {
            newuser.signUp();
           // newuser.save();
        } catch (ParseException e) {
            Log.e(TAG, "Parse exception while saving poll", e);
        }
        return findUserInEmail(email);
    }

    public static UserModel findUser(String userName) {
        ParseUser user = findUserFromParse(userName, "username");
        if (user == null){
            return null;
        }
        System.out.println("not null");
        System.out.println(user.getUsername());
        return new UserModel(user);
    }

    public static UserModel findUserInEmail(String email) {
        ParseUser user = findUserFromParse(email, "email");
        if (user == null){
            return null;
        }
        System.out.println("not null");
        System.out.println(user.getUsername());
        return new UserModel(user);
    }

    private static ParseUser findUserFromParse(String userName, String type){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseUser result = null;

        query.whereEqualTo(type, userName);
        try{
            List<ParseUser> results = query.find();
            if (!results.isEmpty()) {
                result = results.get(0);
            }
        } catch (ParseException e){
            System.err.println( "Error while findUser: " + e.getMessage());
        }

        return result;
    }

    @Override
    public String toString() {
        return parseUser.getString("fullName");
    }

    public void saveInBackground() {
        parseUser.saveInBackground();
    }

}
