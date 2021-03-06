package jUnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import BDA.entry_objects.EmailEntry;
import BDA.entry_objects.InformationEntry;
import BDA.files.ReadAndWriteFile;

/**
 * The Class ReadAndWriteFileTest.
 */
public class ReadAndWriteFileTest {
	
	/** The key words filter list. */
	private List<String> keyWordsFilterList = null;
	
	/** The Constant KEY_WORDS_FILTER_FILE_NAME. */
	private static final String KEY_WORDS_FILTER_FILE_NAME = "key_words_filter.dat";
	
	/** The Constant TEST_FILE_NAME. */
	public static final String TEST_FILE_NAME = "key_words_filter.dat";
	
	/** The Constant DEFAULT_KEY_WORDS_FILTERS. */
	private static final String[] DEFAULT_KEY_WORDS_FILTERS = {"iscte", "universidade", "reitoria", "ista", "biblioteca", "cominvestigar", "tesouraria"};

	
	/**
	 * Test save list of information entry.
	 */
	@Test
	public void testSaveListOfInformationEntry() {

		List<String> key_words_filter = ReadAndWriteFile.loadListOfFilters(KEY_WORDS_FILTER_FILE_NAME);
		ArrayList<InformationEntry> information_entry_list = new ArrayList<InformationEntry>();
		information_entry_list.add(new EmailEntry("", new Date(1, 1, 1), "2", "3", "4"));
	
		ReadAndWriteFile.saveListOfInformationEntry(null, null);
		ReadAndWriteFile.saveListOfInformationEntry(TEST_FILE_NAME, null);
		ReadAndWriteFile.saveListOfInformationEntry(null, information_entry_list); 
		ReadAndWriteFile.saveListOfInformationEntry("@", information_entry_list);
		ReadAndWriteFile.saveListOfInformationEntry("\n",null);
		
		//test sync
		for (int i = 0; i < 40; i++) {
			Thread tester = new Thread() {
				public void run() {
					ReadAndWriteFile.saveListOfInformationEntry(TEST_FILE_NAME, information_entry_list);
		         }
			};
			tester.start();
			information_entry_list.add(new EmailEntry("", new Date(1, 1, 1), "2", "3", "\n"));
			ReadAndWriteFile.saveListOfInformationEntry(TEST_FILE_NAME, information_entry_list);
		}
		
		for (int i = 0; i < 40; i++) {
			Thread tester1 = new Thread() {
				public void run() {
					ReadAndWriteFile.saveListOfInformationEntry(TEST_FILE_NAME, information_entry_list);
		         }
			};
			tester1.start();
			information_entry_list.add(new EmailEntry("", new Date(1, 1, 1), "2", "3", "\n"));
			ReadAndWriteFile.loadListOfInformationEntry(TEST_FILE_NAME);
		}
	}

