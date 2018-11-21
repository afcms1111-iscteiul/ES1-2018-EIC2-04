package jUnitTests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import email.EmailConnection;
import entry_objects.EmailEntry;
import entry_objects.InformationEntry;
import files.ReadAndWriteXMLFile;
import other.XMLUserConfiguration;

public class EmailConnectionTesting {
	
private static EmailConnection email;
	
	/** The user. */
	private static XMLUserConfiguration user = null;
	
	@BeforeClass
	public static void startInstance() {
		try {
			user = ReadAndWriteXMLFile.ReadConfigXMLFile().get(0);
			email = new EmailConnection(user.getUsername(), user.getPassword());
			//email = new EmailConnection("BomDiaAcademiaES1@Hotmail.com", "BDAcademia1!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testEmailConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testReceiveMail() {
		List<InformationEntry> recievedEmails = email.receiveMail();
		assertNotNull( recievedEmails);
	}

	@Test
	public void testGetUsername() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsConnected() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendEmail() {
		email.sendEmail(user.getUsername(), "test", "test");
		List<InformationEntry> recievedEmails = email.receiveMail();
		System.out.println(((EmailEntry)recievedEmails.get(0)).getSubject());
		assertTrue(((EmailEntry)recievedEmails.get(0)).getSubject().contains("test"));
		assertTrue(((EmailEntry)recievedEmails.get(0)).getContent().contains("test"));
		//assertEquals("test",((EmailEntry)recievedEmails.get(0)).getContent());
		//System.out.println(((EmailEntry)recievedEmails.get(0)).getWriterName());
		
	}
	
	@Test
	public void testIncorrectCredetials() {
		EmailConnection emailTest = new EmailConnection(user.getUsername(), "notThePassword");
		List<InformationEntry> recievedEmails = emailTest.receiveMail();
		assertFalse(emailTest.isConnected());
		emailTest = new EmailConnection("dummy@iscte-iul.pt", "notThePassword");
		recievedEmails = emailTest.receiveMail();
		assertFalse(emailTest.isConnected());
	}

}