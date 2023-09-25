package com.hartehanks.optima.api;


import java.io.IOException;
import java.util.Date;

public class COptimaUpdate {
    int m_nIndex;
    String m_sName;
    int m_eType;
    int m_eStatus;
    Date m_dtFileDateTime;
    Date m_dtDownloadDateTime;
    Date m_dtCompareDateTime;
    Date m_dtPublishDateTime;
    String m_sPublishUserName;

    public COptimaUpdate() {
        this.m_eType = 0;
        this.m_eStatus = 0;
    }

    public COptimaUpdate(COptimaUpdate var1) {
        this.m_nIndex = 0;
        this.m_sName = var1.m_sName;
        this.m_eType = var1.m_eType;
        this.m_eStatus = var1.m_eStatus;
        this.m_dtFileDateTime = var1.m_dtFileDateTime;
        this.m_dtDownloadDateTime = var1.m_dtDownloadDateTime;
        this.m_dtCompareDateTime = var1.m_dtCompareDateTime;
        this.m_dtPublishDateTime = var1.m_dtPublishDateTime;
        this.m_sPublishUserName = var1.m_sPublishUserName;
    }

    public int getID() {
        return this.m_nIndex;
    }

    public String getName() {
        return this.m_sName;
    }

    public int getUpdateType() {
        return this.m_eType;
    }

    public int getStatus() {
        return this.m_eStatus;
    }

    public Date getFileDateTime() {
        return this.m_dtFileDateTime;
    }

    public Date getDownloadDateTime() {
        return this.m_dtDownloadDateTime;
    }

    public Date getCompareDateTime() {
        return this.m_dtCompareDateTime;
    }

    public Date getPublishDateTime() {
        return this.m_dtPublishDateTime;
    }

    public String getPublishUsername() {
        return this.m_sPublishUserName;
    }

    void Read(CClientSocket var1) throws IOException {
        this.m_nIndex = var1.ReadInt();
        this.m_sName = var1.ReadString();
        this.m_eType = var1.ReadInt();
        this.m_eStatus = var1.ReadInt();
        this.m_dtFileDateTime = new Date((long)var1.ReadInt() * 1000L);
        this.m_dtDownloadDateTime = new Date((long)var1.ReadInt() * 1000L);
        this.m_dtCompareDateTime = new Date((long)var1.ReadInt() * 1000L);
        this.m_dtPublishDateTime = new Date((long)var1.ReadInt() * 1000L);
        this.m_sPublishUserName = var1.ReadString();
    }
}

