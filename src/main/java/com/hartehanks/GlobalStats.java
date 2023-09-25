package com.hartehanks;

import com.hartehanks.optima.api.*;
import java.util.*;
import java.io.*;
import com.hartehanks.dev.io.*;
import com.hartehanks.dev.misc.*;
import com.hartehanks.dev.app.*;

//
// The globalStats class is used to hold Parsing and validation information at
// a country level. One of these will be created for every country found by
// the GlobalChild when talking to the Optima server.
//
// The global child creates one of these for every record that it processes and
// returns it, filled in, with the return record to the GlobalDriver. the
// global driver locates its compound copy for the country (or makes a new one)
// if it's the first time that the country has been seen. it then adds the
// childs statistics to the cumulating version.
//
// when all stats have been added and the last record finishes processing then
// the printStats method is called to pretty-print the statistics ala Trillium.
//
public class GlobalStats
{
	private static final String[]           parseDesc =
        {
            "No Targetted Conditions Found",
            "   Unidentified Item",
            "Mixed Name Forms",
            "Hold Mail",
            "   Foreign Address",
            "No Names Identified",
            "No Street Identified",
            "No Geography Identified",
            "   Unknown name Pattern",
            "Derived Genders Conflict",
            "More Than One Middle Name",
            "   Unknown Street Pattern",
            "   Invalid Directional",
            "   Unusual Or Long Address",
            "No City or County Identified",
            "   Geography too long",
            "   Corrected City Name Too Long",
            "   Conflicting Geography Types",
            "   Unable to Verify City Name",
            "   Unidentified Line",
            "Multiple Street Line Types",
            "Complete Parsing Failure",
        };

	String		countryName = null;
	String		iso3 = null;
	String		postCode = "";
	long		numCountryRecords = 0;
	long		numCountryGeocoded = 0;
	long[]		numCountryFallback = new long[3];
	long[]		parseGroupCounters = new long[22];
	long[]		validGroupCounters = new long[6];
	long[]		validChangeCounters = new long[7];
	int		loopI;
	int		sortNum;
	int		redInt;
	int		redK;
	Integer		statCount;
	Integer		upCount;
	Integer		redCount;
	String		addPhrase;
	String		redPhrase;
	Hashtable[]	unparsedPhrases = new Hashtable[4];

//
// Constructor to create an GlobalStats instance for a given country name.
// the caller is responsible for making sure that one doesn't already exist
// if they wish to use it for accumulation.
//
	public GlobalStats(String countryName, String iso3)
	{
	    this.countryName =
			(countryName == null || countryName.length() == 0) ?
						"Unknown" : countryName;
	    this.iso3 = (iso3 == null || iso3.length() == 0) ? "???" : iso3;
	    for (loopI = 0; loopI < unparsedPhrases.length; loopI++)
	    {
		unparsedPhrases[loopI] = new Hashtable();
	    }
	}

//
// method that accepts another GlobalStats instance and adds the content to
// this instance
//
	public void addStats(GlobalStats source, boolean doWords)
	{
	    numCountryRecords += source.numCountryRecords;
	    numCountryGeocoded += source.numCountryGeocoded;
	    for (loopI = 0; loopI < parseGroupCounters.length; loopI++)
	    {
		parseGroupCounters[loopI] += source.parseGroupCounters[loopI];
	    }
	    for (loopI = 0; loopI < validGroupCounters.length; loopI++)
	    {
		validGroupCounters[loopI] += source.validGroupCounters[loopI];
	    }
	    for (loopI = 0; loopI < validChangeCounters.length; loopI++)
	    {
		validChangeCounters[loopI] += source.validChangeCounters[loopI];
	    }

	    if (doWords)
	    {
		for (loopI = 0; loopI < unparsedPhrases.length; loopI++)
		{
		    Enumeration enumer = source.unparsedPhrases[loopI].keys();

		    while (enumer.hasMoreElements())
		    {
			addPhrase = (String)enumer.nextElement();
			statCount = (Integer)source.unparsedPhrases[loopI].
								get(addPhrase);
			addUnparsedPhrase(loopI, addPhrase,
							statCount.intValue());
		    }
		}
	    }
	}

//
// Method to permit addition/count of unrecognised words
//
	public final void addUnparsedPhrase(int category, String phrase)
	{
	    addUnparsedPhrase(category, phrase, 1);
	}
	public final void addUnparsedPhrase(int category, String phrase,
								int count)
	{
	    upCount = (Integer)unparsedPhrases[category].get(phrase);
	    if (upCount == null)
	    {
		upCount = new Integer(count);
	    }
	    else
	    {
		upCount = new Integer(upCount.intValue() + count);
	    }
	    unparsedPhrases[category].put(phrase, upCount);
	    if (unparsedPhrases[category].size() > 200)
	    {
		reduceUnparsedPhrases(category);
	    }
	}

