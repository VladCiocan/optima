package com.hartehanks.optima.api;


import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class COptimaServer {
    static final byte CONNECT = 1;
    static final byte DISCONNECT = 2;
    static final byte STANDARDISEADDRESS = 3;
    static final byte FORMATADDRESS = 4;
    static final byte VALIDATEADDRESS = 5;
    static final byte QUERYSESSION = 6;
    static final byte CLEARFIELD = 7;
    static final byte COMMITFIELD = 8;
    static final byte GETALTERNATIVES = 9;
    static final byte INITSESSION = 10;
    static final byte GETCOUNTRYLIST = 11;
    static final byte CLOSESESSION = 12;
    static final byte SEARCHADDRESS = 13;
    static final byte FORMATNAME = 14;
    static final byte LICENSEDATA = 15;
    static final byte VALIDATIONLEVEL = 16;
    static final byte SETOPTION = 17;
    static final byte GETOPTION = 18;
    static final byte SETOPTIONMASK = 19;
    static final byte GETOPTIONMASK = 20;
    static final byte GETOFTTEXT = 21;
    static final byte GETOONTEXT = 22;
    static final byte GETOFSTEXT = 23;
    static final byte GETOCOTEXT = 24;
    static final byte OUTPUTADDRESS = 25;
    static final byte PROCESSADDRESS = 26;
    static final byte GETDEFAULTCOUNTRY = 27;
    static final byte SETDEFAULTCOUNTRY = 28;
    static final byte GETLICENSEMODULE = 29;
    static final byte GETLANGUAGELIST = 30;
    static final byte SETMATCHKEYCODE = 31;
    static final byte GETMATCHKEYCODE = 32;
    static final byte GENERATEMATCHKEY = 33;
    static final byte SELECT = 34;
    static final byte LOGEVENT = 35;
    static final byte COMPARECONTACTS = 36;
    static final byte STATUSINFO = 37;
    static final byte CHECKSESSION = 38;
    static final byte ENHANCECONTACT = 39;
    static final byte GETENHANCEMENTDSLIST = 40;
    static final byte CLUSTERINGCONNECT = 41;
    static final byte ADDEXCEPTION = 42;
    static final byte DELETEEXCEPTION = 43;
    static final byte GETEXCEPTIONS = 44;
    static final byte SELECTALIAS = 45;
    static final byte SELECTUNIQUE = 46;
    static final byte GETCONFIG = 47;
    static final byte SETCONFIG = 48;
    static final byte RESET = 49;
    static final byte VIEWLOG = 50;
    static final byte PAUSE = 51;
    static final byte RESUME = 52;
    static final byte GETOTOTEXT = 53;
    static final byte PASSTHRUVALIDATE = -128;
    static final byte PASSTHRUSELECT = -127;
    static final byte PASSTHRUSEARCH = -126;
    static final byte PING = -125;
    static final byte GETCOUNTRYLISTDIRECT = -124;
    static final byte CLUSTERINGRECONNECT = -123;
    static final byte PASSTHRUSELECTALIAS = -122;
    static final byte PASSTHRUSELECTUNIQUE = -121;
    static final int OPTIMA_SERVER_PORT = 15015;
    String m_sHost = "";
    Vector m_vHostList = new Vector();
    int m_nID = 0;
    int m_nStatus = 0;
    boolean m_bConnected = false;
    String m_sLastError = "";
    CClientSocket m_Socket;
    int m_Timeout;
    String m_sMatchKeyCode = "";
    String m_sDefaultCountry = "";
    int[] m_arrOptions = new int[60];

    public COptimaServer() {
        for(int var1 = 0; var1 < this.m_arrOptions.length; ++var1) {
            this.m_arrOptions[var1] = 0;
        }

        this.m_Timeout = 30000;
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

    public boolean getConnected() {
        return this.m_bConnected;
    }

    public int getTimeout() {
        return this.m_Timeout / 1000;
    }

    public boolean setHost(String var1) {
        if (this.m_bConnected) {
            this.m_nStatus = 3;
            this.m_sLastError = "Cannot change host whilst connected.";
            return false;
        } else {
            this.m_sHost = var1;
            return true;
        }
    }

    public boolean setTimeout(int var1) {
        if (this.m_bConnected) {
            this.m_nStatus = 3;
            this.m_sLastError = "Cannot change timeout whilst connected.";
            return false;
        } else if (var1 >= 1 && var1 <= 3600) {
            this.m_Timeout = var1 * 1000;
            return true;
        } else {
            this.m_nStatus = 5;
            this.m_sLastError = "Invalid timeout value. Timeout must be between 1 and 3600 seconds.";
            return false;
        }
    }

    public boolean AddHost(String var1) {
        if (var1 != null && var1.length() != 0) {
            this.m_vHostList.add(var1);
            return true;
        } else {
            this.m_nStatus = 5;
            this.m_sLastError = OSV.sStatusText[this.m_nStatus];
            return false;
        }
    }

    public boolean Connect() {
        try {
            if (this.m_bConnected) {
                this.m_nStatus = 3;
                this.m_sLastError = "Already connected to server.";
                return false;
            } else if (this.m_vHostList.size() > 0) {
                return this.ClusteringConnect(false);
            } else if (this.m_sHost.length() > 0) {
                this.m_Socket = new CClientSocket();
                this.m_Socket.m_Timeout = this.m_Timeout;
                if (!this.m_Socket.Connect(this.m_sHost)) {
                    this.CloseSocket();
                    this.m_nStatus = 10;
                    this.m_sLastError = "Unable to connect to specified server.";
                    return false;
                } else {
                    this.m_Socket.Write((byte)1);
                    this.m_Socket.Flush();
                    if (!this.CheckStatus()) {
                        return false;
                    } else {
                        this.m_nID = this.m_Socket.ReadInt();
                        this.m_bConnected = true;
                        return true;
                    }
                }
            } else {
                this.m_nStatus = 3;
                this.m_sLastError = "No host specified.";
                return false;
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean Disconnect() {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)2);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                this.CheckStatus();
                this.CloseSocket();
                if (this.m_nStatus != 0) {
                    this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception var2) {
            this.CloseSocket();
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean CheckSession() {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)38);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.CheckSession() : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetLicenseInfo(COptimaLicense var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)15);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetLicenseInfo(var1) : false;
                } else {
                    var1.sUserName = this.m_Socket.ReadString();
                    var1.sCompanyName = this.m_Socket.ReadString();
                    var1.sLicenseKey = this.m_Socket.ReadString();
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String GetDefaultCountry() {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write((byte)27);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetDefaultCountry() : "";
                } else {
                    String var1 = this.m_Socket.ReadString();
                    if (var1.length() == 3) {
                        this.m_sDefaultCountry = var1;
                        return this.m_sDefaultCountry;
                    } else {
                        return "";
                    }
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean SetDefaultCountry(String var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.length() != 3) {
                this.m_nStatus = 3;
                this.m_sLastError = "Not a valid country ISO.";
                return false;
            } else {
                this.m_Socket.Write((byte)28);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SetDefaultCountry(var1) : false;
                } else {
                    this.m_sDefaultCountry = var1;
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String GetMatchKeyCode() {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write((byte)32);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetMatchKeyCode() : "";
                } else {
                    this.m_sMatchKeyCode = this.m_Socket.ReadString();
                    return this.m_sMatchKeyCode;
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean SetMatchKeyCode(String var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)31);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SetMatchKeyCode(var1) : false;
                } else {
                    this.m_sMatchKeyCode = var1;
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetLicenseModule(int var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)29);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetLicenseModule(var1) : false;
                } else {
                    return this.m_Socket.ReadBoolean();
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String GetOFTText(int var1) {
        return this.GetText((byte)21, var1);
    }

    public String GetOONText(int var1) {
        return this.GetText((byte)22, var1);
    }

    public String GetOFSText(int var1) {
        return this.GetText((byte)23, var1);
    }

    public String GetOCOText(int var1) {
        return this.GetText((byte)24, var1);
    }

    public String GetOTOText(int var1) {
        return this.GetText((byte)53, var1);
    }

    public boolean SetOption(int var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)17);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SetOption(var1, var2) : false;
                } else {
                    this.m_arrOptions[var1] = var2;
                    return true;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public int GetOption(int var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return -1;
            } else {
                this.m_Socket.Write((byte)18);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetOption(var1) : -1;
                } else {
                    int var2 = this.m_Socket.ReadInt();
                    this.m_arrOptions[var1] = var2;
                    return var2;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return -1;
        }
    }

    public boolean SetOptionMask(int var1, int var2, boolean var3) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)19);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SetOptionMask(var1, var2, var3) : false;
                } else {
                    if (var2 < 31) {
                        this.m_arrOptions[var1] = this.m_arrOptions[var1] & (2147483647 ^ 1 << var2) | (var3 ? 1 << var2 : 0);
                    } else if (var2 < 62) {
                        this.m_arrOptions[var1 + 1] = this.m_arrOptions[var1 + 1] & (2147483647 ^ 1 << var2 - 31) | (var3 ? 1 << var2 - 31 : 0);
                    } else {
                        this.m_arrOptions[var1 + 2] = this.m_arrOptions[var1 + 2] & (2147483647 ^ 1 << var2 - 62) | (var3 ? 1 << var2 - 62 : 0);
                    }

                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetOptionMask(int var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)20);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetOptionMask(var1, var2) : false;
                } else {
                    boolean var3 = this.m_Socket.ReadBoolean();
                    if (var2 < 31) {
                        this.m_arrOptions[var1] = this.m_arrOptions[var1] & (2147483647 ^ 1 << var2) | (var3 ? 1 << var2 : 0);
                    } else if (var2 < 62) {
                        this.m_arrOptions[var1 + 1] = this.m_arrOptions[var1 + 1] & (2147483647 ^ 1 << var2 - 31) | (var3 ? 1 << var2 - 31 : 0);
                    } else {
                        this.m_arrOptions[var1 + 2] = this.m_arrOptions[var1 + 2] & (2147483647 ^ 1 << var2 - 62) | (var3 ? 1 << var2 - 62 : 0);
                    }

                    return var3;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean ProcessAddress(COptimaContact var1, int var2, int var3, int var4) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)26);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Write(var4);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.ProcessAddress(var1, var2, var3, var4) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean StandardiseAddress(COptimaContact var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)3);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.StandardiseAddress(var1) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean ValidateAddress(COptimaContact var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)5);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.ValidateAddress(var1) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean FormatAddress(COptimaContact var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)4);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.FormatAddress(var1) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GenerateMatchkey(COptimaContact var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)33);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GenerateMatchkey(var1) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean FormatName(COptimaContact var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)14);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.FormatName(var1) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean SearchAddress(COptimaContact var1, int var2, Vector var3) {
        try {
            var3.removeAllElements();
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)13);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SearchAddress(var1, var2, var3) : false;
                } else {
                    var2 = this.m_Socket.ReadInt();

                    for(int var4 = 0; var4 < var2; ++var4) {
                        COptimaContact var5 = new COptimaContact();
                        this.m_Socket.ReadContact(var5);
                        var3.addElement(var5);
                    }

                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public int CompareContacts(COptimaContact var1, COptimaContact var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return -1;
            } else {
                this.m_Socket.Write((byte)36);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.CompareContacts(var1, var2) : -1;
                } else {
                    return this.m_Socket.ReadInt();
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return -1;
        }
    }

    public boolean LogEvent(String var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)35);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.LogEvent(var1) : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean Select(String var1, Vector var2, Vector var3, boolean var4, boolean var5, int var6, int var7, Vector var8) {
        try {
            var8.removeAllElements();
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)34);
                this.m_Socket.Write(this.m_nID);
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
                    return this.ClusteringConnect(true) ? this.Select(var1, var2, var3, var4, var5, var6, var7, var8) : false;
                } else {
                    int var11 = this.m_Socket.ReadInt();

                    for(var9 = 0; var9 < var11; ++var9) {
                        COptimaContact var14 = new COptimaContact();
                        this.m_Socket.ReadContact(var14);
                        var8.addElement(var14);
                    }

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
            var4.clear();
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)45);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2.size());

                int var5;
                for(var5 = 0; var5 < var2.size(); ++var5) {
                    this.m_Socket.Write((COptimaCriteria)var2.elementAt(var5));
                }

                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SelectAlias(var1, var2, var3, var4) : false;
                } else {
                    var5 = this.m_Socket.ReadInt();

                    for(int var6 = 0; var6 < var5; ++var6) {
                        var4.add(this.m_Socket.ReadString());
                    }

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
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)46);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2.size());

                for(int var4 = 0; var4 < var2.size(); ++var4) {
                    this.m_Socket.Write((COptimaCriteria)var2.elementAt(var4));
                }

                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SelectUnique(var1, var2, var3) : false;
                } else {
                    this.m_Socket.ReadContact(var3);
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
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)11);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetCountryList(var1) : false;
                } else {
                    var1.removeAllElements();
                    int var2 = this.m_Socket.ReadInt();

                    for(int var3 = 0; var3 < var2; ++var3) {
                        COptimaCountry var4 = new COptimaCountry();
                        var4.sCountryISO = this.m_Socket.ReadString();
                        var4.sCountryName = this.m_Socket.ReadString();
                        var1.addElement(var4);
                    }

                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetLanguageList(Vector var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)30);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetLanguageList(var1) : false;
                } else {
                    var1.removeAllElements();
                    int var3 = this.m_Socket.ReadInt();

                    for(int var4 = 0; var4 < var3; ++var4) {
                        String var2 = this.m_Socket.ReadString();
                        var1.addElement(var2);
                    }

                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public int GetAcceptanceLevel(String var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return -1;
            } else if (var1.length() != 3) {
                this.m_nStatus = 3;
                this.m_sLastError = "Not a valid country ISO.";
                return -1;
            } else {
                this.m_Socket.Write((byte)16);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetAcceptanceLevel(var1) : -1;
                } else {
                    return this.m_Socket.ReadInt();
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return -1;
        }
    }

    public boolean InitSession(COptimaSearchSession var1, String var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)10);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.InitSession(var1, var2) : false;
                } else {
                    var1.setFieldCount(this.m_Socket.ReadInt());
                    var1.setCurrentCountryISO(var2);

                    for(int var3 = 0; var3 < var1.getFieldCount(); ++var3) {
                        var1.setFieldName(var3, this.m_Socket.ReadString());
                        var1.setFieldType(var3, this.m_Socket.ReadInt());
                    }

                    return true;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean QuerySession(COptimaSearchSession var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.getCurrentCountryISO().length() != 3) {
                this.m_nStatus = 7;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)6);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1.getFieldCount());

                int var2;
                for(var2 = 0; var2 < var1.getFieldCount(); ++var2) {
                    this.m_Socket.Write(var2);
                    this.m_Socket.Write(var1.getFieldValue(var2));
                }

                this.m_Socket.Write(-1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return false;
                } else {
                    for(var2 = 0; var2 < var1.getFieldCount(); ++var2) {
                        var1.setFieldStatus(var2, this.m_Socket.ReadInt());
                        var1.setFieldValue(var2, this.m_Socket.ReadString());
                    }

                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetAlternatives(COptimaSearchSession var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.getCurrentCountryISO().length() != 3) {
                this.m_nStatus = 7;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                var1.setCurrentFieldNumber(var2);
                var1.setAlternativeCount(0);
                this.m_Socket.Write((byte)9);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var1.getFieldValue(var2));
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return false;
                } else {
                    var1.setAlternativeCount(this.m_Socket.ReadInt());

                    for(int var3 = 0; var3 < var1.getAlternativeCount(); ++var3) {
                        var1.setAlternative(var3, this.m_Socket.ReadString());
                        var1.setAlternativeStatus(var3, this.m_Socket.ReadInt());
                    }

                    return true;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean CommitField(COptimaSearchSession var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.getCurrentCountryISO().length() != 3) {
                this.m_nStatus = 7;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var2 >= 0 && var2 < var1.getAlternativeCount()) {
                this.m_Socket.Write((byte)8);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1.getFieldCount());
                this.m_Socket.Write(var1.getCurrentFieldNumber());
                this.m_Socket.Write(var1.getAlternative(var2));
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return false;
                } else {
                    for(int var3 = 0; var3 < var1.getFieldCount(); ++var3) {
                        var1.setFieldValue(var3, this.m_Socket.ReadString());
                        var1.setFieldStatus(var3, this.m_Socket.ReadInt());
                    }

                    return true;
                }
            } else {
                this.m_nStatus = 5;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean ClearField(COptimaSearchSession var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.getCurrentCountryISO().length() != 3) {
                this.m_nStatus = 7;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var2 >= 0 && var2 < var1.getFieldCount()) {
                this.m_Socket.Write((byte)7);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1.getFieldCount());
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return false;
                } else {
                    for(int var3 = 0; var3 < var1.getFieldCount(); ++var3) {
                        var1.setFieldStatus(var3, this.m_Socket.ReadInt());
                        var1.setFieldValue(var3, this.m_Socket.ReadString());
                    }

                    return true;
                }
            } else {
                this.m_nStatus = 5;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean CloseSession(COptimaSearchSession var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else if (var1.getCurrentCountryISO().length() != 3) {
                this.m_nStatus = 7;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)12);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return false;
                } else {
                    var1.Clear();
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String GetStatusInfo() {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write((byte)37);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetStatusInfo() : "";
                } else {
                    return this.m_Socket.ReadString();
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean EnhanceContact(COptimaContact var1, String var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)39);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.EnhanceContact(var1, var2) : false;
                } else {
                    this.m_Socket.ReadContact(var1);
                    return true;
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetEnhancementDatasetList(Vector var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)40);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                this.m_nStatus = this.m_Socket.ReadInt();
                if (this.m_nStatus != 0) {
                    this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                    return false;
                } else {
                    var1.removeAllElements();
                    int var2 = this.m_Socket.ReadInt();

                    for(int var3 = 0; var3 < var2; ++var3) {
                        COptimaEnhancementDataset var4 = new COptimaEnhancementDataset(this.m_Socket.ReadString(), this.m_Socket.ReadString());
                        int var5 = this.m_Socket.ReadInt();

                        while(var5-- > 0) {
                            var4.AddAppliesTo(this.m_Socket.ReadString());
                        }

                        var1.add(var4);
                    }

                    return true;
                }
            }
        } catch (Exception var6) {
            this.m_sLastError = "Exception occurred: " + var6.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean AddExceptionRule(COptimaExceptionRule var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)42);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.AddExceptionRule(var1) : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean DeleteExceptionRule(COptimaExceptionRule var1) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)43);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.DeleteExceptionRule(var1) : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var3) {
            this.m_sLastError = "Exception occurred: " + var3.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public boolean GetExceptionRules(COptimaExceptionRule var1, int var2, Vector var3) {
        try {
            var3.removeAllElements();
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)44);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetExceptionRules(var1, var2, var3) : false;
                } else {
                    int var4 = this.m_Socket.ReadInt();

                    for(int var5 = 0; var5 < var4; ++var5) {
                        COptimaExceptionRule var6 = new COptimaExceptionRule();
                        this.m_Socket.ReadExceptionRule(var6);
                        var3.add(var6);
                    }

                    return true;
                }
            }
        } catch (Exception var7) {
            this.m_sLastError = "Exception occurred: " + var7.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    String GetText(byte var1, int var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write(var1);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetText(var1, var2) : "";
                } else {
                    return this.m_Socket.ReadString();
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public String GetConfigValue(String var1, String var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write((byte)47);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.GetConfigValue(var1, var2) : "";
                } else {
                    return this.m_Socket.ReadString();
                }
            }
        } catch (Exception var4) {
            this.m_sLastError = "Exception occurred: " + var4.getMessage();
            this.m_nStatus = 3;
            return "";
        }
    }

    public boolean SetConfigValue(String var1, String var2, String var3) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)48);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Write(var3);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.SetConfigValue(var1, var2, var3) : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var5) {
            this.m_sLastError = "Exception occurred: " + var5.getMessage();
            this.m_nStatus = 3;
            return false;
        }
    }

    public String ViewLog(int var1, String var2) {
        try {
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return "";
            } else {
                this.m_Socket.Write((byte)50);
                this.m_Socket.Write(var1);
                this.m_Socket.Write(var2);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.ViewLog(var1, var2) : "";
                } else {
                    return this.m_Socket.ReadString();
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
            if (!this.m_bConnected) {
                this.m_nStatus = 6;
                this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                return false;
            } else {
                this.m_Socket.Write((byte)49);
                this.m_Socket.Write(this.m_nID);
                this.m_Socket.Flush();
                if (!this.CheckStatus()) {
                    return this.ClusteringConnect(true) ? this.Reset() : false;
                } else {
                    return true;
                }
            }
        } catch (Exception var2) {
            this.m_sLastError = "Exception occurred: " + var2.getMessage();
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

    private boolean ClusteringConnect(boolean var1) {
        if (this.m_vHostList.size() == 0) {
            return false;
        } else if (var1 && (this.m_nStatus == 3 || this.m_nStatus == 5 || this.m_nStatus == 4 || this.m_nStatus == 9)) {
            return false;
        } else {
            Iterator var2 = this.m_vHostList.iterator();
            boolean var3 = false;
            String var4 = "";

            while(var2.hasNext() && !var3) {
                var4 = (String)var2.next();
                this.CloseSocket();
                this.m_Socket = new CClientSocket();
                this.m_Socket.m_Timeout = this.m_Timeout;
                if (this.m_Socket.Connect(var4)) {
                    var3 = true;
                } else {
                    this.CloseSocket();
                }
            }

            if (!var3) {
                this.m_nStatus = 10;
                this.m_sLastError = "Unable to connect to specified server.";
                return false;
            } else {
                int var5;
                if (!var1) {
                    this.m_Socket.Write((byte)41);

                    try {
                        this.m_Socket.Flush();
                    } catch (IOException var8) {
                        return false;
                    }

                    if (!this.CheckStatus()) {
                        return false;
                    }

                    try {
                        this.m_nID = this.m_Socket.ReadInt();
                        var5 = this.m_Socket.ReadInt();

                        for(int var7 = 0; var7 < var5; ++var7) {
                            if (var7 < 60) {
                                this.m_arrOptions[var7] = this.m_Socket.ReadInt();
                            } else {
                                int var6 = this.m_Socket.ReadInt();
                            }
                        }

                        this.m_sMatchKeyCode = this.m_Socket.ReadString();
                        this.m_sDefaultCountry = this.m_Socket.ReadString();
                    } catch (IOException var10) {
                        this.CloseSocket();
                        this.m_nStatus = 10;
                        this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                        return false;
                    }
                } else {
                    try {
                        this.m_Socket.Write((byte)-123);
                        this.m_Socket.Write(60);

                        for(var5 = 0; var5 < 60; ++var5) {
                            this.m_Socket.Write(this.m_arrOptions[var5]);
                        }

                        this.m_Socket.Write(this.m_sMatchKeyCode);
                        this.m_Socket.Write(this.m_sDefaultCountry);
                        this.m_Socket.Flush();
                        if (!this.CheckStatus()) {
                            return false;
                        }

                        this.m_nID = this.m_Socket.ReadInt();
                    } catch (IOException var9) {
                        this.CloseSocket();
                        this.m_nStatus = 10;
                        this.m_sLastError = OSV.sStatusText[this.m_nStatus];
                        return false;
                    }
                }

                this.m_bConnected = true;
                this.m_sHost = var4;
                return true;
            }
        }
    }

    private void CloseSocket() {
        if (this.m_Socket != null) {
            this.m_Socket.Disconnect();
        }

        this.m_Socket = null;
        this.m_bConnected = false;
        this.m_nID = 0;
    }
}
