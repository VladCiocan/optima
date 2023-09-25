package com.hartehanks.optima.api;

import java.io.IOException;

public class COptimaUpdateItem {
    int m_nIndex;
    boolean m_bStatus;
    COptimaExceptionRule m_Rule;

    public COptimaUpdateItem() {
        this.m_Rule = new COptimaExceptionRule();
    }

    public COptimaUpdateItem(COptimaUpdateItem var1) {
        this.m_nIndex = var1.ID();
        this.m_bStatus = var1.Included();
        this.m_Rule = new COptimaExceptionRule(var1.m_Rule);
    }

    public int ID() {
        return this.m_nIndex;
    }

    public COptimaExceptionRule getRule() {
        return this.m_Rule;
    }

    public boolean Included() {
        return this.m_bStatus;
    }

    void Read(CClientSocket var1) throws IOException {
        this.m_nIndex = var1.ReadInt();
        this.m_bStatus = var1.ReadBoolean();
        var1.ReadExceptionRule(this.m_Rule);
    }
}

