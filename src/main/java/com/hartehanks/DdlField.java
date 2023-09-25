package com.hartehanks;

import java.util.*;
import java.io.*;
import java.math.*;
import com.hartehanks.dev.misc.*;

//
// DdlFields class contains infromation about one field in a data dictionary.
// managed by Ddl class.
// Can not only act as a data dictionary description but can check and convert
// data between certain data types. this is not a full implementation of the
// conversion functionality as found in the Trillium dictlib.a
//
public final class DdlField
{

	public static final int	NOCSET = 0;
	public static final int	ASCII = 1;
	public static final int	EBCDIC = 2;
	public static final int	FIXED = 3;
	public static final int	PCIS = 8;
	private static String[] dataCharSetList =
	{
	    "NOCSET_", "ASCII_", "EBCDIC_", "FIXED_", "PCIS_", "UTF-8_",
	};

	public static final int	NOCLASS = 0;
	public static final int	CHARACTER = 1;
	public static final int	BINARY = 2;
	public static final int	NUMERIC = 3;
	public static final int	DATE = 4;
	public static final int	UINT  = 5;
	public static final int	PACKED = 6;

	public static final BigDecimal[] powers =
	{
	    new BigDecimal(1.0),
	    new BigDecimal(10.0),
	    new BigDecimal(100.0),
	    new BigDecimal(1000.0),
	    new BigDecimal(10000.0),
	    new BigDecimal(100000.0),
	    new BigDecimal(1000000.0),
	    new BigDecimal(10000000.0),
	    new BigDecimal(100000000.0),
	    new BigDecimal(1000000000.0),
	    new BigDecimal(10000000000.0),
	    new BigDecimal(100000000000.0),
	    new BigDecimal(1000000000000.0),
	    new BigDecimal(10000000000000.0),
	    new BigDecimal(100000000000000.0),
	};

	private static String[] dataClassList =
	{
	    "NOCLASS_",  "CHARACTER_", "BINARY_",   "NUMERIC_", "DATE_",
	    "CHRACTER_", "BOOLEAN_",   "INTEGER_",  "PACKED_",  "BITFIELD",
	    "EUROPEAN_", "AMERICAN_",  "CANADIAN_", "JULIAN_", "DECIMAL_",
	};
	private static int[]    dataClassXlate =
	{
	    NOCLASS,     CHARACTER,    BINARY,      NUMERIC,    CHARACTER,
	    CHARACTER,   UINT,         UINT,        PACKED,     UINT,
	    CHARACTER,   CHARACTER,    CHARACTER,   CHARACTER,  NUMERIC,
	};

//
// static function to copy data between a field and another field (same or
// different record or dictionaries with some conversion functionality like
// ascii to ebcdic or vv.
//
	public static void copyField(byte[] sourceRecord, DdlField sourceDdl,
				byte[] targetRecord, DdlField targetDdl)
	{
	    //Tracer.trace();
	    if (sourceDdl.dataCharSet == targetDdl.dataCharSet &&
		sourceDdl.dataClass == targetDdl.dataClass &&
		sourceDdl.length == targetDdl.length)
	    {
		System.arraycopy(sourceRecord, sourceDdl.start,
			targetRecord, targetDdl.start, targetDdl.length);
	    }
	    else
	    {
//
// If data is EBCDIC then convert to ASCII for playing with
//
		byte[] sourceData = new byte[sourceDdl.length];
		System.arraycopy(sourceRecord, sourceDdl.start,
			sourceData, 0, sourceDdl.length);

		if (sourceDdl.dataClass != DdlField.BINARY &&
				sourceDdl.dataCharSet == DdlField.EBCDIC)
		{
		    Conversion.toAscii(sourceData);
		}
//
// Now all we have to do is manipulate the data
//
		switch(targetDdl.dataClass)
		{
		    case DdlField.CHARACTER:
			switch(sourceDdl.dataClass)
			{
			    case DdlField.CHARACTER:
				if (targetDdl.length <= sourceDdl.length)
				{
				    System.arraycopy(sourceData, 0,
					targetRecord, targetDdl.start,
					targetDdl.length);
				}
				else // target is longer than source
				{
				    System.arraycopy(sourceData, 0,
					targetRecord, targetDdl.start,
					sourceDdl.length);
				    for (int i = targetDdl.start+
					sourceDdl.length;
				     	i < targetDdl.start+targetDdl.length;
									i++)
				    {
					targetRecord[i] = (byte)' ';
				    }
				}
				break;

			    case DdlField.BINARY:
				cantDo(sourceDdl, targetDdl);
				break;

			    case DdlField.NUMERIC:
				cantDo(sourceDdl, targetDdl);
				break;

			    case DdlField.DATE:
				cantDo(sourceDdl, targetDdl);
				break;

			    case DdlField.UINT:
				cantDo(sourceDdl, targetDdl);
				break;

			    case DdlField.PACKED:
				cantDo(sourceDdl, targetDdl);
				break;

			    default:
				cantDo(sourceDdl, targetDdl);
				break;
			}
			break;

		    case DdlField.BINARY:
			cantDo(sourceDdl, targetDdl);
			break;

		    case DdlField.NUMERIC:
			cantDo(sourceDdl, targetDdl);
			break;

		    case DdlField.DATE:
			cantDo(sourceDdl, targetDdl);
			break;

		    case DdlField.UINT:
			cantDo(sourceDdl, targetDdl);
			break;

		    case DdlField.PACKED:
			cantDo(sourceDdl, targetDdl);
			break;

		    default:
			cantDo(sourceDdl, targetDdl);
		}
//
// If data is ASCII and ECDIC is required then convert back
//
		if (targetDdl.dataClass != DdlField.BINARY &&
				targetDdl.dataCharSet == DdlField.EBCDIC)
		{
		    Conversion.toEbcdic(targetRecord,
					targetDdl.start, targetDdl.length);
		}
	    }
	}

//
// Simple failure reporting method - invoked where specific data conversions
// are not implemented.
//
	private static void cantDo(DdlField sourceDdl, DdlField targetDdl)
	{
	    //Tracer.trace();
	    System.err.println("Dont know how to copy "+
			dataCharSetList[sourceDdl.dataCharSet]+
			dataClassList[sourceDdl.dataClass]+" To "+
			dataCharSetList[targetDdl.dataCharSet]+
			dataClassList[targetDdl.dataClass]);
            System.exit(1);
	}

