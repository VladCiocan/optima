package com.hartehanks;

import com.hartehanks.optima.api.COptimaContact;
import com.hartehanks.optima.api.OFT;

import java.sql.*;
import java.util.Vector;

public class DBConnectionFactory {
    private StringBuffer cat8910 = new StringBuffer();
    private String inputServerAddress;
    private String inputServerUser;
    private String inputServerPassword;
    private String inputServerTable;
    private String inputServerType;

    private String outputServerAddress;
    private String outputServerUser;
    private String outputServerPassword;
    private String outputServerTable;
    private String outputServerType;
    private PreparedStatement inputStmt;
    private PreparedStatement outputStmt;

    public DBConnectionFactory(String inputServerAddress, String inputServerUser, String inputServerPassword,
                               String inputServerTable, String inputServerType, String outputServerAddress,
                               String outputServerUser, String outputServerPassword, String outputServerTable,
                               String outputServerType) {
        this.inputServerAddress = inputServerAddress;
        this.inputServerUser = inputServerUser;
        this.inputServerPassword = inputServerPassword;
        this.inputServerTable = inputServerTable;
        this.inputServerType = inputServerType;

        this.outputServerAddress = outputServerAddress;
        this.outputServerUser = outputServerUser;
        this.outputServerPassword = outputServerPassword;
        this.outputServerTable = outputServerTable;
        this.outputServerType = outputServerType;
        createInputDBConnection();
        createOutputDBConnection();
    }

    public StringBuffer getCat8910() {
        return cat8910;
    }

    public void setCat8910(StringBuffer cat8910) {
        this.cat8910 = cat8910;
    }

    public String getInputServerAddress() {
        return inputServerAddress;
    }

    public void setInputServerAddress(String inputServerAddress) {
        this.inputServerAddress = inputServerAddress;
    }

    public String getInputServerUser() {
        return inputServerUser;
    }

    public void setInputServerUser(String inputServerUser) {
        this.inputServerUser = inputServerUser;
    }

    public String getInputServerPassword() {
        return inputServerPassword;
    }

    public void setInputServerPassword(String inputServerPassword) {
        this.inputServerPassword = inputServerPassword;
    }

    public String getInputServerTable() {
        return inputServerTable;
    }

    public void setInputServerTable(String inputServerTable) {
        this.inputServerTable = inputServerTable;
    }

    public String getInputServerType() {
        return inputServerType;
    }

    public void setInputServerType(String inputServerType) {
        this.inputServerType = inputServerType;
    }

    public String getOutputServerAddress() {
        return outputServerAddress;
    }

    public void setOutputServerAddress(String outputServerAddress) {
        this.outputServerAddress = outputServerAddress;
    }

    public String getOutputServerUser() {
        return outputServerUser;
    }

    public void setOutputServerUser(String outputServerUser) {
        this.outputServerUser = outputServerUser;
    }

    public String getOutputServerPassword() {
        return outputServerPassword;
    }

    public void setOutputServerPassword(String outputServerPassword) {
        this.outputServerPassword = outputServerPassword;
    }

    public String getOutputServerTable() {
        return outputServerTable;
    }

    public void setOutputServerTable(String outputServerTable) {
        this.outputServerTable = outputServerTable;
    }

    public String getOutputServerType() {
        return outputServerType;
    }

    public void setOutputServerType(String outputServerType) {
        this.outputServerType = outputServerType;
    }

    public Connection createInputDBConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:" + inputServerType + "://" + inputServerAddress + "/" + inputServerTable, inputServerUser, inputServerPassword);
            inputStmt = con.prepareStatement("SELECT * FROM " + inputServerTable + " WHERE id = ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public Connection createOutputDBConnection()  {
        Connection con=null;
        try {
            con = DriverManager.getConnection("jdbc:" + outputServerType + "://" + outputServerAddress + "/" + outputServerTable, outputServerUser, outputServerPassword);
            outputStmt = con.prepareStatement("SELECT * FROM " + outputServerTable + " WHERE id = ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    public ResultSet getAllInputData() throws SQLException {
        Connection conn = createInputDBConnection();
        Statement stmt = conn.createStatement();
        stmt.setFetchSize(1000);
        return stmt.executeQuery("SELECT * FROM " + inputServerTable);
    }

    public void addBatchToOutputDB(String dataString) throws SQLException {
        outputStmt.setString(1, dataString);
        outputStmt.addBatch();
    }

    public void executeOutputBatch() throws SQLException {
        outputStmt.executeBatch();
    }

    public ResultSet getAllOutputData() throws SQLException {
        Connection conn = createOutputDBConnection();
        Statement stmt = conn.createStatement();
        stmt.setFetchSize(1000);
        return stmt.executeQuery("SELECT * FROM " + outputServerTable);
    }

    public void addBatchToInputDB(String dataString) throws SQLException {
        inputStmt.setString(1, dataString);
        inputStmt.addBatch();
    }

    public void executeInputBatch() throws SQLException {
        inputStmt.executeBatch();
    }

    // Be sure to close Connection, Statement, ResultSet once done:
    public void closeResources() throws SQLException {
        inputStmt.close();
        outputStmt.close();
    }
    private String trimAdl(String adl) {
        int ind = -1;
        while ((ind = adl.indexOf("\"\"")) >= 0) {
            adl = adl.substring(0, ind) + adl.substring(ind + 1);
        }
        char[] chars = adl.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = GlobalDriver.faultyChars[chars[i]];
        }
        return new String(chars);
    }
}

