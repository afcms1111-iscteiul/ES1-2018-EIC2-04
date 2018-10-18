package comparators;

import java.util.Comparator;

import entry_objects.InformationEntry;

public class DateComparator implements Comparator<InformationEntry> {

	@Override
	public int compare(InformationEntry informationEntry1, InformationEntry informationEntry2) {
		return informationEntry2.getDate().compareTo(informationEntry1.getDate()); // N�o fa�o ideia se � para fazer assim
	}
		
}
