package com.hartehanks;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.hartehanks.dev.app.StringSort;
import com.hartehanks.dev.io.*;
import com.hartehanks.dev.misc.Conversion;
import com.hartehanks.dev.misc.SuperStringTokenizer;
import com.hartehanks.optima.api.*;

//
// The GlobalDriver module is the main module of the temporary Global Driver
// solution. It processes from a standard Trillium style parameter file,
// expecting parms like input and output file name, accompanying dictionaries.
// It also must be given ip or host names to which it can attach to have the
// Optima Server process the name and address records.
//
// This part of the code does not, itself, communicate with any part of the
// Optima product as it has reliability issues. The GlobalDriver class simply
// reads in records from a file or pipe and presents them to GlobalChild
// instances that perform the dangerous work. The driver monitors the
// behaviour of these children and will terminate them if it suspects that
// the tail is wagging the dog - so to speak (i.e. Optima is hanging the
// service requests).
//
// When the child finishes processing the input record it recurns the completed
// output record to this module, which is then written out to an output disk
// or pipe.
//
// The process as a whole implements the same style of Trillium parser and
// Geocoder behaviour and expects to receive data in 'oraddrl1-10' fields
// and will populate standard pr_* and tg_in/out areas as well as doing the
// normal brute-force copy of the input ORG_RECORD to the output area.
//
// In essence this is a very simple, monolithic, plug-in replacement for a
// standard Trillium parser/geocoder process - almost transparent to the
// process designer.
//
// Note that the entry point for Java programs is the static 'main' function
// found towards the tail of this class.
//
public class GlobalDriver implements GlobalCallback {
    private SuperBufferedInputStream sbis = null;
    private Ddl inDdl = null;
    private int inputRecordLength = -1;
    private int inOrgOffset = -1;
    private int inOrgLength = -1;
    private int[][] inAddressData = new int[99][4];
    public int[] uniqueRecordId = {-1, -1};
    private int maxInAddressCount = 0;
    private StringBuffer sbin = new StringBuffer(2000);
    private String lastTableKey = "   ";
    private HashMap processQueueHash = null;
    private Vector processQueueArray = null;
    private int chainSize = 300;
    protected Vector hashQueue = new Vector(1000);
    protected Vector outQueue = new Vector(1000);
    protected Vector processQueueList =
            new Vector(chainSize * 20);

    private String lastFirstList;

    protected SuperBufferedOutputStream sbos = null;
    private String[] outFileName;
    private Ddl outDdl = null;
    private DdlField[] parsoutDdl = null;
    private Hashtable truncHash = new Hashtable();
    private int outOrgOffset = -1;
    private int[][] outFields;
    private int[][] outXlate;
    private int maxFields = 0;
    private int[] prCategory = new int[2];

    private GlobalChild[] globalChildren = null;
    private GlobalChild[] globalChildrenRunning = null;
    private boolean[] globalChildBusy;
    private int[] childHost = null;
    private int stopThreadOnHost = -2;
    private int lastStopThreadOnHost = -2;
    private int uloadId = -1;
    private int uloadCount = -1;
    private String uloadHost = "";
    private int lastUnderLoadHost = 0;
    private GlobalHost[] globalHosts = null;
    private int primaryHostRange;
    private int currentHostRange;
    private int currentHostCount;
    private int primaryChildRange;
    private int hostMax;
    private int primaryActive = 0;
    private int fallbackActive = 0;
    private int numThreads = 0;
    protected int numZero = 0;
    private int[] inRecordCount = null;
    private int[] prevInRecordCount = null;
    private GlobalQueueEntity[] inQueueItem = null;
    private long print100 = 0;


    protected int minNotifyInterval = 10;
    public static boolean stopping = false;
    protected boolean stoptimer = false;
    protected boolean eof = false;
    private long lastTimeCount =
            Calendar.getInstance().getTime().getTime() / 1000;
    private long thisTimeCount;
    private int maxLostThreads;
    private int underLoadHost;
    private int threadDiff;
    private int hci;
    private int lowHost;
    private int overloadLevel;
    private int altHost;
    private int resetI;
    private int maxExtraThreads;
    private int getI;
    private int finI;
    private int nrunners;
    private StringBuffer ciSb = new StringBuffer(2000);
    private StringBuffer sb = new StringBuffer(2000);

    private HeadlessTimer headlessTimer = null;
    private PrintWriter printWriter = new PrintWriter(
            System.err, true);
    private PrintWriter logWriter = new PrintWriter(
            System.err, true);
    private GlobalLogger globalLogger = null;
    private boolean logIsFile = false;

    protected long numRecordsIn = 0;
    protected long numRecordsParsed = 0;
    protected long numParsedWritten = 0;
    protected long lastSwitchNumRecordsIn = 0;
    protected long numRecordsOut = 0;
    protected long lastNumRecordsOut = 0;
    private long maxin = 0;
    protected long printNth = 0;
    private boolean debug = false;
    private boolean acronyms = false;
    private boolean doSmart = false;
    private boolean busDefined = false;

    private Hashtable hash = new Hashtable();
    private Hashtable countryOptions = new Hashtable();
    private Hashtable countryCount = new Hashtable();
    private GlobalStats worldStat =
            new GlobalStats("Global Stats", " ");
    private GlobalStats myCountryStat;
    private GlobalFallback ofb;

    public static byte[] outRecordEmpty = null;

    public HashMap isoTransTable = new HashMap();
    public HashMap enhanceTable = new HashMap();
    public HashMap pafTransTable = new HashMap();

    public static final int serverTimeout = 600;
    public static final int defaultSearchTimeout =
            ((serverTimeout / 8) > 75) ?
                    75 : serverTimeout / 8;
    public static char[] faultyChars = new char[65536];
    private GlobalFinishManager globalFinishManager;
    private GlobalFinisher globalFinisher;

    //
// This is the GlobalDriver class constructor which is always called when a
// new instance is required (once only in this product)
// The arguments are passed to standard Optarg class and the parmfile (only
// one expected here) is fed to the standard Trillium parm file handler class
// which interprets the optons and makes them available to the process.
//
// the initialise method at the end processes the parameters and then kicks off
// the process.
//
    public GlobalDriver(String[] args) {
        System.err.println("GloParse driver  V6Q - Copyright Harte Hanks" +
                " 2003 - 2009");
        if (args.length > 0 && args[0].equalsIgnoreCase("-f")) {
            setupFaultyChars();
            FormatName f = FormatName.getInstance(null, false,
                    lastFirstList);
            f.doGui(args);
        } else {
            Optarg optarg = new Optarg();
            Optarg.Option parmfile1 = optarg.addStringOption('p', "pf");
            Optarg.Option parmfile2 = optarg.addStringOption('q', "parmfile");

            try {
                optarg.parse(args);
            } catch (Optarg.IllegalOptionValueException iove) {
                System.err.println("GloParse: Illegal option value " +
                        iove.getMessage());
                System.exit(1);
            } catch (Optarg.UnknownOptionException uoe) {
                System.err.println("GloParse: " + uoe.getMessage());
                System.exit(2);
            }
            String parmFileName = (String) optarg.getOptionValue(parmfile1);
            if (parmFileName == null || parmFileName.length() == 0) {
                parmFileName = (String) optarg.getOptionValue(parmfile2);
            }
            if (parmFileName == null || parmFileName.length() == 0) {
                System.err.println("GloParse: Unable to locate parameter " +
                        "file argument");
                System.exit(3);
            }

            ParmfileHandler pfh = new ParmfileHandler(parmFileName);

            if (initialise(pfh)) {
                processInputData();
            }
        }
    }

