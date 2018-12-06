package jUnitTests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import entry_objects.InformationEntry;
import other.Filter;
import twitter.TwitterAuth;
import twitter.TwitterConnection;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

public class TwitterConnectionLoggedOutTest {
	@BeforeClass
	public static void setUp(){
		TwitterConnection.getInstance().logout();
	}
	//Authetication functions
	@Test
	public void testGetAuthUrl(){
		String s = TwitterConnection.getInstance().getAuthUrl();
		assert(s!="");
	}
	@Test
	public void testInputPin(){
		assert(!TwitterConnection.getInstance().confirmAuth("asdasd"));
	}
	@Test
	public void testIsLoggedIn(){
		assert(!TwitterConnection.getInstance().isLoggedIn());
	}
	
	//LoggedIn functions
	@Test
	public void testGetUsername(){
		String s=TwitterConnection.getInstance().getUsername();
		assertEquals(s,"");
	}
	@Test
	public void testRetweet() throws TwitterException {
		Status status = new TestStatus(1064992747311063040L);
		TwitterConnection tf = TwitterConnection.getInstance();
		assert(!tf.retweet(status));
	}

	@Test
	public void testRequestTwitter() throws Exception {
		List<InformationEntry> l=new ArrayList();
		l = TwitterConnection.getInstance().requestTwitter();
		assertNotNull(l);
	}
	

	@Test
	public void testGetTweetsFromUserByDate() {
		Calendar c= Calendar.getInstance();
		c.set(2018, Calendar.OCTOBER , 14,23,0,0);
		Date d = c.getTime();
		List<InformationEntry> l;
		l=TwitterConnection.getInstance().getTweetsFromUserByDate(d, "ANACLET28324119");
		assert(l.isEmpty());
		c.set(2018, Calendar.OCTOBER , 14,0,0,0);
		d=c.getTime();
		l=TwitterConnection.getInstance().getTweetsFromUserByDate(d, "ANACLET28324119");
		assertEquals(l.get(0).toString(),"ola meu outra vez");
		assertEquals(l.get(1).toString(),"ola meu.");
	}

	@Test
	public void testGetTweetsFromUsers() {
		List<InformationEntry> l;
		Calendar c= Calendar.getInstance();
		c.set(2018, Calendar.OCTOBER , 14,0,0,0);
		Date date = c.getTime();
		String [] users=null;
		l=TwitterConnection.getInstance().getTweetsFromUsers(date, users);
		assert(l.isEmpty());
		users=new String[0];
		l=TwitterConnection.getInstance().getTweetsFromUsers(date, users);
		assert(l.isEmpty());
		users=new String[1];
		users[0]="ANACLET28324119";
		l=TwitterConnection.getInstance().getTweetsFromUsers(date, users);
		assert(!l.isEmpty());
		assertEquals(l.get(0).toString(),"ola meu outra vez");
		assertEquals(l.get(1).toString(),"ola meu.");
	}
	@Test 
	public void testGetTweetsFiltered(){
		Filter.getInstance().defineDateIntervalFromCurrentDate(24);
		List<InformationEntry> l=TwitterConnection.getInstance().getTweetsFiltered();
		assert(!l.isEmpty());
	}
	@Test
	public void testGetKeys() {
		String [] l=TwitterConnection.getKeys();
		assertEquals("k4a4y5Wcq3UqdGKs9R6CufWoA", l[0]);
		assertEquals("WTSpB0qE4IS1EpeHA2mAhC5C8wD3iUYqihg5AIBVeIhplHgR8w", l[1]);
		assertEquals("2389545732-VxLp2gwOAuv2hV7cHXV96uYT7LNDPiFTLFf5MRi", l[2]);
		assertEquals("6c0V85yaqaSo5kvLll4tZxDdneQWOhfU78HMucmUM8VZn", l[3]);
	}
	//Logger Testing
	
//	@Test
//	public void TestLogin(){
//		TwitterAuth l = new TwitterAuth();
//		String s=l.getAuthURL();
//		assert(!s.isEmpty());
//		boolean b= l.inputPin("asdasdasd");
//		assert(!b);
//	}
//	

	
	
	private class TestStatus implements Status{
		long post_id=-1L;
		public TestStatus(long l){
			this.post_id=l;
		}
		@Override
		public int compareTo(Status o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getAccessLevel() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public RateLimitStatus getRateLimitStatus() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HashtagEntity[] getHashtagEntities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MediaEntity[] getMediaEntities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SymbolEntity[] getSymbolEntities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URLEntity[] getURLEntities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UserMentionEntity[] getUserMentionEntities() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long[] getContributors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getCreatedAt() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getCurrentUserRetweetId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getDisplayTextRangeEnd() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getDisplayTextRangeStart() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getFavoriteCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public GeoLocation getGeoLocation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getId() {
			// TODO Auto-generated method stub
			return this.post_id;
		}

		@Override
		public String getInReplyToScreenName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getInReplyToStatusId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getInReplyToUserId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLang() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Place getPlace() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Status getQuotedStatus() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getQuotedStatusId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public URLEntity getQuotedStatusPermalink() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRetweetCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Status getRetweetedStatus() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Scopes getScopes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSource() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public User getUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getWithheldInCountries() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isFavorited() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isPossiblySensitive() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRetweet() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRetweeted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRetweetedByMe() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isTruncated() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}