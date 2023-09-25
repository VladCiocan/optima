package com.hartehanks;

class Ksh
{
	public static final String	black;
	public static final String	red;
	public static final String	green;
	public static final String	yellow;
	public static final String	blue;
	public static final String	magenta;
	public static final String	cyan;
	public static final String	white;

	public static final String	bold;
	public static final String	normal;

	static
	{
	    String osname = System.getProperty("os.name").toUpperCase();
	    //System.err.println("OS name "+osname);

	    if (osname.indexOf("WINDOWS") < 0)
	    {
		black = "\033[30m";
		red = "\033[31m";
		green = "\033[32m";
		yellow = "\033[33m";
		blue = "\033[34m";
		magenta = "\033[35m";
		cyan = "\033[36m";
		white = "\033[37m";

		bold = "\033[01m";
		normal = "\033[00m";
	    }
	    else
	    {
		black = "";
		red = "";
		green = "";
		yellow = "";
		blue = "";
		magenta = "";
		cyan = "";
		white = "";

		bold = "";
		normal = "";
	    }
	}

	public static void main(String[] args)
	{
	    System.err.println(red+"Hello World"+normal);
	}
}