    //
// Initialise fetched mandatory arguments from the parameter file handler -
// like input and output file names, dictionaries. It also fetches optional
// arguments that overide the default behaviour (like maxin, stat_fname,
// log_fname. debug, max_threads etc.)
// It performs input and output file open. checks that ORG_RECORD is in both
// dictionaries and the same length. Any problems are reported and it's
// bye-byes.
// Serverhost parameter is mandatory as its required for GlobalChild to connect
// to. Multiple server hosts can be specified so they have a choice.
// if all is suvvessful then the final job is to invoke the 'startDaemons'
// method to commence processing.
//
    private boolean initialise(ParmfileHandler pfh) {
        try {
//
// Open output then input (because input blocks until prior peer opens output)
//
            outFileName = pfh.locateArgumentFor("OUT_DDNAME", 1);
            if (outFileName[0].equalsIgnoreCase("null") == false) {
                sbos = new SuperBufferedOutputStream(outFileName[0]);
            }
//
// Open input
//
            String[] inFileName = pfh.locateArgumentFor("INP_DDNAME", 1);
            sbis = new SuperBufferedInputStream(inFileName[0]);
            String inDdlName[] = pfh.locateArgumentFor("DDL_INP_FNAME", 1);
            inDdl = new Ddl(inDdlName[0]);
            DdlField inOrgDdlField = inDdl.getDdlField("ORG_RECORD");
            inputRecordLength = inDdl.getRecordLength();
            inOrgOffset = inOrgDdlField.start;
            inOrgLength = inOrgDdlField.length;

            String outDdlName[] = pfh.locateArgumentFor("DDL_OUT_FNAME", 1);
            outDdl = new Ddl(outDdlName[0]);
            outRecordEmpty = new byte[outDdl.getRecordLength()];
            for (int i = 0; i < outRecordEmpty.length; i++) {
                outRecordEmpty[i] = 0x20;
            }
            DdlField outOrgDdlField = outDdl.getDdlField("ORG_RECORD");
            outOrgOffset = outOrgDdlField.start;
            if (inOrgLength != outOrgDdlField.length) {
                System.err.println("GloParse: Input ddl ORG_RECORD field " +
                        "length " +
                        "does not match output dictionary field length");
                System.exit(4);
            }
            loadInputAddressSection(pfh);
            loadOutputAddressSection();
//
// Process stat_fname parameter
//
            String inStat[] = {""};
            try {
                inStat = pfh.locateArgumentFor("STAT_FNAME", 1);
                printWriter = new PrintWriter(
                        new FileOutputStream(inStat[0]), true);
            } catch (IllegalArgumentException iae) {
            } catch (IOException ioe) {
                System.err.println("GloParse: Unable to open output " +
                        "statistics file " + inStat[0]);
                System.exit(5);
            }
//
// Process log_fname parameter
//
            String inLog[] = {""};
            try {
                inLog = pfh.locateArgumentFor("LOG_FNAME", 1);
                logWriter = new PrintWriter(
                        new FileOutputStream(inLog[0]), true);
                logIsFile = true;
            } catch (IllegalArgumentException iae) {
            } catch (IOException ioe) {
                System.err.println("GloParse: Unable to open output log " +
                        "file " + inLog[0]);
                System.exit(6);
            }
            globalLogger = new GlobalLogger(logWriter);
//
// Process APAC_NAME_MODE parameter
//
            try {
                String inApac[] =
                        pfh.locateArgumentFor("APAC_NAME_MODE", 1);
                lastFirstList = inApac[0].toUpperCase();
            } catch (IllegalArgumentException iae) {
            }
//
// Process Maxin parameter
//
            try {
                String inMaxin[] = pfh.locateArgumentFor("MAXIN", 1);
                maxin = Long.parseLong(inMaxin[0]);
            } catch (IllegalArgumentException iae) {
            }
//
// Process Print_nth parameter
//
            try {
                String inPrint[] = pfh.locateArgumentFor("PRINT_NTH", 1);
                printNth = Long.parseLong(inPrint[0]);
            } catch (IllegalArgumentException iae) {
            }
//
// Process Debug parameter
//
            try {
                String inDebug[] = pfh.locateArgumentFor("DEBUG", 1);
                debug = true;
            } catch (IllegalArgumentException iae) {
            }
            ;

//
// Process Acronym parameter
//
            try {
                String inAcronyms[] = pfh.locateArgumentFor("ACRONYM", 1);
                acronyms = true;
            } catch (IllegalArgumentException iae) {
            }
            ;
//
// Process fallback parameter
//
            try {
                String inSmart[] = pfh.locateArgumentFor("SMARTMODE", 1);
//
// smart only operates if business name has been defined
//
                doSmart = busDefined;
            } catch (IllegalArgumentException iae) {
            }
            ;

            setupGlobalHosts(pfh);
            setupCountryOptions(pfh);
            System.err.println("GloParse: Operating maximum of " +
                    primaryChildRange + " threads across " +
                    primaryHostRange + " servers");
            System.err.println("GloParse: ApacNameMode resolve list: " +
                    lastFirstList);

            setupFaultyChars();
            //initPafTable();
            return startDaemons();
        } catch (IllegalArgumentException iae) {
            System.err.println("GloParse: Parameter error: " +
                    iae.getMessage());
            System.exit(7);
        } catch (FileNotFoundException fnfe) {
            System.err.println("GloParse: Unable to open file " +
                    fnfe.getMessage());
            System.exit(8);
        } catch (DdlException ddle) {
            System.err.println("GloParse: " + ddle.getMessage());
            System.exit(9);
        } catch (IOException ioe) {
            System.err.println("GloParse: " + ioe.getMessage());
            System.exit(10);
        }
        return false;
    }

    //
// Method to load the ISO file translator for ISO-2/3/N to PAF country name
//
    private void initPafTable() {
        String isoFile = System.getProperties().getProperty("PAFISO");
        if (isoFile == null || isoFile.trim().length() == 0) {
            System.err.println("GloParse: Unable to locate ISO " +
                    "translation table setting required by PAF routing");
            System.exit(1);
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(isoFile), "UTF-8"));
        } catch (FileNotFoundException fnfe) {
            System.err.println("GloParse: Unable to locate ISO " +
                    "translation table: " + isoFile);
            System.exit(1);
        } catch (UnsupportedEncodingException uee) {
            System.err.println("GloParse: Unable to load ISO " +
                    "translation table using UTF-8 encoding");
            System.exit(1);
        }

        String line;

//
// Format of ISO file is:
//
//	ISO3		- String variant ISO code (old value)
//	ISO3		- String ISO-3611-3 code recognized by GA PAF
//
        try {
            while ((line = br.readLine()) != null) {
                SuperStringTokenizer stt = new SuperStringTokenizer(line,
                        "\t", false);
                String[] str = stt.getStringList();

                if (str.length != 2) {
                    System.err.println("GloParse: Format error in PAFISO " +
                            "translation table. Data reads: " + line);
                    System.exit(1);
                }
                Object obj = pafTransTable.put(str[0].toUpperCase(),
                        str[1]);
                if (obj != null) {
                    System.err.println("GloParse: Duplicate error in " +
                            "PAFISO translation table. Data reads: " + line);
                    System.exit(1);
                }
            }

        } catch (IOException ioe) {
            System.err.println("GloParse: I/O error reading PAFISO " +
                    "translation table: " + isoFile);
            System.exit(1);
        }
        try {
            br.close();
        } catch (IOException ioe) {
        }
    }
//
// The loadOutputAddressSection method, called by initialise, maps the Global
// and derived output field information back to the Trillium output dictionary

    //
// This method, invoked by initialise, processes the input dictionary, mapping
// the 10 possible oraddrl fields into the FULLNAME and 8 Global address lines.
// This mapping is done once only for translation performance as each record
// is given to the GlobalChild.
//
    private void loadInputAddressSection(ParmfileHandler pfh) {
//
// Locate overriding person and country fields so that they are not extracted
// from oraddrs into the standard 8 address lines.
//
        String personNameField = "";
        try {
            String inPersonName[] = pfh.locateArgumentFor("FULLNAME", 1);
            personNameField = inPersonName[0];
        } catch (IllegalArgumentException iae) {
        }

        String businessNameField = "";
        try {
            String inBusinessName[] =
                    pfh.locateArgumentFor("BUSINESS_NAME", 1);
            businessNameField = inBusinessName[0];
        } catch (IllegalArgumentException iae) {
        }

        String countryNameField = "";
        try {
            String inCountryName[] = pfh.locateArgumentFor("COUNTRY_NAME", 1);
            countryNameField = inCountryName[0];
//
// If country name field defined then install ISOTABLES
//
            initIsoTable();
        } catch (IllegalArgumentException iae) {
        }
        int usedAddressCount = 0;
        boolean adrLineFound = false;
        for (int i = 0; i < 10; i++) {
            try {
                DdlField inAddress = inDdl.getDdlField("oraddrl" + (i + 1));
                if (inAddress != null) {
                    inAddressData[maxInAddressCount][0] = inAddress.start;
                    inAddressData[maxInAddressCount][1] = inAddress.length;
                    inAddressData[maxInAddressCount][3] = i + 1;

                    if (inAddress.name.equalsIgnoreCase(personNameField)) {
                        inAddressData[maxInAddressCount][2] = OFT.FullName;
                    } else if (inAddress.name.equalsIgnoreCase(
                            businessNameField)) {
                        inAddressData[maxInAddressCount][2] = OFT.Company;
                        busDefined = true;
                    } else if (inAddress.name.equalsIgnoreCase(
                            countryNameField)) {
                        inAddressData[maxInAddressCount][2] = OFT.Country;
                    } else {
                        inAddressData[maxInAddressCount][2] = -1; //adrline
                        adrLineFound = true;
                    }

                    maxInAddressCount++;
                }
            } catch (DdlException ddle) {
            }
        }
//
// Test if not using oraddr - if not then try 'address*'
//
        if (maxInAddressCount == 0) {
            System.err.println("GloParse: OraddrlX input not found - " +
                    "Switching to addressX line mode");
            for (int i = 0; i < 10; i++) {
                try {
                    DdlField inAddress = inDdl.getDdlField("address" + (i + 1));
                    if (inAddress != null) {
                        inAddressData[maxInAddressCount][0] =
                                inAddress.start;
                        inAddressData[maxInAddressCount][1] =
                                inAddress.length;
                        inAddressData[maxInAddressCount][3] = i + 1;
                        if (inAddress.name.equalsIgnoreCase(
                                personNameField)) {
                            inAddressData[maxInAddressCount][2] =
                                    OFT.FullName;
                        } else if (inAddress.name.equalsIgnoreCase(
                                businessNameField)) {
                            inAddressData[maxInAddressCount][2] =
                                    OFT.Company;
                            busDefined = true;
                        } else if (inAddress.name.equalsIgnoreCase(
                                countryNameField)) {
                            inAddressData[maxInAddressCount][2] =
                                    OFT.Country;
                        } else {
                            inAddressData[maxInAddressCount][2] = -1;
                            adrLineFound = true;
                        }

                        maxInAddressCount++;
                    }
                } catch (DdlException ddle) {
                }
            }
        }
//

        try {
            DdlField urid = inDdl.getDdlField("unique_record_id");
            if (urid != null) {
                uniqueRecordId[0] = urid.start;
                uniqueRecordId[1] = urid.length;
            }
        } catch (DdlException ddle) {
        }
//
// There must be at least one address line
//
        if (adrLineFound == false) {
            System.err.println("GloParse: No input original address " +
                    "lines found in input dictionary");
            System.exit(11);
        }
    }

    //