	private byte[]	data = null;
	String		name = null;
	String		type = null;
	String		defvalue = null;
	String		defvalueTrim = null;
	int		dataCharSet = DdlField.NOCSET;
	int		dataClass = DdlField.NOCLASS;
	int		start = 0;
	int		length = 0;
	int		point = 0;
	int		nerrs = 0;
	boolean		redefine = false;

//
// create a quick dummy ddlfield
//
	public DdlField(int dataCharSet, int dataClass, int length)
	{
	    //Tracer.trace();
	    this.dataCharSet = dataCharSet;
	    this.dataClass = dataClass;
	    this.start = 0;
	    this.length = length;
	    this.point = 0;
	    this.redefine = false;
	}
//
// Create a real active Ddl field
//
	public DdlField(String name, String type, String start, String length,
			String point, String defvalue, boolean redefine)
	{
	    //Tracer.trace();
	    this.name = name;
	    this.type = type.trim();
	    type = type.trim().toUpperCase();

	    for (int i = 0; i < dataCharSetList.length; i++)
	    {
		if (type.startsWith(dataCharSetList[i]))
		{
		    dataCharSet = i;
		    type = type.substring(dataCharSetList[i].length());
		}
	    }
	    for (int i = 0; i < dataClassList.length; i++)
	    {
		if (type.startsWith(dataClassList[i]))
		{
		    dataClass = dataClassXlate[i];
		    type = type.substring(dataClassList[i].length());
		}
	    }
	    if (type.length() > 0)
	    {
		System.err.println("Error: "+type+" remains!!");
		System.exit(1);
	    }
	    this.start = Integer.parseInt(start);
	    this.length = Integer.parseInt(length);
	    this.point = Integer.parseInt(point);
	    if (defvalue != null)
	    {
		defvalueTrim = defvalue.trim();

		if (dataClass == NUMERIC)
		{
		    while (defvalue.length() < this.length)
		    {
			defvalue = "          "+defvalue;
		    }
		    defvalue = defvalue.substring(defvalue.length()
								- this.length);
		}
		else
		{
		    while (defvalue.length() < this.length)
		    {
			defvalue += "          ";
		    }
		    defvalue = defvalue.substring(0, this.length);
		}
	    }
	    this.defvalue = defvalue;

	    this.redefine = redefine;
	}

// Constructor essentially clones an existing ddlfield and copies supplied
// data into the internal data storage slot
//
	public DdlField(DdlField specificField, byte[] record)
	{
	    //Tracer.trace();
	    data = new byte[specificField.length];
	    System.arraycopy(record, specificField.start,
			data, 0, specificField.length);
	    name = specificField.name;
	    dataCharSet = specificField.dataCharSet;
	    dataClass = specificField.dataClass;
	    start = 0;
	    length = specificField.length;
	    point = 0;
	    defvalue = specificField.defvalue;
	    defvalueTrim = specificField.defvalueTrim;
	    redefine = specificField.redefine;
	}

//
// Simple string return for printing purposes of DDlField array
//
	public static String toString(DdlField[] fieldList)
	{
	    //Tracer.trace();
	    String ret = (fieldList.length > 0) ? "Keys are: " : "No Keys";
	    for (int i = 0; i < fieldList.length; i++)
	    {
		ret += "L"+(i+1)+"key="+fieldList[i].name+
			"("+fieldList[i].start+","+fieldList[i].length+")  ";
	    }

	    return ret;
	}

//
// returns tha data from the ddlfield that may have been read from a record.
//
	public final byte[] getData()
	{
	    //Tracer.trace();
	    if (data == null)
	    {
		System.err.println("DdlField.getData() method only works with"+
			"getField() DdlField entities");
		System.exit(1);
	    }
	    return data;

	}

//
// Converts internal data to a string - right justified if the data field is
// numeric
//
	public final String getStringValue(byte[] data, boolean trimmed)
	{
	    //Tracer.trace();
	    String ret = getValue(data).toString();
	    if (trimmed)
	    {
		return ret.trim();
	    }

	    if (dataClass == DdlField.NUMERIC)
	    {
		while (ret.length() < length)
		{
			ret = "             "+ret;
		}
		return ret.substring(ret.length() - length);
	    }
	    return ret;
	}

//
// returns an Object (of type String, BigInteger or Integer) containing the
// real value of data in the data field
//
	public final Object getValue(byte[] data)
	{
	    //Tracer.trace();

	    if (dataClass != DdlField.BINARY && dataCharSet == DdlField.EBCDIC)
	    {
		byte[] sourceData = new byte[length];
		System.arraycopy(data, start, sourceData, 0, length);
		Conversion.toAscii(sourceData);
		return getValue(sourceData, 0);
	    }

	    return getValue(data, start);
	}

//
// Converts data from the supplied byte array in the correct data format.
//
	private final Object getValue(byte[] data, int localOff)
	{
	    //Tracer.trace();
	    switch(dataClass)
	    {
		case DdlField.CHARACTER:
		case DdlField.BINARY:
		case DdlField.DATE:
		case DdlField.PACKED:
		    return new String(data, localOff, length);

		case DdlField.NUMERIC:
		    try
		    {
			BigDecimal bi = new BigDecimal(
					new String(data,localOff,length));
			if (point > 0)
			{
			    return bi.divide(powers[point],
					point, BigDecimal.ROUND_UNNECESSARY);
			}

			return bi;
		    }
		    catch (NumberFormatException nfe)
		    {
			if (++nerrs < 10)
			{
			    System.err.println("Field "+name+
				" contains invalid "+
				"numeric value: >"+
				new String(data, localOff, length)+
				"<");
			}
			//return "NaN in "+ new String(data, localOff, length);
			return new String(data, localOff, length);
		    }

		case DdlField.UINT:
		    if (length < 4)
		    {
			byte[] res = new byte[4];
			System.arraycopy(data, localOff, res, 4-length,length);
			return new Integer(Conversion.byteToInteger(res, 0));
		    }
		    return new Integer(Conversion.byteToInteger(
					data, (localOff + length) - 4));

		case DdlField.NOCLASS:
		    System.err.println("No data Class for ddl field "+name);
		    System.exit(1);

	        default:
		    System.err.println("DataClass conversion error. "+
				"Class code is "+dataClass+". Data is "+
				new String(data, localOff, length));
		    Thread.dumpStack();
		    System.exit(1);
	    }

	    return null;
	}

//
// Simple string return for printing purposes of single DDlField item
//
	public final String toString()
	{
	    //Tracer.trace();
	    return "DdlField: "+this.name+" type "+
		dataCharSetList[dataCharSet]+dataClassList[dataClass]+
		"\nstart="+this.start+" length="+this.length+
		" redef="+this.redefine+
		"\ndefault=\""+this.defvalue+"\"";
	}

//
// cleanup to help gc
//
	protected void finalize() throws Throwable
	{
	    data = null;
	    name = null;
	    type = null;
	    defvalue = null;
	    defvalueTrim = null;

	    super.finalize();
	}

}
