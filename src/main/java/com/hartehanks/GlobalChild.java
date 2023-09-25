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
// the GlobalChild is a runnable Thread (run by the GlobalDriver) that is
// responsible for communicating with the Optima server across the network.
// Although resilient it may unexpectedly hang. hangups like these are handled
// by wrapping each service call with a timer - which will trip if the service
// call takes too long.
//
// The GlobalChild constructor is the first point of contact for any new
// instance of an GlobalChild.
//
public class GlobalChild extends Thread
{
	private GlobalDriver parent = null;
	private int				myId = -1;
	private int				myHostId = -1;
	private int				maxThreadCount = 10;
	private CEnhancedOptimaServer		globalServer = null;
	private COptimaContact			parsedAddress = null;
	private COptimaContact			validatedAddress = null;
	private COptimaContact			enhancedAddress = null;
	private COptimaContact			formattedNA = null;
	private COptimaContact			keepContact = null;
	public	static String[]			acrNames =
					new String[OFT.NumFieldsPlusOne];
	public  static int			busyConnects = 0;
	private static String			alphaString =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private long				callTimeoutMillis =
				GlobalDriver.defaultSearchTimeout * 1000;
	private int				currentSearchTimeout =
				GlobalDriver.defaultSearchTimeout;
	private long				callTimeoutMillisStart;
	private long				callTimeoutMillisEnd;

	private GlobalHost[]			globalHosts = null;
	private boolean				optInit = false;
	private GlobalQueueEntity queueEntity = null;
	private COptimaContact[]		globalContacts = null;
	private GlobalRecord globalRecord = null;
	private int[][]				inAddressData;
	private int				maxInAddressCount = 0;
	private NonstopTimer conTimer = null;
	protected boolean			redo = false;
	private boolean				debug = false;
	private boolean				acronyms = false;
	private boolean				doSmart = false;
	private int				smartLevel = 0;
	//private boolean				nowSmart = false;
	//private boolean				nowSmarter = false;
	private boolean				chat = false;
	private boolean				chatted = false;
	private Hashtable			countryOptions;
	private int				countryOpts =
				GlobalDriver.defaultSearchTimeout << 12;
	private int				lastPostCodeWeight = 0;
	private int				lastCityWeight = 0;
	private int				lastTimeout = 0;
	private boolean				doLatLong = false;
	private long				numRecords = 0;
	private int				totalRetries = 0;
	private int				allRetries = 0;
	private int				numRetries = 0;
	private int				totalTimeouts = 0;
	private int				numTimeouts = 0;
	private int				numErrors = 0;
	private StringBuffer			cat8910 = new StringBuffer();
	private Hashtable			acceptanceLevel =
							new Hashtable();
	private String				companyName = null;
	private boolean				companyBypass = false;
	private boolean				companyLine = false;
	private boolean				companyQuotes = false;
	private boolean				hasHung = false;
	private boolean				forceUpdate = false;

	private boolean				serRes;
	private boolean				optRes;
	private boolean				runRes;
	private boolean				valRes;
	private boolean				forRes;
	private boolean				orgRes;
	private boolean				accepted;
	private boolean				formatted;
	private boolean				clear;

	private int				openTries;
	private int				sleepTime;
	private int				numPopLines;
	private int				halfLen;
	private int				validatedLevel;
	private int				accLevel;
	private int				copyInd;
	private int				nDiffs;

	private Vector				addVec;

	private String				adrLine;
	private String				regionFix;
	private String				postcodeFix;
	private int				postcodeMid;
	private String				departmentFix;
	private String				companyFix;
	private String				streetFix;
	private String				premiseFix;
	private String				deptA;
	private String				compA;
	private String				ctryA;
	private String				fixBuild;
	private String				fixSub;
	private String				fixIso3;
	private String				dumpRes;
	private static String			sdumpRes;
	private String				copyIso;
	private String				country;
	private String				org;
	private String				runPac;
	private String				runComp;

	private Integer				accInt;

	private StringBuffer			sbm = new StringBuffer(100);
	private StringBuffer			sm3 = new StringBuffer(100);
	private static StringBuffer		ssb;

	public static int			instanceCount = 0;
	private static int			normalPostCodeWeight = -1;
	private static int			normalCityWeight = -1;

	public static int[]			oftAddressEnums =
	{
	    OFT.AddressLine1, OFT.AddressLine2, OFT.AddressLine3,
	    OFT.AddressLine4, OFT.AddressLine5, OFT.AddressLine6,
	    OFT.AddressLine7, OFT.AddressLine8,
	};

	private int[] clearFields =
	{
	    OFT.Company,
	    OFT.Department,
	    OFT.SubBuilding,
	    OFT.Building,
	    OFT.Premise,
	    OFT.Street,
	    OFT.SubStreet,
	    OFT.POBox,
	    OFT.SubCity,
	    OFT.City,
	    OFT.Postcode,
	    OFT.Cedex,
	    OFT.DPS,
	    OFT.Region,
	    OFT.Principality,
	    OFT.AddressLine1,
	    OFT.AddressLine2,
	    OFT.AddressLine3,
	    OFT.AddressLine4,
	    OFT.AddressLine5,
	    OFT.AddressLine6,
	    OFT.AddressLine7,
	    OFT.AddressLine8,
	};
//
// the public constructor method for the GlobalChild is invoked by the
// globalDriver. the child is told who the driver is, the childs' thread id
// given a list of possible optima server host/ip addresses, output field
// offsets, lengths and data mappings. the same for the input oraddrl fields
// and finally a debug flag (set by the user in the parameter file)
//
// The constructor simply squirrels these away for later when it begins to
// process.
// for tracking, the current instance count of GlobalChild class is incremented
// so that it can be logged when instances actually appear or are garbage
// collected.
//
	public GlobalChild(GlobalDriver parent, int myId,
                       GlobalHost[] globalHosts, int startHostId,
                       int[][] inAddressData, int maxInAddressCount,
                       boolean debug, boolean doSmart,
                       Hashtable countryOptions)
	{
	    this.parent = parent;
	    this.myId = myId;
	    this.globalHosts = globalHosts;
	    this.myHostId = startHostId;
	    this.inAddressData = inAddressData;
	    this.maxInAddressCount = maxInAddressCount;
	    this.debug = debug;
	    this.acronyms = acronyms;
	    this.doSmart = doSmart;
	    this.countryOptions = countryOptions;
	    changeInstanceCount(1);
	    //formatName = FormatName.getInstance(parent.getLogWriter(), true,
							//lastFirstList);
	    //formatName.setDebug(parent.getLogWriter());
	}

//
// Synchronised method to subtract one to the instance count and report
// this is invoked by the Object finalize method.
//
	private static synchronized void changeInstanceCount(int upOrDown)
	{
	    instanceCount += upOrDown;
	    //parent.printLog("Thread "+myId+": instance count is "+
							//instanceCount);
	}

//
// This is the method, invoked from a number of locations within the thread
// to start or restart a server connection.
//
// First time around the parent has given the child a specific host id to which
// it should try and connect. if that fails, or any part of the later process
// fails to communicate with the server the child can, autonomously, attempt
// connection to any other host in the list
//
// note that the connection (as are all globalServer TCP/IP calls) is wrapped
// in a headless timer. if the timer pops befor the call completes then it
// must be assumed that the connection attempt has hung. The timeout is set
// to 60 seconds - which should be ample time.
//
// Once the connection is made the child will attemp to load the OFT lookup
// table from the server. this is a 'static' table, which if already loaded
// in an earlier instance, will be skipped.
//
	private boolean openServerConnection()
	{
	    if (hasHung)
	    {
		return false;
	    }
	    countryOpts = GlobalDriver.defaultSearchTimeout << 12;
	    serRes = false;
	    closeServerConnection(-1);
	    parent.printLog("Thread "+myId+": Connecting to GA "+
			"server at host "+globalHosts[myHostId].hostName);
	    openTries = 0;

	    sleepTime = 5000;

	    while(!serRes || globalServer == null)
	    {
		sleepTime += 1000;
		numRetries = 0; // stop conn leaving prematurely
		conTimer.setTimer(20);
		globalServer = new CEnhancedOptimaServer();
		globalServer.setHost(globalHosts[myHostId].hostName);
		globalServer.setTimeout(GlobalDriver.serverTimeout);
		serRes = globalServer.Connect();
		conTimer.setTimer(-1);

		if (!serRes)
		{
		    serRes = testError(serRes, null,
						"Bad Server Connect Return");
		}
		if (!serRes)
		{
		    openTries++;
		    if (openTries == 1)
		    {
			parent.printLog("Thread "+myId+": Connect attempt "+
			"failed to GA server at "+
			globalHosts[myHostId].hostName+ " - retrying");
		    }
		    else if (openTries >= 10)
		    {
			childHung(61, null);
			closeServerConnection(-2);
			return false;
		    }
		    try
		    {
			parent.printLog("Thread "+myId+
				": Waiting for server for "+(sleepTime / 1000)+
				" seconds");
			Thread.sleep(sleepTime);
		    }
		    catch (InterruptedException ie) { }
		}
	    }
	    //globalHosts[myHostId].setOptInit(false);
	    optInit = false;

	    parent.printLog("Thread "+myId+": Connected to GA "+
			"server at host "+globalHosts[myHostId].hostName);
	    parent.setHostNumber(myId, myHostId);
	    maxThreadCount = parent.getThreadCountForHost(myHostId);

	    if (setServerOptions())
	    {
		redo = true;
	    }
	    if (globalServer == null)
	    {
		parent.printLog("Thread "+myId+": Connection to GA "+
			"server at host "+globalHosts[myHostId].hostName+" failed - unable to set options");
		return false;
	    }

	    return true;
	}

