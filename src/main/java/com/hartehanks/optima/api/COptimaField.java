package com.hartehanks.optima.api;

public class COptimaField {
    String m_sFieldName = "";
    int m_nFieldType = 0;

    public COptimaField() {
        this.m_sFieldName = "";
        this.m_nFieldType = 0;
    }

    public COptimaField(String var1, int var2) {
        this.m_sFieldName = var1;
        this.m_nFieldType = var2;
    }

    public String getFieldName() {
        return this.m_sFieldName;
    }

    public int getFieldType() {
        return this.m_nFieldType;
    }
}