// Method to load the ISO file translator for ISO-2/3/N to Country name
//
    private void initIsoTable() {
        String isoFile = System.getProperties().getProperty("ISOTABLE");
        if (isoFile == null || isoFile.trim().length() == 0) {
            System.err.println("GloParse: Unable to locate ISO " +
                    "translation table setting required by COUNTRY_NAME " +
                    "option");
            System.exit(1);
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(isoFile), "UTF-8"));
        } catch (FileNotFoundException fnfe) {
            System.err.println("GloParse: Unable to locate ISO " +
                    "translation table: " + isoFile);
            System.exit(1);
        } catch (UnsupportedEncodingException uee) {
            System.err.println("GloParse: Unable to load ISO " +
                    "translation table using UTF-8 encoding");
            System.exit(1);
        }

        String line;

//
// Format of ISO file is:
//
//	variant		- String variant code
//	ISO3		- String ISO-3611-2 code recognized by GA
//
        try {
            while ((line = br.readLine()) != null) {
                SuperStringTokenizer stt = new SuperStringTokenizer(line,
                        "\t", false);
                String[] str = stt.getStringList();

                if (str.length < 2 || str.length > 4) {
                    System.err.println("GloParse: Format error in ISO " +
                            "translation table. Data reads: " + line);
                    System.exit(1);
                }
                Object obj = isoTransTable.put(str[0].toUpperCase(),
                        str[1]);
                if (obj != null) {
                    System.err.println("GloParse: Duplicate error in ISO " +
                            "translation table. Data reads: " + line);
                    System.exit(1);
                }
                if (str.length > 2) {
                    if (str[0].length() != 3 ||
                            str[0].equalsIgnoreCase(str[1]) == false) {
                        System.err.println("GloParse: Enhance error in " +
                                "ISO translation table. Data reads: " + line);
                        System.exit(1);
                    }
                    String[] staging = new String[str.length - 2];
                    for (int i = 0; i < staging.length; i++) {
                        staging[i] = str[i + 2];
                    }
                    obj = enhanceTable.put(str[0].toUpperCase(), staging);
                    if (obj != null) {
                        System.err.println("GloParse: Duplicate error in " +
                                "enhance table. Data reads: " + line);
                        System.exit(1);
                    }
                }
            }

        } catch (IOException ioe) {
            System.err.println("GloParse: I/O error reading ISO " +
                    "translation table: " + isoFile);
            System.exit(1);
        }
        try {
            br.close();
        } catch (IOException ioe) {
        }
    }

    //
// The loadOutputAddressSection method, called by initialise, maps the Global
// and derived output field information back to the Trillium output dictionary
// field offsets and lengths. Again this is done only once to provide highly
// performant data move operations by the GlobalChild
//
    private void loadOutputAddressSection() {
        parsoutDdl = outDdl.getDdlFields();
        outFields = new int[parsoutDdl.length][3];
        outXlate = new int[parsoutDdl.length][3];
        for (int i = 0; i < parsoutDdl.length; i++) {
            int[] code =
                    GlobalQueueFinisher.lookupOFTCode(parsoutDdl[i].name);
            if (code != null) {
                outFields[maxFields][0] = parsoutDdl[i].start;
                outFields[maxFields][1] = parsoutDdl[i].length;
                outFields[maxFields][2] = i;
                System.arraycopy(code, 0, outXlate[maxFields], 0, 3);
                maxFields++;
            }
        }
    }

    //
