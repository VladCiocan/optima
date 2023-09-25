package com.hartehanks.optima.api;

public class COptimaLicense {
    String sUserName = "";
    String sCompanyName = "";
    String sLicenseKey = "";

    public COptimaLicense() {
    }

    public String getCompanyName() {
        return this.sCompanyName;
    }

    public String getUserName() {
        return this.sUserName;
    }

    public String getLicenseKey() {
        return this.sLicenseKey;
    }
}

