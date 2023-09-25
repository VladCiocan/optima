package com.hartehanks.optima.api;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

class CClientSocket {
    private String m_sHost;
    private int m_nPort;
    private Socket m_Socket;
    private OutputStream m_WriteStream;
    private InputStream m_ReadStream;
    private byte[] m_WriteBuffer;
    private byte[] m_ReadBuffer;
    private int m_nLenWrite = 0;
    private int m_nLenRead = 0;
    private int m_Cursor = 0;
    private boolean m_bConnected = false;
    int m_Timeout = 10000;

    CClientSocket() {
    }

    CClientSocket(String var1) throws Exception {
        this.Connect(var1);
    }

    boolean Connect(String var1) {
        String var2;
        int var3;
        if (var1.indexOf(58) > -1) {
            var2 = var1.substring(0, var1.indexOf(58));
            var3 = Integer.parseInt(var1.substring(var1.indexOf(58) + 1));
        } else {
            var2 = var1;
            var3 = 15015;
        }

        return this.Connect(var2, var3);
    }

    boolean Connect(String var1, int var2) {
        this.m_WriteBuffer = new byte[0];
        this.m_ReadBuffer = new byte[0];
        this.m_nLenRead = 0;
        this.m_nLenWrite = 0;
        this.m_Cursor = 0;
        this.m_WriteStream = null;
        this.m_ReadStream = null;
        return this.ConnectSocket(var1, var2);
    }

    void Disconnect() {
        try {
            if (this.m_Socket != null) {
                this.m_Socket.close();
            }
        } catch (Exception var6) {
        } finally {
            this.m_Socket = null;
            this.m_nLenRead = 0;
            this.m_nLenWrite = 0;
            this.m_Cursor = 0;
            this.m_bConnected = false;
        }

    }

    boolean getConnected() {
        return this.m_bConnected;
    }

    void Write(String var1) {
        Object var2 = null;

        byte[] var6;
        try {
            if (var1 != null) {
                var6 = var1.getBytes("UTF8");
            } else {
                var6 = "".getBytes("UTF8");
            }
        } catch (UnsupportedEncodingException var5) {
            return;
        }

        byte[] var3 = new byte[this.m_nLenWrite];

        int var4;
        for(var4 = 0; var4 < this.m_nLenWrite; ++var4) {
            var3[var4] = this.m_WriteBuffer[var4];
        }

        this.m_WriteBuffer = new byte[this.m_nLenWrite + var6.length + 1];

        for(var4 = 0; var4 < this.m_nLenWrite; ++var4) {
            this.m_WriteBuffer[var4] = var3[var4];
        }

        for(var4 = 0; var4 < var6.length; ++var4) {
            this.m_WriteBuffer[this.m_nLenWrite + var4] = var6[var4];
        }

        this.m_nLenWrite += var6.length;
        ++this.m_nLenWrite;
        this.m_WriteBuffer[this.m_nLenWrite - 1] = 0;
    }

    void Write(int var1) {
        byte[] var2 = new byte[this.m_nLenWrite + 4];

        int var3;
        for(var3 = 0; var3 < this.m_nLenWrite; ++var3) {
            var2[var3] = this.m_WriteBuffer[var3];
        }

        var2[this.m_nLenWrite + 0] = (byte)(var1 >> 24);
        var2[this.m_nLenWrite + 1] = (byte)(var1 >> 16);
        var2[this.m_nLenWrite + 2] = (byte)(var1 >> 8);
        var2[this.m_nLenWrite + 3] = (byte)var1;
        this.m_nLenWrite += 4;
        this.m_WriteBuffer = new byte[this.m_nLenWrite];

        for(var3 = 0; var3 < this.m_nLenWrite; ++var3) {
            this.m_WriteBuffer[var3] = var2[var3];
        }

    }

    void Write(byte var1) {
        byte[] var2 = new byte[this.m_nLenWrite + 1];

        int var3;
        for(var3 = 0; var3 < this.m_nLenWrite; ++var3) {
            var2[var3] = this.m_WriteBuffer[var3];
        }

        var2[this.m_nLenWrite] = var1;
        ++this.m_nLenWrite;
        this.m_WriteBuffer = new byte[this.m_nLenWrite];

        for(var3 = 0; var3 < this.m_nLenWrite; ++var3) {
            this.m_WriteBuffer[var3] = var2[var3];
        }

    }

    void Write(boolean var1) {
        if (var1) {
            this.Write((byte)1);
        } else {
            this.Write((byte)0);
        }

    }

    void Write(COptimaContact var1) {
        var1.Write(this);
    }

    void Write(COptimaExceptionRule var1) {
        var1.Write(this);
    }

    void Write(COptimaCriteria var1) {
        var1.Write(this);
    }

