package com.hartehanks.optima.api;


import java.util.Vector;

public class COptimaManagementServer {
    static final byte MGMT_SELECT = -64;
    static final byte MGMT_COMPARE = -63;
    static final byte MGMT_UPDATE = -62;
    static final byte MGMT_DOWNLOAD = -61;
    static final byte MGMT_GETJOBITEMLIST = -60;
    static final byte MGMT_SECURITY = -59;
    static final byte MGMT_GETCOUNTRYLIST = -58;
    static final byte MGMT_GETFIELDLIST = -57;
    static final byte MGMT_GETCONFIG = -56;
    static final byte MGMT_SETCONFIG = -55;
    static final byte MGMT_RESET = -54;
    static final byte MGMT_SELECTALIAS = -53;
    static final byte MGMT_GETEXCEPTIONS = -52;
    static final byte MGMT_SELECTUNIQUE = -51;
    static final byte MGMT_UPDATEJOBITEMSTATUS = -50;
    static final byte MGMT_VIEWLOG = -49;
    static final byte MGMT_ADDITEM = -48;
    static final byte MGMT_EDITITEM = -47;
    static final byte MGMT_DELETEITEM = -46;
    static final byte MGMT_GETUPDATEDFILELIST = -45;
    static final byte MGMT_GETFILE = -44;
    static final byte MGMT_GETJOBLIST = -43;
    static final byte MGMT_COMPAREFILE = -42;
    static final byte MGMT_GETSECTIONLIST = -41;
    static final byte MGMT_UPDATEJOBSTATUS = -40;
    static final int OPTIMA_MGMT_SERVER_PORT = 15016;
    String m_sHost = "";
    int m_nStatus = 0;
    String m_sLastError = "";
    CClientSocket m_Socket;
    int m_Timeout = 30000;

    public COptimaManagementServer() {
    }

    public String getHost() {
        return this.m_sHost;
    }

    public String getLastError() {
        return this.m_sLastError;
    }

    public int getStatus() {
        return this.m_nStatus;
    }

    public int getTimeout() {
        return this.m_Timeout / 1000;
    }

    public boolean setHost(String var1) {
        this.m_sHost = var1;
        return true;
    }

    public boolean setTimeout(int var1) {
        if (var1 >= 1 && var1 <= 3600) {
            this.m_Timeout = var1 * 1000;
            return true;
        } else {
            this.m_nStatus = 5;
            this.m_sLastError = "Invalid timeout value. Timeout must be between 1 and 3600 seconds.";
            return false;
        }
    }

    public boolean Select(String var1, Vector var2, Vector var3, boolean var4, boolean var5, int var6, int var7, Vector var8) {
        try {
            var8.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-64);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2.size());

                int var9;
                for(var9 = 0; var9 < var2.size(); ++var9) {
                    this.m_Socket.Write((COptimaCriteria)var2.elementAt(var9));
                }

                this.m_Socket.Write(var3.size());

                for(int var10 = 0; var10 < var3.size(); ++var10) {
                    Integer var12 = (Integer)var3.elementAt(var10);
                    this.m_Socket.Write(var12);
                }

                this.m_Socket.Write(var4);
                this.m_Socket.Write(var5);
                this.m_Socket.Write(var6);
                this.m_Socket.Write(var7);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var11 = this.m_Socket.ReadInt();

                    for(var9 = 0; var9 < var11; ++var9) {
                        COptimaContact var14 = new COptimaContact();
                        this.m_Socket.ReadContact(var14);
                        var8.addElement(var14);
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var13) {
            this.m_sLastError = "Exception occurred: " + var13.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean SelectAlias(String var1, Vector var2, int var3, Vector var4) {
        try {
            var4.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-53);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2.size());

                int var5;
                for(var5 = 0; var5 < var2.size(); ++var5) {
                    this.m_Socket.Write((COptimaCriteria)var2.elementAt(var5));
                }

                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    var5 = this.m_Socket.ReadInt();

                    for(int var6 = 0; var6 < var5; ++var6) {
                        var4.add(this.m_Socket.ReadString());
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean SelectUnique(String var1, Vector var2, COptimaContact var3) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-51);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2.size());

