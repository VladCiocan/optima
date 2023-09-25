package com.hartehanks.optima.api;
public class OptimaUpdateStatus {
    public static final int None = 0;
    public static final int AwaitingCompare = 1;
    public static final int Comparing = 2;
    public static final int UnderReview = 3;
    public static final int Publishing = 4;
    public static final int Published = 5;
    public static final int Rejected = 6;

    public OptimaUpdateStatus() {
    }
}
