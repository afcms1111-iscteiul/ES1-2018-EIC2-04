package facebook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.GraphResponse;
import com.restfb.types.Post;
import com.restfb.types.User;

import entry_objects.FacebookEntry;
import entry_objects.InformationEntry;
import entry_objects.TwitterEntry;
import other.Filter;
import other.Service;

/** This class handles the Facebook connection and respective functions
 * @author Joao Aparicio
 *
 */
public class FacebookConnection {
	
	//activity going to: https://www.facebook.com/Bomdiaacademia-318510688875649/?modal=admin_todo_tour
	
	/**
	 * Access Token for the BomDiaAcademia app
	 */
	private static String accessToken2 ;
	/**
	 * Client used in every connection
	 */
	private static FacebookClient fbClient2;
	/**
	 * 
	 */
	private static User me2 ;
	/**
	 * Instance used for the singleton
	 */
	private static FacebookConnection INSTANCE = new FacebookConnection();
	

	private FacebookConnection() {
		accessToken2 = "EAAePp5MZAcE4BANuO4pcvl7kWxeagvcvJ2rPVVmlBLeoljRRg0UEcRrFrZAqKA18bMfxBI2Viv6TXtA8ZBSPdHwQl3pioifUrUvTXZADJTb3tJUPHO8nhZA2X2ATEAn7qfQ0Ks5sr5gMTiS2CaZAX57DeI6rSOmx1sx6cqaZBuFqAtXokKvp3ZBC";
		fbClient2 = init();
		System.out.println("Facebook:");
		System.out.println("Id: " + me2.getId());
		System.out.println("Name: " + me2.getName());
	}
	
	/**
	 * @return the instance of the singleton
	 */
	public static FacebookConnection getInstance() {
		return INSTANCE;
	}
	
	@SuppressWarnings("deprecation")
	private static DefaultFacebookClient init() {
	
		try {
			fbClient2 = new DefaultFacebookClient(accessToken2);
			me2 = fbClient2.fetchObject("me", User.class);
			try {
				
			} catch (FacebookException e) {
				System.out.println(""+e);
			}

			
		} catch (FacebookException e) {
			System.out.println(e);
		}
		
		return (DefaultFacebookClient) fbClient2;
		
	}


	/**
	 * Setter for app token
	 */
	public static void setAccessToken2(String accessToken2) {
		FacebookConnection.accessToken2 = accessToken2;
	}
	
	/**
	 * Extends the app token
	 */
	public static void ExtendAccessToken() {
		try {
			fbClient2.obtainExtendedAccessToken("2128274727202894", "5b08263178f3db9cbd189e2100f0ee54", accessToken2);
			
		} catch (FacebookException e) {
			System.out.println(e);
		}
		
	}
	
	/**
	 * Initiation Method
	 */
	
	
	
	/**
	 * Request for the bom dia academia page
	 */
	public static List<InformationEntry> requestFacebook() {
		List<InformationEntry> list = new ArrayList<>();
		try {
			Connection<Post> myFeed = fbClient2.fetchConnection("me/feed", Post.class);
			Iterator<List<Post>> it = myFeed.iterator();
			
			while(it.hasNext()) {
			   List<Post> myFeedPage = it.next();
			   for (Post post : myFeedPage) {
				 String postId = post.getId();  
			     //System.out.println("Post: " + post.getId()+ ", Message: "+ post.getMessage() +", Updated time: "+ post.getUpdatedTime());
				 Post post1 = fbClient2.fetchObject(postId, Post.class, Parameter.with("fields", "from,to,likes.summary(true),description.summary(true),comments.summary(true),message.summary(true),attachments.summary(true)"));
				 
				 list.add(new FacebookEntry(post1, post1.getCreatedTime()));
			   }	
			   
			} 
			
		} catch (FacebookOAuthException e) {
			e.printStackTrace();
			FacebookConnection.ExtendAccessToken();
		} catch (FacebookException te) {
			te.printStackTrace();
		}
		return list;
	
		
	}
	public static void like(String id){
		fbClient2.publish(id+"/likes", Boolean.class); 
		
	}
	
	
	public static GraphResponse post(String message) {
		GraphResponse publishMessageResponse = 
				fbClient2.publish("me/feed", GraphResponse.class,
				    Parameter.with("message", message));

		System.out.println("Published message ID: " + publishMessageResponse.getId());
		return publishMessageResponse;
	}
	
	public static void commentOnPost(String id, String message) {
		fbClient2.publish(id+"/comments", String.class, Parameter.with("message",message));
	}
	
	
	
	
	
	public static String getAccessToken2() {
		return accessToken2;
	}
	

	public static void main(String[] args) {
		FacebookConnection fb = getInstance();
		 List<InformationEntry> a = fb.requestFacebook();
		 for (int i = 0; i < a.size(); i++) {
			System.out.println( a.get(i).toString());
		}
		//System.out.println(a);
		like(((FacebookEntry) a.get(0)).getPost().getId());
		
		//GraphResponse b = post("FaceId");
		//like(b.getId());
		//commentOnPost(b.getId(),"Grateful Comment :P");
		ExtendAccessToken();
		
		
	}



}
