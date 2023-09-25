package com.hartehanks;

import java.util.*;

//
// the GlobalFinishManager is a runnable Thread (run by the GlobalDriver)
// that is responsible for finalising output data prepared by the GlobalChild.
// This is then output by the GlobalFinisher agent.
//
public class GlobalFinishManager extends Thread
{
	private GlobalDriver			parent = null;
        private int                             inOrgOffset = -1;
        private int[][]                         outFields;
        private int[][]                         outXlate;
        private boolean                         acronyms = false;
        private int                             maxFields;
        private String                          lastFirstList = "";
//
// the public constructor method for the GlobalFinishManager is invoked by the
// globalDriver. the child is told who the driver is, the childs' thread id
// given a list of possible optima server host/ip addresses, output field
// offsets, lengths and data mappings. the same for the input oraddrl fields
// and finally a debug flag (set by the user in the parameter file)
//
// The constructor simply squirrels these away for later when it begins to
// process.
// for tracking, the current instance count of GlobalFinishManager class is
// incremented
// so that it can be logged when instances actually appear or are garbage
// collected.
//
	public GlobalFinishManager(GlobalDriver parent, int maxFields,
		int inOrgOffset, int[][] outFields, int[][] outXlate,
		boolean acronyms, String lastFirstList)
	{
	    this.parent = parent;
	    this.maxFields = maxFields;
	    this.inOrgOffset = inOrgOffset;
	    this.outFields = outFields;
	    this.outXlate = outXlate;
	    this.acronyms = acronyms;
	    this.lastFirstList = lastFirstList;
	    FormatName.getInstance(parent.getLogWriter(), true, lastFirstList);
	}

//
// the 'run' method is a required interface method for a Thread. it is the
// threads' processing point and the thread remains inside the run method all
// the time it wishes to remain alive. (This doesn't mean it can't call
// other methods - it just mustn't return from here or it will die!
//
// The run method simply opens the server connection, registers that fact with
// the GlobalDriver and then sets to work getting input records from the
// Globaldriver, processing them through the optima server and returning the
// formatted output to the GlobalDriver for outputting.
// When the globalDriver has no more data, the loop breaks and the run method
// closes the server connection and returns - causing the death of the child.
//
// all requests for Parsing, validation and name/Address formatting are either
// performed inline or invoke deeper methods to perform the functionality.
// all optima server calls are headless timed in case they hang.
//
// All server calls have their return code checked by the 'testError' method -
// unfortunately the return codes are either 'good' or 'bad' and don't really
// defined why they should be bad. A certain amount of retry logic has been
// embedded here so that a failed 'parse' or 'validate' has multiple attempts
// at succeeding until deemed to have really failed (due to repeated failures).
//
	public void run()
	{
	    while (!parent.eof || parent.hashQueue.size() > 0)
	    {
		int max = (parent.hashQueue.size() / 3)+1;
		max = (max < 3 && parent.hashQueue.size() > 3) ? 3 : max;
		max = (parent.hashQueue.size() > 0) ? max : 0;

		for (int i = 0; i < max ;  i++)
		{
		    GlobalQueueHeader gqh =
			(GlobalQueueHeader)parent.hashQueue.elementAt(i);
		    if (gqh.finishState == GlobalQueueHeader.IS_FINISHED)
                    {
			//System.err.println("Moving entry "+i+" to outQueue");
			parent.outQueue.add(gqh);
			parent.hashQueue.removeElementAt(i);
			i--;
			max--;
		    }
		    else if (gqh.finishState ==
					GlobalQueueHeader.NEEDS_FINISHING)
		    {
			GlobalQueueEntity gqe = gqh.globalQueueEntity[
					gqh.globalQueueEntity.length - 1];
			if (gqe.recordStatus > GlobalQueueEntity.INPUT)
			{
			    //System.err.println("Finisher starting on "+i);
			    gqh.finishState = GlobalQueueHeader.IS_FINISHING;

			    GlobalQueueFinisher gqf =
						new GlobalQueueFinisher(gqh,
				parent, maxFields, inOrgOffset,
				outFields, outXlate, acronyms, lastFirstList);

			    //gqf.setPriority(Thread.MAX_PRIORITY);
			    gqf.start();
			}
		    }
		}
		try
		{
		    Thread.sleep(3000);
		}
		catch (InterruptedException ie) {}
	    }
	    System.err.println("GloParse: GlobalFinishManager complete");
	}
}