// Method to process the server host list. This may contain a straight list of
// host names or ip addresses, but also, for some or all, a maximum thread
// count
//
    public final void setupGlobalHosts(ParmfileHandler pfh) {
//
// Primary host list - main servers
//
        String[] hostNameList = pfh.locateArgumentFor("SERVERHOST", 0);
        if (hostNameList.length < 1) {
            System.err.println("GloParse: No Serverhost argument() " +
                    "supplied");
            System.exit(12);
        }
        Vector primary = setupServerGroup(hostNameList, true);

//
// Fallback/recovery host list - emergency servers
//
        Vector fallback = new Vector();
        hostNameList = pfh.locateArgumentFor("FALLBACKHOST", 0);
        if (hostNameList.length < 1) {
            System.err.println("GloParse: WARNING - No FallbackHost list " +
                    "specified - redundancy fallback will be unavailable");
        } else {
            fallback = setupServerGroup(hostNameList, false);
        }

        globalHosts = new GlobalHost[primary.size() + fallback.size()];
        primary.copyInto(globalHosts);
        primaryHostRange = primary.size();
        currentHostRange = primary.size();
        currentHostCount = primary.size();

        for (int i = 0; i < fallback.size(); i++) {
            globalHosts[i + primaryHostRange] =
                    (GlobalHost) fallback.elementAt(i);
        }

        primaryChildRange = 0;
        for (int i = 0; i < primaryHostRange; i++) {
            System.err.println("GloParse: Primary Global Host " +
                    globalHosts[i].hostName +
                    " operating " + globalHosts[i].maxThreads + " threads");
            primaryChildRange += globalHosts[i].maxThreads;
        }

        int fallbackThreads = 0;
        for (int i = primaryHostRange; i < globalHosts.length; i++) {
            System.err.println("GloParse: Fallback Global Host " +
                    globalHosts[i].hostName +
                    " operating " + globalHosts[i].maxThreads + " threads");
            fallbackThreads += globalHosts[i].maxThreads;
        }
        numThreads = primaryChildRange + fallbackThreads;
        hostMax = primaryChildRange;
    }

    private Vector setupServerGroup(String[] hostNameList, boolean enabled) {
//
// Look through list, allocate a new GlobalHost instance for each identified
// host/ip. If followed by a count then store too, otherwise set 8 default
//
        Vector v = new Vector(10);
        GlobalHost serverHost = null;
        int lastHost = -1;
        int hostPort = 15015;

        for (int i = 0; i < hostNameList.length; i++) {
            if (Character.isLetter(hostNameList[i].charAt(0)) ||
                    hostNameList[i].indexOf(".") > 0) {
                lastHost = i;
                int colon = hostNameList[i].indexOf(":");
                if (colon >= 0) {
                    int j = 0;
                    for (j = colon + 1; j < hostNameList[i].length(); j++) {
                        if (Character.isDigit(hostNameList[i].charAt(j))
                                == false) {
                            break;
                        }
                    }
                    if (j < colon + 4) {
                        colon = -1;
                    }
                }
                if (colon < 0) {
                    hostPort = 15015;
                } else {
                    try {
                        hostPort = Integer.parseInt(
                                hostNameList[i].substring(colon + 1));
                        hostNameList[i] = hostNameList[i].substring(0, colon);
                    } catch (NumberFormatException nfe) {
                        System.err.println("GloParse: Host port no. in " +
                                hostNameList[i] + " is not a valid format");
                        System.exit(14);
                    }
                }
                serverHost = new GlobalHost(this, hostNameList[lastHost] +
                        ":" + String.valueOf(hostPort), 8, 8);
                v.addElement(serverHost);
            } else {
                try {
                    int localThreads = Integer.parseInt(hostNameList[i]);
                    if (serverHost != null) {
                        serverHost.maxThreads = localThreads;
                        serverHost.enabledThreads =
                                (enabled) ? localThreads : 0;
                        serverHost = null;
                    } else if (lastHost >= 0 && serverHost == null) {
                        hostPort++;
                        serverHost = new GlobalHost(this,
                                hostNameList[lastHost] +
                                        ":" + String.valueOf(hostPort), 8, 8);
                        v.addElement(serverHost);
                        serverHost.maxThreads = localThreads;
                        serverHost.enabledThreads =
                                (enabled) ? localThreads : 0;
                        serverHost = null;
                    } else {
                        System.err.println("GloParse: No predefined " +
                                "hostname for " +
                                "initial thread count value in SERVERHOST " +
                                "parameter");
                        System.exit(15);
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("GloParse: Thread count for host " +
                            lastHost + " in error - '" +
                            hostNameList[i] + "' is not a number");
                    System.exit(16);
                }
            }
        }

        return v;
    }

    //
// Method to process country options. Global country is 'ROW'. Syntax is
// CCC:opt - where CCC is iso3 country code (or 'ROW') and opt(s) are
//
//	M	- Mix parsed address with Geocoded (default)
//	G	- Replace parsed address with Geocoded
//
// Example: ROW:G,GBR:M  - set default output to PAF only except for UK
//
// Mask for options: ABCDEFGH
//
// G = 4 bit postcode weight (0-F) (set using P0 to P9 & PA to PF)
// H = 4 bits low order bit 1=Enforce Blanks
//
    public final void setupCountryOptions(ParmfileHandler pfh) {
        countryOptions.put("ROW", new Integer(defaultSearchTimeout << 12));
        String[] countryOpts;
        try {
            countryOpts = pfh.locateArgumentFor("COUNTRY_OPTIONS", 0);
        } catch (IllegalArgumentException iae) {
            return;
        }
        if (countryOpts.length < 1) {
            System.err.println("GloParse: No Country_Options argument " +
                    "supplied");
        }
//
// Look through list, allocate a new GlobalHost instance for each identified
// host/ip. If followed by a count then store too, otherwise set 8 default
//
        boolean correct = true;
        for (int i = 0; i < countryOpts.length; i++) {
            if (countryOpts[i].length() < 5) {
                System.err.println("GloParse: Invalid country option " +
                        countryOpts[i]);
                correct = false;
            } else if (countryOpts[i].charAt(3) != ':') {
                System.err.println("GloParse: Invalid country option " +
                        countryOpts[i]);
                correct = false;
            } else {
//
// OptFlag structure:
//
//	???TTCPF
//
//	76543210
//	?		- unmapped nibble
//	 ?		- unmapped nibble
//	  ?		- unmapped nibble
//	   TT		- validation timeout (2 nibbles)
//	     C		- City weight 0 -15 for validation
//	      P		- Postcode weight 0 -15 for validation
//	       F	- Flags - currently ?LAE
//			? - Unused flag bit
//			L - Lat/Long mode: L0 - mode off, L1 - mode on
//			A - Address recons mode: A0 - street/geo, A1 = real
//			E - Enforce blanks mode: M - off, G = on
//
//
                int optFlag = defaultSearchTimeout << 12;
                String opts = countryOpts[i].substring(4);
                for (int j = 0; j < opts.length(); j++) {
                    switch (opts.charAt(j)) {
//
// Enforce Blanks - fill in & remove extra data using PAF file.
//
                        case 'G':
                            optFlag |= 1;
                            break;

//
// Reset Enforce Blanks - opposite of G
//
                        case 'M':
                            optFlag &= 0x7FFFFFFE;
                            break;
//
// Address mode real - 1 or separate - 0
//
                        case 'A':
                            optFlag &= 0x7FFFFFFD;
                            if (j == opts.length() - 1 ||
                                    "01".indexOf(opts.charAt(j + 1)) < 0) {
                                System.err.println("GloParse: A argument " +
                                        "in Country_Options data " + countryOpts[i] +
                                        " invalid value. No 0/1 flag");
                                correct = false;
                                break;
                            }
                            j++;
                            if (opts.charAt(j) == '1') {
                                optFlag |= 2;
                                System.err.println("GloParse: Address " +
                                        "recons mode set to 'Real' for " +
                                        countryOpts[i].substring(0, 3));
                            } else {
                                System.err.println("GloParse: Address " +
                                        "recons mode set to 'split' for " +
                                        countryOpts[i].substring(0, 3));
                            }
                            break;

//
// Address mode split - enable address output in street/geog separate mode
//
                        case 'S':
                            optFlag |= 2;
                            break;
//
// Enable Lat-Long mode - bit 2 in nibble 0
//
                        case 'L':
                            optFlag &= 0x7FFFFFFB;
                            if (j == opts.length() - 1 ||
                                    "01".indexOf(opts.charAt(j + 1)) < 0) {
                                System.err.println("GloParse: L argument " +
                                        "in Country_Options data " + countryOpts[i] +
                                        " invalid value. No 0/1 flag");
                                correct = false;
                                break;
                            }
                            j++;
                            if (opts.charAt(j) == '1') {
                                optFlag |= 4;
                                System.err.println("GloParse: Geocode " +
                                        "Lat/Long option enabled for " +
                                        countryOpts[i].substring(0, 3));
                            } else {
                                System.err.println("GloParse: Geocode " +
                                        "Lat/Long option disabled for " +
                                        countryOpts[i].substring(0, 3));
                            }
                            break;
//
// Set Postal validation timeout - default is serverTimeout / 8
//
                        case 'T':
                            int k = j + 1;
                            for (; k < opts.length(); k++) {
                                if (Character.isDigit(opts.charAt(k)) ==
                                        false) {
                                    break;
                                }
                            }
                            if (k < j + 2 || k > j + 3) {
                                System.err.println("GloParse: T argument " +
                                        "in Country_Options data " + countryOpts[i] +
                                        " invalid value. No number or > 99");
                                correct = false;
                                break;
                            }
                            int timeout = Integer.parseInt(
                                    opts.substring(j + 1, k));
                            j = k - 1;
                            optFlag &= 0x7FF00FFF;
                            optFlag |= (timeout << 12);
                            System.err.println("GloParse: Search Timeout " +
                                    "option " +
                                    "set to " + timeout + " seconds for " +
                                    countryOpts[i].substring(0, 3));
                            break;
//
// Set validation postcode weight to a hex number (priority on lookup)
//
                        case 'P': // postcode weight for validation
                            if (j + 1 >= opts.length()) {
                                System.err.println("GloParse: P argument " +
                                        "in " +
                                        "Country_Options data " + countryOpts[i] +
                                        " in error");
                                correct = false;
                                break;
                            }
                            int offset = "0123456789ABCDEF".indexOf(
                                    opts.substring(j + 1, j + 2));
                            if (offset < 0) {
                                System.err.println("GloParse: P argument " +
                                        "in " +
                                        "Country_Options data " + countryOpts[i] +
                                        " invalid value");
                                correct = false;
                                break;
                            }
                            optFlag &= 0x7FFFFF0F;
                            optFlag |= (offset * 16);
                            System.err.println("GloParse: Postcode " +
                                    "weight option " +
                                    "set to " + offset + " for " +
                                    countryOpts[i].substring(0, 3));
                            j++;
                            break;

//
// Set validation city weight to a hex number (priority on lookup)
//
                        case 'C': // city weight for validation
                            if (j + 1 >= opts.length()) {
                                System.err.println("GloParse: C argument " +
                                        "in " +
                                        "Country_Options data " + countryOpts[i] +
                                        " in error");
                                correct = false;
                                break;
                            }
                            int coffset = "0123456789ABCDEF".indexOf(
                                    opts.substring(j + 1, j + 2));
                            if (coffset < 0) {
                                System.err.println("GloParse: C argument " +
                                        "in " +
                                        "Country_Options data " + countryOpts[i] +
                                        " invalid value");
                                correct = false;
                                break;
                            }
                            optFlag &= 0x7FFFF0FF;
                            optFlag |= (coffset * 256);
                            System.err.println("GloParse: City weight " +
                                    "option " +
                                    "set to " + coffset + " for " +
                                    countryOpts[i].substring(0, 3));
                            j++;
                            break;

                        default:
                            System.err.println("GloParse: Invalid option " +
                                    "setting in" +
                                    " Country_Options data " + countryOpts[i]);
                            correct = false;
                            break;
                    }
                }

                countryOptions.put(countryOpts[i].substring(0, 3),
                        new Integer(optFlag));
            }
        }
        if (!correct) {
            System.err.println("GloParse: Stopping due to invalid " +
                    "country options");
            System.exit(17);
        }
    }

    //
// Method to create the faultyChars static replacement table for checking &
// converting dodgy data found in the incoming address lines to blanks or
// nulls
//
    private int[][] faultyRanges =
            {
                    {0x0000, 0x001F},
                    {0x007F, 0x007F},
                    {0x00A0, 0x00A0},
                    {0x2190, 0x21FF},
                    {0x2200, 0x2211}, {0x2213, 0x2214}, {0x2216, 0x22FF},
                    {0x2300, 0x23FF},
                    {0x2400, 0x243F},
                    {0x2440, 0x245F},
                    {0x2500, 0x257F},
                    {0x2580, 0x259F},
                    {0x25A0, 0x25FF},
                    {0x2600, 0x26FF},
                    {0x2700, 0x27BF},
                    {0x27C0, 0x27EF},
                    {0x27F0, 0x27FF},
                    {0x2800, 0x28FF},
                    {0x2900, 0x297F},
                    {0x2980, 0x29FF},
                    {0x2A00, 0x2AFF},
                    {0x2B00, 0x2BFF},
                    {0x4DC0, 0x4DFF},
                    {0xFE00, 0xFE0F},
                    {0xFFF0, 0xFFFF},
            };

    private char[][] replaceChars =
            {
                    {(char) 0x00B4, (char) 0x0027},
                    //{ (char)0x2212, (char)0x002D },
            };

    private void setupFaultyChars() {
        for (int i = 0; i < faultyChars.length; i++) {
            faultyChars[i] = (char) i;
        }
        for (int i = 0; i < faultyRanges.length; i++) {
            for (int j = faultyRanges[i][0]; j <= faultyRanges[i][1]; j++) {
                faultyChars[j] = ' ';
            }
        }
        for (int i = 0; i < replaceChars.length; i++) {
            faultyChars[replaceChars[i][0]] = replaceChars[i][1];
        }
    }

    //
// startDaemons method is the final 'active' method, invoked after
// successful initialise. It creates management control for the GlobalChild
// instances. Each instance is created and started.
// After completion the parent becomes a callback to the children, who
// individually request records for processing, return processed records with
// statistics for output and consolidated reporting.
// Finally, a HeadlessTimer (doesn't require AWT Gui system to be active)
// is started that perpetually monitors child activity.
//
    private boolean startDaemons() throws IOException {
        inRecordCount = new int[10 + numThreads];
        ;
        prevInRecordCount = new int[10 + numThreads];
        ;
        inQueueItem = new GlobalQueueEntity[numThreads];
        globalChildren = new GlobalChild[numThreads];
        globalChildrenRunning = new GlobalChild[numThreads];
        childHost = new int[numThreads];
        globalChildBusy = new boolean[numThreads];

        for (int i = 0; i < numThreads; i++) {
            childHost[i] = -1;
        }

        if (queueRecord() == false)
        //if (sbis.readBytes(recordBucket[0], false) == null)
        {
            System.err.println("GloParser: No data in input file - no " +
                    "threads started");
            return false;
        }

        globalFinishManager = new GlobalFinishManager(this, maxFields,
                inOrgOffset, outFields, outXlate, acronyms, lastFirstList);
        globalFinishManager.setPriority(Thread.MAX_PRIORITY);
        globalFinishManager.start();

        globalFinisher = new GlobalFinisher(this, maxFields,
                inOrgOffset, outFields, outXlate, acronyms);
        globalFinisher.setPriority(Thread.MAX_PRIORITY);
        globalFinisher.start();

        for (int i = 0; i < primaryChildRange; i++) {
            startThreadOnHost(-1);
        }

        headlessTimer = new HeadlessTimer(100000,
                new InterruptActionListener());
        headlessTimer.start();
        return true;
    }

    private int startThreadOnHost(int hostId) {
        for (int i = 0; i < hostMax; i++) {
            if (globalChildren[i] == null) {
                globalChildBusy[i] = false;
                globalChildrenRunning[i] = null;
                globalChildren[i] = new GlobalChild(this, i, globalHosts,
                        hostId, inAddressData, maxInAddressCount,
                        debug, doSmart, countryOptions);
                childHost[i] = -1;
                globalChildren[i].setPriority(Thread.MIN_PRIORITY);
                globalChildren[i].start();
                return i;
            }
        }
        printLog("GloParse attempt to start new child was unsuccessful " +
                "due to no slots available");
        return -1;
    }

    //
// This callback contained class is triggered automatically by the GlobalDriver
// headlessTimer. it is invoked, on average every 100 seconds and reports the
// processing record count for each of the child threads. threads that are
// having issues with a record are specially marked so that the operator/user
// can identify thread bad behaviour.
//
// This callback system changes behaviour afetr the final record is read from
// the input file - it starts to get nasty with any outstanding hung children
// and will restart them. the restarts will reprocess the final record again
// until successfully output. This means that the final statistics report will
// be created irrespective of bad child behaviour.
//
    private class InterruptActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int headTimer = 100000;
            if (stopping && processQueueList.size() > 0) {
                stopping = false;
                stoptimer = false;
            }
            if (!stopping || !stoptimer) {
                int numRunning = 0;
                sb.setLength(0);
                sb.append("GloParse: Record counts per thread...\n");
                numZero = 0;
                for (int i = 0; i < numThreads; i++) {
                    if (globalChildren[i] != null) {
                        int numDiff = inRecordCount[i] - prevInRecordCount[i];
                        if (numDiff == 0 && globalChildBusy[i]) {
                            numZero++;
                        }
                        sb.append(i + "=" + inRecordCount[i] +
                                ((numDiff == 0) ? "# " : " "));
                        numRunning++;
                    }
//
// reset childHost if no running child
//
                    else if (childHost[i] >= 0) {
                        System.err.println("Thread " + i + " - resetting " +
                                "activity count state - in error");
                        childHost[i] = -1;
                    }
                    prevInRecordCount[i] = inRecordCount[i];
                }
                sb.append("\nNum Zero=" + numZero +
                        "    Num Running=" + numRunning + "/" + hostMax);
                if (numZero > 0 && (stopping || stoptimer)) {
                    //System.err.println("GloParse: Num Zero="+numZero+
                    //"  H="+recordBucketCount);
                    minNotifyInterval = 1;
                }
                if (stopping && numZero > 0) {
                    sb.append("Driver indicates stopping with " +
                            numZero + " hung children");
                }
                if (numRunning * 10 <= (hostMax * 9)) {
                    resetHostUsage();
                    headTimer = 10000;
                }
                printLog(sb.toString());
            }
//
// Stopping set but not yet registered here
//
            if (stopping && !stoptimer) {
                stoptimer = true;
                headTimer = 150000;
                printLog("Stop has initiated final stop timer");
            }
//
// if last record in and we've seen the stopping trip then time to get nasty
//
            else if (stopping && stoptimer) {
                printLog("Stopping and Stoptimer now triggered");
                for (int i = 0; i < globalChildrenRunning.length; i++) {
                    if (globalChildrenRunning[i] != null) {
                        printLog("Child " + i +
                                " is not processing " +
                                inQueueItem[i].getRecordNumber());
                        debug = true;
                        childHung(globalChildrenRunning[i], i, 100);
                    }
                }
            }
            headlessTimer = new HeadlessTimer(headTimer,
                    new InterruptActionListener());
            headlessTimer.start();
        }
    }

    private void processInputData() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        while (!eof && (maxin == 0 || numRecordsIn < maxin)) {
            queueRecord();
        }
        System.err.println("GloParse: Read " + numRecordsIn +
                " input records");
        eof = true;
        minNotifyInterval = 1;
    }

