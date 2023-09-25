package com.hartehanks;

public class GlobalRecord
{
	public long		recordNumber;
	public byte[]		data = null;
	public String		fullName = null;

	public GlobalRecord(byte[] data, long recordNumber, String fullName)
	{
	    this.data = data;
	    this.recordNumber = recordNumber;
	    this.fullName = fullName;
	}
}