	private boolean setServerOptions()
	{
	    boolean reset = false;

	    while (globalServer != null && optInit == false)
	    {
		int goes = 0;
//
// Set check bad words in name
//
		goes = 0;
		do
		{
		    conTimer.setTimer(20);
		    optRes = globalServer.SetOption(OON.NameWordCheck, 1);
		    conTimer.setTimer(-1);
		    if (!optRes)
		    {
			try
			{
			    Thread.sleep(1000);
			}
			catch (InterruptedException ie) {}
		    }
		    goes++;
		    if (goes > 100)
		    {
			childHung(51, null);
			closeServerConnection(-3);
			return false;
		    }
		}
		while(!optRes);

//
// Set check bad words in address
//
		goes = 0;
		do
		{
		    conTimer.setTimer(20);
		    optRes = globalServer.SetOption(OON.AddressWordCheck, 1);
		    //optRes = globalServer.SetOption(OON.StandardisationDebugOutput, 1);

		    //optRes = true;
		    conTimer.setTimer(-1);
		    if (!optRes)
		    {
			try
			{
			    Thread.sleep(1000);
			}
			catch (InterruptedException ie) {}
		    }
		    goes++;
		    if (goes > 100)
		    {
			childHung(52, null);
			closeServerConnection(-4);
			return false;
		    }
		}
		while(!optRes);

//
// Set address search timeout to 30 seconds
//
		goes = 0;
		do
		{
		    conTimer.setTimer(20);
		    optRes = globalServer.SetOption(OON.SearchTimeout,
							currentSearchTimeout);
		    //optRes = true;
		    conTimer.setTimer(-1);
		    if (!optRes)
		    {
			try
			{
			    Thread.sleep(1000);
			}
			catch (InterruptedException ie) {}
		    }
		    goes++;
		    if (goes > 100)
		    {
			childHung(53, null);
			closeServerConnection(-5);
			return false;
		    }
		}
		while(!optRes);

		if (normalPostCodeWeight < 0)
		{
		    goes = 0;
		    int pcw = -1;
		    do
		    {
			conTimer.setTimer(20);
			pcw = globalServer.GetOption(OON.PostcodeWeight);
			conTimer.setTimer(-1);
			optRes = (pcw != -1);
			if (!optRes)
			{
			    try
			    {
				Thread.sleep(1000);
			    }
			    catch (InterruptedException ie) {}
			}
			goes++;
			if (goes > 100)
			{
			    childHung(52, null);
			    closeServerConnection(-6);
			    return false;
			}
		    }
		    while(!optRes);
		    if (pcw > normalPostCodeWeight && normalPostCodeWeight==-1)
		    {
			normalPostCodeWeight = pcw;
			System.err.println("GloParse: Normal Postcode Weight "+
						"is "+ normalPostCodeWeight);
		    }
		}

		if (normalCityWeight < 0)
		{
		    goes = 0;
		    int pcw = -1;
		    do
		    {
			conTimer.setTimer(20);
			pcw = globalServer.GetOption(OON.CityWeight);
			conTimer.setTimer(-1);
			optRes = (pcw != -1);
			if (!optRes)
			{
			    try
			    {
				Thread.sleep(1000);
			    }
			    catch (InterruptedException ie) {}
			}
			goes++;
			if (goes > 100)
			{
			    childHung(52, null);
			    closeServerConnection(-7);
			    return false;
			}
		    }
		    while(!optRes);

		    if (pcw > normalCityWeight && normalCityWeight == -1)
		    {
			normalCityWeight = pcw;
			System.err.println("GloParse: Normal City Weight is "+
							normalCityWeight);
		    }
		}

		optInit = true;
		reset = true;
	    }
	    return reset;
	}
//
// This is the opposite to the open server connection. It should, in theory,
// always work but it's not guaranteed - so it too is headless timed.
//
	private void closeServerConnection(int entryCaller)
	{
	    if (globalServer != null)
	    {
		GlobalDisconnect disc = new GlobalDisconnect(globalServer,
							myId, parent);
		disc.start();
		disc = null;
		globalServer = null;
	    }
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
	private synchronized int updateSleepTime(int change)
	{
	    busyConnects += change;
	    //busyConnects = (busyConnects < -1) ? -1 : busyConnects;
	    //System.err.println("Sleep time "+busyConnects+" ("+change+")");
	    return busyConnects;
	}

	public void run()
	{
	    if (myId != 0)
	    {
		int waitTime = updateSleepTime(1);
		for (int i = 0; i < waitTime && GlobalDriver.stopping == false;
									i++)
		{
		    try
		    {
			Thread.sleep(500);
		    }
		    catch (InterruptedException ie) {}
		}
		updateSleepTime(-1);
	    }

	    conTimer = new NonstopTimer(new ResetActionListener());
	    conTimer.start();

	    cat8910.ensureCapacity(303);
	    redo = false;

//
//  Obtain host id for the first time only - after that, any connection
//  problems will result in internal host id cycling.
//  Note that parent may stop us at some point in the future if the host CI
// becomes unworkable for the host that we're on
//
	    if (myHostId < 0)
	    {
		myHostId = parent.getHostNumber();
	    }
	    parent.registerStart(this, myId);
	    if (hasHung || openServerConnection() == false)
	    {
		conTimer.stopTimer();
		closeServerConnection(9);
		return;
	    }

//
// fetch a record from the GlobalDriver - if this returns 'null' then no more
// work to do (so the loop finishes)
//
	    while((queueEntity = parent.getRecord(this, myId)) != null)
	    {
		globalContacts = null;
		chat = (queueEntity.errorCounter > 1) ? true : false;
		if (chat)
		{
		    try
		    {
			Thread.sleep(10000);
		    }
		    catch (InterruptedException ie) {}
		}

		numRecords++;
		numRetries = 0;
		totalTimeouts = 0;
		totalRetries = 0;
		allRetries = 0;
		numErrors = 0;
		smartLevel = 0;
		queueEntity.dqs = '1';
		queueEntity.fixUp = "";

		globalRecord =
			(GlobalRecord)queueEntity.recordVector.elementAt(0);


//
// this 'redo' loop (hate jump backs) tries to process the record through the
// optima server software. The loop will continue around and around if errors
// are returned until either the record gets through or the number of error
// retries is enough to prove that it's not going to work under any
// circumstances.
//
// on occasions, the error test logic may close the server connection so that
// an alternative server is selected and the record parse etc. attempted there.
//
//
redo:		do
		{
		    redo = false;
		    if (hasHung)
		    {
			conTimer.stopTimer();
			closeServerConnection(10);
			return;
		    }
		    if (globalServer == null)
		    {
			if (openServerConnection() == false)
			{
			    conTimer.stopTimer();
			    return;
			}
		    }
//
// The output record area is cleared to 'blank'
// The input record is disected and fields moved into the GlobalContact
// class ready for the first parse attempt. If there are more than 8 address
// lines coming in then some of these are concatenated into one line (which
// optima should be able to deal with).#
//
// Note here that all long fields over 75 characters are truncated as this
// is proven to cause memory overwrites within the optima server.
//
		    setupInputAddress();
		    keepContact = new COptimaContact(parsedAddress);
//
// The parse is attempted. headless timer catches any hangs.
// the server result is checked and the parse retried if it failed.
//
		    runRes = false;
		    numRetries = 0;
		    numTimeouts = 0;

		    if (globalServer == null)
		    {
			System.err.println("Entering loop with null");
		    }
		    chatted = false;
		    while(!runRes)
		    {
			if (chat && !chatted)
			{
			    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				parsedAddress, "Chat Input Address");
			    parent.printLog("Thread "+myId+
						": Calling Standardise");
			    chatted = true;
			}
			if (numTimeouts > 0)
			{
			    globalHosts[myHostId].syncHost(myId,
							GlobalHost.LOCK);
			}
			else if (globalHosts[myHostId].hostLock)
			{
			    globalHosts[myHostId].syncHost(myId,
							GlobalHost.WAITLOCK);
			}
			if (globalServer == null)
			{
			    System.err.println("In loop with null");
			}
			conTimer.setTimer(30+ GlobalDriver.serverTimeout);
			//parent.printLog("Thread "+myId+": Calling Std1");
			callTimeoutMillisStart = System.currentTimeMillis();
                        orgRes = runRes = globalServer.StandardiseAddress(
							parsedAddress);
                        callTimeoutMillisEnd = System.currentTimeMillis();
                        conTimer.setTimer(-1);
			if (numTimeouts > 0)
			{
			    globalHosts[myHostId].syncHost(myId,
							GlobalHost.UNLOCK);
			}

			if (hasHung)
			{
			    conTimer.stopTimer();
			    closeServerConnection(11);
			    return;
			}
			//parent.printLog("Thread "+myId+": Called Std1");
			if (chat)
			{
			    parent.printLog("Thread "+myId+
					": Called Standardise: res="+runRes);
			}
			if (!runRes)
			{
			    runRes = testError(runRes, parsedAddress,
						"Bad Parse Server Return");
			}
			if (globalServer == null)
			{
			    redo = true;
			    continue redo;
			}

                        if (runRes && runRes == orgRes && numTimeouts < 10 &&
				callTimeoutMillisStart + callTimeoutMillis <
				callTimeoutMillisEnd)
                        {
			    numTimeouts++;
			    totalTimeouts++;
                            parent.printLog("Thread "+myId+
                                ": Call timeout First Parse  "+
                                (callTimeoutMillisEnd-callTimeoutMillisStart)+
				"/"+numTimeouts+
				"  Record: "+ globalRecord.recordNumber);
			    if (numTimeouts > 1)
			    {
				try
				{
				    Thread.sleep(numTimeouts * 1000);
				}
				catch (InterruptedException ie) {}
			    }
			    runRes = false;
			    parsedAddress = new COptimaContact(keepContact);
			    continue;
                        }
			else if (numTimeouts > 0)
			{
                            parent.printLog("Thread "+myId+
                                ": Call timeout cleared for "+
				"record: "+ globalRecord.recordNumber);
			}
		    }


		    runPac = parsedAddress.getField(OFT.ACR);
		    runComp = parsedAddress.getField(OFT.Company);
//
// If smart mode but not yet tried it then set it and go around again.
//
		    if (doSmart)
		    {
			switch (smartLevel)
			{
//
// Bypass the company name (remove from the parser input and pass around the
// outside and re-insert later if the address doesn't parse or the company
// name on input doesn't pass to the parsed output company field
//
			    case 0:
				if (runPac.indexOf("A0T0R0Z0") > 0 ||
					(companyLine == true &&
							runComp.length() == 0))
				{
				    if (debug || (numRecords % 100) == 0)
				    {
					dumpContactRecord(parent,
					    globalHosts[myHostId].hostName,
					    globalRecord.recordNumber, myId,
					    parsedAddress,
					    "Smart Switch Input Address");
				    }
				    queueEntity.dqs = '2';
				    if (runPac.indexOf("A0T0R0Z0") < 0)
				    {
					queueEntity.dqs = '3';
				    }
				    smartLevel = 1;
				    redo = true;
				    continue redo;
				}
// Parsed correctly
				break;
//
// Check for situation where incoming company line declared and present but
// fallback parse has populated company field with something, probably from
// the physical address. In this case put the company name directly into the
// parser output field before parse so it's already in position.
//
			    case 1:
				if (runComp.length() > 0 &&
					companyName != null &&
					companyName.length() > 0)
				{
				    if (debug || (numRecords % 50) == 0)
				    {
					dumpContactRecord(parent,
					    globalHosts[myHostId].hostName,
					    globalRecord.recordNumber, myId,
					    parsedAddress,
					    "Smart Switch Company Corrupt");
				    }
				    queueEntity.dqs = '4';
				    smartLevel = 2;
				    redo = true;
				    continue redo;
				}
// Parsed correctly
				break;

//
// If aaN country code then try joining A3 to A6 as input
//
			    case 2:
				String iso3 = parsedAddress.getField(
							OFT.CountryISO).trim();
				if (iso3.length() == 3 &&
					Character.isDigit(iso3.charAt(2)) &&
					iso3.equalsIgnoreCase("CH2")== false &&
                                        iso3.equalsIgnoreCase("KO2")== false)
				{
				    if (debug || (numRecords % 50) == 0)
				    {
					dumpContactRecord(parent,
					    globalHosts[myHostId].hostName,
					    globalRecord.recordNumber, myId,
					    parsedAddress,
					    "Smart Switch Concatenation Mode");
				    }
				    queueEntity.dqs = '5';
				    smartLevel = 3;
				    redo = true;
				    continue redo;
				}
//				No Break - failed - so bad DQS
//
// Last smart mode exhausted
//
			    default:
				break;

			} // end of doSmart switch
		    } // end of doSmart

		    queueEntity.dqs = (runPac.indexOf("A0T0R0Z0") > 0) ?
							'9' : queueEntity.dqs;
		    if (queueEntity.dqs == '9')
		    {
			if (numPopLines < 2 && runPac.length() == 21 &&
							runPac.startsWith("U"))
			{
			    runPac = "C" + runPac.substring(1);
			}
			parsedAddress.setField(OFT.ACR, runPac);
		    }

//
// Fix Company Placeholder if in OFT.Company
//
		    if (runComp.toUpperCase().indexOf("SNPLACEHOLDER") >= 0 ||
			(companyName != null &&
			companyName.toUpperCase().indexOf(
						"SNPLACEHOLDER") >= 0))
		    {
			parsedAddress.setField(OFT.Company, "");
			companyName = null;
		    }

//
// If standardised (as determined by the Optima ACR code starting with a 'C')
// then validation s to be done as well. Again, the validate is headless timed
// in case of hang and the error code checked with a loop around until success
// or solid proven failure.
//
//
		    if (companyQuotes)
		    {
			String coName = parsedAddress.getField(OFT.Company);
			if (coName.length() >= 2 &&
				coName.endsWith("\"") == false &&
				coName.charAt(coName.length() - 2) != '"' &&
				coName.indexOf('"') >= 0)
			{
			    //System.err.println("Coname quote lost "+coName);
			    parsedAddress.setField(OFT.Company, coName+"\"");
			}
		    }
		    if (companyName != null && companyName.length() > 0)
		    {
			parsedAddress.setField(OFT.Company, companyName);
			companyName = null;
		    }

		    String iso3 = parsedAddress.getField(OFT.CountryISO);
		    iso3 = (iso3.length() != 3) ? "ROW" : iso3;

		    parsedAddress.setField(OFT.Company,
		       fixAcronyms(parsedAddress.getField(OFT.Company), iso3));
		    parsedAddress.setField(OFT.Building,
		       fixAcronyms(parsedAddress.getField(OFT.Building),iso3));
//
// If the record parsed and is not for country ROW then it's worth validating.
//
		    enhancedAddress = null;
		    if (queueEntity.dqs != '9' && runPac.startsWith("C") &&
						iso3.equals("ROW") == false)
		    {
			setCountryOptions(iso3);

			if (debug)
			{
			    dumpContactRecord(parent,
					globalHosts[myHostId].hostName,
					globalRecord.recordNumber, myId,
					parsedAddress, "Good Parsed Address");
			}
			fixParsed(parsedAddress, iso3);
			validatedAddress = validateAddress(parsedAddress);
			if (hasHung)
			{
			    conTimer.stopTimer();
			    closeServerConnection(12);
			    return;
			}
			if (globalServer == null)
			{
			    redo = true;
			    continue redo;
			}

			if (doLatLong)
			{
			    enhancedAddress = enhanceAddress(validatedAddress);
			    if (hasHung)
			    {
				conTimer.stopTimer();
				closeServerConnection(14);
				return;
			    }
			    if (globalServer == null)
			    {
				redo = true;
				continue redo;
			    }
			}
			validatedAddress.setField(OFT.Company,
				fixAcronyms(validatedAddress.getField(
					OFT.Company), iso3));
			validatedAddress.setField(OFT.Building,
				fixAcronyms(validatedAddress.getField(
					OFT.Building),iso3));
//
// name and address formatting is undertaken post-parse
//
			formatNameAddress(validatedAddress, false, iso3);
			if (hasHung)
			{
			    conTimer.stopTimer();
			    closeServerConnection(15);
			    return;
			}
			if (globalServer == null)
			{
			    redo = true;
			    continue redo;
			}
//
// Finally, the complete parse, validate and address format data is copied
// into the output record fields.
//
			globalContacts = new COptimaContact[]
			{
				parsedAddress,
				validatedAddress,
				formattedNA,
			};
		    }
//
// Badly parsed - but still try and format name and address
//
		    else
		    {
			if (debug)
			{
			    dumpContactRecord(parent,
					globalHosts[myHostId].hostName,
					globalRecord.recordNumber, myId,
					parsedAddress, "Bad Parsed Address");
			}
			if (iso3.equals("ROW") == false)
			{
			    for (int i = 0; i < clearFields.length; i++)
			    {
				parsedAddress.setField(clearFields[i], "");
			    }
			}
			validatedAddress = parsedAddress;

			formatNameAddress(parsedAddress, true, iso3);
			if (hasHung)
			{
			    conTimer.stopTimer();
			    closeServerConnection(16);
			    return;
			}
			if (globalServer == null)
			{
			    redo = true;
			    continue redo;
			}
			globalContacts = new COptimaContact[]
			{
				parsedAddress,
				parsedAddress,
				formattedNA,
			};
		    }
		}
		while (redo == true);

//
// eventually, after as many retries and redos as are necessary, the parent
// GlobalDriver can be given the record back along with all of the statistics
// and error counts.
//
		accepted = parent.setRecord(this, myId, myHostId,
					totalTimeouts, globalContacts);
		if (accepted &&
			(queueEntity.errorCounter >= 2 || numErrors > 50))
		{
		    parent.printLog("Thread "+myId+": Finally returned "+
			"faulty record "+ globalRecord.recordNumber);
		}
	    }
//
// When we get here we have left the record processing loop (because the parent
// has decided not to supply any more records).
// A bit of tidy-up to close the connection and register the fact that we've
// finally finished for good. note that the JVM will reclaim this thread when
// we return.
//
	    closeServerConnection(0);
	    conTimer.stopTimer();

	    parent.childFinished(this, myId);
	}