//
// This callback contained class is triggered automatically by the GlobalDriver
// headlessTimer. it is invoked, on average every 100 seconds and reports the
// processing record count for each of the child threads. threads that are
// having issues with a record are specially marked so that the operator/user
// can identify thread bad behaviour.
//

    private boolean queueRecord() {
        byte[] rec = new byte[inputRecordLength];

        if (sbis.readBytes(rec, false) != null) {
            numRecordsIn++;
            sbin.setLength(0);
            int numFldsIn = 0;
            String line;
            String tableKey = " ";
            String fullName = "";
            for (int i = maxInAddressCount - 1; i >= 0; i--) {
                line = new String(rec,
                        inAddressData[i][0],
                        inAddressData[i][1]).trim();
                if (inAddressData[i][2] != OFT.FullName) {
                    sbin.append(line.toUpperCase());
                    if (numFldsIn < 2 && line.length() > 0) {
                        numFldsIn++;
                        tableKey = sbin.toString();
                    }
                    sbin.append("|");
                } else {
                    fullName = trimAdl(line.replace('~', '-'));
                }
            }
            GlobalRecord globalRecord = new GlobalRecord(rec, numRecordsIn,
                    fullName);

            if (processQueueHash == null ||
                    numRecordsIn - lastSwitchNumRecordsIn >= chainSize * 5 ||
                    (tableKey.equals(lastTableKey) == false &&
                            processQueueHash.size() >= chainSize)) {
                //System.err.println("TK="+tableKey+"  LTK="+lastTableKey);
                //sleep(1000);
                if (processQueueArray != null) {
                    GlobalQueueEntity[] gqe =
                            new GlobalQueueEntity[processQueueArray.size()];
                    processQueueArray.copyInto(gqe);
                    GlobalQueueHeader gqh = new GlobalQueueHeader(gqe);
                    hashQueue.add(gqh);
                }
                processQueueHash = new HashMap(chainSize);
                processQueueArray = new Vector(chainSize);
                lastSwitchNumRecordsIn = numRecordsIn;
            }
            lastTableKey = tableKey;
            GlobalQueueEntity globalQueueEntity =
                    (GlobalQueueEntity) processQueueHash.get(sbin.toString());
            if (globalQueueEntity == null) {
                globalQueueEntity = new GlobalQueueEntity();
                processQueueHash.put(sbin.toString(), globalQueueEntity);
                processQueueArray.add(globalQueueEntity);
                globalQueueEntity.add(globalRecord);
                processQueueList.add(globalQueueEntity);
            } else {
                globalQueueEntity.add(globalRecord);
            }
            if (hashQueue.size() > 0 &&
                    (processQueueList.size() > chainSize * 20 ||
                            //numZero > 0 ||
                            numRecordsIn - numRecordsOut > (chainSize * 40))) {
                sleep((processQueueList.size() < 6000) ? 300 : 2000);
                while (numRecordsIn - numRecordsOut >= chainSize * 41) {
                    sleep(2000);
                }
            }
            return true;
        }
        if (processQueueArray != null && processQueueArray.size() > 0) {
            GlobalQueueEntity[] gqe =
                    new GlobalQueueEntity[processQueueArray.size()];
            processQueueArray.copyInto(gqe);
            GlobalQueueHeader gqh = new GlobalQueueHeader(gqe);
            hashQueue.add(gqh);
            //hashQueue.add(processQueueArray);
            processQueueArray = null;
        }
        eof = true;
        minNotifyInterval = 1;
        return false;
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
        adl = new String(chars).trim();

        while (adl.length() >= 2 &&
                ((adl.startsWith("\"") && adl.endsWith("\"")) ||
                        (adl.startsWith("'") && adl.endsWith("'")))) {
            adl = adl.substring(1, adl.length() - 1);
        }
        return adl;
    }

    private void sleep(int msecs) {
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException ie) {
        }
    }

    //