	/**
	 * Test load list of information entry.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testLoadListOfInformationEntry() throws IOException {
		ArrayList<InformationEntry> information_entry_list = new ArrayList<InformationEntry>();
		
		ReadAndWriteFile.loadListOfInformationEntry(null);
		ReadAndWriteFile.loadListOfInformationEntry("");
		ReadAndWriteFile.loadListOfInformationEntry(TEST_FILE_NAME);
		ReadAndWriteFile.loadListOfInformationEntry("\n");
		
		ReadAndWriteFile.saveListOfInformationEntry("NoFile.txt", information_entry_list);
		ReadAndWriteFile.loadListOfInformationEntry("NoFile.txt");
		//ReadAndWriteFile.loadListOfInformationEntry();
		
		for (int i = 0; i < 40; i++) {
			Thread tester = new Thread() {
				public void run() {
					try {
						FileOutputStream fos = new FileOutputStream(new File(TEST_FILE_NAME));
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						bos.write((byte)797869978); 
					} catch (Exception e) {
						System.err.println(e);
					}
					
		         }
			};
			tester.start();
			ReadAndWriteFile.loadListOfInformationEntry(TEST_FILE_NAME);	
		}
		
		
		
		File file = new File (TEST_FILE_NAME);
		BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
		out.flush();
		out.write("\n");
		System.out.println(ReadAndWriteFile.loadListOfInformationEntry(TEST_FILE_NAME).toString());
		out.close();
		
		
		//ReadAndWriteFile.loadListOfInformationEntry((String) );
		
		
		List<InformationEntry> information_entry_list2 = null;
		Date date = new Date();
		information_entry_list2 = new ArrayList<InformationEntry>();
		information_entry_list2.add(new EmailEntry("", date, "Person", "Subject", "Content"));
		
		ReadAndWriteFile.saveListOfInformationEntry("emailEntrysTest.dat", information_entry_list2);
		ReadAndWriteFile.loadListOfInformationEntry("emailEntrysTest.dat");
		
		assertEquals(ReadAndWriteFile.loadListOfInformationEntry("emailEntrysTest.dat").get(0).getDate(),date);
		
		assertTrue(((EmailEntry)ReadAndWriteFile.loadListOfInformationEntry("emailEntrysTest.dat").get(0)).getContent().contains("Content"));
		assertTrue(((EmailEntry)ReadAndWriteFile.loadListOfInformationEntry("emailEntrysTest.dat").get(0)).getWriterName().contains("Person"));
		assertTrue(((EmailEntry)ReadAndWriteFile.loadListOfInformationEntry("emailEntrysTest.dat").get(0)).getSubject().contains("Subject"));		
		
		System.out.println(ReadAndWriteFile.loadListOfInformationEntry("nonExistingFile.dat"));
		assertEquals( ReadAndWriteFile.loadListOfInformationEntry("nonExistingFile.dat"),new ArrayList<InformationEntry>());
		
		

		
	}

	/**
	 * Test save list of filters.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testSaveListOfFilters() throws IOException {
		List<String> key_words_filter = ReadAndWriteFile.loadListOfFilters(KEY_WORDS_FILTER_FILE_NAME);
		
		ReadAndWriteFile.saveListOfFilters(null, key_words_filter);
		File file = new File (TEST_FILE_NAME);
		BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
		out.write("TEST_FILE_NAME");
		ReadAndWriteFile.saveListOfFilters(TEST_FILE_NAME, key_words_filter);
		out.write("TEST_FILE_NAME");
		out.close();
		
		FileOutputStream fos = new FileOutputStream(new File(TEST_FILE_NAME));
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write((byte)1); 
		
		
		ReadAndWriteFile.saveListOfFilters(KEY_WORDS_FILTER_FILE_NAME, null);
		ReadAndWriteFile.saveListOfFilters(null, null);
		
		ReadAndWriteFile.saveListOfFilters(KEY_WORDS_FILTER_FILE_NAME, key_words_filter);
		ReadAndWriteFile.saveListOfFilters("\n", key_words_filter);
		
	}

	/**
	 * Test load list of filters.
	 */
	@Test
	public void testLoadListOfFilters() {
		List<String> key_words_filter = ReadAndWriteFile.loadListOfFilters(KEY_WORDS_FILTER_FILE_NAME);
		ReadAndWriteFile.saveListOfFilters(KEY_WORDS_FILTER_FILE_NAME, key_words_filter);
		List<String> key_words_filter1 = ReadAndWriteFile.loadListOfFilters(KEY_WORDS_FILTER_FILE_NAME);
		assertEquals(key_words_filter, key_words_filter1);
		
		ReadAndWriteFile.loadListOfFilters(TEST_FILE_NAME);
		ReadAndWriteFile.loadListOfFilters("");
		ReadAndWriteFile.saveListOfFilters("NoFile.txt", key_words_filter);
		ReadAndWriteFile.loadListOfFilters("NoFile.txt");
		ReadAndWriteFile.loadListOfFilters(null);
		
		try{  		
    		File file = new File("NoFile.txt");
        	
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}   
    	}catch(Exception e){
    		e.printStackTrace();
    		
    	}
	}
		
		
	
	


}
