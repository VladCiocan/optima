package com.hartehanks.optima.api;


import java.io.IOException;

public class COptimaExceptionRule {
    public static final int None = 0;
    public static final int Add = 1;
    public static final int Delete = 2;
    public static final int Alias = 3;
    public static final int Transformation = 4;
    public static final int Postcode_Range_Alias = 5;
    public static final int Postcode_Range_Add = 6;
    private COptimaContact m_ScopeContact = new COptimaContact();
    private COptimaContact m_TransformationContact = new COptimaContact();
    private int m_nRuleType = 0;

    public COptimaExceptionRule() {
    }

    public COptimaExceptionRule(COptimaExceptionRule var1) {
        this.m_nRuleType = var1.m_nRuleType;
        this.m_ScopeContact = new COptimaContact(var1.m_ScopeContact);
        this.m_TransformationContact = new COptimaContact(var1.m_TransformationContact);
    }

    public void Clear() {
        this.m_nRuleType = 0;
        this.m_ScopeContact.Clear();
        this.m_TransformationContact.Clear();
    }

    public String getScopeField(int var1) {
        return this.m_ScopeContact.getField(var1);
    }

    public void setScopeField(int var1, String var2) {
        this.m_ScopeContact.setField(var1, var2);
    }

    public String getTransformationField(int var1) {
        return this.m_TransformationContact.getField(var1);
    }

    public void setTransformationField(int var1, String var2) {
        this.m_TransformationContact.setField(var1, var2);
    }

    public int getRuleType() {
        return this.m_nRuleType;
    }

    public boolean setRuleType(int var1) {
        if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 6 && var1 != 5) {
            return false;
        } else {
            this.m_nRuleType = var1;
            return true;
        }
    }

    void Write(CClientSocket var1) {
        this.m_ScopeContact.Write(var1);
        this.m_TransformationContact.Write(var1);
        var1.Write(this.m_nRuleType);
    }

    void Read(CClientSocket var1) throws IOException {
        this.Clear();
        this.m_ScopeContact.Read(var1);
        this.m_TransformationContact.Read(var1);
        this.m_nRuleType = var1.ReadInt();
    }
}

