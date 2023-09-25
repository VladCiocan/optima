package com.hartehanks.optima.api;

public class COptimaSearchSession {
    private String[] arrFieldNames;
    private String[] arrFieldValues;
    private int[] arrFieldStatuses;
    private int[] arrFieldTypes;
    private String[] arrAlternatives;
    private int[] arrAlternativeStatuses;
    private int nFieldCount;
    private int nAltCount;
    private int nCurrentFieldNumber;
    private String sCurrentCountryISO;

    public COptimaSearchSession() {
        this.Clear();
    }

    public String getFieldName(int var1) {
        return var1 >= 0 && var1 < this.nFieldCount ? this.arrFieldNames[var1] : "";
    }

    public String getFieldValue(int var1) {
        return var1 >= 0 && var1 < this.nFieldCount ? this.arrFieldValues[var1] : "";
    }

    public String getCurrentCountryISO() {
        return this.sCurrentCountryISO;
    }

    public int getFieldStatus(int var1) {
        return var1 >= 0 && var1 < this.nFieldCount ? this.arrFieldStatuses[var1] : -1;
    }

    public int getFieldType(int var1) {
        return var1 >= 0 && var1 < this.nFieldCount ? this.arrFieldTypes[var1] : -1;
    }

    public String getAlternative(int var1) {
        return var1 >= 0 && var1 < this.nAltCount ? this.arrAlternatives[var1] : "";
    }

    public int getAlternativeStatus(int var1) {
        return var1 >= 0 && var1 < this.nAltCount ? this.arrAlternativeStatuses[var1] : -1;
    }

    public int getCurrentFieldNumber() {
        return this.nCurrentFieldNumber;
    }

    public int getFieldCount() {
        return this.nFieldCount;
    }

    public int getAlternativeCount() {
        return this.nAltCount;
    }

    void setFieldName(int var1, String var2) {
        if (var1 >= 0 && var1 < this.nFieldCount) {
            this.arrFieldNames[var1] = var2;
        }

    }

    public void setFieldValue(int var1, String var2) {
        if (var1 >= 0 && var1 < this.nFieldCount) {
            this.arrFieldValues[var1] = var2;
        }

    }

    void setCurrentCountryISO(String var1) {
        this.sCurrentCountryISO = var1;
    }

    void setFieldStatus(int var1, int var2) {
        if (var1 >= 0 && var1 < this.nFieldCount) {
            this.arrFieldStatuses[var1] = var2;
        }

    }

    void setFieldType(int var1, int var2) {
        if (var1 >= 0 && var1 < this.nFieldCount) {
            this.arrFieldTypes[var1] = var2;
        }

    }

    void setAlternative(int var1, String var2) {
        if (var1 >= 0 && var1 < this.nAltCount) {
            this.arrAlternatives[var1] = var2;
        }

    }

    void setAlternativeStatus(int var1, int var2) {
        if (var1 >= 0 && var1 < this.nAltCount) {
            this.arrAlternativeStatuses[var1] = var2;
        }

    }

    void setCurrentFieldNumber(int var1) {
        this.nCurrentFieldNumber = var1;
    }

    void setFieldCount(int var1) {
        if (var1 >= 0) {
            this.nFieldCount = var1;
            this.arrFieldNames = new String[this.nFieldCount];
            this.arrFieldValues = new String[this.nFieldCount];
            this.arrFieldStatuses = new int[this.nFieldCount];
            this.arrFieldTypes = new int[this.nFieldCount];

            for(int var2 = 0; var2 < this.nFieldCount; ++var2) {
                this.arrFieldNames[var2] = "";
                this.arrFieldValues[var2] = "";
                this.arrFieldStatuses[var2] = 0;
                this.arrFieldTypes[var2] = 0;
            }
        }

    }

    void setAlternativeCount(int var1) {
        if (var1 >= 0) {
            this.nAltCount = var1;
            this.arrAlternatives = new String[this.nAltCount];
            this.arrAlternativeStatuses = new int[this.nAltCount];

            for(int var2 = 0; var2 < this.nAltCount; ++var2) {
                this.arrAlternatives[var2] = "";
                this.arrAlternativeStatuses[var2] = 0;
            }
        }

    }

    public void Clear() {
        this.arrFieldNames = null;
        this.arrFieldValues = null;
        this.arrFieldStatuses = null;
        this.arrFieldTypes = null;
        this.arrAlternatives = null;
        this.arrAlternativeStatuses = null;
        this.nFieldCount = 0;
        this.nAltCount = 0;
        this.nCurrentFieldNumber = -1;
        this.sCurrentCountryISO = "";
    }
}

