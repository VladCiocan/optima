package com.hartehanks.optimized;

import com.hartehanks.*;
import com.hartehanks.optima.api.CEnhancedOptimaServer;
import com.hartehanks.optima.api.COptimaContact;
import com.hartehanks.optima.api.OFT;
import com.hartehanks.optima.api.OON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class RecordSender extends Thread {

    private FormatName formatName ;

    private String outputFile;
    private RecordParser recordParser;
    private DBConnectionFactory dbConnectionFactory;
    private String outputTableName;
    private GlobalDriver parent = null;
    private int myId = -1;
    private int myHostId = -1;
    private int maxThreadCount = 10;
    private CEnhancedOptimaServer globalServer = null;
    private COptimaContact parsedAddress = null;
    private COptimaContact validatedAddress = null;
    private COptimaContact enhancedAddress = null;
    private COptimaContact formattedNA = null;
    private COptimaContact keepContact = null;
    public static String[] acrNames =
            new String[OFT.NumFieldsPlusOne];
    public static int busyConnects = 0;
    private static String alphaString =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private long callTimeoutMillis =
            GlobalDriver.defaultSearchTimeout * 1000;
    private int currentSearchTimeout =
            GlobalDriver.defaultSearchTimeout;
    private long callTimeoutMillisStart;
    private long callTimeoutMillisEnd;

    private GlobalHost[] globalHosts = null;
    private boolean optInit = false;
    private GlobalQueueEntity queueEntity = null;
    private COptimaContact[] globalContacts = null;
    private GlobalRecord globalRecord = null;
    private int[][] inAddressData;
    private int maxInAddressCount = 0;
    private NonstopTimer conTimer = null;
    protected boolean redo = false;
    private boolean debug = false;
    private boolean acronyms = false;
    private boolean doSmart = false;
    private int smartLevel = 0;
    //private boolean				nowSmart = false;
    //private boolean				nowSmarter = false;
    private boolean chat = false;
    private boolean chatted = false;
    private Hashtable countryOptions;
    private int countryOpts =
            GlobalDriver.defaultSearchTimeout << 12;
    private int lastPostCodeWeight = 0;
    private int lastCityWeight = 0;
    private int lastTimeout = 0;
    private boolean doLatLong = false;
    private long numRecords = 0;
    private int totalRetries = 0;
    private int allRetries = 0;
    private int numRetries = 0;
    private int totalTimeouts = 0;
    private int numTimeouts = 0;
    private int numErrors = 0;
    private StringBuffer cat8910 = new StringBuffer();
    private Hashtable acceptanceLevel =
            new Hashtable();
    private String companyName = null;
    private boolean companyBypass = false;
    private boolean companyLine = false;
    private boolean companyQuotes = false;
    private boolean hasHung = false;
    private boolean forceUpdate = false;

    private boolean serRes;
    private boolean optRes;
    private boolean runRes;
    private boolean valRes;
    private boolean forRes;
    private boolean orgRes;
    private boolean accepted;
    private boolean formatted;
    private boolean clear;

    private int openTries;
    private int sleepTime;
    private int numPopLines;
    private int halfLen;
    private int validatedLevel;
    private int accLevel;
    private int copyInd;
    private int nDiffs;

    private Vector addVec;

    private String adrLine;
    private String regionFix;
    private String postcodeFix;
    private int postcodeMid;
    private String departmentFix;
    private String companyFix;
    private String streetFix;
    private String premiseFix;
    private String deptA;
    private String compA;
    private String ctryA;
    private String fixBuild;
    private String fixSub;
    private String fixIso3;
    private String dumpRes;
    private static String sdumpRes;
    private String copyIso;
    private String country;
    private String org;
    private String runPac;
    private String runComp;

    private Integer accInt;

    private StringBuffer sbm = new StringBuffer(100);
    private StringBuffer sm3 = new StringBuffer(100);
    private static StringBuffer ssb;

    public static int instanceCount = 0;
    private static int normalPostCodeWeight = -1;
    private static int normalCityWeight = -1;
    private ProcessManager manager;

    public ProcessManager getManager() {
        return manager;
    }

    public void setManager(ProcessManager manager) {
        this.manager = manager;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public RecordParser getRecordParser() {
        return recordParser;
    }

    public void setRecordParser(RecordParser recordParser) {
        this.recordParser = recordParser;
    }

    public static int[] oftAddressEnums =
            {
                    OFT.AddressLine1, OFT.AddressLine2, OFT.AddressLine3,
                    OFT.AddressLine4, OFT.AddressLine5, OFT.AddressLine6,
                    OFT.AddressLine7, OFT.AddressLine8,
            };

    private int[] clearFields =
            {
                    OFT.Company,
                    OFT.Department,
                    OFT.SubBuilding,
                    OFT.Building,
                    OFT.Premise,
                    OFT.Street,
                    OFT.SubStreet,
                    OFT.POBox,
                    OFT.SubCity,
                    OFT.City,
                    OFT.Postcode,
                    OFT.Cedex,
                    OFT.DPS,
                    OFT.Region,
                    OFT.Principality,
                    OFT.AddressLine1,
                    OFT.AddressLine2,
                    OFT.AddressLine3,
                    OFT.AddressLine4,
                    OFT.AddressLine5,
                    OFT.AddressLine6,
                    OFT.AddressLine7,
                    OFT.AddressLine8,
            };

    private List<COptimaContact> contacts;

    public void setOutputTableName(String outputTableName) {
        this.outputTableName = outputTableName;
    }

    public List<COptimaContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<COptimaContact> contacts) {
        this.contacts = contacts;
    }

    public void setDbConnectionFactory(DBConnectionFactory dbConnectionFactory) {
        this.dbConnectionFactory = dbConnectionFactory;
    }
    public List<COptimaContact> getResults(){
        return Arrays.asList(globalContacts);
    }

    public FormatName getFormatName() {
        return formatName;
    }

    public void setFormatName(FormatName formatName) {
        this.formatName = formatName;
    }

    //
// This is the opposite to the open server connection. It should, in theory,
// always work but it's not guaranteed - so it too is headless timed.
//
    public void closeServerConnection(int entryCaller) {
        if (globalServer != null) {
            GlobalDisconnect disc = new GlobalDisconnect(globalServer,
                    myId, parent);
            disc.start();
            disc = null;
            globalServer = null;
        }
    }

    public void run() {
        try {
            Connection inputServerConnection = dbConnectionFactory.createInputDBConnection();
            inputServerConnection.setAutoCommit(true); // assuming outputServerConnection is your DB connection
            System.out.println("Truncating table "+outputTableName);
            inputServerConnection.prepareStatement("truncate table "+outputTableName).execute();
            System.out.println("Table truncated");
            PreparedStatement pstmt = inputServerConnection.prepareStatement("INSERT INTO "+outputTableName+" (\n" +
                    "   recordID," +
                    "    title," +
                    "    firstName," +
                    "    middleInitials," +
                    "    lastName," +
                    "    fullName," +
                    "    nameSuffix," +
                    "    gender," +
                    "    jobTitle, " +
                    "    salutation," +
                    "    department," +
                    "    company," +
                    "    building," +
                    "    subBuilding," +
                    "    premise," +
                    "    street," +
                    "    subStreet," +
                    "    pOBox," +
                    "    subCity," +
                    "    city," +
                    "    region," +
                    "    principality," +
                    "    postcode," +
                    "    country," +
                    "    dPS," +
                    "    cedex," +
                    "    mKN," +
                    "    mKA," +
                    "    mKC," +
                    "    aCR," +
                    "    wCR," +
                    "    nCR," +
                    "    tCR," +
                    "    eCR," +
                    "    percent," +
                    "    duplicate," +
                    "    dUPmaster," +
                    "    dUPconfidence," +
                    "    other1," +
                    "    other2," +
                    "    other3," +
                    "    other4," +
                    "    other5," +
                    "    other6," +
                    "    other7," +
                    "    other8," +
                    "    other9," +
                    "    other10," +
                    "    addressLine1," +
                    "    addressLine2," +
                    "    addressLine3," +
                    "    addressLine4," +
                    "    addressLine5," +
                    "    addressLine6," +
                    "    addressLine7," +
                    "    addressLine8," +
                    "    countryISO," +
                    "    mobileTelephone," +
                    "    telephone1," +
                    "    telephone2," +
                    "    telephone3," +
                    "    telephone4," +
                    "    email1," +
                    "    email2," +
                    "    uRL1," +
                    "    uRL2, " +
                    "    field1 " +
                    ") VALUES (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?)");

            do {
                if (hasHung) {
                    System.out.println("Thread " + myId + ": Hung");
                    conTimer.stopTimer();
                    closeServerConnection(10);
                    System.out.println("Thread " + myId + ": Connection closed");
                    pstmt.close();
                    return;
                }
                try {
                    if(contacts != null && contacts.size() > 0){
                        System.out.println("Sending records to output table. Size: "+contacts.size());
                        for (COptimaContact contact : contacts) {
                            System.out.print("Inserting record into output table with values : ");
                            for(int i = 0;i<contact.getArrFieldValues().length;i++){
                                System.out.print(contact.getArrFieldValues()[i]+",");
                                pstmt.setString(i+1, contact.getArrFieldValues()[i]);
                            }
                            pstmt.setString(contact.getArrFieldValues().length+1, "field1");
                            System.out.println();
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                        System.out.println("Batch executed");
                        inputServerConnection.setAutoCommit(true);
                        System.out.println("Auto commit set to true");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e.getMessage());
                }
                int found= 0;
                boolean done= manager.start();
                if(done) {


                    PreparedStatement readStmt = inputServerConnection.prepareStatement("SELECT * FROM " + outputTableName + " WHERE percent = 100");

                    globalContacts = new COptimaContact[0];
                    System.out.println("Reading from output table");
                    ResultSet rs = readStmt.executeQuery();
                    while (rs.next()) {
                        COptimaContact contact = new COptimaContact();
                        for (int i = 0; i < contact.getArrFieldValues().length; i++) {
                            contact.setField(i, rs.getString(i + 1));
                        }
                        System.out.println("Adding record to globalContacts");
                        System.out.println(contact.getArrFieldValues().toString());
                        COptimaContact[] newGlobalContacts = new COptimaContact[globalContacts.length + 1];
                        System.arraycopy(globalContacts, 0, newGlobalContacts, 0, globalContacts.length);
                        globalContacts = newGlobalContacts;
                        globalContacts[globalContacts.length - 1] = contact;
                    }
                    rs.close();
                    readStmt.close();
                }


            } while (redo == true);

            pstmt.close();

            inputServerConnection.setAutoCommit(true);
            for (int i = 0; i < globalContacts.length; i++) {
                boolean formatted = formatName.formatName(globalContacts[i]);
                if (!formatted) {
                    System.out.println("Error: Name formatting failed for record: " + globalContacts[i].getRecordID());
                }else {
                    System.out.println("Name formatted successfully for record: " + globalContacts[i].getRecordID());
                }
            }


            recordParser.writeContactsToFile(Arrays.asList(globalContacts),outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }
}
