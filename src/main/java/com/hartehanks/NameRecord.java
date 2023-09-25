package com.hartehanks;

import com.hartehanks.dev.misc.*;
import java.io.*;

public class NameRecord
{
	String		inputName = "";
	String		inputIso = "";

	String[][]	resultData = new String[0][0];
	boolean		needsWriting = false;

	public NameRecord(String line)
	{
	    if (line.startsWith("|"))
	    {
		line = " "+line;
	    }
	    SuperStringTokenizer stt = new SuperStringTokenizer(
							line, "|", false);
	    String[] str = stt.getStringList();

	    inputName = (str.length > 0) ? str[0].trim() : "";
	    inputIso = (str.length > 1 && str[1].trim().length() == 3) ?
					str[1].trim().toUpperCase() : "ROW";
/*
	    System.err.println("inputLine is "+line);
	    System.err.println("inputName is "+inputName);
	    System.err.println("inputISO  is "+inputIso);
*/

	    fillResultData(str);
	}

	public NameRecord(String name, String iso)
	{
	    this(name + "|" + iso);
	}

	private void fillResultData(String[] line)
	{
	    if (line.length < 3)
	    {
		return;
	    }

	    SuperStringTokenizer stt = new SuperStringTokenizer(
							line[2], "!", false);
	    String[] str = stt.getStringList();
	    resultData = new String[str.length][];

	    for (int i = 0; i < str.length; i++)
	    {
		resultData[i] = fillResultItem(str[i]);
	    }
	}

	private String[] fillResultItem( String line)
	{
	    SuperStringTokenizer stt = new SuperStringTokenizer(
							line, "_", false);
	    return stt.getStringList();
	}

	public void dump()
	{
	    System.err.println("Input Name: "+inputName);
	    System.err.println("Input ISO : "+inputIso);
	    if (resultData.length == 14)
	    {
		System.err.println("Origin 1  : "+expandArray(resultData[0]));
		System.err.println("Recode 1  : "+expandArray(resultData[1]));
		System.err.println("Pattern 1 : "+expandArray(resultData[2]));
		System.err.println("Origin 2  : "+expandArray(resultData[3]));
		System.err.println("Recode 2  : "+expandArray(resultData[4]));
		System.err.println("Pattern 1 : "+expandArray(resultData[5]));
		System.err.println("Target    : "+expandArray(resultData[6]));
		System.err.println("Value     : "+expandArray(resultData[7]));
		System.err.println("Pattern 3 : "+expandArray(resultData[8]));
		System.err.println("Full Name : "+expandArray(resultData[9]));
		System.err.println("Salutation: "+expandArray(resultData[10]));
		System.err.println("Patt ISO  : "+expandArray(resultData[11]));
		System.err.println("Gender    : "+expandArray(resultData[12]));
		System.err.println("Patt Num. : "+expandArray(resultData[13]));
	    }
	    else
	    {
		for (int i = 0; i < resultData.length; i++)
		{
		    System.err.println("Resdata "+i+": "+
						expandArray(resultData[i]));
		}
	    }
	}

	public String expandArray(String[] array)
	{
	    String str = "";
	    for (int i = 0; i < array.length; i++)
	    {
		str += array[i] + " ";
	    }
	    return str;
	}

	public boolean writeNameRecord(BufferedWriter bw)
	{
	    if (needsWriting && bw != null)
	    {
		StringBuffer sb = new StringBuffer();
		sb.append(inputName + "|" + inputIso + "|");
		for (int i = 0; i < 9 && i < resultData.length; i++)
		{
		    for (int j = 0; j < resultData[i].length; j++)
		    {
			sb.append(resultData[i][j] + "_");
		    }
		    sb.append("_!");
		}
		for (int i = 9; i < 13 && i < resultData.length; i++)
		{
		    sb.append(resultData[i][0].trim() + " !");
		}
		if (resultData.length >= 14)
		{
		    sb.append(resultData[13][0].trim() + " \n");
		}
		needsWriting = false;
		try
		{
		    bw.write(sb.toString());
		}
		catch (IOException ioe)
		{
		    return false;
		}
	    }
	    return true;
	}
}
