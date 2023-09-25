package com.hartehanks.dev.misc;
import java.io.*;
import java.util.*;

public final class SuperStringTokenizer extends StringTokenizer
{
	String delimList = " ";

	public SuperStringTokenizer(String s)
	{
	    super(s);
	}
	public SuperStringTokenizer(String s, String delim)
	{
	    super(s, delim);
	    delimList = delim;
	}
	public SuperStringTokenizer(String s, String delim, boolean ret)
	{
	    super(s, delim, ret);
	    delimList = delim;
	}
	public final Vector getVectorList()
	{
	    Vector v = new Vector(10,10);

	    while(hasMoreElements())
	    {
		v.addElement(nextToken());
	    }

	    return v;
	}
	public final String[] getStringList()
	{
	    Vector v = getVectorList();

	    String[] s = new String[v.size()];
	    v.copyInto(s);
	    v = null;

	    return s;
	}

	public final String[] getQuotedStringList()
						throws QuotedStringException
	{
	    return getQuotedStringList(false);
	}
	public final String[] getQuotedStringList(boolean return_quotes)
						throws QuotedStringException
	{
	    //System.err.println("Return quotes is "+return_quotes);
	    Vector v = new Vector(10,10);
	    String build_string = " ";

	    while(hasMoreElements())
	    {
		String nextEl = (String) nextElement();
		//System.err.println("Next element is "+nextEl);
		build_string += nextEl;
		//System.err.println("Composite is >"+build_string+"<");
		if (build_string.charAt(1) == "'".charAt(0) ||
						build_string.charAt(1) == '"')
		{
		    if (build_string.length() < 3 ||
			build_string.charAt(build_string.length() -1) !=
						build_string.charAt(1))
		    {
			//System.err.println("At 1");
			continue;
		    }
		    if (return_quotes == false)
		    {
			//System.err.println("At 2");
			build_string = build_string.substring(
				1, build_string.length() - 1);
		    }
		}

		build_string = build_string.substring(1);
		//System.err.println("Added to vector is >"+build_string+"<");
		v.addElement(build_string);
		build_string = " ";
	    }
	    if (build_string.length() > 1)
	    {
		throw new QuotedStringException("Missing end quote on" +
						build_string);
	    }

	    String[] s = new String[v.size()];
	    v.copyInto(s);
	    v = null;

	    return s;
	}
}
