package com.hartehanks;

import java.util.*;
import com.hartehanks.optima.api.*;

public class GlobalQueueEntity
{
	public static final int INPUT = 0;
	public static final int PARSING = 1;
	public static final int PARSED = 2;
	public static final int FORMATTED = 3;
	public static final int WRITTEN = 4;

	protected int		errorCounter = 0;
	protected int		recordStatus = INPUT;
	protected Vector	recordVector = new Vector(1);
	protected String[]	addressLines;

	protected COptimaContact[]	sources = new COptimaContact[3];
	protected GlobalStats countryStat;
        protected char			dqs = '1';
        protected int			addressFormat = 0;
        protected String		fixUp = "";

	public GlobalQueueEntity()
	{
	}

	public void add(GlobalRecord globalRecord)
	{
	    recordVector.add(globalRecord);
	}

	public final long getRecordNumber()
	{
	    GlobalRecord record = (GlobalRecord)recordVector.elementAt(0);
	    return record.recordNumber;
	}

	public final void clear()
	{
	    recordVector = null;
	    sources = null;
	    addressLines = null;
	}
}