	private String fixAcronyms(String toFix, String iso3)
	{
	    int fixLen = toFix.length();
	    if (fixLen > 0 && " JP2 JP3 ".indexOf(iso3) > 0)
	    {
		for (int i = 0; i < fixLen; i++)
		{
		    if (alphaString.indexOf(toFix.charAt(i)) >= 0)
		    {
			int acEnd = i;
			boolean noChar = false;
			for (int j = i; j < fixLen; j++)
			{
			    if (alphaString.indexOf(toFix.charAt(j)) >= 0)
			    {
				if (j < i + 3 && noChar == false)
				{
				    acEnd = j;
				}
				else
				{
				    return toFix; // last char > 3 from first
				}
			    }
			    else
			    {
				noChar = true;
			    }
			}
			return toFix.toUpperCase();
		    }
		}
	    }
	    return toFix;
	}

//
// Change validation options
//
	private void setCountryOptions(String iso3)
	{
//
// Keep current options
//
	    int lastCountryOpts = countryOpts;
//
// Get new options according to this country code
//
	    Integer opts = (Integer)countryOptions.get(iso3);
	    if (opts == null)
	    {
		opts = (Integer)countryOptions.get("ROW");
		countryOptions.put(iso3, opts);
	    }
	    countryOpts = opts.intValue();

	    queueEntity.addressFormat = countryOpts & 0x00000002;

	    if (lastCountryOpts != countryOpts)
	    {
		if ((countryOpts & 0x00000001)!=(lastCountryOpts & 0x00000001))
		{
		    globalServer.SetOption(OON.EnforceBlanks,
						countryOpts & 0x00000001);
		    //parent.printLog("Thread "+myId+ ": Switching EB country "+
			//"option for "+iso3+" to "+(countryOpts & 0x00000001));
		}
		doLatLong = ((countryOpts & 0x04) == 0x04) ? true : false;
		int postCodeWeight = (countryOpts & 0x000000F0) / 16;
		postCodeWeight = (postCodeWeight == 0) ? normalPostCodeWeight :
								postCodeWeight;
		if (postCodeWeight != lastPostCodeWeight)
		{
		    globalServer.SetOption(OON.PostcodeWeight, postCodeWeight);
		    lastPostCodeWeight = postCodeWeight;
		    parent.printLog("Setting PC weight for "+iso3+" to "+
							postCodeWeight);
		}

		int cityWeight = (countryOpts & 0x00000F00) / 256;
		cityWeight = (cityWeight == 0) ? normalCityWeight : cityWeight;
		if (cityWeight != lastCityWeight)
		{
		    globalServer.SetOption(OON.CityWeight, cityWeight);
		    lastCityWeight = cityWeight;
		    parent.printLog("Setting City weight for "+iso3+" to "+
							cityWeight);
		}
//
// check/set validation timeout
//
		int timeout = (countryOpts & 0x000FF000) >> 12;
		if (timeout != currentSearchTimeout)
		{
		    currentSearchTimeout = timeout;
		    globalServer.SetOption(OON.SearchTimeout,
							currentSearchTimeout);
		    parent.printLog("Setting Search Timeout for "+iso3+" to "+
							currentSearchTimeout);
		}
	    }
	}
//
// This method takes an address from the parent and formats it into the
// name and 8 addres lines. It returns a standard COptimaContact instance
// ready for parsing.
//
	private void setupInputAddress()
	{
	    cat8910.setLength(0);

	    parsedAddress = new COptimaContact();
	    addVec = new Vector(10);
	    companyName = null;
	    companyBypass = false;
	    companyLine = false;
	    companyQuotes = false;
	    queueEntity.addressLines = new String[11];
	    numPopLines = 0;
	    int i;
	    boolean countrySpecific = false;
	    sm3.setLength(0);

	    for (i = 0; i < maxInAddressCount; i++)
	    {
		adrLine = trimAdl(new String(globalRecord.data,
				inAddressData[i][0],
				inAddressData[i][1]).trim().replace('~', '-'));
		if (adrLine.length() > 0)
		{
		    queueEntity.addressLines[inAddressData[i][3]] = adrLine;
		    if (smartLevel == 3)
		    {
			if (inAddressData[i][3] >= 3 &&
						inAddressData[i][3] <= 6)
			{
			    sm3.append(" ");
			    sm3.append(adrLine);
			    numPopLines++;
			}
		    }
		}
		else if (inAddressData[i][2] == OFT.Company)
		{
		    adrLine = "CamprnySNPlaceholder Ltd";
		    //queueEntity.addressLines[inAddressData[i][3]] = adrLine;
		}

		if (sm3.length() > 0 && inAddressData[i][3] == 6)
		{
		    //adrLine = trimAdl(sm3.toString().trim());
		    adrLine = sm3.toString().trim();
		    sm3.setLength(0);
		    numPopLines--;
		}

		if (inAddressData[i][2] == OFT.FullName)
		{
		    //adrLine = adrLine.replace('_',' ');
		    //parsedAddress.setField(OFT.FullName, adrLine.trim());
		}
		else if (inAddressData[i][2] == OFT.Company && smartLevel >= 2)
		{
		    parsedAddress.setField(OFT.Company, adrLine);
		    companyBypass = true;
		}
		else if (inAddressData[i][2] == OFT.Company &&
					(smartLevel > 0 || doSmart == false))
		{
		    companyName = adrLine;
		    companyBypass = true;
		}
		else if (inAddressData[i][2] == OFT.Country)
		{
		    if (adrLine.length() < 2)
                    {
                        adrLine = "ROW";
                    }
		    String trans = (String)parent.isoTransTable.get(
						adrLine.toUpperCase());
		    if (trans != null)
		    {
			numPopLines++;
			//trans = fixDupeCountry(trans, addVec);
			addVec.addElement(trans);
		    }
		    else if (adrLine.length() > 0)
		    {
			numPopLines++;
			//adrLine = fixDupeCountry(adrLine, addVec);
			addVec.addElement(adrLine);
		    }
		}
		else
		{
		    if (inAddressData[i][2] == OFT.Company)
		    {
			companyLine = true;
			if (adrLine.endsWith("\"") &&
				adrLine.indexOf('"') < adrLine.length() - 1)
			{
			    companyQuotes = true;
			    //System.err.println("Setting quotes on "+adrLine);
			}
		    }
		    if (sm3.length() == 0)
		    {
			if (adrLine.length() > 0)
			{
			    numPopLines++;
			}
			addVec.addElement(adrLine);
		    }
		}
	    }
//
// if there are more than 8 address lines (GA limit) then remove as many
// blank ones until we get down to 8.
//
	    if (addVec.size() > 8)
	    {
		for (i = addVec.size() - 1; i >= 0 && addVec.size() > 8;  i--)
		{
		    if (((String)addVec.elementAt(i)).length() == 0)
		    {
			addVec.removeElementAt(i);
		    }
		}
//
// If the number of address lines is still more than 8 then we need to cat the
// one at 8 onto the end of 7, separated by commas
//
		while (addVec.size() > 8)
		{
		    addVec.setElementAt(
			(String)addVec.elementAt(7) + ", "+
					(String)addVec.elementAt(8), 7);
			//trimAdl((String)addVec.elementAt(7) + ", "+
					//(String)addVec.elementAt(8)), 7);
		    addVec.removeElementAt(8);
		}
	    }
//
// If theres one or less populated address line then insert a special blank one
// Note that addVec will have a number of potentially empty lines as all of the
// Oraddrlx lines other than those cherry-picked above will come through addVec
// even if blank.
//
	    if (numPopLines < 2)
	    {
		String s = (String)addVec.elementAt(0);
		if (s.length() == 0)
		{
		    addVec.setElementAt("...", 0);
		}
		else
		{
		    if (addVec.size() > 1)
		    {
			addVec.removeElementAt(1);
		    }
		    addVec.insertElementAt("...", 0);
		}
	    }
//
// Now there are 8 or less lines we can copy then into the Global input fields.
//
	    int target = 0;
	    for (i = 0; i < addVec.size(); i++)
	    {
		if (((String)addVec.elementAt(i)).length() > 0)
		{
		    parsedAddress.setField(oftAddressEnums[target++],
						(String)addVec.elementAt(i));
		}
	    }

//
// And finally dump the parser input address if requested
//
	    if (chat && parent.uniqueRecordId[1] > 0)
	    {
		parsedAddress.setField(OFT.Other10,
			new String(globalRecord.data, parent.uniqueRecordId[0],
			parent.uniqueRecordId[1]).trim());
	    }
	    if (debug)
	    {
		dumpContactRecord(parent,
			globalHosts[myHostId].hostName,
			globalRecord.recordNumber, myId,
			parsedAddress,"Input Address");
	    }
	}

