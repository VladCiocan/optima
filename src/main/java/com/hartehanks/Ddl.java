package com.hartehanks;

import java.util.*;
import java.io.*;
import com.hartehanks.dev.misc.*;

//
// Ddl class opens, reads and creates an array of Ddlfields that represent the
// content of a standard Trillium data Dictionary.
//
public final class Ddl
{
	private Hashtable	ddlFields = new Hashtable(100);
	private String		ddlFile = null;
	private String		recordName;
	private	int		recLen = -1;

//
// Only constructor expects the name of a Data Dictionary text file and will
// perform the open and load immediately.
// DdlFields can be accessed later using public methods
//
	public Ddl(String ddlFile) throws DdlException
	{
	    this.ddlFile = ddlFile;
	    loadDdlFile();
	}

//
// opens and creates thedata dictionary
//
	private void loadDdlFile() throws DdlException
	{
	    BufferedReader br = null;

//
//  Open input parm file and check success
//
            try
            {
                br = new BufferedReader(new FileReader(ddlFile));
            }
            catch (FileNotFoundException enofile)
            {
		throw new DdlException("Unable to open Ddl file "+ddlFile);
            }
//
//  Now read each parm line and store the parm key and parm values into the
//  list area supplied by the user
//
	    int	 lineno = 0;
            try
            {
                String   line = null;
                String   field = null;
                String   type = null;
                String   starts = null;
                String   length = null;
                String   point = "0";
                String   defvalue = null;
		boolean	redefine = false;

		try
		{
		    while ((line = br.readLine()) != null)
		    {
			lineno++;
			SuperStringTokenizer str = new SuperStringTokenizer(line);
			String[] stringarea = new String[0];

			try
			{
			    stringarea = str.getQuotedStringList(false);
			    str = null;
			}
			catch (QuotedStringException qse)
			{
			    br.close();
			    br = null;
			    str = null;
			    stringarea = null;
			    throw new DdlException("Quoted String error "+
				qse.getMessage()+" on line number "+lineno);
			}

			int salen = stringarea.length;

			if (salen > 0)
			{
//
// look for 'Dictionary' statement and convert RECORD, and FIELD
//
			    if (stringarea[0].equalsIgnoreCase("RECORD") ||
                                stringarea[0].equalsIgnoreCase("FIELD") ||
				stringarea[0].equalsIgnoreCase("//REDEFINE") ||
				stringarea[0].equalsIgnoreCase("//ANCHOR"))
			    {
				if (field != null)
				{
				    if (type == null || length == null)
				    {
					if (stringarea[0].equalsIgnoreCase(
								"//REDEFINE")||
					    stringarea[0].equalsIgnoreCase(
								"//ANCHOR"))
					{
					    redefine = true;
					    stringarea = null;
					    continue;
					}
					br.close();
					throw new DdlException(
						"Invalid data in Ddl "+
						ddlFile + " - line is "+line+
						" on line number "+lineno);
				    }
				    ddlFields.put(field.toUpperCase(),
					new DdlField(field, type,
					starts, length, point,
					defvalue, redefine));
				    redefine = false;
				}
				if (stringarea[0].equalsIgnoreCase(
							"//REDEFINE") ||
				    stringarea[0].equalsIgnoreCase("//ANCHOR"))
				{
				    redefine = true;
				    field = null;
				}
				else if (stringarea[0].equalsIgnoreCase(
								"RECORD"))
				{
				    field = "Record Descriptor";
				    if (stringarea[1].equalsIgnoreCase("IS"))
				    {
					recordName = stringarea[2];
				    }
				    else
				    {
					recordName = stringarea[1];
				    }
				}
				else if (stringarea[1].equalsIgnoreCase("IS"))
				{
				    field = stringarea[2];
				}
				else
				{
				    field = stringarea[1];
				}
				type = null;
				starts = "0";
				length = null;
				point = "0";
				defvalue = null;
			    }
//
// Test for user comments - skip straight out
//
			    else if (stringarea[0].startsWith("//"))
			    {
				stringarea = null;
				continue;
			    }
			    else if (stringarea[0].equalsIgnoreCase("TYPE"))
			    {
				int start = 1;
				type = " ";

				if (stringarea[1].equalsIgnoreCase("IS"))
				{
				    start = 2;
				}
				for (int j = start; j < salen; j++)
				{
				    type = type + stringarea[j]+"_";
				}
			    }
			    else if (stringarea[0].equalsIgnoreCase("STARTS"))
			    {
				starts = stringarea[salen - 1];
			    }
			    else if (stringarea[0].equalsIgnoreCase("LENGTH"))
			    {
				length = stringarea[salen - 1];
			    }
			    else if (salen > 2 &&
				stringarea[0].equalsIgnoreCase("ATTRIBUTES") &&
			    	stringarea[salen- 2].equalsIgnoreCase("POINT"))
			    {
				point = stringarea[salen - 1];
			    }
			    else if (stringarea[0].equalsIgnoreCase("DEFAULT"))
			    {
				defvalue = stringarea[salen - 1];
			    }
			}
			stringarea = null;
		    }
		}
		catch (ArrayIndexOutOfBoundsException aioobe)
		{
		    br.close();
		    throw new DdlException(
				"Ddl Record/Field name missing for "+
				ddlFile + " on line number "+lineno);
		}
                br.close();
		br = null;
                if (field != null)
                {
                    if (type == null || length == null)
                    {
			throw new DdlException("Invalid data in Ddl "+
						ddlFile + " - line is "+line+
						" on line number "+lineno);
                    }
		    ddlFields.put(field.toUpperCase(),
					new DdlField(field, type, starts,
					length, point, defvalue, redefine));
                }
            }
            catch (IOException eio)
            {
		try
		{
            	    br.close();
		}
		catch (IOException ioe) {}
                throw new DdlException(
                                "I/O exception found during read of Ddl file"+
				eio.getMessage()+ " on line number "+lineno);
            }
	    DdlField rec = (DdlField)ddlFields.get(
					"Record Descriptor".toUpperCase());
	    if (rec == null)
	    {
		throw new DdlException("Ddl "+ddlFile+" does not contain a "+
						"Record descriptor");
	    }
	    recLen = rec.length;
	    ddlFields.remove("Record Descriptor".toUpperCase());
	}

//
// Public access method to obtain the ddl record length
//
	public final int getRecordLength()
	{
	    return recLen;
	}

//
// Returns an array of DdlFields according to a list of field names supplied
//
	public final DdlField[] getLevelKeys(String[] keyList)
							throws DdlException
	{
	    DdlField[] ddlList = new DdlField[keyList.length];
	    for (int i = 0; i < keyList.length; i++)
	    {
		if (keyList[i] == null)
		{
		    Thread.dumpStack();
		    throw new DdlException("The level"+(i+1)+
				"Key supplied on "+ "open for Ddl file "+
				ddlFile+" was null or invalid");
		}
		ddlList[i] = (DdlField)ddlFields.get(keyList[i].toUpperCase());
		if (ddlList[i] == null)
		{
		    Thread.dumpStack();
		    throw new DdlException("The level"+(i+1)+"Key '"+
			keyList[i]+ "' supplied on "+ "open for Ddl file "+
			ddlFile+ " was not found");
		}
	    }
	    return ddlList;
	}

//
// returns the string value from a supplied record for a given field.
//
	public final String getKey(DdlField[] levelKeys, byte[] buffer)
	{
	    String res = "";

	    for (int i = 0; i < levelKeys.length; i++)
	    {
		res += new String(buffer, levelKeys[i].start,
							levelKeys[i].length);
	    }

	    return res;
	}

//
// returns rname
//
	public final String getRecordName()
	{
	    return recordName;
	}

//
// Builds a record that uses the 'Default is xxx' data from the dictionary.
// but if undefined then the record fields are set to the default Default -
// i.e. blank for ascii character.
//
	public final byte[] createPadRecord()
	{
	    String	key;
	    byte[]	padRecord = new byte[recLen];
	    byte[]	sourceData = new byte[0];
	    DdlField target;

	    Enumeration enumer = ddlFields.keys();
            while (enumer.hasMoreElements())
            {
                key = (String)enumer.nextElement();
                target = (DdlField)ddlFields.get(key);
		if (target.redefine == false)
		{
		    DdlField tempDdl =
			new DdlField("dummy","ASCII_CHARACTER_",
						"0", "0", "0", null, false);
		    tempDdl.dataCharSet = target.dataCharSet;
		    tempDdl.dataClass = target.dataClass;
		    DdlField.copyField(sourceData, tempDdl,
							padRecord, target);
		}
	    }
	    return padRecord;
	}

//
// Simple enumerator for users to cycle over contained DdlFields
//
	public final Enumeration getDdlEnumerator()
	{
	    return ddlFields.keys();
	}

//
// returns a specific Ddlfield by field name.
//
	public final DdlField getDdlField(String fieldName) throws DdlException
	{
	    return getDdlField(fieldName, true);
	}

//
// same as above but can be asked not to throw an exception.
//
	public final DdlField getDdlField(String fieldName, boolean fail)
							throws DdlException
	{
	    DdlField temp = (DdlField)ddlFields.get(fieldName.toUpperCase());
	    if (temp == null && fail == true)
	    {
		throw new DdlException("DDlField for field named '"+fieldName+
				"' not found in ddl file "+ddlFile);
	    }
	    //System.err.println("Temp is "+temp.toString());
	    return temp;
	}

//
// writes the content of all DdlFields to stderr.
//
	public final void dumpDdl()
	{
	    System.err.println("Ddl dump of file "+ddlFile+" follows:\n");
	    Enumeration enumer = ddlFields.keys();
            while (enumer.hasMoreElements())
            {
                String key = (String)enumer.nextElement();
                DdlField target = (DdlField)ddlFields.get(key);
		System.err.println("DumpTarget="+target.toString());
	    }
	}

//
// returns an array of all the DdlFields in the data dictionary sorted by
// start position and start/length if redefines exist.
//
	public final DdlField[] getDdlFields()
	{
	    Vector v = new Vector(100,10);
	    Enumeration enumer = ddlFields.keys();
            while (enumer.hasMoreElements())
            {
                String key = (String)enumer.nextElement();
                v.addElement(ddlFields.get(key));
	    }

	    DdlField[] ret = new DdlField[v.size()];
	    v.copyInto(ret);

	    boolean swap = true;

	    do
	    {
		swap = false;

		for (int i = 0; i < ret.length - 1; i++)
		{
		    if (ret[i].start < ret[i+1].start)
		    {
			continue;
		    }

		    if (ret[i].start == ret[i+1].start &&
			ret[i].length <= ret[i+1].length)
		    {
			continue;
		    }

		    DdlField temp = ret[i];
		    ret[i] = ret[i+1];
		    ret[i+1] = temp;
		    swap = true;
		}
	    }
	    while (swap);

	    return ret;
	}

//
// object finalize resets ddlfields hash as this is slow to clear otherwise
//
	protected void finalize() throws Throwable
	{
	    ddlFields = new Hashtable(1);
	    super.finalize();
	}
}
