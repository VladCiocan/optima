package com.hartehanks.optima.api;

public class OSV {
    public static final int OK = 0;
    public static final int InvalidSession = 1;
    public static final int NoSession = 2;
    public static final int GeneralError = 3;
    public static final int InvalidCommand = 4;
    public static final int InvalidParameter = 5;
    public static final int NotConnected = 6;
    public static final int SessionNotInitialized = 7;
    public static final int MemoryError = 8;
    public static final int NotLicensed = 9;
    public static final int SocketError = 10;
    public static String[] sStatusText = new String[]{"No error.", "Invalid session.", "No sessions available.", "Unknown error.", "Invalid command.", "Invalid parameter.", "Not connected to server.", "Search session not initialized.", "Memory allocation error.", "License error.", "Communication error."};

    public OSV() {
    }
}