	private String fixDupeCountry(String aLine, Vector addVec)
	{
	    for (int i = 0; i < addVec.size(); i++)
	    {
		if (aLine.equalsIgnoreCase((String)addVec.elementAt(i)))
		{
		    return aLine + "_";
		}
	    }
	    return aLine;
	}

	private String trimAdl(String adl)
	{
	    int ind = -1;
	    while ((ind = adl.indexOf("\"\"")) >= 0)
	    {
		adl = adl.substring(0, ind) + adl.substring(ind+1);
	    }
	    char[] chars = adl.toCharArray();
	    for (int i = 0; i < chars.length; i++)
	    {
		chars[i] = GlobalDriver.faultyChars[chars[i]];
	    }
	    return new String(chars);
	}
//
// This is the headless timer's callback to the hung thread - i.e. it should
// only get invoked if the timer pops after 60 seconds because an global
// service call has not returned.
// The GlobalDriver is notified of the hang so that it can take evasive action
// and respawn a new child to process the input record again.
//
	private class ResetActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent ae)
	    {
		redo = true;
		if (!childHung(-1, null))
		{
		    parent.printLog("Thread "+myId+
			": Request timeout (-1) - possible server "+
			"failure on "+globalHosts[myHostId].hostName);
		}
	    }
	}

