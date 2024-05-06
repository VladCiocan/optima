package com.hartehanks.optimized;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessManager {
    private   String LOGIN_URL = "https://dm-uk.informaticacloud.com/ma/home";
    private   String START_PROCESS_URL = "https://example.com/startProcess";
    private   String CHECK_STATUS_URL = "https://example.com/checkStatus";
    private   int INTERVAL = 120000; // 2 minutes in milliseconds
    private  String InputTableName;
    private  String TargetKeyFileName;
    private  String ConnectionName;
    private  String SourceDB;
    private  String WhereClause;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String authToken;

    public String getLOGIN_URL() {
        return LOGIN_URL;
    }

    public void setLOGIN_URL(String LOGIN_URL) {
        this.LOGIN_URL = LOGIN_URL;
    }

    public String getSTART_PROCESS_URL() {
        return START_PROCESS_URL;
    }

    public void setSTART_PROCESS_URL(String START_PROCESS_URL) {
        this.START_PROCESS_URL = START_PROCESS_URL;
    }

    public String getCHECK_STATUS_URL() {
        return CHECK_STATUS_URL;
    }

    public void setCHECK_STATUS_URL(String CHECK_STATUS_URL) {
        this.CHECK_STATUS_URL = CHECK_STATUS_URL;
    }

    public int getINTERVAL() {
        return INTERVAL;
    }

    public void setINTERVAL(int INTERVAL) {
        this.INTERVAL = INTERVAL;
    }

    public String getInputTableName() {
        return InputTableName;
    }

    public void setInputTableName(String inputTableName) {
        InputTableName = inputTableName;
    }

    public String getTargetKeyFileName() {
        return TargetKeyFileName;
    }

    public void setTargetKeyFileName(String targetKeyFileName) {
        TargetKeyFileName = targetKeyFileName;
    }

    public String getConnectionName() {
        return ConnectionName;
    }

    public void setConnectionName(String connectionName) {
        ConnectionName = connectionName;
    }

    public String getSourceDB() {
        return SourceDB;
    }

    public void setSourceDB(String sourceDB) {
        SourceDB = sourceDB;
    }

    public String getWhereClause() {
        return WhereClause;
    }

    public void setWhereClause(String whereClause) {
        WhereClause = whereClause;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public static void main(String[] args) {
        ProcessManager manager = new ProcessManager();
        manager.start();
    }
    public ProcessManager() {
    }

    public ProcessManager(String InputTableName, String TargetKeyFileName, String ConnectionName, String SourceDB, String WhereClause,
                          String loginUrl, String startProcessUrl, String checkStatusUrl) {
        this.InputTableName = InputTableName;
        this.TargetKeyFileName = TargetKeyFileName;
        this.ConnectionName = ConnectionName;
        this.SourceDB = SourceDB;
        this.WhereClause = WhereClause;
        this.LOGIN_URL = loginUrl;
        this.START_PROCESS_URL = startProcessUrl;
        this.CHECK_STATUS_URL = checkStatusUrl;

    }

    public boolean start() {
        int maxAttempts = 100;
        int attempt = 1;

        try {
            // Login to get the authentication token
            login();
            // Start the process
            startProcess();

            while (attempt <= maxAttempts) {
                // Wait for 2 minutes before checking the status
                Thread.sleep(INTERVAL);

                // Check status of the process
                String status = checkStatus();

                if ("finished".equalsIgnoreCase(status)) {
                    // If process is finished, return true
                    return true;
                } else {
                    // If process is not finished, print status and attempt again
                    System.out.println("Process status: " + status);
                    attempt++;
                }
            }

            System.out.println("Failed to complete process after " + maxAttempts + " attempts.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
    public void login() throws IOException {
        // Perform POST request to login and retrieve authentication token
        URL url = new URL(LOGIN_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        String requestBody = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        System.out.println("Using auth credentials:");
        System.out.println(requestBody);

        con.getOutputStream().write(requestBody.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Extract authentication token from response
        authToken = response.toString();
        System.out.println("Authentication token: " + authToken);
    }

    public void startProcess() throws IOException {
        // Perform POST request to start the process with authentication token in header
        URL url = new URL(START_PROCESS_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("IDS-SESSION-ID", authToken);
        ServerParamsDto paramsDto = new ServerParamsDto();
        paramsDto.setInputTableName(InputTableName);
        paramsDto.setTargetKeyFileName(TargetKeyFileName);
        paramsDto.setConnectionName(ConnectionName);
        paramsDto.setSourceDB(SourceDB);
        paramsDto.setWhereClause(WhereClause);
        Gson gson = new Gson();
        String requestBody = gson.toJson(paramsDto);
        con.setRequestProperty("Content-Type", "application/json");

        // Write JSON body to request
        con.getOutputStream().write(requestBody.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Extract status from response
        System.out.println("Server response for start process");
        System.out.println(response.toString());
    }

    public String checkStatus() throws IOException {
        // Perform GET request to check status of the process with authentication token in header
        URL url = new URL(CHECK_STATUS_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("IDS-SESSION-ID", authToken);

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println("Check status response:"+response.toString());
        // Extract status from response
        return response.toString();
    }
}
