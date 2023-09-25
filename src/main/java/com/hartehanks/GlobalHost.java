package com.hartehanks;

public class GlobalHost
{
	private GlobalCallback	parent = null;
	public String		hostName = null;
	public int		maxThreads = 8;
	public int		enabledThreads = 8;
	private boolean		optInit = false;
	public int		currentThreads = 0;
	public int		hostCount = 0;
	public double		hostCI = 0;
	public int		hostTotal = 0;
	public int		retryCount = 0;

	public boolean		hostLock = false;
	private Object		thisHostObj = new Object();

	public static final int	WAITLOCK = 0;
	public static final int	LOCK = 1;
	public static final int	UNLOCK = 2;

	public GlobalHost(GlobalCallback parent, String hostName,
					int maxThreads, int enabledThreads)
	{
	    this.parent = parent;
	    this.hostName = hostName;
	    this.maxThreads = maxThreads;
	    this.enabledThreads = enabledThreads;
	}

	public final synchronized boolean isOptInit()
	{
	    return optInit;
	}

	public final synchronized void setOptInit(boolean state)
	{
	    optInit = state;
	}

	public final void syncHost(int threadId, int option)
	{
	    boolean printed = false;
	    switch (option)
	    {
		case UNLOCK:
		    hostLock = false;
		    parent.printLog("Thread "+threadId+
					": Reset    lock for host "+hostName);
		    break;

		case LOCK:
		    while (hostLock)
		    {
			try
			{
			    if (!printed)
			    {
				parent.printLog("Thread "+threadId+
					": Wait/Set lock for host "+hostName);
				printed = true;
			    }
			    Thread.sleep(1000);
			}
			catch (InterruptedException ie) {}
		    }

		    synchronized(thisHostObj)
		    {
			setLock(threadId, printed);
		    }
		    parent.printLog("Thread "+threadId+
					": Set      lock for host "+hostName);
		    try
		    {
			Thread.sleep(2000);
		    }
		    catch (InterruptedException ie) {}
		    break;

		case WAITLOCK:
		    int sleepTime = 3000;
		    while (hostLock)
		    {
			try
			{
			    if (!printed)
			    {
				parent.printLog("Thread "+threadId+
					": Wait     lock for host "+hostName);
				printed = true;
			    }
			    Thread.sleep(sleepTime);
			}
			catch (InterruptedException ie) {}
			sleepTime = 1000;
		    }
		    break;
	    }
	}

	private final void setLock(int threadId, boolean printed)
	{
	    while (hostLock)
	    {
		try
		{
		    if (!printed)
		    {
			parent.printLog("Thread "+threadId+
					": Set/Wait lock for host "+hostName);
			printed = true;
		    }
		    Thread.sleep(1000);
		}
		catch (InterruptedException ie) {}
	    }
	    hostLock = true;
	}

}