// This is a callback method used by an globalChild to indicate that it has
// hung whilst issuing an global server request. each request to the Global
// Server is wrapped by a headless timer which gives the server 60 seconds to
// respond. if it doesnt't then the child has probably hung and must be
// replaced. (This timer stuff is handled within the GlobalChild - this code
// is called by the childs' headless timer when the event times out so that
// the GlobalDriver can re-spawn a new child to take over.
//
    public final boolean childHung(GlobalChild child,
                                   int childId, int actionCode) {
        if (globalChildrenRunning[childId] != child) {
            printLog("Thread " + childId +
                    ": Bogus hangup registration attempt (" + actionCode + ")");
            return true;
        }
        long recnum = (inQueueItem[childId] == null) ? -1 :
                inQueueItem[childId].getRecordNumber();
        printLog("Thread " + childId + ": Hangup reported (" +
                actionCode +
                ") on record " + recnum +
                " quiescing");
        if (globalChildBusy[childId]) {
            inQueueItem[childId].errorCounter++;

            printLog("Thread " + childId + ": Record to error " +
                    inQueueItem[childId].errorCounter +
                    " Host " + childHost[childId]);
            globalChildBusy[childId] = false; // record sent elsewhere
            inQueueItem[childId].recordStatus = GlobalQueueEntity.INPUT;
            inQueueItem[childId] = null;
            stopping = false;
            stoptimer = false;
        }
        childFinished(child, childId);
        return false;
    }

    //
// This is a callback method used by an globalChild to indicate that it has
// successfully connected to an GlobalServer and is ready to roll.
// the child is added to the running list so that, when it requests a record
// to process the parent knows that it is the correct child to which it should
// be handed.
//
    public final void registerStart(GlobalChild child, int childId) {
        if (globalChildrenRunning[childId] == null) {
            if (globalChildren[childId] == child) {
                globalChildrenRunning[childId] = child;
            } else {
                printLog("Thread " + childId +
                        ": Bogus registration attempt");
            }
        } else if (globalChildrenRunning[childId] == child) {
            printLog("Thread " + childId + ": already registered");
        } else // child not equal to new child
        {
            printLog("Thread " + childId + ": Bogus registration attempt");
        }
    }

    //
// this callback method is used by the child, repeatedly, to obtain the next
// record for processing. Under normal circumstances the next record will be
// read from the input stream and supplied to the child. when no more records
// are available a 'null' is returned. the Globalchild treats this as an eof
// and will terminate.
//
// Note that, if a child is replaced because it hung, the replacement child
// will get the original input record that the hung child had been processing.
// If a supposedly hung child attempts to fetch a new record it will be told
// to terminate as it is no longer considered active and has been replaced.
//
// only the registered active child thread will be serviced with a record for
// processing.
//
// when the last input record has been requested and no further items are
// available all children making calls to this method are instructed to
// terminate. A 'stopping' flag is also set true so that the GlobalDrivers
// headless timer knows that it can now start getting nasty with hung children.
//
    public final GlobalQueueEntity getRecord(
            GlobalChild child, int childId) {
        boolean printed = false;

        while (!eof || processQueueList.size() > 0) {
            if (child != globalChildrenRunning[childId]) {
                printLog("Thread " + childId + ": Detected dead collect " +
                        "request");
                return null;
            }
//
// If a host is overloaded then it's possible that the 'stopThreadOnHost' has
// been set. If so, and this requestor is such a host user then deny it a
// record - it will automatically terminate in such an eventuality
//
            if (childHost[childId] == stopThreadOnHost) {
                printLog("GloParse has initiated a Thread " +
                        "stop due to overload on host " +
                        globalHosts[stopThreadOnHost].hostName +
                        "\nThis is not an error");
                stopThreadOnHost = -2;
                return null;
            }
            GlobalQueueEntity gqe = _getRecord(child, childId);

            if (gqe != null) {
                return gqe;
            }
            print100++;
            if (!printed && (print100 % 100) == 0) {
                printLog("GloParse: GA Thread " + childId +
                        " waiting on input queue: " + processQueueList.size());
                printed = true;
            }
            sleep((globalFinisher.mustIdle == 0 &&
                    processQueueList.size() > currentHostRange) ? 2500 : 5000);
        }
        return null;
    }

    private final synchronized GlobalQueueEntity _getRecord(
            GlobalChild child, int childId) {
        if (globalChildBusy[childId] == true) // shouldn't be unless repl.
        {
            printLog("Thread " + childId +
                    ": Collect call for record number " +
                    inQueueItem[childId].getRecordNumber() +
                    ": Errors = " + inQueueItem[childId].errorCounter);
            return inQueueItem[childId];
        }

        if (!eof || processQueueList.size() > 0) {
            int maxScan = (globalFinisher.mustIdle > 0) ?
                    currentHostRange + currentHostRange :
                    processQueueList.size();
            maxScan = (maxScan > processQueueList.size()) ?
                    processQueueList.size() : maxScan;
            for (int i = 0; i < maxScan; i++) {
                try {
                    if (((GlobalQueueEntity) processQueueList.elementAt(i)).
                            recordStatus == GlobalQueueEntity.INPUT) {
                        inQueueItem[childId] =
                                (GlobalQueueEntity) processQueueList.elementAt(i);
                        inQueueItem[childId].recordStatus =
                                GlobalQueueEntity.PARSING;
                        inRecordCount[childId]++;
                        globalChildBusy[childId] = true;
                        return inQueueItem[childId];
                    }
                } catch (ArrayIndexOutOfBoundsException aiob) {
                }
            }
            return null;
        }
        stopping = true;
        return null;
    }

    //
