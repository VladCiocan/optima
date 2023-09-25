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
// the GlobalDisconnect is a runnable Thread (run by the GlobalDriver) that is
// responsible for communicating with the Optima server across the network.
// Although resilient it may unexpectedly hang. hangups like these are handled
// by wrapping each service call with a timer - which will trip if the service
// call takes too long.
//
// The GlobalDisconnect constructor is the first point of contact for any new
// instance of an GlobalDisconnect.
//
public class GlobalDisconnect extends Thread
{
	private CEnhancedOptimaServer	globalServer = null;
	private int		threadId = -1;
	private GlobalDriver parent = null;

//
// the public constructor method for the GlobalDisconnect is invoked by the
// GlobalChild when a COptimaServer connection needs closing.
//
	public GlobalDisconnect(CEnhancedOptimaServer globalServer,
					int threadId, GlobalDriver parent)
	{
	    this.globalServer = globalServer;
	    this.threadId = threadId;
	    this.parent = parent;
	}

//
// This is the opposite to the opIt should, in theory,
// always work but it's not guaranteed - so it too is headless timed.
//
	public final void run()
	{
	    if (globalServer != null)
	    {
		//String status = globalServer.GetStatusInfo();

		boolean status = globalServer.Disconnect();
		parent.printLog("Thread "+threadId+": Disconnected from host"+
				" through slave. Call returned "+status);
		//parent.printLog("Thread "+threadId+": Server Status: "+
								//status);
		if (globalServer.getConnected())
		{
		    parent.printLog("Thread "+threadId+
				" - Disconnect failed. Last error was "+
				globalServer.getLastError());
		    System.err.println("Thread "+threadId+
			" - Socket is still open - close failed");
		    parent.printLog("Thread "+threadId+
			" - Socket is still open - close failed");
		}

		globalServer = null;
		try
		{
		    Thread.sleep(1000);
		}
		catch (InterruptedException ie) {}
		System.runFinalization();
	    }
	}
}
