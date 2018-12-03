package jUnitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jfoenix.controls.JFXListView;

import email.EmailConnection;
import entry_objects.InformationEntry;
import facebook.FacebookConnection;
import files.ReadAndWriteXMLFile;
import gui.MainController;
import gui.MainWindow;
import gui.PostBox;
import other.ControlCenter;
import other.OtherStaticFunction;
import other.XMLUserConfiguration;
import tasks.EmailReaderTask;
import tasks.FacebookPostReaderTask;
import tasks.ServiceReadTask;
import tasks.TwitterPostReaderTask;
import threads.ThreadPool;
import twitter.TwitterFunctions;

public class OtherStaticFunctionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thread t = new Thread("MainWindow Thread") {
			public void run() {
				MainWindow.main(new String[0]);
			}
		};

		t.setDaemon(true);
		t.start();
		Thread.sleep(500);
		
		MainController.getInstance().loadPosts(new ArrayList<InformationEntry>(), true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRefreshGUIWithThreadsWithNoInfoOnControlCenter() throws Exception {
		JFXListView<PostBox> previousExecutionPostBox = MainController.getInstance().getPosts();
		ServiceReadTask task;
		List<ServiceReadTask> tasks = new ArrayList<ServiceReadTask>();
		List<XMLUserConfiguration> list_of_user_configuration = ReadAndWriteXMLFile.ReadConfigXMLFile();
		for(XMLUserConfiguration xml_user_config : list_of_user_configuration) {
			switch (xml_user_config.getService()) {
			case EMAIL:
				task = new EmailReaderTask(new EmailConnection(xml_user_config.getUsername(), xml_user_config.getPassword()));
				break;
				
			case TWITTER:
				task = new TwitterPostReaderTask(); // Require to be implemented object oriented and not function oriented
				break;
				
			case FACEBOOK:
				task = new FacebookPostReaderTask(); // needs implementation still...
				break;

			default:
				task = null;
				break;
			}
			if(task != null) {
				tasks.add(task);
			}
		}
		
		OtherStaticFunction.refreshGUIWithThreads();
		
		long valueNeededToWaitBeforeCheckOnList = 60000;
		Thread.sleep(valueNeededToWaitBeforeCheckOnList);
		
		JFXListView<PostBox> actualExecutionPostBox = MainController.getInstance().getPosts();
		
		assertNotEquals(previousExecutionPostBox, actualExecutionPostBox);
	}
	
	@Test
	public void testRefreshGUIWithThreadsWithInfoOnControlCenter() throws Exception {
		JFXListView<PostBox> previousExecutionPostBox = MainController.getInstance().getPosts();
		ServiceReadTask task;
		List<ServiceReadTask> tasks = new ArrayList<ServiceReadTask>();
		List<XMLUserConfiguration> list_of_user_configuration = ReadAndWriteXMLFile.ReadConfigXMLFile();
		
		List<EmailConnection> email_list = new ArrayList<EmailConnection>();
		List<TwitterFunctions> twitter_list = new ArrayList<TwitterFunctions>();
		List<FacebookConnection> facebook_list = new ArrayList<FacebookConnection>();
		
		EmailConnection email;
		TwitterFunctions twitter;
		FacebookConnection facebook;
		
		for(XMLUserConfiguration xml_user_config : list_of_user_configuration) {
			switch (xml_user_config.getService()) {
			case EMAIL:
				email = new EmailConnection(xml_user_config.getUsername(), xml_user_config.getPassword());
				task = new EmailReaderTask(email);
				email_list.add(email);
				break;
				
			case TWITTER:
				twitter = TwitterFunctions.getInstance();
				task = new TwitterPostReaderTask(); // Require to be implemented object oriented and not function oriented
				twitter_list.add(twitter);
				break;
				
			case FACEBOOK:
				facebook = FacebookConnection.getInstance();
				task = new FacebookPostReaderTask(); // needs implementation still...
				facebook_list.add(facebook);
				break;

			default:
				task = null;
				break;
			}
			if(task != null) {
				tasks.add(task);
			}
		}
		
		ControlCenter.getInstance().setEmailList(email_list);
		ControlCenter.getInstance().setTwitterList(twitter_list);
		ControlCenter.getInstance().setFacebookList(facebook_list);
		
		OtherStaticFunction.refreshGUIWithThreads();
		
		long valueNeededToWaitBeforeCheckOnList = 60000;
		Thread.sleep(valueNeededToWaitBeforeCheckOnList);
		
		JFXListView<PostBox> actualExecutionPostBox = MainController.getInstance().getPosts();
		
		assertNotEquals(previousExecutionPostBox, actualExecutionPostBox);
	}

}