// this method is used by the GlobalChild to obtain the server ip host number
// or name to which it should initially connect. This method selects the host
// with the lowest contention index.
//
    public final synchronized int getHostNumber() {
        double low = 999999.9;
        lowHost = -1;

        overloadLevel = -999;
        altHost = 0;

        for (getI = 0; getI < globalHosts.length; getI++) {
            if (globalHosts[getI].hostCI < low &&
                    globalHosts[getI].currentThreads <
                            globalHosts[getI].enabledThreads) {
                lowHost = getI;
                low = globalHosts[getI].hostCI;
            }
            if (globalHosts[getI].enabledThreads -
                    globalHosts[getI].currentThreads > overloadLevel) {
                altHost = getI;
                overloadLevel = globalHosts[getI].enabledThreads -
                        globalHosts[getI].currentThreads;
            }
        }
        if (lowHost < 0) {
            //System.err.println("All servers overloaded - using least "+
            //"overloaded host "+globalHosts[altHost].hostName);
            lowHost = altHost;
        }
        globalHosts[lowHost].hostCI = (globalHosts[lowHost].hostCI < 0.2) ?
                0.2 : globalHosts[lowHost].hostCI * 1.2;
        globalHosts[lowHost].currentThreads++;
        return lowHost;
    }

    public final int getThreadCountForHost(int hostId) {
        return (hostId >= 0) ? globalHosts[hostId].maxThreads : 10;
    }

    //
// when a child manages to connect to a host it reports this statistic to the
// GlobalDriver so that it can report on it.
//
    public final void setHostNumber(int childId, int hostNumber) {
        childHost[childId] = hostNumber;
    }

    //
