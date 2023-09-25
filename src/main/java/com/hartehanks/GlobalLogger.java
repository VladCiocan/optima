package com.hartehanks;

import java.io.*;

public class GlobalLogger
{
	PrintWriter	logWriter = null;

	public GlobalLogger(PrintWriter logWriter)
	{
	    this.logWriter = logWriter;
	}

	public final synchronized void println(String toPrint)
	{
	    logWriter.println(toPrint);
	}
}
