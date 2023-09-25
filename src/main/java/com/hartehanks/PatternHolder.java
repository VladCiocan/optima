package com.hartehanks;

import java.util.*;
import com.hartehanks.dev.misc.*;

public class PatternHolder
{
	String		countryIso = null;
	String[]	sourcesOrg = null;
	String[]	sources = null;
	int[]		sourcesPull = null;
	String[]	replaces = null;
	String[]	formats = null;
	int		patternPos = 0;
	int		sourceLine = 0;
	PatternHolder	nextAtLevel = null;
	Hashtable	nextHash = null;
	int		compileGender = 2;

	static final int	BEGIN_TO_MIDDLE = 0x08;
	static final int	BEGIN_TO_END	= 0x04;
	static final int	MIDDLE_TO_MIDDLE= 0x02;
	static final int	MIDDLE_TO_END	= 0x01;
	static final int	ANYWHERE	= 0x0F;



	public PatternHolder()
	{
	}

	public PatternHolder(String countryIso, String source, String replace,
				String format, String position, int sourceLine)
	{
	    if (source != null)
	    {
		if (replace == null || format == null)
		{
		    System.err.println("Not all args to new PatternHolder "+
			"populated when source is");
		    System.exit(1);
		}
		this.countryIso = countryIso.toUpperCase();

//
// Unpack source, replace and pattern into new array slots
//
		SuperStringTokenizer stt = new SuperStringTokenizer(
					source.toUpperCase(), " ", false);
		sourcesOrg = stt.getStringList();
		sources = new String[sourcesOrg.length];
		sourcesPull = new int[sources.length];
		for (int i = 0; i < sourcesPull.length; i++)
		{
		    sources[i] = new String(sourcesOrg[i]);
		    sourcesPull[i] = 0;
		}

		stt = new SuperStringTokenizer(replace.toUpperCase(), " ",
									false);
		replaces = stt.getStringList();

		stt = new SuperStringTokenizer(format.toUpperCase(), " ",
									false);
		formats = stt.getStringList();

		position = position.toUpperCase();
		if (position.equals("A"))
		{
		    patternPos = PatternHolder.ANYWHERE;
		}
		else if (position.length() < 2 || position.length() % 2 != 0)
		{
		    System.err.println("Invalid position code for "+
						"PatternMod "+position);
		    System.exit(1);
		}
		else
		{
		    for (int i = 0; i < position.length(); i+=2)
		    {
			if (position.substring(i, i+2).equals("BM"))
			{
			    patternPos |= PatternHolder.BEGIN_TO_MIDDLE;
			}
			else if (position.substring(i, i+2).equals("BE"))
			{
			    patternPos |= PatternHolder.BEGIN_TO_END;
			}
			else if (position.substring(i, i+2).equals("MM"))
			{
			    patternPos |= PatternHolder.MIDDLE_TO_MIDDLE;
			}
			else if (position.substring(i, i+2).equals("ME"))
			{
			    patternPos |= PatternHolder.MIDDLE_TO_END;
			}
			else
			{
			    System.err.println("Invalid position code for "+
						"PatternMod "+position);
			    System.exit(1);
			}
		    }
		}
		if (patternPos == 0)
		{
		    System.err.println("PatternPos calculation error");
		}
		this.sourceLine = sourceLine;
	    }
	}

	public String toString()
	{
	    return countryIso + "\t"+toString(sourcesOrg)+"\t"+
				toString(replaces)+"\t"+
				toString(formats)+"\t"+
				getPatternPos();
	}

	public String getPatternPos()
	{
	    if (patternPos == PatternHolder.ANYWHERE)
	    {
		return "A";
	    }
	    String str = "";
	    str = (((patternPos & PatternHolder.BEGIN_TO_MIDDLE) ==
			 	PatternHolder.BEGIN_TO_MIDDLE) ? "BM" : "") +
	    	  (((patternPos & PatternHolder.BEGIN_TO_END) ==
			 	PatternHolder.BEGIN_TO_END) ? "BE" : "") +
	    	  (((patternPos & PatternHolder.MIDDLE_TO_MIDDLE) ==
			 	PatternHolder.MIDDLE_TO_MIDDLE) ? "MM" : "") +
	    	  (((patternPos & PatternHolder.MIDDLE_TO_END) ==
			 	PatternHolder.MIDDLE_TO_END) ? "ME" : "");
	    return str;
	}

	public String toString(String[] toUnpack)
	{
	    if (toUnpack == null)
	    {
		return "";
	    }
	    String str = new String(toUnpack[0]);
	    for (int i = 1; i < toUnpack.length; i++)
	    {
		str += " "+toUnpack[i];
	    }
	    return str;
	}

	public String toString(int[] toUnpack)
	{
	    if (toUnpack == null)
	    {
		return "";
	    }
	    String str = String.valueOf(toUnpack[0]);
	    for (int i = 1; i < toUnpack.length; i++)
	    {
		str += " "+String.valueOf(toUnpack[i]);
	    }
	    return str;
	}

	public void dump(String title)
	{
	    System.err.println("\nDump of "+title);
	    System.err.println("CountryIso:  "+countryIso);
	    System.err.println("SourcesOrg:  "+toString(sourcesOrg));
	    System.err.println("Sources:     "+toString(sources));
	    System.err.println("SourcesPull: "+toString(sourcesPull));
	    System.err.println("Replaces:    "+toString(replaces));
	    System.err.println("Formats:     "+toString(formats));
	    System.err.println("PatternPos:  "+patternPos);
	    System.err.println("SourceLine:  "+sourceLine+"\n");
	}

	public Object clone()
	{
	    PatternHolder ph = new PatternHolder();

	    ph.countryIso = new String(countryIso);

	    ph.sourcesOrg = new String[sourcesOrg.length];
	    for (int i = 0; i < sourcesOrg.length; i++)
	    {
		ph.sourcesOrg[i] = new String(sourcesOrg[i]);
	    }

	    ph.sources = new String[sources.length];
	    for (int i = 0; i < sources.length; i++)
	    {
		ph.sources[i] = new String(sources[i]);
	    }

	    ph.sourcesPull = new int[sourcesPull.length];
	    for (int i = 0; i < sourcesPull.length; i++)
	    {
		ph.sourcesPull[i] = sourcesPull[i];
	    }

	    ph.replaces = new String[replaces.length];
	    for (int i = 0; i < replaces.length; i++)
	    {
		ph.replaces[i] = new String(replaces[i]);
	    }

	    ph.formats = new String[formats.length];
	    for (int i = 0; i < formats.length; i++)
	    {
		ph.formats[i] = new String(formats[i]);
	    }


	    ph.patternPos = patternPos;
	    ph.sourceLine = sourceLine;
	    ph.compileGender = compileGender;
	    ph.nextHash = null;
	    ph.nextAtLevel = null;

	    return ph;
	}
}
