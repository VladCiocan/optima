package com.hartehanks;

import java.util.*;

public class GlobalFallback
{
	public long	recCount = 0;
	public long	sinCount = 0;
	public long[]	fbCount = null;
	private boolean	doPerc = false;
	private long	pct;
	private Long[]	l;

	private static String[][] threshHolds =
	{
	    {"ALA", "3", "5"},
	    {"AND", "3", "5"},
	    {"AUS", "1", "3"},
	    {"AUT", "1", "3"},
	    {"BEL", "1", "3"},
	    {"BIH", "3", "5"},
	    {"BHR", "5", "10"},
	    {"BRA", "1", "3"},
	    {"CAN", "1", "3"},
	    {"CHE", "1", "3"},
	    {"CHL", "4", "7"},
	    {"COL", "10", "15"},
	    {"CH2", "5", "10"},
	    {"CHN", "5", "10"},
	    {"COL", "5", "10"},
	    {"CZE", "1", "3"},
	    {"DEU", "1", "3"},
	    {"DNK", "3", "5"},
	    {"ECU", "5", "10"},
	    {"ESP", "1", "3"},
	    {"FIN", "3", "5"},
	    {"FRA", "1", "3"},
	    {"GBR", "1", "3"},
	    {"GGY", "1", "3"},
	    {"GLP", "3", "5"},
	    {"GRC", "1", "3"},
	    {"GRL", "3", "5"},
	    {"HKG", "2", "4"},
	    {"HRV", "1", "3"},
	    {"HUN", "1", "3"},
	    {"IMN", "1", "3"},
	    {"IRL", "2", "4"},
	    {"ISL", "10", "15"},
	    {"ITA", "3", "5"},
	    {"JEY", "1", "3"},
	    {"JPN", "3", "5"},
	    {"JP2", "1", "3"},
	    {"JP3", "1", "3"},
	    {"KO2", "10", "15"},
	    {"KWT", "5", "10"},
	    {"LIE", "1", "3"},
	    {"LUX", "1", "3"},
	    {"LVA", "1", "3"},
	    {"MAR", "3", "5"},
	    {"MCO", "1", "3"},
	    {"MDA", "3", "5"},
	    {"MEX", "3", "5"},
	    {"MKD", "1", "3"},
	    {"MNE", "1", "3"},
	    {"MTQ", "1", "3"},
	    {"NLD", "1", "3"},
	    {"NOR", "1", "3"},
	    {"NZL", "16", "20"},
	    {"PRI", "1", "3"},
	    {"PRT", "3", "5"},
	    {"REU", "1", "3"},
	    {"ROW", "0", "0"},
	    {"RUS", "2", "4"},
	    {"RU2", "2", "4"},
	    {"SWE", "3", "5"},
	    {"SCG", "1", "3"},
	    {"SRB", "1", "3"},
	    {"SGP", "3", "5"},
	    {"SVK", "1", "3"},
	    {"SVN", "1", "3"},
	    {"TUR", "5", "10"},
	    {"USA", "1", "3"},
	    {"ZAF", "3", "5"},
	    {"???", "0", "0"},
	};

	private static HashMap threshHash = new HashMap();

	static
	{
	    for (int i = 0; i < threshHolds.length; i++)
	    {
		Long[] vals = new Long[2];
		vals[0] = new Long(Long.parseLong(threshHolds[i][1]));
		vals[1] = new Long(Long.parseLong(threshHolds[i][2]));

		threshHash.put(threshHolds[i][0], vals);
	    }
	}

	public GlobalFallback(long[] fallbacks, long addCount)
	{
	    fbCount = new long[fallbacks.length];
	    addFallbackCount(fallbacks, addCount);
	}

	public final void addFallbackCount(long[] fallbacks, long addCount)
	{
	    for (int i = 0; i < fbCount.length; i++)
	    {
		fbCount[i] += fallbacks[i];
	    }
	    this.recCount += addCount;
	    sinCount++;
	}

	public final String toString(String iso3, boolean doSmart)
	{
	    String str = String.valueOf(recCount)+"("+sinCount+")";
	    int maxFb = (doSmart) ? 2 : 1;
	    for (int i = 0; i < maxFb; i++)
	    {
		str += makeCount(fbCount[i]);
	    }
	    //str += makeCount(fbCount[2]);

	    l = (Long[])threshHash.get(iso3);
	    if (doPerc)
	    {
		str += "  (";
		for (int i = 0; i < maxFb; i++)
		{
		    str += makePct(fbCount[i], (i < maxFb - 1) ? "/" : ")");
		}
		if (l != null)
		{
//
// Set magenta for ok - but not perfect
//
		    if (pct >= l[1].longValue())
		    {
			return "\033[31m"+str+"\033[30m";
		    }
//
// Set Red for below acceptable threshold
//
		    else if (pct >= l[0].longValue())
		    {
			return "\033[35m"+str+"\033[30m";
		    }
//
// Set Blue (Good) otherwise
//
		    else
		    {
			return "\033[34m"+str+"\033[30m";
		    }
		}
	    }
	    else if (l != null)
	    {
		return "\033[34m"+str+"\033[30m";
	    }

	    return str;
	}

	private String makeCount(long fbc)
	{
	    pct = (fbc > 0) ? (fbc * 100) / recCount: 0;
	    doPerc = (pct > 0) ? true: doPerc;
	    return "/"+String.valueOf(fbc);
	}

	private String makePct(long fbc, String endChar)
	{
	    pct = (fbc > 0) ? (fbc * 100) / recCount: 0;
	    return pct+"%"+endChar;
	}
}