//
// this testerror method is the core of dealing with the flaky optima service
// results - if we get here then the service request for connect, parse,
// validate, name/address format and server close has returned but may not
// have been successfull.
//
// if the request is successful then this method returns immediately.
//
// if the reques failed the client end of the server api is asked for an
// explanation. this explanation is ambiguous and can mean any one of a number
// of things have happened. basically, however, the child will be expected to
// retry the prior request until either successfull or it has tried so many
// times that it's proven that it's not going to get anywhere. In the final
// case the record is literally 'forced' past any further errors and output to
// the GlobalDriver.
//
	private boolean testError(boolean state, COptimaContact contact,
								String message)
	{
	    if (!state)
	    {
		allRetries++;
		try
		{
		    Thread.sleep(1000);
		}
		catch (InterruptedException ie) {}

		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		//parent.printLog("Thread "+myId+": Calling TEGLE");
		String le = globalServer.getLastError();
		//parent.printLog("Thread "+myId+": Called TEGLE");
		conTimer.setTimer(-1);
//
// if this record has had the same problem for 10 or more Thread instances then
// the error is permanent - so force the record past under all circumstances.
//
		if (queueEntity != null && queueEntity.errorCounter >= 3)
		{
		    parent.printLog("Thread "+myId+
				": Error count exceeded - last error was "+le);
		    return true; // get past error - don't perform any more
		}


		if (chat)
		{
		    parent.printLog("Thread "+myId+": Last error was "+le);
		}

		if (le.indexOf("Connection refused") >= 0)
		{
		    //parent.printLog("Connection refused text is >"+le+"<");
//
// If more than 100 retries then try closing/reopening the connection
//
		    if ((allRetries % 100) == 0)
		    {
			parent.printLog("Thread "+myId+": Restarting server "+
				"connection after "+allRetries+" attempts");
			closeServerConnection(1);
			myHostId++;
			myHostId = (myHostId >= globalHosts.length) ?
								0 : myHostId;
		    }
		    return state;
		}
//
// other types imply immediate server disconnect and reconnect as there is
// definitely a communication issue.
//
		else if (le.indexOf("Invalid session") >= 0 ||
			le.indexOf("Exception occurred") >= 0 ||
			le.indexOf("No sessions available") >= 0 ||
			le.indexOf("Not connected to server") >= 0)
		{
		    parent.printLog("Thread "+myId+
				": Global server session has failed - "+
				"attempting reconnect: Reason "+le);
		    closeServerConnection(2);
		    if (le.indexOf("Read timed out") > 0)
		    {
			try
			{
			    Thread.sleep(5000 +((myId % maxThreadCount)*2500));
			    allRetries += 39;
			}
			catch (InterruptedException ie) { }
		    }
		    else if (le.indexOf("Exception occurred") >= 0)
		    {
			try
			{
			    Thread.sleep(3000);
			}
			catch (InterruptedException ie) { }
			setSeriousError(contact);
		    }
		    return state;
		}
//
// Certain types of error may require the child to restart the connection
// should they persist.
//
		else if (le.indexOf("Unknown error") >= 0)
		{
		    setSeriousError(contact);
		    return state;
		}
//
// Final trapped error situation is for an unlicensed Global server - which can
// only be properly handled with a dead stop. If a single host in a list is
// unlicensed then user must remove from the list.
//
		else if (le.indexOf("not licensed") >= 0)
		{
		    System.err.println("Thread "+myId+": Module not licensed "+
			"on host "+globalHosts[myHostId].hostName+
			" for record "+
			globalRecord.recordNumber +" - stopping");
		    parent.printLog("Thread "+myId+" Module not licensed "+
			"on host "+globalHosts[myHostId].hostName+
			" for record "+
			globalRecord.recordNumber +" - stopping");
		    System.exit(99);
		}
//
// standard return condition - return the state as true if the condition is
// not recognised.
//
		parent.printLog("Thread "+myId+
					": Error returned by server "+le);
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		//parent.printLog("Thread "+myId+": Calling TEGS");
		parent.printLog("Thread "+myId+": Status code was "+
						globalServer.getStatus());
		//parent.printLog("Thread "+myId+": Called TEGS");
		conTimer.setTimer(-1);

		if (contact != null)
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				contact, message);
		}
		state = true;
	    }
	    return state;
	}

	private void setSeriousError(COptimaContact contact)
	{
//
// If overall record retry count gets to 50 then bad error has occurred for
// whole record
//
	    if (++totalRetries >= 10)
	    {
		childHung(50, contact);
		closeServerConnection(3);
		parent.printLog("Thread "+myId+
				": Number of errors reached "+totalRetries+
				" on record "+ globalRecord.recordNumber +
				" - restarting thread");
	    }
//
// turn on logging for rest of record if retry count gets silly
//
	    if (++numRetries >= 5)
	    {
		chat = true;
	    }
	}

	protected boolean childHung(int hangCode, COptimaContact hungRec)
	{
	    if (hasHung)
	    {
		return true;
	    }
	    if (hungRec != null)
	    {
		dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				hungRec, "Child Hung Fault");
	    }
	    hasHung = true;
	    return parent.childHung(this, myId, hangCode);
	}
//
// Method to fix the region field in a parsed address where it contains two
// copies of the same text. No exclusion list available at this time
//
	private void fixParsed(COptimaContact contact, String iso3)
	{
	    regionFix = contact.getField(OFT.Region).toUpperCase();
	    if ((regionFix.length() % 2) == 1)
	    {
		halfLen = regionFix.length() / 2;
		if (regionFix.charAt(halfLen) == ' ' &&
				regionFix.substring(0, halfLen).equals(
					regionFix.substring(halfLen+1)))
		{
		    contact.setField(OFT.Region,
					regionFix.substring(0, halfLen));
		    //parent.printLog("Thread "+myId+
				//": Fixed duplicated region "+regionFix);
		}
	    }

	    premiseFix = contact.getField(OFT.Premise);

	    if (premiseFix.length() > 2 &&
		(premiseFix.length() % 2) == 1 &&
		premiseFix.charAt(premiseFix.length() / 2) == ' ' &&
		premiseFix.startsWith(premiseFix.substring(
						(premiseFix.length() / 2) +1)))
	    {
		//System.err.println("Premise Number >"+premiseFix+"<");
		premiseFix = premiseFix.substring(0, premiseFix.length() / 2);
		contact.setField(OFT.Premise, premiseFix);
	    }

	    postcodeFix = contact.getField(OFT.Postcode);
	    if (postcodeFix.length() >= 9 &&
		(postcodeFix.length() % 2) == 1)
	    {
		postcodeMid = postcodeFix.length() / 2;
		if (postcodeFix.charAt(postcodeMid) == ' ' &&
			postcodeFix.substring(0, postcodeMid).equals(
				postcodeFix.substring(postcodeMid+1)))
		{
		    contact.setField(OFT.Postcode, postcodeFix.substring(
							postcodeMid+1));

		}
	    }

	    departmentFix = contact.getField(OFT.Department);
	    companyFix = contact.getField(OFT.Company);
	    if (departmentFix.length() > 1 &&
			companyFix.length() > departmentFix.length() &&
			" JP2 JP3 ".indexOf(iso3) > 0 &&
			companyFix.endsWith(departmentFix))
	    {
		contact.setField(OFT.Department, departmentFix);
		companyFix = companyFix.substring(0, companyFix.length() -
						departmentFix.length());
		contact.setField(OFT.Company, companyFix);
	    }
	}
