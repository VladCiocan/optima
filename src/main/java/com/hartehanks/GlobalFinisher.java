package com.hartehanks;

import com.hartehanks.optima.api.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import com.hartehanks.dev.io.*;
import com.hartehanks.dev.misc.*;
import com.hartehanks.dev.app.*;

//
// the GlobalFinisher is a runnable Thread (run by the GlobalDriver)
// that is
// responsible for finalising output data prepared by the GlobalChild.
//
//
public class GlobalFinisher extends Thread
{
	private GlobalDriver			parent = null;
        private int                             inOrgOffset = -1;
        private int[][]                         outFields;
        private int[][]                         outXlate;
        private boolean                         acronyms = false;
        private int                             maxFields;

        private long                            printLastTimeCount = 0;
        private long                            printThisTimeCount;
        private long                            printIdleTimeCount;
	private long                            lastTimeCount =
                        Calendar.getInstance().getTime().getTime() / 1000;
        private long                            thisTimeCount;
        private long                            lastReportedCount = 0;
	protected int				mustIdle = 0;

//
// the public constructor method for the GlobalFinisher is invoked by the
// globalDriver. the child is told who the driver is, the childs' thread id
// given a list of possible optima server host/ip addresses, output field
// offsets, lengths and data mappings. the same for the input oraddrl fields
// and finally a debug flag (set by the user in the parameter file)
//
// The constructor simply squirrels these away for later when it begins to
// process.
// for tracking, the current instance count of GlobalFinisher class is
// incremented
// so that it can be logged when instances actually appear or are garbage
// collected.
//
	public GlobalFinisher(GlobalDriver parent, int maxFields,
		int inOrgOffset, int[][] outFields, int[][] outXlate,
		boolean acronyms)
	{
	    this.parent = parent;
	    this.maxFields = maxFields;
	    this.inOrgOffset = inOrgOffset;
	    this.outFields = outFields;
	    this.outXlate = outXlate;
	    this.acronyms = acronyms;
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
	    boolean needsReport = false;
            printLastTimeCount =
                        Calendar.getInstance().getTime().getTime() / 1000;

	    while (!parent.eof || parent.hashQueue.size() > 0 ||
						parent.outQueue.size() > 0)
	    {
		if (parent.outQueue.size() > 0)
		{
		    GlobalQueueHeader gqh =
			(GlobalQueueHeader)parent.outQueue.elementAt(0);

		    if (gqh.finishState != GlobalQueueHeader.IS_FINISHED)
		    {
			System.err.println("IS finished error "+
							gqh.finishState);
			System.exit(1);
		    }

		    if (mustIdle > 0)
		    {
			mustIdle--;
			parent.printLog("Idler state "+mustIdle);
		    }

		    int numFinished = 0;
		    boolean canBeRefinished = true;

		    //System.err.println("Finisher B finishing Leader "+
					//gqh.globalQueueEntity.length);

		    for (int k = 0; k < gqh.globalQueueEntity.length; k++)
		    {
			GlobalQueueEntity gqe = gqh.globalQueueEntity[k];
			if (gqe == null ||
				gqe.recordStatus == GlobalQueueEntity.WRITTEN)
			{
			    numFinished++;
			    continue;
			}
//
// Could be that a finisher has exited due to timeout - so some records
// are unformatted.
//
			if (gqe.recordStatus != GlobalQueueEntity.FORMATTED)
			{
			    System.err.println("Unfinished error");
			    System.exit(1);
			}

			for (int j = 0; j < gqe.recordVector.size(); j++)
			{
			    GlobalRecord gr =
				   (GlobalRecord)gqe.recordVector.elementAt(j);

			    if (parent.sbos != null)
			    {
				try
				{
				    parent.sbos.write(gr.data);
				}
				catch (IOException ioe)
				{
				    System.err.println("Exception during "+
						"write of output record "+
						parent.numRecordsOut+": "+
						ioe.getMessage());
				    parent.printLog("Exception during "+
						"write of output record "+
						parent.numRecordsOut+": "+
						ioe.getMessage());
				    System.exit(19);
				}
			    }
			    parent.numRecordsOut++;
			    printThisTimeCount = Calendar.getInstance().
						getTime().getTime() / 1000;
			    if ((printThisTimeCount >=
					 printLastTimeCount + 60 &&
					(parent.numRecordsOut %
						parent.minNotifyInterval)==0)||
					(parent.printNth > 0 &&
					(parent.numRecordsOut %
							 parent.printNth)==0))
			    {
				if (parent.numRecordsOut > lastReportedCount)
				{
				    System.err.println("rec "+
					parent.numRecordsOut+ "  "+
					gqe.countryStat.iso3+ "/"+
					Conversion.toPaddedString(
					gqe.countryStat.postCode, 10)+
					"  TZ="+GlobalChild.instanceCount+
					"/"+ parent.numZero+
					" IPC="+ parent.numRecordsIn+
					"/"+ parent.numRecordsParsed+
					"/"+ (1+parent.numParsedWritten)+
					" Q="+ (parent.hashQueue.size()+
						parent.outQueue.size())+
					"/"+ parent.processQueueList.size());

				    parent.printLog("rec "+
					parent.numRecordsOut+ "  "+
					gqe.countryStat.iso3+ "/"+
					Conversion.toPaddedString(
					gqe.countryStat.postCode, 10)+
					"  T="+GlobalChild.instanceCount+
					" Z="+ parent.numZero+
					" IPC="+ parent.numRecordsIn+
					"/"+ parent.numRecordsParsed+
					"/"+ (1+parent.numParsedWritten)+
					" Q="+ (parent.hashQueue.size()+
						parent.outQueue.size())+
					"/"+ parent.processQueueList.size());
				}
				printLastTimeCount = printThisTimeCount;
				lastReportedCount = parent.numRecordsOut;
			    }
			    if ((parent.numRecordsOut % 1000) == 0)
			    {
				thisTimeCount = Calendar.getInstance().
						getTime().getTime() / 1000;
				if (thisTimeCount >= lastTimeCount + 60)
				{
				    needsReport = true;
				}
			    }
			}
			gqe.recordStatus = GlobalQueueEntity.WRITTEN;
			parent.addCountryData(gqe.countryStat);
			parent.numParsedWritten++;
			numFinished++;
//
// Release this resource - it's the biggest memory hog in the system
//
			gqh.globalQueueEntity[k].clear();

			if (needsReport)
			{
			    parent.printCI((double)(
						parent.numRecordsOut -
						parent.lastNumRecordsOut));
			    parent.resetHostUsage();
			    lastTimeCount = thisTimeCount;
			    needsReport = false;
			}
		    }
		    if (numFinished != gqh.globalQueueEntity.length)
		    {
			System.err.println("GQH numFinished discrepancy");
			System.exit(1);
		    }
		    parent.outQueue.removeElementAt(0);
		}
		else
		{
		    try
		    {
			Thread.sleep(1000);
		    }
		    catch (InterruptedException ie) {}
		}
		printThisTimeCount = Calendar.getInstance().
						getTime().getTime() / 1000;
		if (printThisTimeCount >= printLastTimeCount + 45 &&
		   		mustIdle < 3 && parent.hashQueue.size() > 1)
		{
		    mustIdle = 3;
		    printIdleTimeCount = printLastTimeCount;
		}
		if (mustIdle > 0 &&
				 printThisTimeCount >= printIdleTimeCount + 60)
		{
		    System.err.println("rec "+
					parent.numRecordsOut+
					"                "+
					"  TZ="+GlobalChild.instanceCount+
					"/"+ parent.numZero+
					" IPC="+ parent.numRecordsIn+
					"/"+ parent.numRecordsParsed+
					"/"+ (1+parent.numParsedWritten)+
					" Q="+ (parent.hashQueue.size()+
						parent.outQueue.size())+
					"/"+ parent.processQueueList.size());

		    printIdleTimeCount = printThisTimeCount;
		    lastReportedCount = parent.numRecordsOut;
		}
	    }
	    System.err.println("GloParse: Finisher completed "+
					parent.numRecordsOut+" records");
	    parent.report();
	}
}
