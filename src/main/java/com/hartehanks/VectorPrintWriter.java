package com.hartehanks;

import java.io.*;
import java.util.*;
import java.lang.*;

public final class VectorPrintWriter extends Vector
{
	private PrintWriter	printWriter = null;
	private boolean		append = false;

	public VectorPrintWriter(Writer out)
	{
	    printWriter = new PrintWriter(out);
	}
	public VectorPrintWriter(Writer out, boolean autoFlush)
	{
	    printWriter = new PrintWriter(out, autoFlush);
	}
	public VectorPrintWriter(OutputStream out)
	{
	    printWriter = new PrintWriter(out);
	}
	public VectorPrintWriter(OutputStream out, boolean autoFlush)
	{
	    printWriter = new PrintWriter(out, autoFlush);
	}

	public VectorPrintWriter()
	{
	}

	private void addOrAppend(String item)
	{
	    if (append == true)
	    {
		String org = (String)lastElement();
		org += item;
		setElementAt(org, size()-1);
	    }
	    else
	    {
		addElement(item);
	    }
	}

	public final boolean checkError()
	{
	    return (printWriter == null) ? false : printWriter.checkError();
	}
	public final void close()
	{
	    if (printWriter != null)
	    {
		printWriter.close();
		printWriter = null;
	    }
	}
	public final void flush()
	{
	    if (printWriter != null)
	    {
		printWriter.flush();
	    }
	}
	public final void print(boolean b)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(b));
		append = true;
	    }
	    else
	    {
		printWriter.print(b);
	    }
	}
	public final void print(char c)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(c));
		append = true;
	    }
	    else
	    {
		printWriter.print(c);
	    }
	}
	public final void print(int i)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(i));
		append = true;
	    }
	    else
	    {
		printWriter.print(i);
	    }
	}
	public final void print(long l)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(l));
		append = true;
	    }
	    else
	    {
		printWriter.print(l);
	    }
	}
	public final void print(float f)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(f));
		append = true;
	    }
	    else
	    {
		printWriter.print(f);
	    }
	}
	public final void print(double d)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(d));
		append = true;
	    }
	    else
	    {
		printWriter.print(d);
	    }
	}
	public final void print(char[] s)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(s));
		append = true;
	    }
	    else
	    {
		printWriter.print(s);
	    }
	}
	public final void print(String s)
	{
	    if (printWriter == null)
	    {
		addOrAppend(s);
		append = true;
	    }
	    else
	    {
		printWriter.print(s);
	    }
	}
	public final void print(Object obj)
	{
	    if (printWriter == null)
	    {
		addOrAppend(obj.toString());
		append = true;
	    }
	    else
	    {
		printWriter.print(obj);
	    }
	}
	public final void println()
	{
	    if (printWriter == null)
	    {
		addOrAppend("");
		append = false;
	    }
	    else
	    {
		printWriter.println();
	    }
	}
	public final void println(boolean b)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(b));
		append = false;
	    }
	    else
	    {
		printWriter.println(b);
	    }
	}
	public final void println(char c)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(c));
		append = false;
	    }
	    else
	    {
		printWriter.println(c);
	    }
	}
	public final void println(int i)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(i));
		append = false;
	    }
	    else
	    {
		printWriter.println(i);
	    }
	}
	public final void println(long l)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(l));
		append = false;
	    }
	    else
	    {
		printWriter.println(l);
	    }
	}
	public final void println(float f)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(f));
		append = false;
	    }
	    else
	    {
		printWriter.println(f);
	    }
	}
	public final void println(double d)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(d));
		append = false;
	    }
	    else
	    {
		printWriter.println(d);
	    }
	}
	public final void println(char[] s)
	{
	    if (printWriter == null)
	    {
		addOrAppend(String.valueOf(s));
		append = false;
	    }
	    else
	    {
		printWriter.println(s);
	    }
	}
	public final void println(String s)
	{
	    if (printWriter == null)
	    {
		addOrAppend(s);
		append = false;
	    }
	    else
	    {
		printWriter.println(s);
	    }
	}
	public final void println(Object obj)
	{
	    if (printWriter == null)
	    {
		addOrAppend(obj.toString());
		append = false;
	    }
	    else
	    {
		printWriter.println(obj);
	    }
	}

	public final void write(int c)
	{
	    print(c);
	}
	public final void write(char[] buf, int off, int len)
	{
	    print(String.valueOf(buf, off, len));
	}
	public final void write(char[] c)
	{
	    print(c);
	}
	public final void write(String buf, int off, int len)
	{
	    print(buf.substring(off, off + len));
	}
	public final void write(String s)
	{
	    print(s);
	}

	public final String toString()
	{
	    return "Is a vector "+(printWriter == null);
	}

	protected void finalize() throws Throwable
	{
	    close();
	    printWriter = null;
	    super.finalize();
	}
}