//
// simple looping method to Validate (paf) the parsed address. it will
// stick inside a loop until successfull or tried enough times.
//
	private COptimaContact validateAddress(COptimaContact parsedAddress)
	{
	    validatedAddress = new COptimaContact(parsedAddress);
//
// Check MKA and modify to PAF MKA if required
//
	    valRes = false;
	    numRetries = 0;

	    if (forceUpdate)
	    {
		valRes = false;
		numRetries = 0;

		while (!valRes)
		{
		    conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		    valRes = globalServer.SetOptionMask(OON.ForceUpdate1,
							OFT.Company, false);
		    conTimer.setTimer(-1);
		    if (!valRes)
		    {
			valRes = testError(valRes, formattedNA,
				"Bad ForceUpdate(false) Server Return");
		    }
		    if (chat)
		    {
			parent.printLog("Thread "+myId+
				": Called ForceUpdate(false): res="+valRes);
		    }
		    if (hasHung || globalServer == null)
		    {
			return null;
		    }
		}
		forceUpdate = false;
	    }

	    keepContact = new COptimaContact(validatedAddress);
	    if (queueEntity.errorCounter < 4)
	    {
		valRes = false;
	    }
	    else
	    {
		valRes = true;
		System.err.println("Thread "+myId+" Has forced bypass of "+
			"Validate for record "+ globalRecord.recordNumber +
			" on port "+ globalHosts[myHostId].hostName);
	    }

	    numRetries = 0;
	    numTimeouts = 0;

	    while(!valRes)
	    {
		if (chat)
		{
		    parent.printLog("Thread "+myId+ ": Calling Validate");
		}

		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.LOCK);
		}
		else if (globalHosts[myHostId].hostLock)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.WAITLOCK);
		}
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		callTimeoutMillisStart = System.currentTimeMillis();
                orgRes=valRes = globalServer.ValidateAddress(
							validatedAddress);
		//orgRes = true;
		//valRes = true;

                callTimeoutMillisEnd = System.currentTimeMillis();
                conTimer.setTimer(-1);
		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.UNLOCK);
		}

		if (chat)
		{
		    parent.printLog("Thread "+myId+
					": Called Validate: res="+valRes);
		}
		if (!valRes)
		{
		    valRes = testError(valRes, validatedAddress,
						"Bad Validate Server Return");
		}
		if (hasHung || globalServer == null)
		{
		    return null;
		}

		if (valRes && valRes == orgRes &&  numTimeouts < 10 &&
			callTimeoutMillisStart + (currentSearchTimeout *1000) <
			callTimeoutMillisEnd)
		{
		    numTimeouts++;
		    totalTimeouts++;
		    parent.printLog("Thread "+myId+
				": Call timeout Validate     "+
				(callTimeoutMillisEnd-callTimeoutMillisStart)+
				"/"+numTimeouts+
				"  Record: "+ globalRecord.recordNumber);
		    if (numTimeouts > 1)
		    {
			try
			{
			    Thread.sleep(numTimeouts * 1000);
			}
			catch (InterruptedException ie) {}
		    }
		    valRes = false;
		    validatedAddress = new COptimaContact(keepContact);
		    continue;
		}
	    }
//
// Check acceptable postal validate
//
	    //validatedAddress.setField(OFT.MKA, keptIso);
	    if (debug)
	    {
		validatedLevel = validatedAddress.getField(OFT.ACR).
							charAt(1) - 0x30;
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		accLevel = getAcceptanceLevel(
				validatedAddress.getField(OFT.CountryISO));
		conTimer.setTimer(-1);

		if (validatedLevel >= accLevel)
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				validatedAddress, "Good Validated Address");
		}
		else
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				validatedAddress, "Poor Validated Address");
		}
	    }
	    return validatedAddress;
	}
//
// simple looping method to Validate (paf) the parsed address. it will
// stick inside a loop until successfull or tried enough times.
//
	private COptimaContact enhanceAddress(COptimaContact orgAddress)
	{
	    String iso = orgAddress.getField(OFT.CountryISO).trim();
	    iso = (iso == null || iso.length() != 3) ? "ROW" : iso;

	    String[] enhanceList = (String[])parent.enhanceTable.get(iso);
	    if (enhanceList == null)
	    {
		return null;
	    }
	    valRes = false;
	    numRetries = 0;
	    numTimeouts = 0;

	    while(!valRes)
	    {
		enhancedAddress = new COptimaContact(orgAddress);
		enhancedAddress.setField(OFT.TCR, "");
		if (chat)
		{
		    parent.printLog("Thread "+myId+ ": Calling Enhance");
		}

		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.LOCK);
		}
		else if (globalHosts[myHostId].hostLock)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.WAITLOCK);
		}
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		callTimeoutMillisStart = System.currentTimeMillis();
                orgRes=valRes = globalServer.EnhanceContact(enhancedAddress,
							enhanceList[0]);

                callTimeoutMillisEnd = System.currentTimeMillis();
                conTimer.setTimer(-1);
		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.UNLOCK);
		}

		if (chat)
		{
		    parent.printLog("Thread "+myId+
					": Called Enhance: res="+valRes);
		}
		if (!valRes)
		{
		    valRes = testError(valRes, enhancedAddress,
						"Bad Enhance Server Return");
		}
		if (hasHung || globalServer == null)
		{
		    return null;
		}

		if (valRes && valRes == orgRes &&  numTimeouts < 10 &&
			callTimeoutMillisStart + (currentSearchTimeout *1000) <
			callTimeoutMillisEnd)
		{
		    numTimeouts++;
		    totalTimeouts++;
		    parent.printLog("Thread "+myId+
				": Call timeout Enhanve     "+
				(callTimeoutMillisEnd-callTimeoutMillisStart)+
				"/"+numTimeouts+
				"  Record: "+ globalRecord.recordNumber);
		    if (numTimeouts > 1)
		    {
			try
			{
			    Thread.sleep(numTimeouts * 1000);
			}
			catch (InterruptedException ie) {}
		    }
		    valRes = false;
		}
	    }
	    if (debug)
	    {
		dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				enhancedAddress, "Good Enhanced Address");
	    }
	    String elc = enhancedAddress.getField(OFT.TCR).trim();
	    String o9 = enhancedAddress.getField(OFT.Other9).trim();
	    String o10 = enhancedAddress.getField(OFT.Other10).trim();

	    if (elc.length() >= 16 && elc.startsWith("EL") &&
		o9.length() > 0 && o9.equalsIgnoreCase("NULL") == false &&
		o10.length() > 0 && o10.equalsIgnoreCase("NULL") == false)
	    {
		elc = elc.substring(2,3) + " "+
			elc.substring(5,6)+elc.substring(7,8) + "-" +
			elc.substring(9,10)+elc.substring(11,12) +
			elc.substring(13,14)+ "-"+
			elc.substring(15,16);
	    }
	    else
	    {
		elc = "0 00-000-0";
		o9 = "";
		o10 = "";
	    }
	    orgAddress.setField(OFT.Other8, elc);
	    orgAddress.setField(OFT.Other9, o9);
	    orgAddress.setField(OFT.Other10, o10);
	    return enhancedAddress;
	}

	private int getAcceptanceLevel(String countryISO)
	{
	    if (countryISO.length() > 0)
	    {
		accInt = (Integer)acceptanceLevel.get(countryISO);
		if (accInt == null)
		{
		    //parent.printLog("Thread "+myId+": Calling GAL");
		    accInt = new Integer(globalServer.GetAcceptanceLevel(
								countryISO));
		    //parent.printLog("Thread "+myId+": Called GAL");
		    acceptanceLevel.put(countryISO, accInt);
		}
		return accInt.intValue();
	    }
	    return 10; // ultra high becuase no ISO - can't have validated.
	}

