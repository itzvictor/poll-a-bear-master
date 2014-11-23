package com.calhacks.pollabear.models;

import android.util.Log;

import com.calhacks.pollabear.PollApplication;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by samuel on 04/10/14.
 */
public class PollModel {

    private static final String TAG = "PollModel";

    public static final int TEXT_POLL = 0;
    public static final int PHOTO_POLL = 1;

    private ParseObject parseObject;

    public PollModel(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public PollModel(UserModel creator) {
        parseObject = ParseObject.create("Poll");
        parseObject.put("ended", false);
        parseObject.put("parent", creator.getParseUser());
        parseObject.setACL(new ParseACL(creator.getParseUser()));
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public int getType() {
        return parseObject.getInt("type");
    }

    public void setType(int type) {
        parseObject.put("type", type);
    }

    public String getQuestion() {
        return parseObject.getString("question");
    }

    public void setQuestion(String question) {
        parseObject.put("question", question);
    }

    public String[] getVoters(){
        JSONArray votesArray = parseObject.getJSONArray("participants");
        String[] voters = new String[votesArray.length()];
        try{
            for (int i = 0; i < votesArray.length(); i++){
                voters[i] = votesArray.getString(i);
            }
        } catch (JSONException e){
            throw new RuntimeException("Error parsing JSON from Parse!");
        }
        return voters;
    }

    public String[] getTextOptions() {
        if (getType() != TEXT_POLL) {
            throw new RuntimeException("Trying to get text options from photo poll!");
        }
        JSONArray array = parseObject.getJSONArray("options");
        if (array.length() > 4) {
            Log.w(TAG, "More than 4 options retrieved!");
        }
        String[] ret = new String[array.length()];
        try {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (String)array.get(i);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error parsing JSON from Parse!");
        }
        return ret;
    }

    public ParseFile[] getPhotoOptions() {
        if (getType() != PHOTO_POLL) {
            throw new RuntimeException("Trying to get photo options from text poll!");
        }
        List<ParseObject> array = parseObject.getList("options");
        if (array.size() > 4) {
            Log.w(TAG, "More than 4 options retrieved!");
        }
        ParseFile[] ret = new ParseFile[array.size()];
        for (int i = 0; i < ret.length; i++) {
            try {
                array.get(i).fetchIfNeeded();
                ret[i] = (ParseFile)array.get(i).get("file");
            } catch (ParseException e) {
                Log.e(TAG, "Error while fetching photo", e);
            }
        }
        return ret;
    }

    public void clearOptions() {
        parseObject.put("options", new ArrayList());
    }

    public void addOption(String textOption) {
        if (getType() != TEXT_POLL) {
            throw new RuntimeException("Trying to add text option to non-text poll!");
        }
        parseObject.add("options", textOption);
    }

    public void addOption(ParseFile photoOption) {
        if (getType() != PHOTO_POLL) {
            throw new RuntimeException("Trying to add photo option to non-photo poll!");
        }
        ParseObject option = ParseObject.create("Photo");
        option.put("file", photoOption);
        parseObject.add("options", option);
    }

    public void addVoter(String userId) {
        JSONObject vote = new JSONObject();
        try {
            vote.put("voter", userId);
            vote.put("option", -1);
        } catch (JSONException e) {
            Log.e(TAG, "Error writing JSON", e);
        }
        parseObject.add("votes", vote);
        parseObject.add("participants", userId);
        save();
    }

    public int[] getVotes(){
        int[] voteSummary = {0,0,0,0};
        int counter =0;
        JSONArray votes = parseObject.getJSONArray("votes");
        JSONObject vote;
        int voteValue =-1;
        for(int y = 0; y<votes.length(); y++){
            try {
                voteValue = votes.getJSONObject(y).getInt("option");
                if(voteValue > -1){
                    voteSummary[voteValue]++;
                }
                else counter++; //-> We need this to verify stuff later
            }
            catch (JSONException e)
            {
                throw new RuntimeException("Error parsing JSON from Parse!");
            }
        }

        return voteSummary;
    }

    public void insertVote(int vote){
        JSONArray votes = parseObject.getJSONArray("votes");
        JSONObject new_vote = new JSONObject();
        String voter = null;
        try {
            voter = votes.getJSONObject(0).getString("voter");
            new_vote.put("voter", voter );
            new_vote.put("option", vote);
        } catch (JSONException e) {
            Log.e(TAG, "Error writing JSON", e);
        }
        parseObject.add("votes", new_vote);
        save();
    }



    public boolean getEnded() {
        return parseObject.getBoolean("ended");
    }

    public void setEnded(boolean ended) {
        parseObject.put("ended", ended);
    }

    public void saveInBackground() {
        parseObject.saveInBackground();
    }

    public void save() {
        try {
            parseObject.save(); // TODO: Probably good to check if indeed saved before sending push notifications
            pushNotifyVoters("New Poll from "+ PollApplication.loggedInUser.getFullName() + ": " + getQuestion());
        } catch (ParseException e) {
            Log.e(TAG, "Parse exception while saving poll", e);
        }
    }

    public static PollModel findPoll(String id) {
        PollModel ret = null;
        try {
            ret = new PollModel(ParseQuery.getQuery("Poll").get(id));
        } catch (ParseException e) {
            Log.e(TAG, "Could not fetch poll for id " + id, e);
        }
        return ret;
    }

    public void pushNotifyVoters(String message){
        String[] voters = getVoters();

        if(voters.length > 0){
            // Setup and send Push notifications regarding new poll
            LinkedList<String> channels = new LinkedList<String>();
            for(String userObjectId : voters){
                channels.add(userObjectId);
            }
            ParsePush push = new ParsePush();
            push.setChannels(channels); // Notice we use setChannels not setChannel
            push.setMessage(message);
            push.sendInBackground();
        }
    }

    public static List<PollModel> getPollsByCreator(ParseUser parseUser) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
        query.whereEqualTo("parent", parseUser);
        List<PollModel> ret = new ArrayList<PollModel>();
        try {
            for (ParseObject parseObject : query.find()) {
                ret.add(new PollModel(parseObject));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing polls", e);
        }
        return ret;
    }

}
