package com.hartehanks;

import java.util.*;
import java.io.*;
import com.hartehanks.dev.misc.*;

//
// Simple class to read a standard Trillium parameter file and store the
// arguments as accessible values against the parameter to which they are
// associated. i.e.
//
//   INPUT_FILE	/pub/mydir/fred.dat
//   INPUT_DICT /pub/mydir/fred.ddl
//   ...
//
// The creator can ask for the arguments to be returned by supplying the
// paramter value (case insensitive)
//
public class ParmfileHandler
{
	private String		parmFileName = "";
	private Hashtable	parmHash = new Hashtable();

//
// Simple constructor expects the name of a parameter file and will immediately
// load it
//
	public ParmfileHandler(String parmFileName)
	{
	    this.parmFileName = parmFileName;
	    loadParmFile(openParmFile(parmFileName));
	}

//
// All arguments for a parameter are returned (or null if parameter not found)
// Note that all the String array returned by this technique also includes the
// parameter value in slot 0 (if the parameter existed).
//
	public final String[] getArgumentsForParameter(String parmName)
	{
	    return (String[])parmHash.get(parmName);
	}

//
// internal reader open to get into supplied parm file
//
	private BufferedReader openParmFile(String parmFileName)
	{
            if (parmFileName == null || parmFileName.length() == 0)
            {
                System.err.println("Parameter file name is missing or empty");
                System.exit(1);
            }

            BufferedReader br = null;

//
//  Open input parm file and check success
//
            try
            {
                br = new BufferedReader(new InputStreamReader(
			new FileInputStream(parmFileName), "ISO8859_1"));
            }
            catch (FileNotFoundException enofile)
            {
                System.err.println("Parameter file open error for file '"+
							parmFileName+"'");
                System.exit(1);
            }
	    catch (UnsupportedEncodingException uce)
	    {
                System.err.println("Parameter file encoding exception on '"+
							parmFileName+"'");
                System.exit(1);
	    }
	    return br;
	}

//
// This method reads, line by line, the parameter file, tokenizes the line
// and takes the first token as the parameter and all successive as arguments.
// if the line starts with # then its commented out. Very basic indeed and
// doesn't allow for multiple line (\n) continuation. Also only uses space, tab
// and newline as delims.
//
	private void loadParmFile(BufferedReader br)
	{
	    String line = "";
	    String cline = "";

	    try
            {
                while ((line = br.readLine()) != null)
                {
		    if (line.trim().startsWith("*") == false)
		    {
			if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
			{
			    cline += line;
			}
			else
			{
			    processLine(cline);
			    cline = line;
			}
		    }
		}
		processLine(cline);
		br.close();
	    }
	    catch (IOException eio)
            {
		System.err.println("IO error reading parameter file");
                System.exit(1);
            }
	}

	private void processLine(String cline)
	{
	    try
	    {
		SuperStringTokenizer str = new SuperStringTokenizer(cline,
							" ,\n\r\t", false);
		String[] stt = str.getQuotedStringList(false);
		if (stt.length > 0)
		{
		    if (parmHash.get(stt[0].toUpperCase()) != null)
		    {
			System.err.println("Illegal parameter "+
					"respecification: "+ stt[0]);
			System.exit(1);
		    }
		    parmHash.put(stt[0].toUpperCase(), stt);
		}
	    }
	    catch (QuotedStringException qse)
            {
		System.err.println("Parameter has unbalanced quotes on line "+
									cline);
                System.exit(1);
            }
	}

//
// A nicer argument accessor method that will check that the number of
// arguments expected match those found in the parm file before returning them
// as a String array (minus the first 'parameter' entry).
//
	public final String[] locateArgumentFor(String parmName, int argCount)
	{
	    String[] argList = (String[])parmHash.get(parmName.toUpperCase());
	    if (argList == null)
	    {
		throw new IllegalArgumentException("Parameter "+parmName+
				" not found in parm file '"+parmFileName+"'");
	    }
	    if (argCount > 0)
	    {
		if (argList.length != argCount + 1)
		{
		    throw new IllegalArgumentException("Parameter "+parmName+
				" has "+(argList.length - 1)+" arguments. "+
				"Expected "+argCount);
		}
	    }
	    else
	    {
		argCount = argList.length - 1;
	    }

	    String[] ret = new String[argCount];

	    for (int i = 0; i < argCount; i++)
	    {
		ret[i] = argList[i + 1];
	    }
	    return ret;
	}
}