//
// Another helper method to ask the server to format the person's name and
// their delivery address. Again, this is retried as much as necessary to get
// it past any temporary server error states.
//
	private void formatNameAddress(COptimaContact toFormat,
				boolean bypassFormatAddress, String iso3)
	{
	    forRes = false;
	    formatted = false;
	    numRetries = 0;
//
// Before formatting deal with building/sub-building duplication and other
// country specific recons issues
//
	    fixupFormatting(toFormat);

	    try
	    {
		formattedNA = new COptimaContact(toFormat);
	    }
	    catch (NullPointerException npe)
	    {
		parent.printLog("Thread "+myId+": NPE thrown at 1: "+
							toFormat);
		parent.printLog("          NPE thrown at 1: "+
							formattedNA);
	    }

	    if (bypassFormatAddress)
	    {
		return;
	    }


//
// Only turn on forceUpdate on company if it was bypassed during Parse and
// hasn't been updated by validate
//
	    if (companyBypass && parsedAddress.getField(OFT.Company).equals(
				validatedAddress.getField(OFT.Company)) &&
				forceUpdate == false)
	    {
		forRes = false;
		numRetries = 0;

		while (!forRes)
		{
		    conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		    forRes = globalServer.SetOptionMask(OON.ForceUpdate1,
							OFT.Company, true);
		    conTimer.setTimer(-1);
		    if (!forRes)
		    {
		        forRes = testError(forRes, formattedNA,
					"Bad ForceUpdate(true) Server Return");
		    }
		    if (chat)
		    {
			parent.printLog("Thread "+myId+
				": Called ForceUpdate(true): res="+forRes);
		    }
		    if (hasHung || globalServer == null)
		    {
			return;
		    }
		}
		forceUpdate = true;
	    }

	    keepContact = new COptimaContact(formattedNA);
	    forRes = false;
	    numRetries = 0;
	    numTimeouts = 0;

	    while (!forRes)
	    {
		if (chat)
		{
		    parent.printLog("Thread "+myId+ ": StdCalling FmtAddress");
		}
		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.LOCK);
		}
		else if (globalHosts[myHostId].hostLock)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.WAITLOCK);
		}
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		callTimeoutMillisStart = System.currentTimeMillis();
                orgRes = forRes = globalServer.StandardiseAddress(formattedNA);
                callTimeoutMillisEnd = System.currentTimeMillis();
                conTimer.setTimer(-1);
		if (numTimeouts > 0)
		{
		    globalHosts[myHostId].syncHost(myId, GlobalHost.UNLOCK);
		}

		if (!forRes)
		{
		    forRes = testError(forRes, formattedNA,
					"Bad StdFormatAddress Server Return");
		}
		if (chat)
		{
		    parent.printLog("Thread "+myId+
					": Called StdFmtAddress: res="+forRes);
		}
		if (hasHung || globalServer == null)
		{
		    return;
		}

		if (forRes && forRes == orgRes && numTimeouts < 10 &&
				callTimeoutMillisStart + callTimeoutMillis <
				callTimeoutMillisEnd)
		{
		    numTimeouts++;
		    totalTimeouts++;
		    parent.printLog("Thread "+myId+
			": Call timeout Second Parse "+
			(callTimeoutMillisEnd-callTimeoutMillisStart)+
			"/"+numTimeouts+
			"  Record: "+ globalRecord.recordNumber);
		    if (numTimeouts > 1)
		    {
			try
			{
			    Thread.sleep(numTimeouts * 1000);
			}
			catch (InterruptedException ie) {}
		    }
		    forRes = false;
		    formattedNA = new COptimaContact(keepContact);
		    continue;
		}
		else if (numTimeouts > 0)
		{
		    parent.printLog("Thread "+myId+
			": Call timeout cleared for "+
			"record: "+ globalRecord.recordNumber);
		}
		if (companyQuotes)
		{
		    String coName = formattedNA.getField(OFT.Company);
		    if (coName.length() >= 2 &&
				coName.endsWith("\"") == false &&
				coName.charAt(coName.length() - 2) != '"' &&
				coName.indexOf('"') >= 0)
		    {
			//System.err.println("Coname quote lost "+coName);
			formattedNA.setField(OFT.Company, coName+"\"");
		    }
		}
	    }
	    if (debug)
	    {
		dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				formattedNA, "Re-Standardised Address");
	    }
	    formattedNA.setField(OFT.Company,
				fixAcronyms(formattedNA.getField(
					OFT.Company), iso3));
	    formattedNA.setField(OFT.Building,
				fixAcronyms(formattedNA.getField(
					OFT.Building),iso3));

	    if (queueEntity.addressFormat == 0)
	    {
	 	COptimaContact geogPart = formatStreetAddress(true);
		if (geogPart != null)
		{
		    formatGeographicAddress(geogPart);
		}
	    }
	    else  // format address in homogenous structure
	    {
	 	formatStreetAddress(false);
	    }
	    formattedNA.setField(OFT.Company,
				fixAcronyms(formattedNA.getField(
					OFT.Company), iso3));
	    formattedNA.setField(OFT.Building,
				fixAcronyms(formattedNA.getField(
					OFT.Building),iso3));
	}

//
// Method to take CoptimaContact record, remove geographical components and
// force address reconstruction for street delivery.
//
	private COptimaContact formatStreetAddress(boolean splitMode)
	{
	    COptimaContact geogPart = new COptimaContact();

	    if (splitMode)
	    {
		//deptA = formattedNA.getField(OFT.Department);
		compA = formattedNA.getField(OFT.Company);
		ctryA = formattedNA.getField(OFT.Country);

		geogPart.setField(OFT.Cedex, formattedNA.getField(OFT.Cedex));
		geogPart.setField(OFT.City, formattedNA.getField(OFT.City));
		geogPart.setField(OFT.Region,
				formattedNA.getField(OFT.Region));
		geogPart.setField(OFT.Principality,
				formattedNA.getField(OFT.Principality));
		geogPart.setField(OFT.Postcode,
				formattedNA.getField(OFT.Postcode));
		geogPart.setField(OFT.DPS, formattedNA.getField(OFT.DPS));
		geogPart.setField(OFT.CountryISO,
				formattedNA.getField(OFT.CountryISO));

		//formattedNA.setField(OFT.Department, "");
		formattedNA.setField(OFT.Company, "");
		formattedNA.setField(OFT.Cedex, "");
		formattedNA.setField(OFT.City, "");
		formattedNA.setField(OFT.Region, "");
		formattedNA.setField(OFT.Principality, "");
		formattedNA.setField(OFT.Postcode, "");
		formattedNA.setField(OFT.DPS, "");
		formattedNA.setField(OFT.Country, " ");
	    }

//
// Format address (street)
//
	    forRes = false;
	    formatted = false;
	    numRetries = 0;

	    while (!forRes)
	    {
		if (chat)
		{
		    parent.printLog("Thread "+myId+ ": Calling FmtStrAddress");
		}
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		forRes = formatted = globalServer.FormatAddress(formattedNA);
		conTimer.setTimer(-1);

		if (!formatted)
		{
		    forRes = testError(formatted, formattedNA,
					"Bad FormatStrAddress Server Return");
		}
		if (chat)
		{
		    parent.printLog("Thread "+myId+
					": Called FmtStrAddress: res="+forRes);
		}
		if (hasHung || globalServer == null)
		{
		    return null;
		}
	    }
	    if (splitMode)
	    {
		//formattedNA.setField(OFT.Department, deptA);
		formattedNA.setField(OFT.Company, compA);
		formattedNA.setField(OFT.Country, ctryA);
		formattedNA.setField(OFT.Cedex, geogPart.getField(OFT.Cedex));
		formattedNA.setField(OFT.City, geogPart.getField(OFT.City));
		formattedNA.setField(OFT.Region, geogPart.getField(OFT.Region));
		formattedNA.setField(OFT.Principality,
					geogPart.getField(OFT.Principality));
		formattedNA.setField(OFT.Postcode,
					geogPart.getField(OFT.Postcode));
		formattedNA.setField(OFT.DPS, geogPart.getField(OFT.DPS));
		formattedNA.setField(OFT.Country,
					geogPart.getField(OFT.Country));
		formattedNA.setField(OFT.CountryISO,
					geogPart.getField(OFT.CountryISO));
	    }

	    if (debug)
	    {
		if (formatted)
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				formattedNA, "Good Formatted Street Address");
		}
		else
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				formattedNA, "Bad Formatted Street Address");
		}
	    }
	    return geogPart;
	}

	private void formatGeographicAddress(COptimaContact geogPart)
	{
//
// Format address (geography)
//
	    forRes = false;
	    formatted = false;
	    numRetries = 0;

	    while (!forRes)
	    {
		if (chat)
		{
		    parent.printLog("Thread "+myId+ ": Calling FmtGeoAddress");
		}
		conTimer.setTimer(30+ GlobalDriver.serverTimeout);
		forRes = formatted = globalServer.FormatAddress(geogPart);
		conTimer.setTimer(-1);

		if (!formatted)
		{
		    forRes = testError(formatted, geogPart,
					"Bad FormatGeoAddress Server Return");
		}
		if (chat)
		{
		    parent.printLog("Thread "+myId+
					": Called FmtGeoAddress: res="+forRes);
		}
		if (hasHung || globalServer == null)
		{
		    return;
		}
	    }

	    formattedNA.setField(OFT.AddressLine6,
					geogPart.getField(OFT.AddressLine1));
	    formattedNA.setField(OFT.AddressLine7,
					geogPart.getField(OFT.AddressLine2));
	    formattedNA.setField(OFT.AddressLine8,
					geogPart.getField(OFT.AddressLine3));
	    if (debug)
	    {
		if (formatted)
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				formattedNA, "Good Formatted Geog Address");
		}
		else
		{
		    dumpContactRecord(parent,
				globalHosts[myHostId].hostName,
				globalRecord.recordNumber, myId,
				formattedNA, "Bad Formatted Geog Address");
		}
	    }
	}

	private void removeZ(COptimaContact toRemove, int oftField)
	{
	    String val = toRemove.getField(oftField);
	    int indZ = val.lastIndexOf("xyzzy");
	    if (indZ >= 0)
	    {
		toRemove.setField(oftField, val.substring(0, indZ)+
							val.substring(indZ+5));
	    }
	}

	private void fixupFormatting(COptimaContact toFix)
	{
	    fixBuild = toFix.getField(OFT.Building);
	    fixSub = toFix.getField(OFT.SubBuilding);
	    if (fixBuild.length() >0 && fixBuild.equalsIgnoreCase(fixSub))
	    {
		toFix.setField(OFT.SubBuilding, "");
		fixSub = "";
		//parent.printLog("Thread "+myId+": Deduplicated Building "+
								//fixBuild);
		queueEntity.fixUp = "A";
	    }
	    fixIso3 = toFix.getField(OFT.CountryISO);
	    if (fixIso3.equalsIgnoreCase("GBR"))
	    {
		if (fixBuild.length() == 0 && fixSub.length() > 0)
		{
		    clear = true;

		    for (int i = 0; i < fixSub.length(); i++)
		    {
			if (fixSub.charAt(i) < '0' || fixSub.charAt(i) > '9')
			{
			    clear = false;
			    break;
			}
		    }
		    if (clear)
		    {
			toFix.setField(OFT.SubBuilding, "");
			fixSub = "";
			queueEntity.fixUp = "B";
		    }
		}
	    }
	}

	private String fixupInitials(String comp)
	{
	    String mask = " "+toMask(comp, false);
	    int ind;

	    for(;;)
	    {
		ind = mask.indexOf("PaP");
		if (ind < 0)
		{
		    break;
		}
		comp = comp.substring(0, ind) +
			comp.substring(ind, ind+1).toUpperCase() +
			comp.substring(ind + 1);
		mask = mask.substring(0, ind + 1) +
			mask.substring(ind+1, ind+2).toUpperCase() +
			mask.substring(ind + 2);
	    }

	    mask = toMask(comp, true);

	    for(;;)
	    {
		ind = mask.indexOf(".AA");
		if (ind < 0)
		{
		    break;
		}
		comp = comp.substring(0, ind) + ". "+
			comp.substring(ind + 1);
		mask = comp.substring(0, ind) + ". "+
			mask.substring(ind + 1);
	    }

	    return comp.trim();
	}