	private String[] reduceUnparsedPhrases(int category)
	{
	    String[] sort = new String[unparsedPhrases[category].size()];
	    sortNum = 0;

	    Enumeration enumer = unparsedPhrases[category].keys();

	    while (enumer.hasMoreElements())
	    {
		redPhrase = (String)enumer.nextElement();
		redCount = (Integer)unparsedPhrases[category].get(redPhrase);
		sort[sortNum++] = Conversion.toPaddedString(
					redCount.intValue(), 10) + redPhrase;
	    }
	    unparsedPhrases[category] = new Hashtable();
	    new StringSort(sort, false);
	    for (redInt = 0; redInt < 20 && redInt < sort.length; redInt++)
	    {
		redK = Integer.parseInt(sort[redInt].substring(0,10).trim());
		unparsedPhrases[category].put(sort[redInt].substring(10),
							new Integer(redK));
	    }
	    return sort;
	}
//
// Pretty-print method to format the statistics held by this GlobalStats
// instance to the supplied PrintWriter (stdout, stderr or allocated file).
//
	public void printStats(PrintWriter printWriter)
	{
	    printWriter.println("Global Address Standardisation Report "+
			"for country "+countryName+"   ISO: "+iso3+"\n");
	    printWriter.println("Review Group        Record Count    %");
	    printWriter.println(
				"____________        ____________    ______");
	    for (int i = 0; i < parseGroupCounters.length; i++)
	    {
		String grp = "   "+i;
		String count =   "                "+parseGroupCounters[i];
		String pc;
		if (numCountryRecords > 0)
		{
		    double pct = (((double)parseGroupCounters[i]) * 100.0)/
						(double)numCountryRecords;
		    pc = "         "+String.valueOf(pct)+".0";
		    pc = pc.substring(0, 2+pc.indexOf("."));
		    pc = pc.substring(pc.length() - 6)+"%";
		}
		else
		{
		    pc = "    0.0%";
		}

		printWriter.println("     "+grp.substring(grp.length() - 2)+
				"           "+
				count.substring(count.length() - 12)+
				"     "+pc+"   "+parseDesc[i]);
	    }

	    printWriter.println(
			"Global Address Postal Validation Report for "+
			"country "+countryName+"   ISO: "+iso3+"\n");
	    printWriter.println("Record Count  Description");
	    printWriter.println(
				"____________  _____________________________");
	    printWriter.println(
			Conversion.toPaddedString(numCountryRecords, 12)+
			"  Records Read");
	    printWriter.println(
			Conversion.toPaddedString(numCountryGeocoded, 12)+
			"  Records Validated");
	    printWriter.println(Conversion.toPaddedString(
			numCountryRecords - numCountryGeocoded, 12)+
			"  Records Not Validated due to failed Parsing");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[5], 12)+
			"  Records matched Postal Directory to level 5");
	    printWriter.println(Conversion.toPaddedString(
			numCountryGeocoded - validGroupCounters[5], 12)+
			"  Records failed to match full Postal Directory");
	    printWriter.println();
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[0], 12)+
			"  Records with State/Province name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[1], 12)+
			"  Records with City name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[2], 12)+
			"  Records with Street name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[4], 12)+
			"  Records with Building name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[3], 12)+
			"  Records with Sub-Building name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[5], 12)+
			"  Records with Business name changed or added");
	    printWriter.println(
			Conversion.toPaddedString(validChangeCounters[6], 12)+
			"  Records with Post/Zipcode changed or added");
	    printWriter.println("\nReasons for postal validation failure");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[0], 12)+
			"  Records failed because no components validated");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[1], 12)+
			"  Records validated at Country level");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[2], 12)+
			"  Records validated at City & Country");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[3], 12)+
			"  Records validated at PC, City & Country");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[4], 12)+
			"  Records validated at Street, PC, City & Country");
	    printWriter.println(
			Conversion.toPaddedString(validGroupCounters[5], 12)+
			"  Records validated at Premise, Street, PC, City & Cntry");
	}

	public void printWords(PrintWriter printWriter)
	{
	    printWriter.println("Global Address Unrecognised Words Report "+
			"for country "+countryName+"   ISO: "+iso3+"\n");
	    for (int i = 0; i < unparsedPhrases.length; i++)
	    {
		String[] sortedPhrases = reduceUnparsedPhrases(i);
		if (sortedPhrases.length > 0)
		{
		    printWriter.println("\nPhrases for catgeory "+
					sortedPhrases[0].substring(10,11));
		    for (int j = 0; j < 10 && j < sortedPhrases.length; j++)
		    {
			printWriter.println(sortedPhrases[j].substring(0,10)+
				sortedPhrases[j].substring(11));
		    }
		}
	    }
	}
}