// This is the opposite end of the child record processing logic - the child
// returns a completed record to the GlobalDriver for outputting.
//
// The child id and class address is carefully checked against that expected
// so that dead/hung children cannot send back processed data.
//
// This method also accepts the current country statistics for the processed
// record and appends them to the stats database. (an array of GlobalStats).
//
    public final boolean setRecord(GlobalChild child,
                                   int childId, int hostNumber, int timeouts,
                                   COptimaContact[] globalContacts) {
        if (child != globalChildrenRunning[childId]) {
            printLog("Thread " + childId +
                    ": Detected dead return call on record number " +
                    inQueueItem[childId].getRecordNumber());
            return false;
        }
        if (globalChildBusy[childId] != true) // shouldn't happen ever
        {
            printLog("Thread " + childId +
                    ": Detected incorrect dead return call " +
                    "on record number " +
                    inQueueItem[childId].getRecordNumber());
            System.exit(18);
        }

        synchronized (globalHosts) {
            globalHosts[hostNumber].hostCount++;
            globalHosts[hostNumber].hostTotal++;
            globalHosts[hostNumber].retryCount += timeouts;
            //inQueueItem[childId].errorCounter;
        }

        child.saveGlobalContacts(globalContacts);

        childHost[childId] = hostNumber;
        globalChildBusy[childId] = false;

        synchronized (processQueueList) {
            if (inQueueItem[childId].recordStatus >=
                    GlobalQueueEntity.PARSED) {
                processQueueList.remove(inQueueItem[childId]);
                numRecordsParsed++;
            } else {
                inQueueItem[childId].errorCounter++;
                System.err.println("IQI status is " +
                        inQueueItem[childId].recordStatus);
                inQueueItem[childId].recordStatus = GlobalQueueEntity.INPUT;
                System.err.println("IMPOSSIBLE SITUATION AT ZZZZ");
                System.exit(1);
            }
        }

        //String iso = inQueueItem.countryStat.iso3;
        //iso3 = (iso3 == null || iso3.length() != 3) ? "ROW" : iso3;

        inQueueItem[childId] = null;

        boolean printed = false;
        while (globalFinisher.mustIdle > 0 ||
                globalHosts[hostNumber].hostLock) {
            boolean mustWait = false;
            if (globalHosts[hostNumber].hostLock == false) {
                int numFound = 2;
                for (int i = 0; i < childId; i++) {
                    if (childHost[i] == hostNumber) {
                        numFound--;
                        if (numFound <= 0) // allow N threads/server port
                        {
                            mustWait = true;
                            if (printed == false) {
                                printLog("Thread " + childId + " sleeping due" +
                                        " to idler state " +
                                        globalFinisher.mustIdle);
                                printed = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                mustWait = true;
                if (printed == false) {
                    printLog("Thread " + childId +
                            " sleeping due to hostlock on host " + hostNumber);
                    printed = true;
                }
            }

            if (mustWait == false) {
                break;
            }
            sleep(5000);
        }

        return true;
    }

    //
// This method is called for every 100 records to recalculat the host balance
// (detect if currentThreads exceed enabledThreads or possible CI overload
//
    protected void resetHostUsage() {
        for (resetI = 0; resetI < globalHosts.length; resetI++) {
            globalHosts[resetI].currentThreads = 0;
        }

        primaryActive = 0;
        fallbackActive = 0;
        for (resetI = 0; resetI < childHost.length; resetI++) {
            if (childHost[resetI] >= 0) {
                globalHosts[childHost[resetI]].currentThreads++;
                primaryActive += (childHost[resetI] < primaryHostRange) ?
                        1 : 0;
                fallbackActive += (childHost[resetI] >= primaryHostRange) ?
                        1 : 0;
            }
        }

        if (processQueueList.size() == 0) {
            return;
        }

        if (primaryActive < 1 && currentHostRange < globalHosts.length) {
            System.err.println("GloParse: No primary active threads - " +
                    "Switching to Fallback servers");
            currentHostRange = globalHosts.length;
            currentHostCount = globalHosts.length - primaryHostRange;
            for (int i = primaryHostRange; i < globalHosts.length; i++) {
                globalHosts[i].enabledThreads = globalHosts[i].maxThreads;
            }
            hostMax = numThreads;
            lastUnderLoadHost = primaryHostRange;
        } else if (primaryActive > 0 && currentHostRange > primaryHostRange) {
            System.err.println("GloParse: Primary host(s) now active - " +
                    "Returning to status quo");
            currentHostRange = primaryHostRange;
            currentHostCount = primaryHostRange;
            for (int i = primaryHostRange; i < globalHosts.length; i++) {
                globalHosts[i].enabledThreads = 0;
            }
            hostMax = primaryChildRange;
        }

        maxExtraThreads = 0;
        lastStopThreadOnHost = stopThreadOnHost;

        for (resetI = 0; resetI < globalHosts.length; resetI++) {
            threadDiff = globalHosts[resetI].enabledThreads -
                    globalHosts[resetI].currentThreads;
            if (threadDiff < 0 && threadDiff < maxExtraThreads) {
                if (lastStopThreadOnHost != resetI) {
                    maxExtraThreads = threadDiff;
                    stopThreadOnHost = resetI;
                }
            }
        }
        //lastUnderLoadHost++;
        lastUnderLoadHost = (lastUnderLoadHost >= currentHostRange) ?
                0 : lastUnderLoadHost;

        maxLostThreads = 0;
        underLoadHost = -1;

        for (resetI = lastUnderLoadHost; resetI < currentHostRange;
             resetI++) {
            threadDiff = globalHosts[resetI].enabledThreads -
                    globalHosts[resetI].currentThreads;
            if (threadDiff > 0 && threadDiff > maxLostThreads) {
                maxLostThreads = threadDiff;
                underLoadHost = resetI;
            }
        }
        if (uloadId >= 0) {
            if (globalChildBusy[uloadId] == true || uloadCount >= 5 ||
                    globalChildren[uloadId] == null) {
                if (uloadCount >= 3) {
                    printLog("GloParse start of new underload thread " +
                            "assumed on host " + uloadHost);
                }
                uloadId = -1;
                uloadCount = 0;
            } else {
                uloadCount++;
                printLog("GloParse start of new underload thread pending " +
                        "on host " + uloadHost + " " + uloadCount + "/3");
            }
        }
//
// If request is to start a new thread on an overloaded host (?) then ignore
// and skip to next host ready for next check
//
        if (stopThreadOnHost == underLoadHost ||
                (underLoadHost < 0 && uloadId < 0)) {
            lastUnderLoadHost++;
        }
//
// Otherwise if the underLoadHost is active (found underloaded host) and the
// Driver is not now stopping and there are no pending underload start actions
// in ptogress then start a new GlobalChild to service requests
//
        else if (underLoadHost >= 0 && !stopping && uloadId < 0) {
            uloadHost = globalHosts[underLoadHost].hostName;
            printLog("GloParse starting new underload thread on host " +
                    uloadHost);
            uloadId = startThreadOnHost(underLoadHost);
            uloadCount = 0;
            lastUnderLoadHost = underLoadHost + 1;
        }
    }

    //
// every 1000 records processed will invoke this method that correlates the
// activity of all children and their retries due to server contention into
// a simple statistic to go into the run log.
//
// ths contention index is recalculated and is used when assigning or
// re-assigning children to alternative servers.
//
    protected void printCI(double numRec) {
        lastNumRecordsOut = numRecordsOut;

        printLog("GloParse: " +
                Calendar.getInstance().getTime().toString().substring(0, 20));
        ciSb.setLength(0);
        ciSb.append("GloParse: Host ratings:  ");

        int maxList = (fallbackActive > 0) ? globalHosts.length :
                primaryHostRange;
        int printColumn = 0;
        for (int i = 0; i < maxList; i++) {
            if (globalHosts[i].hostCount > 0) {
                hci = (globalHosts[i].retryCount * 100) /
                        globalHosts[i].hostCount;
                globalHosts[i].hostCI = ((double) hci) / 100.0;
            } else {
                globalHosts[i].hostCI = 0.0;
            }
            if (i == primaryHostRange) {
                ciSb.append("\nFallback:                ");
                printColumn = 0;
            } else if (printColumn >= 3) {
                ciSb.append("\n                         ");
                printColumn = 0;
            }
            ciSb.append((i + 1) + "=" + globalHosts[i].hostCount + "/" +
                    globalHosts[i].currentThreads + "/" +
                    globalHosts[i].enabledThreads + "/" +
                    globalHosts[i].maxThreads + "/" +
                    //globalHosts[i].hostCI+"  ");
                    globalHosts[i].retryCount + "  ");
            globalHosts[i].hostCount = 0;
            globalHosts[i].retryCount = 0;
            printColumn++;
        }

        ciSb.append("\nGloParse: Country stats: ");
        printColumn = 0;

        Enumeration enumer = countryCount.keys();
        String[] isoStats = new String[countryCount.size()];

        int i = 0;
        while (enumer.hasMoreElements()) {
            isoStats[i++] = (String) enumer.nextElement();
        }
        new StringSort(isoStats);
        int[] total = new int[2];

        for (i = 0; i < isoStats.length; i++) {
            ofb = (GlobalFallback) countryCount.get(isoStats[i]);
            ciSb.append(isoStats[i] + "=" + ofb.toString(isoStats[i], doSmart));
            total[0] += ofb.recCount;
            total[1] += ofb.sinCount;
            ciSb.append("\n                         ");
        }
        ciSb.append("Tot=" + total[0] + "(" + total[1] + ")");

        printLog(ciSb.toString());
        if (logIsFile) {
            System.err.println(ciSb.toString());
        }
        countryCount = new Hashtable();
    }

    //
// A simple method called by the setRecord callback to accumulate the
// GlobalChild statistics for a processed record into the global statistics
// tables by country name.
//
    public final void addCountryData(GlobalStats countryStat) {
        //System.err.println("Appending country stat for "+
        //countryStat.countryName);
        myCountryStat = (GlobalStats) hash.get(countryStat.countryName);
        if (myCountryStat == null) {
            myCountryStat = new GlobalStats(countryStat.countryName,
                    countryStat.iso3);
            hash.put(countryStat.countryName, myCountryStat);
        }

        myCountryStat.addStats(countryStat, true);
        worldStat.addStats(countryStat, false);

        ofb = (GlobalFallback) countryCount.get(countryStat.iso3);
        if (ofb == null) {
            ofb = new GlobalFallback(countryStat.numCountryFallback,
                    countryStat.numCountryRecords);
        } else {
            ofb.addFallbackCount(countryStat.numCountryFallback,
                    countryStat.numCountryRecords);
        }
        countryCount.put(countryStat.iso3, ofb);
    }

    //
// When a child finishes (usually due to being given no more data to process)
// it notifies the GlobalDriver so that it can be signed-off as complete.
// When all children are complete the process has finished and final country
// statistics are ready for outputting.
//
    public final synchronized void childFinished(GlobalChild child,
                                                 int childId) {
        if (child != globalChildrenRunning[childId]) {
            printLog("Thread " + childId + ": Detected false finished call");
            return;
        }
        globalChildrenRunning[childId] = null;
        globalChildren[childId] = null;
        childHost[childId] = -1;
        if (!stopping) {
            return;
        }
        nrunners = 0;
        for (finI = 0; finI < globalChildrenRunning.length; finI++) {
            if (globalChildrenRunning[finI] != null) {
                nrunners++;
            }
        }

        if (nrunners > 0) {
            printLog("GloParse pending stop - waiting for " +
                    nrunners + " running threads");
            return;
        }

        boolean canStop = true;

        for (finI = 0; finI < globalChildren.length; finI++) {
            if (globalChildren[finI] != null) {
                printLog("GloParse: Thread " + finI +
                        " has no connection registered - " +
                        "server(s) may have hung");
                canStop = false;
            }
            if (canStop == false) {
                printLog("GloParse pending stop - waiting for " +
                        "starting/hung threads");
                return;
            }
        }
        System.err.println("GloParse: All GA clients complete");
    }

    protected void report() {
//
// all children home and safe plus finisher has output all data
//
        printCI((double) (numRecordsOut - lastNumRecordsOut));
        for (finI = 0; finI < globalHosts.length; finI++) {
            globalHosts[finI].hostCount = globalHosts[finI].hostTotal;
            globalHosts[finI].retryCount = 0;
        }
        printCI(1);
        writeReports();

        FormatName.printStats(logWriter);
        printTruncations(logWriter);
        try {
            sbos.close();
        } catch (IOException ioe) {
            System.err.println("GloParse: I/O error closing output file. " +
                    "Error: " + ioe.getMessage());
            System.exit(20);
        }
        System.exit(0);
    }

    //
// Simple emthod to determine where log output is sent. if a logWriter is
// defined then it goes there, otherwise it goes to the standard stats or
// stdout file (whatever printWriter has been allocated to).
//
    public final void printLog(String toPrint) {
        if (logIsFile == true) {
            globalLogger.println(toPrint);
        } else {
            printWriter.println(toPrint);
        }
    }

    //
// Write reports, invoked by the childFinished method asks each of the
// GlobalStats classes to create their specific country-level report to the
// stats file provided.
//
// the reporting format is controlled by the GlobalStats class.
//
    private void writeReports() {
        worldStat.printStats(printWriter);
        String[] hashData = new String[hash.size()];

        Enumeration enumer = hash.keys();
        int i = 0;

        while (enumer.hasMoreElements()) {
            hashData[i++] = (String) enumer.nextElement();
        }

        StringSort ss = new StringSort(hashData);
        for (i = 0; i < hashData.length; i++) {
            GlobalStats countryStat = (GlobalStats) hash.get(hashData[i]);
            countryStat.printStats(printWriter);
        }
        for (i = 0; i < hashData.length; i++) {
            GlobalStats countryStat = (GlobalStats) hash.get(hashData[i]);
            countryStat.printWords(logWriter);
        }

    }

    public final PrintWriter getLogWriter() {
        return logWriter;
    }

    //
// Private method to log name of truncated output dictionary field
//
    public final void logTruncatedField(int fieldNum, byte[] data) {
        Integer tlen = (Integer) truncHash.get(parsoutDdl[fieldNum].name);
        if (tlen == null || tlen.intValue() < data.length) {
            tlen = new Integer(data.length);
            truncHash.put(parsoutDdl[fieldNum].name, tlen);
        }
    }

    //
// Method to write list of truncated fields to log
//
    private void printTruncations(PrintWriter pw) {
        pw.println("\nList of truncated data fields in output dictionary:" +
                "\n");
        Enumeration enumer = truncHash.keys();

        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            Integer tlen = (Integer) truncHash.get(key);
            key += "                                        ";
            pw.println("Field   " + key.substring(0, 40) + "  " +
                    Conversion.toPaddedString(tlen.intValue(), 5));
        }
    }

    //
//*****************************************************************************
//
//
// Main always receives the passed arguments (from the command line) in an
// array of simple strings.
// main creates a single instance of the GlobalDriver class - to which the
// arguments are also supplied
//
    public static void main(String[] args) {
        GlobalDriver od = new GlobalDriver(args);
    }
}
