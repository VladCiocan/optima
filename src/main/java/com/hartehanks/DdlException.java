package com.hartehanks;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class DdlException extends Exception
{
	public DdlException()
	{
	    super("No message supplied");
	}
	public DdlException(String message)
	{
	    super(message);
	}
}
