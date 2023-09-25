package com.hartehanks.optima.api;


import java.util.Vector;

public class COptimaCriteria {
    public static final int OPTIMA_EXACT_SEARCH = 1;
    public static final int OPTIMA_EXACT_COMPLETE_SEARCH = 2;
    public static final int OPTIMA_APPROX_SEARCH = 4;
    public static final int OPTIMA_APPROX_COMPLETE_SEARCH = 8;
    public static final int OPTIMA_NOT_SEARCH = 128;
    public static final int OPTIMA_NOT_EXACT_SEARCH = 129;
    public static final int OPTIMA_NOT_EXACT_COMPLETE_SEARCH = 130;
    public static final int OPTIMA_NOT_APPROX_SEARCH = 132;
    public static final int OPTIMA_NOT_APPROX_COMPLETE_SEARCH = 136;
    String m_sValue = "";
    int m_nSearchType = 0;
    Vector m_FieldList = new Vector();

    public COptimaCriteria() {
    }

    public boolean setCriteriaValue(String var1) {
        if (var1.length() == 0) {
            return false;
        } else {
            this.m_sValue = var1;
            return true;
        }
    }

    public boolean setCriteriaSearchType(int var1) {
        if (var1 != 1 && var1 != 2 && var1 != 4 && var1 != 8 && var1 != 129 && var1 != 130 && var1 != 132 && var1 != 136) {
            return false;
        } else {
            this.m_nSearchType = var1;
            return true;
        }
    }

    public boolean addCriteriaField(int var1) {
        if (var1 >= 0 && var1 < 66) {
            this.m_FieldList.addElement(new Integer(var1));
            return true;
        } else {
            return false;
        }
    }

    public void ClearCriteria() {
        this.m_sValue = "";
        this.m_nSearchType = 0;
        this.m_FieldList.removeAllElements();
    }

    void Write(CClientSocket var1) {
        var1.Write(this.m_nSearchType);
        var1.Write(this.m_sValue);
        var1.Write(this.m_FieldList.size());

        for(int var2 = 0; var2 < this.m_FieldList.size(); ++var2) {
            var1.Write((Integer)this.m_FieldList.elementAt(var2));
        }

    }
}