//
// utility debug method that dumps the content of the current GlobalContact
// instance to the log - good for debugging and analysis.
//
	private void dumpContactRecord(
            GlobalDriver parent, String hostName,
            long recNum, int id, COptimaContact dumpRecord, String title)
	{
	    for (int i = 0; i < OFT.NumFieldsPlusOne; i++)
	    {
		if (acrNames[i] == null || acrNames[i].startsWith("!"))
		{
		    dumpRes = dumpRecord.getField(i);
		    if (dumpRes != null && dumpRes.length() > 0)
		    {
			setupAcrName(i);
		    }
		}
	    }
	    dumpRecord(parent, hostName, recNum, id, dumpRecord, title);
	}


	private static synchronized void dumpRecord(
            GlobalDriver parent, String hostName,
            long recNum, int id, COptimaContact dumpRecord, String title)
	{
	    ssb = new StringBuffer(2000);
	    ssb.append("\n*****************************************"+
			"************************************\n"+
			recNum+"/"+id+" - "+title+ " "+ hostName +
			formatAcr(dumpRecord.getField(OFT.ACR)));
	    //OFT enum = dumpRecord.keys();
	    for (int i = 0; i < OFT.NumFieldsPlusOne; i++)
	    {
		sdumpRes = dumpRecord.getField(i);
		if (sdumpRes != null && sdumpRes.length() > 0)
		{
		    ssb.append("\nField "+acrNames[i]+ " value="+sdumpRes);
		}
	    }
	    parent.printLog(ssb.toString());
	}

	private void setupAcrName(int acrNumber)
	{
	    if (acrNames[acrNumber] == null ||
					acrNames[acrNumber].startsWith("!"))
	    {
		if (globalServer != null)
		{
		    for (int ng = 0; ng < 100 &&
				(acrNames[acrNumber] == null ||
				acrNames[acrNumber].length() == 0); ng++)
		    {
			//parent.printLog("Thread "+myId+": Calling GetOFT");
			String acrName = globalServer.GetOFTText(acrNumber);
			//parent.printLog("Thread "+myId+": Called GetOFT");
			if (acrName == null || acrName.length() == 0)
			{
			    try
			    {
				Thread.sleep(myId * 100);
			    }
	 		    catch (InterruptedException ie) {}
			}
			else
			{
			    acrNames[acrNumber] = acrName;
			}
		    }
		}
		else
		{
		    acrNames[acrNumber] = String.valueOf("!"+acrNumber);
		}
	    }
	}


	protected void saveGlobalContacts(COptimaContact[] globalContacts)
	{
	    if (queueEntity.recordStatus == GlobalQueueEntity.PARSING)
	    {
		queueEntity.sources[0] = globalContacts[0];
		queueEntity.sources[1] = globalContacts[1];
		queueEntity.sources[2] = globalContacts[2];

		country = globalContacts[0].getField(OFT.Country);
		if (country.length() == 0 ||
				Character.isLetter(country.charAt(0)) == false)
		{
		    country = " UNKNOWN COUNTRY";
		}
		copyIso = globalContacts[0].getField(OFT.CountryISO);
		copyIso = (copyIso.length() != 3) ? "ROW" : copyIso;

		GlobalStats countryStat = new GlobalStats(country, copyIso);
		countryStat.postCode =
			globalContacts[0].getField(OFT.Postcode).trim();
		countryStat.postCode = (countryStat.postCode.length() == 0) ?
			globalContacts[0].getField(OFT.City).trim() :
			countryStat.postCode;
		queueEntity.countryStat = countryStat;
		queueEntity.recordStatus = GlobalQueueEntity.PARSED;
	    }
	}
//
// Method to convert a string to a mask - useful for Initial casing in compnay
// names
//
	private String toMask(String value, boolean normalMode)
	{
	    sbm.setLength(0);
            for (int i = 0; i < value.length(); i++)
            {
                if (Character.isLetter(value.charAt(i)))
                {
		    if (normalMode == false &&
					Character.isLowerCase(value.charAt(i)))
		    {
                	sbm.append("a");
		    }
		    else
		    {
                	sbm.append("A");
		    }
                }
                else if (Character.isDigit(value.charAt(i)))
                {
                    sbm.append("N");
                }
                else if (normalMode == false)
                {
                    sbm.append("P");
                }
		else if (Character.isWhitespace(value.charAt(i)))
		{
                    sbm.append(" ");
		}
		else
		{
		    sbm.append(value.charAt(i));
		}
            }
            return sbm.toString();
	}
//
// Converts the complex ACR code into humanly readable text for debugging or
// analysis - usually output to log by caller.
///
	private static String formatAcr(String acrCode)
	{
	    if (acrCode.length() != 21)
	    {
		return "   ACR is Invalid: '"+acrCode+"'";
	    }
	    StringBuffer acrRet = new StringBuffer(
			"\n                    ACR            - "+
			acrCode+"\n                    ");
	    switch (acrCode.charAt(0))
	    {
		case 'C':
		    acrRet.append("Component Form - ");
		    break;
		case 'L':
		    acrRet.append("Formatted Form - ");
		    break;
		case 'U':
		    acrRet.append("Unstandardised - ");
		    break;
		default:
		    acrRet.append("UNKNOWN FORM   - ");
	    }
	    switch (acrCode.charAt(1))
	    {
		case '0':
		    acrRet.append("No validation");
		    break;
		case '1':
		    acrRet.append("Validated Country");
		    break;
		case '2':
		    acrRet.append("Validated City & Country");
		    break;
		case '3':
		    acrRet.append("Validated PC, City & Country");
		    break;
		case '4':
		    acrRet.append("Validated Street, PC, City & Country");
		    break;
		case '5':
		    acrRet.append("Validated Prem, Strt, PC, City & Country");
		    break;
		default:
		    acrRet.append("UNKNOWN VALIDATION");
	    }
	    for (int i = 3; i < 17; i+=2)
	    {
		acrRet.append("\n                    ");
		switch (acrCode.charAt(i))
		{
		    case 'P':
			acrRet.append("Premise        - ");
			break;
		    case 'S':
			acrRet.append("Street         - ");
			break;
		    case 'A':
			acrRet.append("Locality       - ");
			break;
		    case 'T':
			acrRet.append("City           - ");
			break;
		    case 'R':
			acrRet.append("Region         - ");
			break;
		    case 'Z':
			acrRet.append("PostCode       - ");
			break;
		    case 'C':
			acrRet.append("Country        - ");
			break;
		    default:
			acrRet.append("BAD COMP       - ");
		}
		switch (acrCode.charAt(i+1))
		{
		    case '0':
			acrRet.append("Empty");
			break;
		    case '1':
			acrRet.append("Populated");
			break;
		    case '2':
			acrRet.append("Standard On Input");
			break;
		    case '3':
			acrRet.append("Standardised");
			break;
		    case '4':
			acrRet.append("Postal Validated");
			break;
		    case '5':
			acrRet.append("Postal Corrected");
			break;
		    case '6':
			acrRet.append("Postal Added");
			break;
		    case '7':
			acrRet.append("Correctly Empty");
			break;
		    case '8':
			acrRet.append("Needs Standardising");
			break;
		    case '9':
			acrRet.append("Needs Correcting to Paf lookup");
			break;
		    default:
			acrRet.append("UNKNOWN MEANING");
		}
	    }
	    acrRet.append("\n                    Final Code     - "+
						acrCode.substring(18));
	    return acrRet.toString();
	}

//
// Object finalize intercepts child garbage collection and decrements
// GlobalChild instance count - so we know if children are not getting cleaned
// up. diagnostic seful to give to Optima when diagnosing the number of service
// call hangups.
//
	public synchronized void finalize() throws Throwable
	{
	    changeInstanceCount(-1);
	    if (globalServer != null)
	    {
		System.err.println("GloParse - Illegal server connection");
		globalServer.Disconnect();
		globalServer = null;
	    }
	    super.finalize();
	}
}
