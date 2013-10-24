package cmu.teamCooper.masterpizza.twitter;

import java.io.File;
import oauth.signpost.OAuth;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.util.Log;

public class TwitterUtils {

	public static boolean isAuthenticated(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		
		if(token.equals("")) {
			return false; 
		} else {
			
			AccessToken a = new AccessToken(token,secret);
			
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(a);
			return true;
		}
	}
	
	public static void sendTweet(SharedPreferences prefs,String path) throws Exception {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");	
		
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
  
		try{
	        StatusUpdate status = new StatusUpdate("I just made a pizza by MasterPizza!!! ");
	        System.out.println("haha " + path);
	        File file = new File(path);
	        status.setMedia(file);
	        twitter.updateStatus(status);
	        } catch(TwitterException e){
	        Log.d("TAG", "Pic Upload error" + e.getErrorMessage());
	        throw e;
	    }
		

	}
}