                for(int var4 = 0; var4 < var2.size(); ++var4) {
                    this.m_Socket.Write((COptimaCriteria)var2.elementAt(var4));
                }

                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    this.m_Socket.ReadContact(var3);
                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetCountryList(Vector var1) {
        try {
            var1.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-58);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var2 = this.m_Socket.ReadInt();

                    for(int var3 = 0; var3 < var2; ++var3) {
                        COptimaCountry var4 = new COptimaCountry();
                        var4.sCountryISO = this.m_Socket.ReadString();
                        var4.sCountryName = this.m_Socket.ReadString();
                        var1.addElement(var4);
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetSectionList(Vector var1) {
        try {
            var1.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-41);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var2 = this.m_Socket.ReadInt();

                    for(int var3 = 0; var3 < var2; ++var3) {
                        var1.add(this.m_Socket.ReadString());
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetFieldList(String var1, Vector var2) {
        try {
            if (var1.length() != 3) {
                this.m_nStatus = 3;
                this.m_sLastError = "Not a valid country ISO.";
                return false;
            } else {
                var2.removeAllElements();
                this.m_Socket = new CClientSocket();
                this.m_Socket.m_Timeout = this.m_Timeout;
                if (!this.m_Socket.Connect(this.m_sHost)) {
                    this.CloseSocket();
                    this.m_nStatus = 10;
                    this.m_sLastError = "Unable to connect to specified server.";
                    return false;
                } else {
                    this.m_Socket.Write((byte)-57);
                    this.m_Socket.Write(var1);
                    this.m_Socket.Flush();
                    if (!this.CheckStatus()) {
                        this.CloseSocket();
                        return false;
                    } else {
                        int var3 = this.m_Socket.ReadInt();

                        for(int var4 = 0; var4 < var3; ++var4) {
                            var2.add(new COptimaField(this.m_Socket.ReadString(), this.m_Socket.ReadInt()));
                        }

                        this.CloseSocket();
                        return true;
                    }
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetExceptions(COptimaExceptionRule var1, int var2, Vector var3) {
        try {
            var3.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-52);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var4 = this.m_Socket.ReadInt();

                    for(int var5 = 0; var5 < var4; ++var5) {
                        COptimaExceptionRule var6 = new COptimaExceptionRule();
                        this.m_Socket.ReadExceptionRule(var6);
                        var3.add(var6);
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean AddItem(String var1, COptimaExceptionRule var2, String var3) {
        var3 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-48);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                boolean var4 = this.CheckStatus();
                var3 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var4;
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean EditItem(String var1, COptimaExceptionRule var2, COptimaExceptionRule var3, String var4) {
        var4 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-47);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                boolean var5 = this.CheckStatus();
                var4 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var5;
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean DeleteItem(String var1, COptimaExceptionRule var2, String var3) {
        var3 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-46);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                boolean var4 = this.CheckStatus();
                var3 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var4;
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public int CheckSecurity(String var1) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return -1;
            } else {
                this.m_Socket.Write((byte)-59);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return -1;
                } else {
                    int var2 = this.m_Socket.ReadInt();
                    this.CloseSocket();
                    return var2;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return -1;
        }
    }

    public String GetConfig(String var1, String var2) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return "";
            } else {
                this.m_Socket.Write((byte)-56);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return "";
                } else {
                    String var3 = this.m_Socket.ReadString();
                    this.CloseSocket();
                    return var3;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean SetConfig(String var1, String var2, String var3, String var4) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-55);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Write(var4);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String ViewLog(int var1, String var2) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return "";
            } else {
                this.m_Socket.Write((byte)-49);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return "";
                } else {
                    String var3 = this.m_Socket.ReadString();
                    this.CloseSocket();
                    return var3;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean Reset() {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-54);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetUpdateList(String var1, Vector var2) {
        try {
            var2.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-43);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var3 = this.m_Socket.ReadInt();

                    for(int var4 = 0; var4 < var3; ++var4) {
                        COptimaUpdate var5 = new COptimaUpdate();
                        this.m_Socket.ReadUpdate(var5);
                        var2.add(var5);
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean CompareUpdate(String var1, int var2, String var3) {
        var3 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                var3 = this.m_sLastError;
                return false;
            } else {
                this.m_Socket.Write((byte)-63);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                boolean var4 = this.CheckStatus();
                var3 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var4;
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            var3 = this.m_sLastError;
            return false;
        }
    }

    public boolean CompareFile(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8) {
        var8 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                var8 = this.m_sLastError;
                return false;
            } else {
                this.m_Socket.Write((byte)-42);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Write(var4);
                this.m_Socket.Write(var5);
                this.m_Socket.Write(var6);
                this.m_Socket.Write(var7);
                this.m_Socket.Flush();
                boolean var9 = this.CheckStatus();
                var8 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var9;
            }
        } catch (Exception var11) {
            this.m_sLastError = "Exception occurred: " + var11.getMessage();
            this.m_nStatus = 3;
            var8 = this.m_sLastError;
            return false;
        }
    }

    public boolean Publish(String var1, int var2, String var3, String var4) {
        var4 = "";

        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                var4 = this.m_sLastError;
                return false;
            } else {
                this.m_Socket.Write((byte)-62);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                boolean var5 = this.CheckStatus();
                var4 = this.m_Socket.ReadString();
                this.CloseSocket();
                return var5;
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            var4 = this.m_sLastError;
            return false;
        }
    }

    public boolean GetUpdateItemList(String var1, int var2, Vector var3) {
        try {
            var3.removeAllElements();
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-60);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    int var4 = this.m_Socket.ReadInt();

                    for(int var5 = 0; var5 < var4; ++var5) {
                        COptimaUpdateItem var6 = new COptimaUpdateItem();
                        this.m_Socket.ReadUpdateItem(var6);
                        var3.add(var6);
                    }

                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean UpdateJobItemStatus(String var1, int var2, int var3, boolean var4) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-50);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Write(var4);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean UpdateJobStatus(String var1, int var2, int var3) {
        try {
            this.m_Socket = new CClientSocket();
            this.m_Socket.m_Timeout = this.m_Timeout;
            if (!this.m_Socket.Connect(this.m_sHost)) {
                this.CloseSocket();
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                this.m_Socket.Write((byte)-40);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    this.CloseSocket();
                    return false;
                } else {
                    this.CloseSocket();
                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    private boolean CheckStatus() {
        if (this.m_Socket != null && this.m_Socket.getConnected()) {
            int var1;
            try {
                var1 = this.m_Socket.ReadInt();
            } catch (Exception var3) {
                this.m_nStatus = 10;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                this.CloseSocket();
                return false;
            }

            if (var1 >= 0) {
                this.m_nStatus = var1;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return this.m_nStatus == 0;
            } else {
                this.m_nStatus = 10;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                this.CloseSocket();
                return false;
            }
        } else {
            this.m_nStatus = 10;
            this.m_sLastError = OSV.sStatusText[this.m_nStatus];
            this.CloseSocket();
            return false;
        }
    }

    private void CloseSocket() {
        if (this.m_Socket != null) {
            this.m_Socket.Disconnect();
        }

        this.m_Socket = null;
    }
}

