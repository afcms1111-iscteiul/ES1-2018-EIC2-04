package twitter;

import java.util.Scanner;

import files.ReadAndWriteXMLFile;
import other.XMLUserConfiguration;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author DElfim
 *
 */
public class Logger {
	/**
	 * 
	 */
	private static String TWITTER_CONSUMER_KEY = null;
	private static String TWITTER_SECRET_KEY = null;
	private static AccessToken userToken = null;
	private static Twitter twitter;
	private static XMLUserConfiguration twitterKeys = null;



	/**
	 * 
	 */
	public void getAuthUrl() {
		
		try {
			getKeysFromXML();
		} catch (Exception e1) {
			System.out.println("Could not get api keys from xml file(Twitter)");
			e1.printStackTrace();
		}
		
		
		twitter = buildAuthenticationTwitter();
		twitterAutentication(twitter);

	}
	private void getKeysFromXML() throws Exception{
		twitterKeys = ReadAndWriteXMLFile.ReadConfigXMLFile().get(1);
		TWITTER_CONSUMER_KEY = twitterKeys.getTwitterConsumerKey();
		TWITTER_SECRET_KEY = twitterKeys.getTwitterSecretKey();
	}
	private Twitter buildAuthenticationTwitter(){
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setDebugEnabled(true).setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_SECRET_KEY);
		Configuration configuration = builder.build();

		TwitterFactory factory = new TwitterFactory(configuration);
		return factory.getInstance();
	}
	private void twitterAutentication(Twitter twitter){
		RequestToken requestToken;
		AccessToken accessToken;
		Scanner in = new Scanner(System.in);
		try {
			requestToken = twitter.getOAuthRequestToken("oob");
			String url = requestToken.getAuthenticationURL();
			System.out.println(url);
			System.out.println("Enter pin pls ...");
			accessToken = twitter.getOAuthAccessToken(requestToken, in.nextLine());
			twitter.setOAuthAccessToken(accessToken);
			userToken=accessToken;

		} catch (TwitterException e) {
			e.printStackTrace();
		}
		in.close();
	}
	/**
	 * @return Twitter
	 */
	public static Twitter authenticatedInstance(){
		if (twitter==null){
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setDebugEnabled(true).setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_SECRET_KEY);
			Configuration configuration = builder.build();
	
			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();
			twitter.setOAuthAccessToken(userToken);

		}
		return twitter;
	}
}
