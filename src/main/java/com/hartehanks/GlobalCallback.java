package com.hartehanks;

import com.hartehanks.optima.api.COptimaContact;

import java.io.*;
//
// List of methods that the GlobalDriver must implement to support the child's
// constant chit-chat.
//
public interface GlobalCallback
{
    public void registerStart(GlobalChild child, int childId);
    public GlobalQueueEntity  getRecord(GlobalChild child, int childId);
    public int  getThreadCountForHost(int hostId);
    public boolean setRecord(GlobalChild child, int childId,
                             int hostNumber, int numRetries, COptimaContact[] globalContac);
    public void printLog(String toPrint);
    public void childFinished(GlobalChild child, int childId);
    public boolean childHung(GlobalChild child, int childId, int action);
    public int getHostNumber();
    public void setHostNumber(int childId, int hostNumber);
    public PrintWriter getLogWriter();
    public void logTruncatedField(int fieldNum, byte[] data);
}

