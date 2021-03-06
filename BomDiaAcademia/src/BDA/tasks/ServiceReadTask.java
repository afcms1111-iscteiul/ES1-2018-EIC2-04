package BDA.tasks;

import BDA.threads.InformationEntryGatherer;

/**
 * The Interface ServiceReadTask.
 * @author Alexandre Mendes
 * @version 1.0
 */
public interface ServiceReadTask extends Task{ //
	
	/**
  * Sets the barrier.
  *
  * @param barrier the new barrier
  */
 public void setBarrier(InformationEntryGatherer barrier);
	
}
