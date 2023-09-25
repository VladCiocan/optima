package com.hartehanks.optima.api;


import java.util.Vector;

public class COptimaEnhancementDataset {
    String m_sName;
    String m_sDescription;
    Vector m_vAppliesTo = new Vector();

    COptimaEnhancementDataset() {
        this.m_sName = "";
        this.m_sDescription = "";
        this.m_vAppliesTo.clear();
    }

    COptimaEnhancementDataset(String var1, String var2) {
        this.m_sName = var1;
        this.m_sDescription = var2;
        this.m_vAppliesTo.clear();
    }

    COptimaEnhancementDataset(String var1, String var2, String var3) {
        this.m_sName = var1;
        this.m_sDescription = var2;
        this.m_vAppliesTo.add(var3);
    }

    void AddAppliesTo(String var1) {
        this.m_vAppliesTo.add(var1);
    }

    public String getName() {
        return this.m_sName;
    }

    public String getDescription() {
        return this.m_sDescription;
    }

    public Vector getAppliesTo() {
        return this.m_vAppliesTo;
    }
}