    void Flush() throws IOException {
        byte[] var1 = new byte[4];
        if (this.m_nLenWrite < 1) {
            throw new IOException("Socket flush requested but no data waiting");
        } else {
            var1[0] = (byte)(this.m_nLenWrite >> 24);
            var1[1] = (byte)(this.m_nLenWrite >> 16);
            var1[2] = (byte)(this.m_nLenWrite >> 8);
            var1[3] = (byte)this.m_nLenWrite;

            try {
                this.m_WriteStream.write(var1);
                this.m_WriteStream.write(this.m_WriteBuffer);
                this.m_WriteStream.flush();
            } catch (IOException var5) {
                try {
                    if (!this.ConnectSocket(this.m_sHost, this.m_nPort)) {
                        return;
                    }

                    this.m_WriteStream.write(var1);
                    this.m_WriteStream.write(this.m_WriteBuffer);
                    this.m_WriteStream.flush();
                } catch (IOException var4) {
                    return;
                }
            }

            this.m_nLenWrite = 0;
            this.m_WriteBuffer = new byte[0];
        }
    }

    void ReadContact(COptimaContact var1) throws IOException {
        var1.Read(this);
    }

    void ReadExceptionRule(COptimaExceptionRule var1) throws IOException {
        var1.Read(this);
    }

    void ReadUpdate(COptimaUpdate var1) throws IOException {
        var1.Read(this);
    }

    void ReadUpdateItem(COptimaUpdateItem var1) throws IOException {
        var1.Read(this);
    }

    String ReadString() throws IOException {
        this.RefreshBuffer();

        int var1;
        for(var1 = this.m_Cursor; this.m_ReadBuffer[var1] != 0; ++var1) {
        }

        String var2 = new String(this.m_ReadBuffer, this.m_Cursor, var1 - this.m_Cursor, "UTF-8");
        this.m_Cursor = var1 + 1;
        return var2;
    }

    int ReadInt() throws IOException {
        this.RefreshBuffer();
        if (this.m_Cursor + 4 > this.m_nLenRead) {
            return -1;
        } else {
            byte var1 = 0;
            int var2 = var1 + ((this.m_ReadBuffer[this.m_Cursor] & 255) << 24);
            var2 += (this.m_ReadBuffer[this.m_Cursor + 1] & 255) << 16;
            var2 += (this.m_ReadBuffer[this.m_Cursor + 2] & 255) << 8;
            var2 += (this.m_ReadBuffer[this.m_Cursor + 3] & 255) << 0;
            this.m_Cursor += 4;
            return var2;
        }
    }

    byte ReadByte() throws IOException {
        this.RefreshBuffer();
        if (this.m_Cursor + 1 > this.m_nLenRead) {
            return -1;
        } else {
            byte var1 = this.m_ReadBuffer[this.m_Cursor];
            ++this.m_Cursor;
            return var1;
        }
    }

    boolean ReadBoolean() throws IOException {
        return this.ReadByte() == 1;
    }

    void RefreshBuffer() throws IOException {
        if (this.m_Cursor >= this.m_nLenRead) {
            this.m_nLenRead = 0;
            this.m_Cursor = 0;
            boolean var1 = false;
            byte[] var2 = new byte[4];

            try {
                this.m_ReadStream.read(var2);
            } catch (IOException var9) {
                return;
            }

            byte var3 = 0;
            int var11 = var3 + ((var2[0] & 255) << 24);
            var11 += (var2[1] & 255) << 16;
            var11 += (var2[2] & 255) << 8;
            var11 += (var2[3] & 255) << 0;
            this.m_ReadBuffer = new byte[var11];

            int var10;
            try {
                var10 = this.m_ReadStream.read(this.m_ReadBuffer);
            } catch (IOException var8) {
                return;
            }

            if (var10 < 0) {
                throw new IOException("Error reading message from socket");
            }

            boolean var4 = false;

            int var5;
            int var12;
            for(var5 = 0; var10 < var11 && var5 < 100; var10 += var12) {
                try {
                    var12 = this.m_ReadStream.read(this.m_ReadBuffer, var10, var11 - var10);
                } catch (IOException var7) {
                    return;
                }

                if (var12 < 0) {
                    throw new IOException("Error reading message portion from socket");
                }

                if (var12 == 0) {
                    ++var5;
                }
            }

            if (var5 == 100) {
                throw new IOException("Failed to read complete message");
            }

            this.m_nLenRead = var10;
        }

    }

    private boolean ConnectSocket(String var1, int var2) {
        if (this.m_bConnected || this.m_Socket != null) {
            this.Disconnect();
        }

        this.m_bConnected = false;

        try {
            this.m_Socket = new Socket(var1, var2);
            this.m_Socket.setSoTimeout(this.m_Timeout);
            this.m_ReadStream = this.m_Socket.getInputStream();
            this.m_WriteStream = this.m_Socket.getOutputStream();
        } catch (Exception var4) {
            this.m_Socket = null;
            return false;
        }

        this.m_sHost = var1;
        this.m_nPort = var2;
        this.m_bConnected = true;
        return true;
    }
}

