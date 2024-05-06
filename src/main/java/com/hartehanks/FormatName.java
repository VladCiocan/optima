package com.hartehanks;

import com.hartehanks.optima.api.*;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.hartehanks.dev.misc.*;
import com.hartehanks.dev.app.*;
import com.hartehanks.dev.swing.*;

public class FormatName {
    static Hashtable wordStrings = new Hashtable();
    static Hashtable leadStrings = new Hashtable();
    static Hashtable tgnTypes = new Hashtable();
    static int[] patternTgnMax;
    static HashMap patternStrings = new HashMap();
    static Hashtable countryStrings = new Hashtable();
    static Hashtable countrySpecials = new Hashtable();
    static String[] countryCodes;
    static Hashtable defaultTitles = new Hashtable();
    static Hashtable modifyHash = new Hashtable();
    static PatternHolder[] modifyPatternHolder;
    static String miscChars = "?\\#;:~+=_*^%$!{}";
    static Hashtable missedWords = new Hashtable();
    static Hashtable missedPatterns = new Hashtable();
    //
// C - Connector
// D - Drop
// E - Email
// F - First
// G - Greedy Prefix - ie. Huis in t' Wood
// H - Hold word
// I - Initial
// J - Job Title
// L - Last
// P - Last Prefix - ie. van der, da costa
// R - Redefine
// S - Suffix
// T - Title
// U - Plural Title
// V - Vocational Title (Prof, Dr. etc.)
// W - Special reparse		 (Use in Patmod)
// Z - Empty / Meaningless value (Use in Patmod)
//
    static String typeCodes = "TUVFCGPLSIEJDHR";
    static String[] typeCodeWords =
            {
                    "Select", "Title", "Plural Title", "Voc. Title", "First",
                    "Connector",
                    "Greedy Prefix", "Prefix", "Last", "Suffix", "Initial", "Email",
                    "Job Title", "Drop", "Hold Word", "Redefine",
            };
    //
// Following characters are not gender typed and may appear in a stream
// depending upon the decoded string: Mummy-C/3daddies = A-C/NA
// There is no recode length code as each attribute is allocated as:
// A,target,-,target,C,target,/,target,N,target,A,target
//
// A - Alpha
// C - Character (single) - would only appear if not in wordStrings or is part
//                          of an alpha, numeric or allowable punct sequence
// N - Numeric
// - - Hyphen
// K - Comma (will appear on own - never as part of an AlphaGroup
// S - Semicolon
// T - Tilde (replaces existing blanks when in a unicode String)
// U - Unicode syllable (>= 4096)
// / - Fwd Slash
// . - Dot
// ? - Any other character - don't know what they might be but...
//
//
    static String alphaGroupPunct = "-/,.'?()[]@";
    static String alphaGroup = "ACNKSTU" + alphaGroupPunct;
    static String leadKeepGroup = "-'(";
    static String trailKeepGroup = ".')-";
    //
//
// M - Male
// F - Female
// U - Unknown
//
    static String standardGenderCodes = "MFU";
    static String[] standardGenderCodeWords =
            {
                    "Select", "Male", "Female", "Unknown",
            };
    static String extendedGenderCodes = "MFU+*";
    static String forcedPatternGenderCodes = "MFUC*";
    //
// Target fields by initial letter code are:
//
// T - Title
// F - First
// M - Middle
// L - Last
// S - Suffix
// I - Ignore (throw away)
//
    static String targetCodes = "TFMLSDEJ";
    static String normalTargetCodes = "TFFLSDEJ";

    static String[] dummyTitleGroup = {"", "", ""};
    static int titleGender = 1;
    static int firstGender = 1;
    static int lastGender = 1;
    private static boolean initialised = false;
    private static boolean loadingDefs = false;
    private static PrintWriter logFile = null;

    String orgName;
    String[] nameBits = new String[100];
    ParsedEntity[] parsedEntities = null;
    ;
    ParsedEntity[] newParsedEntities = null;
    ;
    ParsedEntity[] newIgnoreEntities = null;
    ;
    String[][] resDecomp = new String[9][3];
    String retPattern;
    String patternIso = "";
    String[] isoList = null;
    int finNspos;
    //String			midPattern;
    String modPattern;
    String modPattern2;
    String patcode = "";
    int[] vtfmlsCode = new int[6];
    PrintWriter logWriter = null;
    StringBuffer debugText = new StringBuffer(2000);
    StringBuffer unicodeSpacer = new StringBuffer(100);
    Vector missedWordVector = new Vector(10, 10);
    String defaultLastFirstList =
            "CH2L=L,CHNR=R,JP2L=L,JPNR=R,JP3L=L," +
                    "KO2L=L,KORR=R,TW2L=L,TWNR=R,TH2R=R,THAR=R";

    HashMap lastFirstHash = new HashMap();
    boolean isCJK = false;
    String direction = "L";
    int recurseLevel = 0;

    //
//***************************************************************************
//
    public static synchronized FormatName getInstance(PrintWriter logF,
                                                      boolean loadDefaults, String lastFirstList) {
        if (initialised == false) {
            logFile = logF;
            if (logF == null) {
                logFile = new PrintWriter(System.err, true);
            }
            String path = getTablePath();

            try {
                miscChars += String.valueOf('\n');
                miscChars += String.valueOf('\r');
                loadCountries(path + File.separator + "countrydata.txt");
                loadCountrySpecials(path + File.separator +
                        "countryspecial.txt");
                loadWords(path + File.separator + "namedata.txt");
                loadDefaultTitles(path + File.separator + "defaulttitles.txt");
                loadPatternMods(path + File.separator + "patternmods.txt");
                loadingDefs = false;
                loadPatterns(path + File.separator + "patterndata.txt",
                        true, true);
                if (loadDefaults) {
                    loadingDefs = true;
                    loadPatterns(path + File.separator + "patterndefs.txt",
                            true, false);
                }
            } catch (IOException ioe) {
                System.err.println("Unexpected IO Exception loading data");
                System.exit(1);
            }
            new FormatName(true, lastFirstList);
            initialised = true;
        }
        return new FormatName(true, lastFirstList);
    }

    //
//***************************************************************************
//
    public static synchronized String getTablePath() {
        String path = System.getProperties().getProperty("NAMETABLES");
        if (path == null) {
            System.err.println("No NAMETABLE define found that " +
                    "describes name table path for FormatName");
            System.err.println("Using ./ path instead");
            path = ".";
        }
        return path;
    }

    //
//***************************************************************************
//
    public FormatName() {
        System.err.println("Cannot use FormatName constructor");
        System.err.println("Use FormatName.getInstance(" +
                "PrintWriter logFile) to obtain an instance");
        System.exit(1);
    }

    //
//***************************************************************************
//
    private FormatName(boolean ti, String lastFirstList) {
        if (lastFirstList == null || lastFirstList.length() < 6 ||
                lastFirstList.charAt(4) != '=') {
            if (initialised == false) {
                System.err.println("ApacNameMode parameter is not in " +
                        "current format - using default");
                System.err.println("Supplied is " + lastFirstList);
                System.err.println("Default is " + defaultLastFirstList);
            }
            lastFirstList = defaultLastFirstList;
        }
        SuperStringTokenizer stt = new SuperStringTokenizer(lastFirstList,
                ",:", false);
        String[] str = stt.getStringList();
        for (int i = 0; i < str.length; i++) {
            if (str[i].length() != 6 || str[i].charAt(4) != '=' ||
                    "LR".indexOf(str[i].charAt(3)) < 0 ||
                    "LR".indexOf(str[i].charAt(5)) < 0) {
                if (initialised == false) {
                    System.err.println("ApacNameMode list error at entry " +
                            str[i] + " - Format = ISOx=x where x is L or R");
                }
            } else {
                lastFirstHash.put(str[i].substring(0, 4),
                        str[i].substring(5));
            }
        }
    }

    //
//***************************************************************************
//
    private static void loadWords(String fileName) throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;

        while ((line = br.readLine()) != null) {
            lineno++;
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line, "\t", false);

            stt = str.getStringList();

            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
            if (stt[0].length() != 1) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Initial type code is not one character");
                System.exit(1);
            }

            if (stt[0].charAt(0) != 'D') {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Unrecognised initial type code " + stt[0]);
                System.exit(1);
            }

            if (stt.length != 5) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": 5 values expected for a word " +
                        "definition");
                System.exit(1);
            }
            if (countryStrings.get(stt[1]) == null) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Country code " + stt[1] +
                        " not recognised");
                System.exit(1);
            }
            if (validateTypeGender(stt[4]) == false) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Type/Gender code has invalid " +
                        "value or length");
                System.exit(1);
            }

            if (insertWordEntry(stt) == false) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Duplicate Define for " +
                        "TypeGender " + stt[4] +
                        " for definition of " + stt[1] + ":" + stt[2]);
                System.exit(1);
            }
        }
    }

    //
//***************************************************************************
//
// Data layout of 5 arg String[]:
//
//	1:	D		- define
//	2:	ISO		- Three letter country ISO code for entry
//	3:	Lookup		- Word definition
//	4:	Replace		- Replacement value for definition
//	5:	Type/Gender	- Type (TFMLSV etc.) plus gender (MFU)
//
    private static boolean insertWordEntry(String[] stt) {
//
// Tokenize the result string so that the word length can be built up
//
        SuperStringTokenizer rss = new SuperStringTokenizer(
                stt[3], "-. ", false);
        String[] rct = rss.getStringList();

        stt[1] = stt[1].toUpperCase();
        stt[2] = stt[2].toUpperCase();

// Define to be 'ISO  Lookup'
//
        String[] ws = (String[]) wordStrings.get(stt[1] + "  " + stt[2]);

        if (ws == null) {
            ws = new String[1];
        } else {
            String[] ws2 = new String[ws.length + 1];
            System.arraycopy(ws, 0, ws2, 0, ws.length);
            ws = ws2;
        }
        ws[ws.length - 1] = "D " + stt[4] + String.valueOf(rct.length) +
                " " + stt[3];
        tgnTypes.put(stt[4].replace('V', 'T') + String.valueOf(rct.length),
                "");
        if (stt[4].equals("IU") && rct.length == 4) {
            System.err.println("IU4 is " + stt[3]);
            System.exit(1);
        }

//
// If this is not the first entry for this definition then check that this is
// not a duplicate of any already defined
//
        if (ws.length > 1) {
            new StringSort(ws);
            for (int i = 0; i < ws.length - 1; i++) {
                if (ws[i].charAt(2) == ws[i + 1].charAt(2)) {
                    return false;
                }
            }
        }
//
// If this is the first entry and it contains multiple words then add all
// permutations up to the last word but not including it to the leadStrings
// list
//
        else {
            SuperStringTokenizer stx = new SuperStringTokenizer(
                    stt[2], " ", false);
            String[] stxa = stx.getStringList();
            if (stxa.length > 1) {
                String leadStr = "";
                for (int i = 0; i < stxa.length - 1; i++) {
                    leadStr += " " + stxa[i];
                    leadStrings.put(leadStr.substring(1), "");
                }
            }
        }
        wordStrings.put(stt[1] + "  " + stt[2], ws);
        return true;
    }

    //
//***************************************************************************
//
    private static boolean removeWordEntry(String[] stt) {
        boolean removed = false;
        System.err.println("Remove request for " + stt[0] + " " + stt[1] + "  " +
                stt[2] + " " + stt[3] + " " + stt[4]);
        stt[1] = stt[1].toUpperCase();
        stt[2] = stt[2].toUpperCase();
        String[] ws = (String[]) wordStrings.get(stt[1] + "  " + stt[2]);

        if (ws != null) {
            for (int i = 0; i < ws.length; i++) {
                if (ws[i].substring(2, 4).equals(stt[4])) {
                    for (int j = i; j < ws.length - 1; j++) {
                        ws[j] = ws[j + 1];
                    }
                    String[] ws2 = new String[ws.length - 1];
                    System.arraycopy(ws, 0, ws2, 0, ws2.length);
                    ws = ws2;
                    System.err.println("Deleted");
                    removed = true;
                    i--;
                }
            }
            if (ws.length > 0) {
                wordStrings.put(stt[1] + "  " + stt[2], ws);
            } else {
                wordStrings.remove(stt[1] + "  " + stt[2]);
            }
        }
        return removed;
    }

    //
//***************************************************************************
//
    private static boolean loadPatterns(String fileName,
                                        boolean batch, boolean reset) throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;
        StringBuffer sb = new StringBuffer(1000);
        boolean error = false;
        boolean anyError = false;
        if (reset) {
            patternStrings = new HashMap(500000);
            patternTgnMax = new int[typeCodes.length()];
        }
        tgnTypes.put("EU1", "");
        Vector patternList = new Vector(50, 50);

        while ((line = br.readLine()) != null) {
            anyError = (error == true) ? true : anyError;
            error = false;
            lineno++;
//
// remove all whitespace characters
//
            sb.setLength(0);
            for (int i = 0; i < line.length(); i++) {
                if (Character.isWhitespace(line.charAt(i)) == false) {
                    sb.append(line.charAt(i));
                }
            }
            line = sb.toString().toUpperCase();

            //logFile.println("Line is "+line);

//
// Tokenize pattern to ISO ST pairs and GenderPattern
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line.toUpperCase(), "[]", false);

            stt = str.getStringList();
/*
		for (int i = 0; i < stt.length; i++)
		{
		    logFile.println("Token "+i+" is "+stt[i]);
		}
*/
//
// Check for comment lines or empty line
//
            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
//
// Check that stt is at least 3 tokens - error otherwisr
//
            if (stt.length < 3) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        " has less than 3 items in the line definition");
                error = true;
                continue;
            }
//
// Check that first field is 3 characters and is country Iso code
//
            if (countryStrings.get(stt[0]) == null) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Country " + stt[0] + " not recognised");
                error = true;
                continue;
            }
//
// Check length of final field is at least 3 character (2 for pattern minimum)
//
            if (stt[stt.length - 1].length() < 3) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Pattern definition is less than 3 chars");
                error = true;
                continue;
            }

            PatternDefinition patternDefinition =
                    createPatternDefinition(stt, fileName, lineno);

            if (patternDefinition == null) {
                error = true;
                continue;
            }
            patternList.addElement(patternDefinition);
        }

//
// If any error then stop now
//
        if (error || anyError) {
            System.err.println("Too many pattern definition errors to " +
                    "proceed with processing");
            if (batch) {
                System.exit(1);
            } else {
                return false;
            }
        }
        for (int i = 0; i < patternTgnMax.length; i++) {
            patternTgnMax[i]++;
/*
		System.err.println("Pat Spec TgN for "+
			typeCodes.substring(i, i+1)+
			" = "+patternTgnMax[i]);
*/
        }
//
// Now the Hashtable pattern build must start. This is performed by a
// recursive algorithm that perms all left patterns from each pattern set
// to each in the next set etc. etc.
//
// During this process the gender is derived from all fields where the left
// type code matches the right target type and the gender use  is not
// deactivated.
//
//
/*
		logFile.println("Left + right iterators are:");
		for (int i = 0; i < numPats; i++)
		{
		    logFile.println("Left  "+i+" = "+leftIterate[i]);
		    logFile.println("Right "+i+" = "+rightIterate[i]);
		}
*/

//
// Lift each pattern definition from the patternList vector and perform an
// initial leftIterator modification to remove TGN patterns that will not be
// used because the patternTgnMax sets the limit
//
        for (int i = 0; i < patternList.size(); i++) {
            int numPattGo = patternStrings.size();
            PatternDefinition patDef =
                    (PatternDefinition) patternList.elementAt(i);
            modifyLeftIterators(patDef);

            patternIterator(patDef.leftIterate, patDef.rightIterate,
                    patDef.countryIso, "", 0, 1, 1, 1, patDef.patGender,
                    patDef.patternId, "", false, false, 0);
/*
		patternIterator(leftIterate, rightIterate, stt[0], "", 0, 1,
				1, 1, patGender, pattern, "", false, false, 0);
*/
            if (numPattGo == patternStrings.size()) {
                System.err.println("Pattern definition " + patDef.patternId +
                        " yielded no new patterns");
            }
        }
        if (reset) {
            System.err.println("GloParse: " + patternStrings.size() +
                    " primary pattern permutations generated");
        } else {
            System.err.println("GloParse: " + patternStrings.size() +
                    " secondary pattern permutations generated");
        }
        return true;
    }

    //
//***************************************************************************
//
// Create left/right container string holders for stt.length -2 elements
// then seperate left right pairs to left right arrays
//
    private static PatternDefinition createPatternDefinition(String[] stt,
                                                             String fileName, int lineno) {
        int numPats = stt.length - 2;
        String[] left = new String[numPats];
        String[] right = new String[numPats];
        String[] leftIterate = new String[numPats];
        String[] rightIterate = new String[numPats];
        boolean error = false;
//
// Check last field starts with valid MFUC* value
//
        String patGender = stt[stt.length - 1].substring(0, 1);
        String patternId = stt[stt.length - 1].substring(1);
        if (forcedPatternGenderCodes.indexOf(patGender) < 0) {
            System.err.println("Line " + lineno + " in file " + fileName +
                    ": Gender definition not one of M,F,U,C or *");
            return null;
        }

        for (int i = 1; i < stt.length - 1; i++) {
            int index = stt[i].indexOf(":");
            if (index < 1 || index >= stt[i].length() - 1) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Pattern definition is missing or missing Colon");
                error = true;
            }

            left[i - 1] = stt[i].substring(0, index);
            right[i - 1] = stt[i].substring(index + 1);
        }
//
// Any problem with left-right split then continue with next pattern now
//
        if (error == true) {
            return null;
        }

        PatternDefinition patternDefinition =
                new PatternDefinition(patGender, stt[0], patternId);
//
// Left could be:
//
//	    alphaGroup		ACN-/,.'?
//	    TGN		Where:	T - data type - Title, First etc.
//	    TGN&TGN...		G - Gender - M, F, U, + (MF) or * (MFU)
//	    TGN,TGN...		N - Number of words in converted phrase
//
// Right could be:
//
//	    target
//	    -target
//	    TGN,[TGN,...]target
//	    TGN,[TGN,...]-target
//
//
// Now we process each left/right pair and check syntax and compatibility
//
        for (int i = 0; i < numPats; i++) {
//
// initialise left complex status
//
            boolean leftIsComplex = (left[i].indexOf(",") >= 0 ||
                    left[i].indexOf("+") >= 0 ||
                    left[i].indexOf("*") >= 0);
            boolean leftIsTarget = (left[i].indexOf("&") >= 0);
//
// Now check that left does not complex and multiple
//
            if (leftIsComplex && leftIsTarget) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Left side contains mixture of " +
                        "and and or operators (& and ,)");
                error = true;
                continue;
            }

//
// Check left is alpha group or length is mod 4
//
            SuperStringTokenizer lll = new SuperStringTokenizer(
                    left[i], ",&", false);

            String[] lft = lll.getStringList();
            String[] leftOut = new String[lft.length * 30];
            int out = 0;
            boolean isAlphaGroup = false;

            for (int l = 0; l < lft.length; l++) {
                if (lft[l].startsWith("#")) {
                    if (validateAlphaGroup(lft[l]) == false) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Left side AlphaGroup " +
                                "contains illegal character");
                        error = true;
                        continue;
                    }
                    isAlphaGroup = true;
                    leftOut[out++] = lft[l];
                } else {
                    if (lft[l].length() != 3) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Left side source at " +
                                lft[l] + " is not " +
                                "correct length");
                        error = true;
                        continue;
                    }
//
// This is not an AlphaGroup so check it conforms with triple standard:
// Now look  over each TGN and check each of the 3 characters for validity
// Test type codes at pos 0
//
                    if (typeCodes.indexOf(lft[l].substring(0, 1)) < 0) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Left side Type code invalid");
                        error = true;
                        continue;
                    }
//
// Test gender at pos 1 - Male, Female, Unknown, + (Male or Female) * - MFU
//
                    if (extendedGenderCodes.indexOf(
                            lft[l].substring(1, 2)) < 0) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Left side gender code invalid");
                        error = true;
                        continue;
                    }
//
// Test position 3 field length between 0 and 9
//
                    if (lft[l].charAt(2) != '*' &&
                            (lft[l].charAt(2) < '0' || lft[l].charAt(2) > '9')) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Left side length value not 0-9");
                        error = true;
                        continue;
                    }
//
// Now convert Complex multiple state to complex simple state:
//
//	TF1,F*2	-> TF1,FM2,FF2,FU2
//	T**	-> TF1,TF2,TF3...TM1,TM2,TM3...TU1,TU1,TU3...
//
                    String typeChar = lft[l].substring(0, 1);
                    int typeInd = typeCodes.indexOf(typeChar);

                    int start = 0;
                    int stop = 0;
                    start = stop = standardGenderCodes.indexOf(
                            lft[l].substring(1, 2));
                    if (start < 0) {
                        start = 0;
                        stop = (lft[l].charAt(1) == '+') ? 1 : 2;
                    }

                    int rangeLo = 1;
                    int rangeHi = 9;

                    if (lft[l].charAt(2) != '*') {
                        rangeLo = Integer.parseInt(lft[l].substring(2));
                        rangeHi = rangeLo;
                        patternTgnMax[typeInd] =
                                (rangeHi > patternTgnMax[typeInd]) ?
                                        rangeHi : patternTgnMax[typeInd];
                    }

                    for (int m = start; m <= stop; m++) {
                        for (int n = rangeLo; n <= rangeHi; n++) {
                            leftOut[out] = typeChar +
                                    standardGenderCodes.substring(m, m + 1) +
                                    String.valueOf(n);
                            if (tgnTypes.get(leftOut[out]) != null) {
                                out++;
                            }
                        }
                    }
                }
            }
/*
		logFile.println("Left string for item "+i+
							" is now "+left[i]);
*/
//
// Check for duplicates in lft and rebuild left at same time
//
            left[i] = "";
            if (out == 0) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": No patterns found");
                System.exit(2);
            }
            for (int l = 0; l < out; l++) {
                for (int m = l + 1; m < out; m++) {
                    if (leftOut[l].equals(leftOut[m])) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Duplicate multiple code " +
                                "definition in line");
                        error = true;
                        break;

                    }
                    if (leftIsTarget && leftOut[l].charAt(0) ==
                            leftOut[m].charAt(0)) {
                        System.err.println("Line " + lineno +
                                " in file " + fileName + " has duplicate " +
                                "left side Type definition in the '&'" +
                                " use case");
                        error = true;
                        break;
                    }
                }
                left[i] += " " + leftOut[l];
            }
            if (leftIsTarget) {
                left[i] = left[i].substring(1).replace(' ', '&');
            } else {
                left[i] = left[i].substring(1);
            }
//
// Another check is to see that left side multiple patterns does not have a
// defined gender defined that is M or F
//
            if (out > 1 &&
                    (patGender.charAt(0) != '*' && patGender.charAt(0) != 'U')) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Cannot have multiple mode " +
                        "sourcing and define a specific gender");
                error = true;
                continue;
            }
//
// Now test the right hand portion (target) of the pattern
//
// This can be for reminder:
//
//		target
//		-target
//		TGN,[TGN,...]target
//		TGN,[TGN,...]-target
//
            int lastComma = right[i].lastIndexOf(",");
            String rightLeft = "";
            String rightRight = right[i];

            if (lastComma >= 0) {
                rightLeft = right[i].substring(0, lastComma);
                rightRight = right[i].substring(lastComma + 1);
            }
            boolean rightIsComplex = (rightLeft.length() > 0);
//
// leftIsTarget requires rightIsComples - otherwise error as targetting hast to
// be specific
//
            if (leftIsTarget == true && rightIsComplex == false) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Right side source is not complex" +
                        " but left side is '&' type");
                error = true;
                continue;
            }
//
// alphaGroup requires rightIsComples o be false - simple targetting in oper.
//
            if (isAlphaGroup == true && rightIsComplex == true) {
                System.err.println("Line " + lineno + " in file " +
                        fileName + ": Right side source cannot be " +
                        "complex with an AlphAgroup");
                error = true;
                continue;
            }
//
// Test rightLeft is either 0 or length+1 % 4 = 0
//
            if (rightIsComplex) {
                if (((rightLeft.length() + 1) % 4) != 0) {
                    System.err.println("Line " + lineno + " in file " +
                            fileName + ": Right side source match is not " +
                            "correct length");
                    error = true;
                    continue;
                }
//
// Now check each TGN code is valid - simply see if it's in the left side!
//
                for (int j = 0; j < rightLeft.length(); j += 4) // step 4
                {
                    if (left[i].indexOf(rightLeft.substring(j, j + 2)) < 0) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + ": Right side pattern " +
                                rightLeft.substring(j, j + 2) + " cannot " +
                                " be found in left side: " + left[i]);
                        error = true;
                    }
                }
            }
//
// Finally test rightRight for Gender ignore notation ("-") and target field
//
            boolean ignoreGender = false;
            boolean retryGender = false;

            if (rightRight.startsWith("-")) {
                ignoreGender = true;
                rightRight = rightRight.substring(1);
            } else if (rightRight.startsWith("+")) {
                retryGender = true;
                rightRight = rightRight.substring(1);
            }
//
// Check that target field is one or more characters
//
            if (rightRight.length() < 1) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Right side target field is missing");
                error = true;
            }
//
// Finally, test right string target field
//
            else if (targetCodes.indexOf(rightRight.substring(0, 1)) < 0) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Right side target field is not one of " +
                        targetCodes);
                error = true;
            }
//
// if any error then continue with next field
//
            if (error) {
                continue;
            }
//
// Left and right sides have been verified - now check for left-right
// incompatibilities:
//
// Left and right can't both be complex (wildcard left and selectively
// targetted right)
//
            if (leftIsComplex && rightIsComplex) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Left and right sides of pattern are of a " +
                        "mutually exclusive nature - multiple source " +
                        "and selective targetting");
                error = true;
                continue;
            }

            if (error) {
                continue;
            }
//
// Left and right are ok - some housekeeping required for later perm system
//
// Left is TGN... and needs to become 't TGN' or '- TGN' (gender derive or not)
//
            if (ignoreGender) {
                leftIterate[i] = "-" + getStandardTarget(rightRight) +
                        "-" + getStandardTarget(rightRight) + " " + left[i];
            } else if (retryGender) {
                leftIterate[i] = getStandardTarget(rightRight) +
                        getStandardTarget(rightRight) +
                        "-" + getStandardTarget(rightRight) + " " + left[i];
            } else {
                leftIterate[i] = getStandardTarget(rightRight) +
                        getStandardTarget(rightRight) +
                        getStandardTarget(rightRight) +
                        getStandardTarget(rightRight) + " " + left[i];
            }
//
// Right side targetting output syntax is required - this is made up as:
//
//	Trn	- Where:	T - Target field
//				r - source resource index (complex targetting)
//				n - Source word number - '*' is all
//
            if (rightIsComplex == false) {
                rightIterate[i] = rightRight.substring(0, 1) + "1*";
            } else // complex right - require  [xxxxxx] group
            {
                rightIterate[i] = "";
                for (int j = 0; j < rightLeft.length(); j += 4) {
                    int ind = left[i].indexOf(
                            rightLeft.substring(j, j + 2));
                    if (ind < 0) {
                        System.err.println("Fatal error with " +
                                left[i] + " when looking for " +
                                rightLeft.substring(j, j + 2));
                        System.exit(1);
                    }

                    rightIterate[i] += rightRight.substring(0, 1) +
                            String.valueOf((ind / 4) + 1);
                    if (rightLeft.charAt(j + 2) == '*') {
                        rightIterate[i] += "*";
                    } else if (rightLeft.charAt(j + 2) < '1' ||
                            rightLeft.charAt(j + 2) > left[i].charAt(ind + 2)) {
                        System.err.println("Line " + lineno + " in file " +
                                fileName + " Specific targetting word " +
                                "number is not in input range");
                        error = true;
                        continue;
                    } else // target is in range of source
                    {
                        rightIterate[i] += rightLeft.substring(j + 2, j + 3);
                    }
                }
                if (rightIterate[i].length() > 3) {
                    rightIterate[i] = "[" + rightIterate[i] + "]";
                }
            }
        }
//
// If any error then don't coninue with pattern build
//
        if (error) {
            return null;
        }
        patternDefinition.leftIterate = leftIterate;
        patternDefinition.rightIterate = rightIterate;
        return patternDefinition;
    }

    //
//***************************************************************************
//
    private static void modifyLeftIterators(PatternDefinition patDef) {
        for (int i = 0; i < patDef.leftIterate.length; i++) {
            if (patDef.leftIterate[i].indexOf("&") < 0) {
                Hashtable patternTgnDropped = new Hashtable();

                //System.err.println("Starting with "+
                //patDef.leftIterate[i]);
                SuperStringTokenizer lll = new SuperStringTokenizer(
                        patDef.leftIterate[i], " ", false);
                String[] lft = lll.getStringList();

                patDef.leftIterate[i] = lft[0];

                for (int j = 1; j < lft.length; j++) {
                    if (lft[j].startsWith("#") == false) {
                        int typeInd =
                                typeCodes.indexOf(lft[j].substring(0, 1));
                        int patVal = Integer.parseInt(lft[j].substring(2));

                        if (patVal > patternTgnMax[typeInd]) {
                            patternTgnDropped.put(
                                    " " + lft[j].substring(0, 2) +
                                            String.valueOf(
                                                    patternTgnMax[typeInd]), "");
                            continue;
                        }
                    }
                    patDef.leftIterate[i] += " " + lft[j];
                }
//
// Check that, for each dropped type check that the system will be creating
// a TGN entry for the max value
//
                Enumeration enumer = patternTgnDropped.keys();

                while (enumer.hasMoreElements()) {
                    String key = (String) enumer.nextElement();
                    if (patDef.leftIterate[i].indexOf(key) < 0) {
                        patDef.leftIterate[i] += key;
                    }
                }
//
// Take care of complete removal situation - just in case
//
                if (patDef.leftIterate[i].length() < 5) {
                    patDef.leftIterate[i] += " " + lft[1];
                }
                //System.err.println("Ending   with "+
                //patDef.leftIterate[i]);
            }
        }
    }

    //
//***************************************************************************
//
// This is the main recurse Iterator that is repsonible for routing pattern
// construction according to whether the pattern is targetted or not.
//
//
    private static void patternIterator(String[] leftSide,
                                        String[] rightSide, String pattern, String result, int depth,
                                        int genNo, int hiddenGen, int titleGen, String gender,
                                        String pattPref, String pattSuff, boolean hold, boolean zed,
                                        int patCount) {
//
// First check that genNo is not zero and gender is not '*' - otherwise nogo
//
        if (genNo == 0 && "*U".indexOf(gender.substring(0, 1)) >= 0) {
/*
		logFile.println("Pattern "+pattern+" dropped as "+
						"unresolved gender conflict");
*/
            return;
        }
//
// If pattern length is over 10 then don't add as it's not likely to happen and
// even if it did it would be pretty dodgy
//
        if (patCount > 10) {
            return;
        }
//
// Next, check depth as it could have reached the bottom of the left/right
// list - in which case we can add the pattern to the PatternStrings hash
//
        if (depth >= leftSide.length) {
//
// Finally is gender is known from pattern - if not does gender word exist?
//
            if (genNo == 1 && gender.charAt(0) == '*') {
/*
		    logFile.println("Pattern "+pattern+" abandoned due to "+
			"unresolvable gender and none supplied");
*/
                return;
            }

            String nqs = evaluateOneNqs(hold, zed, genNo, hiddenGen, gender);

/*
		if (nqs.charAt(0) == '3')
		{
		    logFile.println("Pattern "+pattern+" is Unresolved...");
		}
*/

//
// If titleGen and genNo conflict then set titleGen to C position. A conflict
// is where genNo > 1 && titleGen > 1 && genNo != titleGen
//
            if (genNo > 1) {
                if (titleGen > 1 && titleGen != genNo) {
                    titleGen = 0;
                }
                result += " " + ("--MF".substring(genNo, genNo + 1)) +
                        "CUMF".substring(titleGen, titleGen + 1) + nqs + pattPref;
            } else {
                result += " " + gender +
                        "CUMF".substring(titleGen, titleGen + 1) + nqs + pattPref;
            }

            if (pattSuff.length() > 0) {
                result += "-" + pattSuff;
            }

            String res = (String) patternStrings.get(pattern);
            if (res != null) {
                if (result.substring(0, result.indexOf(" ")).equals(
                        res.substring(0, res.indexOf(" ")))) {
                    if (result.equals(res) == false) {
/*
			    logFile.println("Warning: Identical pattern found"+
						" "+ result+ " vs "+res);
*/
                    }
                } else if (loadingDefs == false) {
                    logFile.println("Duplicate pattern detected " + result +
                            " vs " + res);
                    logFile.println("Patterns are different");
                    System.err.println("Patterns are different");
                    System.exit(1);
                }
            } else {
                //logFile.println("Adding pattern "+pattern+"\t\t"+result);
                patternStrings.put(pattern, result);
            }
            return;
        }
        if (leftSide[depth].indexOf("&") > 0) {
            processTargetted(leftSide, rightSide, pattern, result, depth,
                    genNo, hiddenGen, titleGen, gender, pattPref, pattSuff,
                    hold, zed, patCount);
        } else {
            processMultiple(leftSide, rightSide, pattern, result, depth,
                    genNo, hiddenGen, titleGen, gender, pattPref, pattSuff,
                    hold, zed, patCount);
        }
    }

    //
//***************************************************************************
//
//
// method that completes the 'targetted' pattern state: &
//
//
    private static void processTargetted(String[] leftSide,
                                         String[] rightSide, String pattern, String result, int depth,
                                         int genNo, int hiddenGen, int titleGen, String gender,
                                         String pattPref, String pattSuff, boolean hold, boolean zed,
                                         int patCount) {
//
// loop through right side to see if source field matches the target field
// gender type
//
        int i = (rightSide[depth].startsWith("[")) ? 1 : 0;

        for (; i < rightSide[depth].length(); i += 4) {
            if (rightSide[depth].charAt(i) == leftSide[depth].charAt(0)) {
                for (int j = 5; j < leftSide[depth].length(); j += 4) {
                    if (leftSide[depth].charAt(j) ==
                            leftSide[depth].charAt(0)) {
                        genNo = testOneGender(genNo,
                                leftSide[depth].substring(j + 1, j + 2));
                    }
                    if (leftSide[depth].charAt(j) ==
                            leftSide[depth].charAt(1)) {
                        hiddenGen = testOneGender(hiddenGen,
                                leftSide[depth].substring(j + 1, j + 2));
                    }
                    if (leftSide[depth].charAt(j) ==
                            leftSide[depth].charAt(1) &&
                            leftSide[depth].charAt(1) == 'T') {
                        titleGen = testOneGender(titleGen,
                                leftSide[depth].substring(j + 1, j + 2));
                    }
                }
            }
        }
//
//
// resulting pattern appends left+2
// resulting target appends target keys
//
        pattern += " " + leftSide[depth].substring(5);
        result += rightSide[depth];
        patternIterator(leftSide, rightSide, pattern, result, depth + 1,
                genNo, hiddenGen, titleGen, gender, pattPref, pattSuff,
                hold, zed, patCount + 1);
    }

    //
//***************************************************************************
//
//
// method that completes the 'multiple' pattern state: TM1 TF1 TU1 etc.
//
//
    private static void processMultiple(String[] leftSide,
                                        String[] rightSide, String pattern, String result, int depth,
                                        int genNo, int hiddenGen, int titleGen, String gender,
                                        String pattPref, String pattSuff, boolean hold, boolean zed,
                                        int patCount) {
//
// Loop through left side processing each pattern item to the next level
//

        SuperStringTokenizer lll = new SuperStringTokenizer(
                leftSide[depth].substring(5), " ", false);

        String[] lft = lll.getStringList();

        for (int i = 0; i < 3; i += 2) {
//
// No point in performing second pass if the field code is the same as the
// first pass
//
            if (i == 2 &&
                    leftSide[depth].charAt(0) == leftSide[depth].charAt(2)) {
                continue;
            }

            for (int j = 0; j < lft.length; j++) {
                int outGenNo = genNo;
                int outHiddenGenNo = hiddenGen;
                int outTitleGen = titleGen;
                int outPatCount = patCount + 1;

                if (lft[j].startsWith("#") == false) {
                    if (leftSide[depth].charAt(i) == lft[j].charAt(0)) {
                        outGenNo = testOneGender(genNo,
                                lft[j].substring(1, 2));
                    }
                    if (leftSide[depth].charAt(i + 1) == lft[j].charAt(0)) {
                        outHiddenGenNo = testOneGender(hiddenGen,
                                lft[j].substring(1, 2));
                    }
                    if (leftSide[depth].charAt(i + 1) == lft[j].charAt(0) &&
                            leftSide[depth].charAt(i + 1) == 'T') {
                        outTitleGen = testOneGender(titleGen,
                                lft[j].substring(1, 2));
                    }
                    outPatCount = patCount +
                            Integer.parseInt(lft[j].substring(2, 3));
                }
                String outPattern = pattern + " " + lft[j];
                String outResult = result + rightSide[depth];
                String outPatSuff = pattSuff + String.valueOf(j + 1);
                boolean outHold = (lft[j].charAt(0) == 'H') ?
                        true : hold;
                boolean outZed = (lft[j].charAt(0) == 'Z') ?
                        true : zed;

                patternIterator(leftSide, rightSide, outPattern, outResult,
                        depth + 1, outGenNo, outHiddenGenNo, outTitleGen, gender,
                        pattPref, outPatSuff, outHold, outZed, outPatCount);
            }
        }
    }

    //
//***************************************************************************
//
    private static int testOneGender(int genNo, String gender) {
        switch (gender.charAt(0)) {
            case 'M':
                return (genNo == 3) ? 0 : 2;

            case 'F':
                return (genNo == 2) ? 0 : 3;

            case 'U':
                return genNo;

            default:
                break;

        }

        System.err.println("Fatal error in testOneGender with " + gender);
        System.exit(1);
        return 0;
    }

    //
//***************************************************************************
//
//
// This method translates the target field types to a default value
//
//
    private static String getStandardTarget(String target) {
        int index = targetCodes.indexOf(target.substring(0, 1));
        return normalTargetCodes.substring(index, index + 1);
    }

    //
//***************************************************************************
//
    private static void loadCountries(String fileName) throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;

        while ((line = br.readLine()) != null) {
            lineno++;
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line.toUpperCase(), "\t", false);

            stt = str.getStringList();

            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
            if (stt.length != 5) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Expected 5 tab delimited fields");
                System.exit(1);
            }
            switch (stt[1].length()) {
                case 1:
                    stt[1] = "C0" + stt[1];
                    break;

                case 2:
                    stt[1] = "C" + stt[1];
                    break;

                default:
                    System.err.println("Line " + lineno + " in file " + fileName +
                            ": Country group number blank or > 2 digits");
                    System.exit(1);
            }
            for (int i = 0; i < 2; i++) {
                if (stt[i].length() != 3) {
                    System.err.println("Line " + lineno + " in file " + fileName +
                            ": A language iso code is not 3 characters");
                    System.exit(1);
                }
            }
            if (countryStrings.get(stt[0]) != null) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Country " + stt[0] + " already defined");
                System.exit(1);
            }
            countryStrings.put(stt[0], stt[1]);
            if (countryStrings.get(stt[1]) == null) {
                countryStrings.put(stt[1], "ROW");
            }
        }
        countryCodes = new String[countryStrings.size()];
        int pos = 0;
        Enumeration enumer = countryStrings.keys();

        while (enumer.hasMoreElements()) {
            countryCodes[pos++] = (String) enumer.nextElement();

        }
        new StringSort(countryCodes);
    }


    //
//***************************************************************************
//
// Method to load CJK handling routes
//
    private static void loadCountrySpecials(String fileName)
            throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;

        while ((line = br.readLine()) != null) {
            lineno++;
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line.toUpperCase(), "\t", false);

            stt = str.getStringList();

            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
            if (stt.length != 3) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Expected 3 tab delimited fields");
                System.exit(1);
            }
            for (int i = 0; i < 3; i += 2) {
                if (stt[i].length() != 3) {
                    System.err.println("Line " + lineno + " in file " + fileName +
                            ": A language iso code is not 3 characters");
                    System.exit(1);
                }
            }
            if (stt[1].toUpperCase().equals("TRUE")) {
                stt[0] += "_TRUE";
            } else if (stt[1].toUpperCase().equals("FALSE")) {
                stt[0] += "_FALSE";
            } else {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": CJK flag value must be True or False");
                System.exit(1);
            }
            if (countrySpecials.get(stt[0]) != null) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Country " + stt[0] + " already defined");
                System.exit(1);
            }
            countrySpecials.put(stt[0], stt[2]);
        }
    }

    //
//***************************************************************************
//
//***************************************************************************
//
    private static void loadDefaultTitles(String fileName)
            throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;

        while ((line = br.readLine()) != null) {
            lineno++;
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line, "\t", false);

            stt = str.getStringList();

            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
            if (stt.length != 3) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Expected 3 tab delimited fields");
                System.exit(1);
            }
            switch (stt[0].length()) {
                case 1:
                    stt[0] = "C0" + stt[0];
                    break;

                case 2:
                    stt[0] = "C" + stt[0];
                    break;

                default:
                    System.err.println("Line " + lineno + " in file " + fileName +
                            ": Country group number blank or > 2 digits");
                    System.exit(1);
            }
            if (stt[0].length() != 3) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": A language code is not 3 characters");
                System.exit(1);
            }
            if (standardGenderCodes.indexOf(stt[1].toUpperCase()) < 0) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": A Gender code is not valid: " + stt[1]);
                System.exit(1);
            }
            defaultTitles.put(stt[0] + stt[1], stt[2]);
        }
    }

    //
//***************************************************************************
//
    private static void loadPatternMods(String fileName) throws IOException {
        BufferedReader br = openInputFile(fileName);
        String line;
        int lineno = 0;
        String[] stt;
        Vector modifyPatternVector = new Vector(100, 100);

        while ((line = br.readLine()) != null) {
            lineno++;
            SuperStringTokenizer str = new SuperStringTokenizer(
                    line, "\t", false);

            stt = str.getStringList();

            if (stt.length < 1 || stt[0].startsWith("#") ||
                    stt[0].startsWith("//")) {
                continue;
            }
            if (stt.length != 5) {
                System.err.println("Line " + lineno + " in file " + fileName +
                        ": Expected 5 tab delimited fields");
                System.exit(1);
            }

            modifyPatternVector.add(new PatternHolder(stt[0],
                    stt[1], stt[2], stt[3], stt[4], lineno));
        }

        modifyPatternHolder = new PatternHolder[modifyPatternVector.size()];
        modifyPatternVector.copyInto(modifyPatternHolder);

        try {
            addModifyPatterns(fileName, false);
        } catch (Exception e) {
        }
    }

    //
//***************************************************************************
//
    private static void addModifyPatterns(String fileName, boolean user)
            throws Exception {
        modifyHash = new Hashtable();
        for (int i = 0; i < modifyPatternHolder.length; i++) {
            //modifyPatternHolder[i].dump();
/*
		System.err.println("ModifyHash size is "+modifyHash.size());
		System.err.println("Re-Adding pattern "+
			modifyPatternHolder[i].toString(
			modifyPatternHolder[i].sources)+
			" Position "+modifyPatternHolder[i].patternPos);
*/
            PatternHolder[] resolvedPatterns = resolveModifyPattern(
                    modifyPatternHolder[i]);
            for (int j = 0; j < resolvedPatterns.length; j++) {
                //resolvedPatterns[j].dump("Re-adding");
                try {
                    addModifyPattern(resolvedPatterns[j], fileName);
                } catch (Exception e) {
                    if (user == false) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                    throw e;
                }
            }
            //modifyPatternHolder[i].dump();
        }
    }

    //
//***************************************************************************
//
    private static PatternHolder[] resolveModifyPattern(
            PatternHolder toResolve) {
        Vector v = new Vector();
        v.addElement(toResolve);

        for (int i = 0; i < v.size(); i++) {
            PatternHolder ph = (PatternHolder) v.elementAt(i);

            int gender = 0;
//
// Run through each source pattern field looking for standard TGN data types
// to see if they are in the format TnN - wher 'n' is a digit to imply gender
// replacement rather than a specific gender. If found then proceed with
// replication
//
// The n can also be a + or * to indicate gender permutation without recourse
// to gender conflict - useful for last-name routing from first name items.
//
            for (int j = 0; j < ph.sourcesOrg.length; j++) {
                if (ph.sourcesOrg[j].length() == 3 &&
                        ph.sourcesOrg[j].startsWith("#") == false &&
                        "1234567890+*".indexOf(ph.sourcesOrg[j].substring(1, 2))
                                >= 0) {
//
// Collect source key and then see if there is a specific target that will be
// using the source key substitute letter that will also require replacement.
//
                    String source = ph.sourcesOrg[j];
                    int repTarg = -1;
                    for (int k = 0; k < ph.replaces.length; k++) {
                        if (ph.replaces[k].length() == 3 &&
                                ph.replaces[k].startsWith("#") == false &&
                                ph.replaces[k].charAt(1) == source.charAt(1)) {
                            repTarg = k;
                            break;
                        }
                    }
//
// for each gender type - MFU - substitute the TnN for TgN and submit to the
// replication pool
// Exception is that, where a patternholder compileGender code is not
// compatible with the new pattern being created.
//
                    int stop = (ph.sourcesOrg[j].charAt(1) == '+') ? 2 : 3;
                    for (int k = 0; k < stop; k++) {
                        if (ph.compileGender < 2 && k < 2 &&
                                ph.compileGender != k &&
                                "+*".indexOf(ph.sourcesOrg[j].substring(1, 2))
                                        < 0) {
                            continue;
                        }
//
// No gender conflict so continue to create a new perm.
//
                        PatternHolder nph = (PatternHolder) ph.clone();
                        nph.sourcesOrg[j] = source.substring(0, 1) +
                                standardGenderCodes.substring(k, k + 1) +
                                source.substring(2);
                        if (repTarg >= 0) {
                            nph.replaces[repTarg] =
                                    nph.replaces[repTarg].substring(0, 1) +
                                            standardGenderCodes.substring(k, k + 1) +
                                            nph.replaces[repTarg].substring(2);
                        }
//
// Only update gender for conflict if iteration type is neither + or *
//
                        if ("+*".indexOf(ph.sourcesOrg[j].substring(1, 2))
                                < 0) {
                            nph.compileGender = k;
                        }
                        v.addElement(nph);
                    }
                    v.removeElementAt(i);
                    i--;
                    break;
                }
            }
        }

        PatternHolder[] pp = new PatternHolder[v.size()];
        v.copyInto(pp);

        return pp;
    }

    //
//***************************************************************************
//
    private static void addModifyPattern(PatternHolder newPh,
                                         String fileName) throws Exception {
        try {
            validatePatternHolder(newPh, fileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }

        newPh.nextHash = new Hashtable();
        newPh.nextAtLevel = null;
        PatternHolder prevPh = (PatternHolder) modifyHash.get(
                newPh.countryIso);

        if (prevPh == null) {
            prevPh = new PatternHolder(null, null, null, null, null, -1);
            prevPh.countryIso = newPh.countryIso;
            prevPh.nextHash = new Hashtable();
            modifyHash.put(newPh.countryIso, prevPh);
        }
        int sourceDepth = newPh.sources.length - 1;
        //System.err.println("Source depth to scan is "+sourceDepth);

        PatternHolder nextPh;

        for (int i = 0; i < sourceDepth; i++) {
            //System.err.println("Looking for step item "+i+" = "+
            //newPh.sources[i]);
            if (prevPh.nextHash == null) {
                prevPh.nextHash = new Hashtable();
            }

            nextPh = (PatternHolder) prevPh.nextHash.get(newPh.sources[i]);
            //System.err.println("NextPh is "+nextPh);

            if (nextPh == null) {
                nextPh = new PatternHolder(null, null, null, null, null, -2);
                nextPh.nextHash = new Hashtable(); // needed
                prevPh.nextHash.put(newPh.sources[i], nextPh);
            }
            prevPh = nextPh;
        }
//
// prevPh will be parent for newPh - check if last position is populated yet
//
        //System.err.println("TopPh is "+prevPh);
        //System.err.println("TopPh nh "+prevPh.nextHash);
        //System.err.println("newph sources at sourcedepth is "+
        //newPh.sources[sourceDepth]);
        nextPh = (PatternHolder) prevPh.nextHash.get(
                newPh.sources[sourceDepth]);
        if (nextPh == null) {
            prevPh.nextHash.put(newPh.sources[sourceDepth], newPh);
        }
//
// If pattern already exists then check if this new one is a valid variant of
// the current (i.e. data same but patternPos is exclusive
//
        else if (nextPh.sources != null) // already populated !!
        {
            //System.err.println("Line "+newPh.sourceLine+" in file "+
            //fileName+ ": Modify pattern already exists in line "+
            //nextPh.sourceLine);
            //System.err.println("Sources data is "+nextPh.toString(
            //nextPh.sources));
            PatternHolder chainPh = nextPh;

            do {
                if ((chainPh.patternPos & newPh.patternPos) > 0) {
                    chainPh.dump("Existing");
                    newPh.dump("New Add");

                    throw new Exception("Line " + newPh.sourceLine +
                            " in file " + fileName +
                            ": Modify pattern already exists in line " +
                            chainPh.sourceLine);
                }
                chainPh = chainPh.nextAtLevel;
            }
            while (chainPh != null);
//
// New pattern is friendly with existing data positions
//
            newPh.nextAtLevel = nextPh.nextAtLevel;
            nextPh.nextAtLevel = newPh;
        } else {
            newPh.nextHash = nextPh.nextHash;
            prevPh.nextHash.put(newPh.sources[sourceDepth], newPh);
        }
    }

    //
//***************************************************************************
//
// Perform some basic format and pattern tests - and update
//
    private static void validatePatternHolder(PatternHolder newPh,
                                              String fileName) throws Exception {
//
// First is the format string  - decode to count number of input/output tokens
// and number of words that are representing output.
//
        int numFormatWords = newPh.formats.length;
        int numFormatTokensIn = 0;
        int numFormatTokensOut = 0;
        int numFormatTokensOnce = 0;
        int numSpaces;

        for (int i = 0; i < newPh.formats.length; i++) {
            numSpaces = 0;
            numFormatTokensOnce = 0;

            int lenToGo = newPh.formats[i].indexOf(",");
            lenToGo = (lenToGo < 1) ? newPh.formats[i].length() : lenToGo;

            for (int j = 0; j < lenToGo; j++) {
                switch (newPh.formats[i].charAt(j)) {
                    case 'U':
                    case 'L':
                    case 'P':
                    case 'A':
                        numFormatTokensOut++;
                        numFormatTokensOnce++;
                    case 'D':
                        numFormatTokensIn++;
                        break;

                    case 'S':
                        numSpaces++;
                        break;

                    default:
                        throw new Exception("Line " + newPh.sourceLine +
                                " in file " +
                                fileName + ": Format " + newPh.formats[i] +
                                " contains invalid format code: " +
                                newPh.formats[i].substring(j, j + 1));
                }
            }

            if (lenToGo < newPh.formats[i].length()) {
                int numPosits = newPh.formats[i].length() - (lenToGo + 1);
                if (numPosits != numSpaces + numFormatTokensOnce) {
                    throw new Exception("Line " + newPh.sourceLine +
                            " in file " +
                            fileName + ": Format " + newPh.formats[i] +
                            " contains invalid format numbering: " +
                            newPh.formats[i]);
                }
//
// Correct length for data - now check that all numbers from 1 to numPosits
// have been coded in the ',321' trailer
//
                for (int j = 0; j < numPosits; j++) {
                    if (newPh.formats[i].substring(lenToGo).indexOf(
                            "123456789".charAt(j)) < 0) {
                        throw new Exception("Line " + newPh.sourceLine +
                                " in file " +
                                fileName + ": Format " + newPh.formats[i] +
                                " contains missing position code '" +
                                (j + 1) + "': " + newPh.formats[i]);
                    }
                }
            }
        }
//
// Analyse the input source patterns for the true number of format elements
//
        int[] numSources = checkCoders(newPh, true, fileName);
//
//
// Now perform some validity checks - there must be as many patterns as the
// source specifies
//
        if (numSources[1] != numFormatTokensIn) {
            throw new Exception("Format " + newPh.toString(newPh.formats) +
                    " specifies " + numFormatTokensIn +
                    " tokens whilst source" +
                    " pattern " + newPh.toString(newPh.sources) +
                    " specifies " + numSources[1] + " tokens");
        }
//
// Count the number of output words expected using the results coder
//
        int[] numReplaces = checkCoders(newPh, false, fileName);
//
// Check that the number of format 'replaces' count is the same number of
// actual format statements
//
        if (numReplaces[0] != numFormatWords) {
            throw new Exception("Format " + newPh.toString(newPh.formats) +
                    " specifies " + numFormatWords + " output words whilst " +
                    "replacement pattern " + newPh.toString(newPh.replaces) +
                    " specifies " + numReplaces[0] + " fields");
        }
//
// Add replacement tokens to tgnTypes so that the pattern generator knows
// which patterns are worth creating.
//
        for (int i = 0; i < newPh.replaces.length; i++) {
            if (newPh.replaces[i].length() == 3 &&
                    newPh.replaces[i].startsWith("#") == false) {
                //System.err.println("Adding "+newPh.replaces[i]);
                if (newPh.replaces[i].charAt(1) == '*') {
                    String start = newPh.replaces[i].substring(0, 1);
                    String end = newPh.replaces[i].substring(2, 3);
                    tgnTypes.put(start.replace('V', 'T') + "M" + end, "");
                    tgnTypes.put(start.replace('V', 'T') + "F" + end, "");
                    tgnTypes.put(start.replace('V', 'T') + "U" + end, "");
                } else {
                    tgnTypes.put(newPh.replaces[i].replace('V', 'T'), "");
                }
            }
        }
    }

    //
//***************************************************************************
//
//
// Look for entry in modifyPatternHolder array of active ph's
//
    private static void removeModifyEntry(String stt[]) {
        //System.err.println("Stt 1 is "+stt[1]);
//
// Convert stt[4] to a bit code
//
        int patternPos = -1;
        for (int i = 0; i < modifyPositions.length; i++) {
            if (stt[4].equals(modifyPositions[i])) {
                patternPos = modifyPositionCodes[i];
                break;
            }
        }
        for (int i = 0; i < modifyPatternHolder.length; i++) {
            PatternHolder ph = modifyPatternHolder[i];
            //System.err.println("Pattern "+i+" is line "+ph.sourceLine+
            //"  patt="+ ph.toString(ph.sources));

            if (ph.countryIso.equals(stt[0]) &&
                    ph.toString(ph.sourcesOrg).equals(stt[1]) &&
                    ph.toString(ph.replaces).equals(stt[2]) &&
                    ph.toString(ph.formats).equals(stt[3]) &&
                    ph.patternPos == patternPos) {
                modifyPatternHolder[i].nextHash = null;
                modifyPatternHolder[i].nextAtLevel = null;
                modifyPatternHolder[i] =
                        modifyPatternHolder[modifyPatternHolder.length - 1];
                PatternHolder[] newModifyPatternHolder =
                        new PatternHolder[modifyPatternHolder.length - 1];
                System.arraycopy(modifyPatternHolder, 0,
                        newModifyPatternHolder, 0,
                        newModifyPatternHolder.length);
                modifyPatternHolder = newModifyPatternHolder;
                //System.err.println("Removed ph from mph at "+i);
                i--;
            }
        }
        try {
            addModifyPatterns("Remove Action", true);
        } catch (Exception e) {
        }
    }

//
//***************************************************************************
//

    private static boolean insertModifyEntry(String[] stt) {
        PatternHolder newPh = new PatternHolder(
                stt[0], stt[1], stt[2],
                stt[3], stt[4], modifyPatternHolder.length + 1000);
//
// First check that there isn't one of these already
//
        for (int i = 0; i < modifyPatternHolder.length; i++) {
            PatternHolder ph = modifyPatternHolder[i];

            if (ph.countryIso.equals(newPh.countryIso) &&
                    ph.toString(ph.sourcesOrg).equals(
                            newPh.toString(newPh.sourcesOrg)) &&
                    ph.toString(ph.replaces).equals(
                            newPh.toString(newPh.replaces)) &&
                    ph.toString(ph.formats).equals(
                            newPh.toString(newPh.formats))) {
                //System.err.println("Modify Pattern exists - can't add");
                return false;
            }
        }

//
// This is a new entry so add to table
//
        PatternHolder[] newModifyPatternHolder =
                new PatternHolder[modifyPatternHolder.length + 1];
        System.arraycopy(modifyPatternHolder, 0,
                newModifyPatternHolder, 0,
                modifyPatternHolder.length);
        modifyPatternHolder = newModifyPatternHolder;
        modifyPatternHolder[modifyPatternHolder.length - 1] = newPh;
        //newPh.dump("Init add");

        try {
            addModifyPatterns("Rebuild Action", true);
        } catch (Exception e) {
            removeModifyEntry(stt);
            return false;
        }

        return true;
    }

    //
//***************************************************************************
//
// Return is two ints - one that is count of words of data and second is count
// of output words (TGn or AG level) and number of tokens across all words.
//
// So:  TU1		will return 1,1 - 1 pattern with one word
//      TU1 FM2		will return 2.3 - 2 patterns with 3 words
//      TU1&SU2:SU2	will return 1,2 - 1 pattern with two words
//
    private static int[] checkCoders(PatternHolder newPh,
                                     boolean sourceCheck, String fileName) {
        if (sourceCheck) {
            for (int i = 0; i < newPh.sourcesOrg.length; i++) {
                newPh.sources[i] = new String(newPh.sourcesOrg[i]);
            }
        }
        int[] numCoders = new int[2];
        String[] coders = (sourceCheck) ? newPh.sources : newPh.replaces;

        for (int i = 0; i < coders.length; i++) {
            if (validateAlphaGroup(coders[i])) {
                numCoders[1] += (sourceCheck) ? coders[i].length() - 1 : 1;
                continue;
            } else if (sourceCheck == false && coders[i].length() != 3) {
                System.err.println("Line " + newPh.sourceLine + " in file " +
                        fileName + ": Source pattern " + coders[i] +
                        " is not an " +
                        "AlphaGroup and is not a valid TGn format");
                numCoders[0] = -1;
                return numCoders;
            } else if (((coders[i].length() + 1) % 4) != 0) {
                System.err.println("Line " + newPh.sourceLine + " in file " +
                        fileName + ": Source pattern " + coders[i] +
                        " is not an " +
                        "AlphaGroup and is not a valid TGn or TGn&TGn " +
                        "format");
                numCoders[0] = -2;
                return numCoders;
            }
//
// Check that the n of TGn is a digit (which means that all n's in a
// multiple are valid)
//
            if (Character.isDigit(coders[i].charAt(2)) == false) {
                System.err.println("Line " + newPh.sourceLine + " in file " +
                        fileName + ": Source pattern " + coders[i] +
                        " does not have a valid length value in the " +
                        "last character");
                numCoders[0] = -5;
                return numCoders;
            }
//
// Check that extended coder is & type and has final : qualifier TGN
//
            if (coders[i].length() > 3) {
                SuperStringTokenizer str = new SuperStringTokenizer(
                        coders[i], ":", false);
                String[] stt = str.getStringList();
                if (stt.length != 2) {
                    System.err.println("Line " + newPh.sourceLine +
                            " in file " + fileName + ": Source pattern " +
                            "is & type which requires TGN&TGN:TGN format");
                    numCoders[0] = -3;
                    return numCoders;
                }

                for (int j = 3; j < stt[0].length(); j += 4) {
                    if (stt[0].charAt(j) != '&') {
                        System.err.println("Line " + newPh.sourceLine +
                                " in file " + fileName + ": Source pattern " +
                                stt[0] + " is multiple but does not " +
                                "contain required '&' join character");
                        numCoders[0] = -3;
                        return numCoders;
                    }
/*
			if (stt[0].charAt(j-1) != stt[0].charAt(j+3))
			{
			    System.err.println("Line "+newPh.sourceLine+
				" in file "+ fileName+ ": Source pattern "+
				stt[0]+
				" is multiple but specifies different source "+
				"data length values");
			    numCoders[0] = -4;
			    return numCoders;
			}
*/
                }
//
// Now check that target is 3 chars and is in source group.
//
                if (stt[1].length() != 3 ||
                        (stt[0].indexOf(stt[1]) % 4) != 0) {
                    System.err.println("Line " + newPh.sourceLine +
                            " in file " + fileName + ": Source selection " +
                            "pattern " + stt[1] + " not found in source " + stt[0]);
                    numCoders[0] = -3;
                    return numCoders;
                }
                newPh.sourcesPull[i] = stt[0].indexOf(stt[1]) / 4;
                coders[i] = stt[0];
                numCoders[1] += Integer.parseInt(stt[1].substring(2, 3));
            } else {
                numCoders[1] += Integer.parseInt(coders[i].substring(2, 3));
            }
        }
        numCoders[0] = coders.length;
        return numCoders;
    }

    //
//***************************************************************************
//
    private static boolean validateTypeGender(String typeGender) {
        if (typeGender.length() < 2) {
            return false;
        }
        typeGender = typeGender.toUpperCase();
        if (typeCodes.indexOf(typeGender.substring(0, 1)) < 0) {
            return false;
        }
        if (standardGenderCodes.indexOf(typeGender.substring(1, 2)) < 0) {
            return false;
        }
        return true;
    }

    //
//***************************************************************************
//
    private static boolean validateAlphaGroup(String testGroup) {
        if (testGroup.length() < 2 || testGroup.startsWith("#") == false) {
            return false;
        }
        for (int i = 1; i < testGroup.length(); i++) {
            if (alphaGroup.indexOf(testGroup.substring(i, i + 1)) < 0) {
                return false;
            }
        }
        return true;
    }

    //
//***************************************************************************
//
    private static String evaluateOneNqs(boolean hold, boolean zed,
                                         int resGen, int resHidden, String defGen) {
        if (hold) return "9";
        if (zed) return "8";
        if (defGen.charAt(0) == 'C') return "6";
//
// If the provided gender is overriding the discovered gender then this is
// quite serious - but not too much
//
        if (standardGenderCodes.indexOf(defGen.substring(0, 1)) >= 0 &&
                resGen == 0) {
            return "5";
        }
//
// If the provided gender is M, F or C and the discovered gender does not
// match then this is also dodgy - user can mask using gender ignore hyphen
//
        if ("MFC".indexOf(defGen.substring(0, 1)) >= 0 &&
                "C-MF".charAt(resGen) != defGen.charAt(0)) {
            return "4";
        }
//
// If the provided gender is there to force the creation of a multi pattern
// containing unknown gender entries and the gender could not be found in the
// data then this pattern is weak - the user could tune the data more
//
        if (defGen.charAt(0) == 'U' && resGen == 1) {
            return "3";
        }
//
// If there is a gender conflict against masked gender items then this is a
// little risky too
//
        if (resHidden == 0) {
            return "2";
        }
        return "1";
    }

    //
//***************************************************************************
//
    private static BufferedReader openInputFile(String fileName) {
        BufferedReader br = null;
        try {
            //br = new BufferedReader(new FileReader(fileName), 1000000);
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"), 1000000);
        } catch (FileNotFoundException enofile) {
            System.err.println("Unable to open table " + fileName);
            return null;
        } catch (UnsupportedEncodingException uce) {
            System.err.println("Can't support UTF-8 encoding");
            return null;
        }
        return br;
    }

    //
//***************************************************************************
//
    private static BufferedReader closeInputFile(BufferedReader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException ioe) {
            }
        }
        return null;
    }

    //
//***************************************************************************
//
    private static BufferedWriter openOutputFile(String fileName) {
        BufferedWriter bw = null;
        try {
            //bw = new BufferedWriter(new FileWriter(fileName), 1000000);
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"), 1000000);
        } catch (FileNotFoundException enofile) {
            System.err.println("Unable to open table " + fileName);
            return null;
        } catch (IOException ioe) {
            System.err.println("Unable to open table " + fileName);
            return null;
        }
        return bw;
    }

    //
//***************************************************************************
//
    private static BufferedWriter closeOutputFile(BufferedWriter bw) {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException ioe) {
            }
        }
        return null;
    }

    //
//***************************************************************************
//
    public static void printStats(PrintWriter logWriter) {
        VectorPrintWriter vp = new VectorPrintWriter(logWriter);
        printWordStats(vp);
        printPatternStats(vp);
    }

    public static void printWordStats(VectorPrintWriter logWriter) {
        Enumeration enumer = missedWords.keys();
        Vector v = new Vector(1000, 1000);
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            Integer val = (Integer) missedWords.get(key);
            v.add(Conversion.toPaddedString(val.intValue(), 10) + "   " +
                    key);
        }
        missedWords = new Hashtable();
        if (v.size() > 0) {
            String[] str = new String[v.size()];
            v.copyInto(str);
            new StringSort(str, false);
            logWriter.println("\n\nFrequency of top 250 missed words\n");
            for (int i = 0; i < 250 && i < str.length; i++) {
                logWriter.println("    " + str[i]);
            }
            logWriter.println("\n\nWord Definition for name table:\n");
            for (int i = 0; i < 250 && i < str.length; i++) {
                try {
                    String phrase = str[i].substring(13);
                    if (phrase.length() > 4) {
                        phrase = phrase.substring(4, 5).toUpperCase() +
                                phrase.substring(5).toLowerCase();
                    } else if (phrase.length() == 4) {
                        phrase = phrase.substring(4).toUpperCase();
                    } else {
                        phrase += " - ERROR - too short";
                    }
                    logWriter.println("D\t" + str[i].substring(17) + "\t" +
                            phrase + "\t??");
                } catch (Exception e) {
                    logWriter.println("EXCEPTION formatting >" + str[i] +
                            "<  Message: " + e.getMessage());
                }
            }
        }
    }

    public static void printPatternStats(VectorPrintWriter logWriter) {
        Enumeration enumer = missedPatterns.keys();
        Vector v = new Vector(1000, 1000);
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            Hashtable hash = (Hashtable) missedPatterns.get(key);
            Integer val = (Integer) hash.get("PTNCOUNT");
            v.addElement(Conversion.toPaddedString(val.intValue(), 10) + key);
        }

        if (v.size() > 0) {
            String[] str = new String[v.size()];
            v.copyInto(str);
            new StringSort(str, false);
            logWriter.println("\n\nFrequency of missed patterns\n");

            for (int i = 0; i < str.length; i++) {
                Hashtable h = (Hashtable) missedPatterns.get(
                        str[i].substring(10));
                v = new Vector(10, 10);

                enumer = h.keys();
                while (enumer.hasMoreElements()) {
                    String key = (String) enumer.nextElement();
                    if (key.equals("PTNCOUNT") == false) {
                        v.add(key + "        " + h.get(key));
                    }
                }
                String[] str2 = new String[v.size()];
                v.copyInto(str2);
                new StringSort(str2, false);
                logWriter.println("\n    Pattern " + str[i].substring(10) +
                        "  (" + str[i].substring(0, 10).trim() + ")\n");
                for (int j = 0; j < str2.length; j++) {
                    logWriter.println("        " + str2[j]);
                }
            }
        }
    }

    //
//***************************************************************************
//
    public final void setDebug(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    //
//***************************************************************************
//
    public final boolean formatName(COptimaContact contact) {
        debugText.setLength(0);
        missedWordVector = new Vector(10, 10);
        vtfmlsCode = new int[6];

        for (int i = 0; i < resDecomp.length; i++) {
            resDecomp[i][0] = "";
            resDecomp[i][1] = "";
            resDecomp[i][2] = "";
        }

        try {
            return fmtName(contact);
        } catch (Exception e) {
            return setParseValues(contact, "E", dummyTitleGroup, "", "", "",
                    "", "", "", "", "");
        }
    }

    public final boolean fmtName(COptimaContact contact) {
        orgName = contact.getField(OFT.FullName).trim();

        if (orgName.length() == 0) {
            return setParseValues(contact, "B", dummyTitleGroup, "", "", "",
                    "", "", "", "", "");
        }

        int nbPos = 0;

        if (logWriter != null) {
            debugText.append("-------------------------------------------" +
                    "\nProcessing " + orgName + "\n");
        }

        orgName = removeMiscChars(orgName);
        orgName = spaceLongUnicodeChars(orgName);

        isoList = buildIsoList(contact.getField(OFT.CountryISO));

        int i = 0;
        int j = 0;

        while (i < orgName.length()) {
//
// J becomes starting postion of next string of characters
//
            j = i;
            //System.err.println("Starting point is "+orgName.substring(i));

            for (; i < orgName.length() &&
                    Character.isWhitespace(orgName.charAt(i)) == false &&
                    orgName.charAt(i) != ','; i++)
                ;

            if (i == j && orgName.charAt(j) == ',') {
                i++;
            }

            nameBits[nbPos] = trimLeadTrailPunct(orgName.substring(j, i));
            //System.err.println("Returned "+nameBits[nbPos]+ " from "+
            //orgName.substring(j, i));
//
// Handle word redefines as each word is found and copied
//
            String[] redefKey = getWordList(nameBits[nbPos]);

            if (nameBits[nbPos].length() > 0 && redefKey == null) {
                String nb = new String(nameBits[nbPos]);

                if (testForEmail(nb) == false) {
                    do {
                        boolean wasDot = false;
                        while (nb.endsWith(".")) {
                            nb = nb.substring(0, nb.length() - 1);
                            wasDot = true;
                        }

                        int dotPos = nb.lastIndexOf(".");
                        if (dotPos < 0) {
                            if (wasDot) {
                                nb += ".";
                            }
                            break;
                        }
                        nb = nb.substring(0, dotPos + 1);
                        i = j + nb.length();

                        redefKey = getWordList(nb);
                    }
                    while (redefKey == null);
                }

                nameBits[nbPos] = nb;
            }

            if (redefKey != null && redefKey[0].charAt(2) == 'R') {
                if (logWriter != null) {
                    debugText.append("Recoded " + nameBits[nbPos] + " to" +
                            redefKey[0].substring(1) + "\n");
                }
                if (redefKey[0].length() > 6) {
                    SuperStringTokenizer red = new SuperStringTokenizer(
                            redefKey[0].substring(6), " ", false);
                    String[] rsl = red.getStringList();
                    for (int k = 0; k < rsl.length; k++) {
                        nameBits[nbPos++] = rsl[k];
                    }
                }
                nbPos--;
            }

            if (nbPos >= 0) {
                nbPos += (nameBits[nbPos] != null &&
                        nameBits[nbPos].length() > 0) ? 1 : 0;
            } else {
                nbPos = 0;
            }

            if (orgName.charAt(j) == ',') {
                for (; i < orgName.length() &&
                        (Character.isWhitespace(orgName.charAt(i)) ||
                                orgName.charAt(i) == ','); i++)
                    ;
            } else {
                for (; i < orgName.length() &&
                        Character.isWhitespace(orgName.charAt(i)); i++)
                    ;
            }
        }

//
// If no data in output components then nothing to parse
//
        if (nbPos < 1) {
            return setParseValues(contact, "U", dummyTitleGroup, "", "", "",
                    "", "", "", "", "");
        }
//
// If last nameBits entry is a comma then remove as it's meaningless
//
        if (nameBits[nbPos - 1].equals(",")) {
            nbPos--;
        }

//
// Now, for every element see if theres a leadString entry - iterate until
// the entry terminates - when it does then, using the found elements plus
// the next one - look the word up in the lookupPhrase table. If found then we
// have an answer - if not found then trim off the end word and retry until we
// do find the answer
//

        Vector pe = new Vector(10, 10);
        Vector ipe = new Vector(10, 10);

        for (i = 0; i < nbPos; i++) {
            String leadStr = "";
            for (j = i; j < nbPos; j++) {
                leadStr += " " + nameBits[j];
                if (getLeadStrings(
                        leadStr.substring(1).toUpperCase()) == false) {
                    break;
                }
            }

            j = (j >= nbPos) ? nbPos - 1 : j;  // fallen off end?
//
// If j is more than I then there is a leadString situation. We need to work
// backwards down the chain to determine where the first fully qualified phrase
// actually terminates.
//
            leadStr = "";
            for (; j >= i; j--) {
                leadStr = "";
                for (int k = i; k <= j; k++) {
                    leadStr += " " + nameBits[k];
                }
                if (getWordList(leadStr.substring(1)) != null) {
                    break;
                }
            }
            if (j < i) // didn't find word - so alpha or something
            {
                j++;
            }
            if (logWriter != null && leadStr.lastIndexOf(" ") > 1) {
                debugText.append("Found multi-word string " + leadStr + "\n");
            }
//
// lookupPhrase returns an array of info for resBits:
//
// Element 0:	- list of type codes like TU1,FM2 C'A etc.
//	   1:	- Replacement phrase for first item in element 0
//	   2:   - Replacement phrase for second item in element 0 etc. etc.
//
            ParsedEntity ph = new ParsedEntity(leadStr.substring(1));
            if (ph.tgnGroup.length() == 3 &&
                    "EDJ".indexOf(ph.tgnGroup.substring(0, 1)) >= 0) {
                ipe.addElement(ph);
            } else {
                pe.addElement(ph);
            }

            if (logWriter != null) {
                debugText.append("Added " + ph.orgToken +
                        " - lookup value " + ph.tgnGroup +
                        " - recode value 1 >" + ph.tgnTokens[0] + "<\n");
            }
//
// push I to last word of current phrase so ready for next loop increment into
// first word of next phrase
//
            i = j;

        }

        repairInput(pe, ipe);
//
// Next phase is to modify discovered pattern using the sub-pattern recoder
// module: modifyPattern which creates a newParsedEntities block from the
// parsedEntities initial lookup
//
// Args are:	Country Iso
//
        modifyPattern(ipe);

        finNspos = 0;

/*
	    midPattern = "";
	    for (i = 0; i < parsedEntities.length; i++)
	    {
		midPattern += " "+parsedEntities[i].tgnGroup;
	    }
*/

        modPattern = "";

        for (i = 0; i < newParsedEntities.length; i++) {
            modPattern += " " +
                    reducePattern(newParsedEntities[i].tgnGroup);
        }
        modPattern2 = new String(modPattern);

        if (processPattern(contact, ipe, 0, false)) {
            //System.err.println("Done !!!!");
            return true;
        }
        //System.err.println("Not Done");
//
// If failed then remove lead Title data and retry
//
        //System.err.println("Missed "+modPattern);
        String redPat;
        String failPat = new String(modPattern);
        modPattern = "";
        int modGender = 2;
        int modStart = 0;


        for (i = 0; i < newParsedEntities.length; i++) {
            redPat = reducePattern(newParsedEntities[i].tgnGroup);

            if (redPat.length() != 3 || redPat.startsWith("T") == false ||
                    modPattern.length() > 0) {
                modPattern += " " + redPat;
            } else {
                modStart = i + 1;

                switch (modGender) {
                    case 0:  // M
                        modGender = (redPat.charAt(1) == 'F') ? -1 :
                                modGender;
                        break;

                    case 1:  // F
                        modGender = (redPat.charAt(1) == 'M') ? -1 :
                                modGender;
                        break;

                    case 2:  // U
                        modGender = standardGenderCodes.indexOf(
                                redPat.charAt(1));
                        break;

                    default: // anything else (inc -1)
                        modGender = -1;
                }
                vtfmlsCode[1] = 7;
                resDecomp[1][0] = "title";
                resDecomp[1][1] += " " + newParsedEntities[i].tgnTokens[0];
                resDecomp[1][1] = resDecomp[1][1].trim();
                resDecomp[1][2] += "T0* ";
            }
        }
        if (modGender >= 0 && modStart > 0 && modStart < i) {
            //System.err.println("Missed "+failPat);
            //System.err.print("Processing with modstart - ");
            boolean res = processPattern(contact, ipe, modStart, true);

            String gender = contact.getField(OFT.Gender).trim();
            int gcode = standardGenderCodes.indexOf(gender);

            if (res && gcode >= 0) {
                if (gcode == 2 && modGender < 2) {
                    gcode = modGender;
                } else if (gcode <= 2 && modGender == 2) {
                    gcode = gcode;
                } else if (gcode < 2 && gcode != modGender) {
                    res = false;
                    //System.err.print("Gender conflict! - ");
                }
            }

            if (res) {
                //System.err.println("Success!");
                contact.setField(OFT.Gender,
                        standardGenderCodes.substring(gcode, gcode + 1));
            } else {
                modPattern = modPattern2;
                vtfmlsCode[1] = 0;
                resDecomp[1][0] = "";
                resDecomp[1][1] = "";
                resDecomp[1][2] = "";
                processPattern(contact, ipe, 0, true);
                //System.err.println("Failed");
            }
        } else {
            modPattern = modPattern2;
            processPattern(contact, ipe, 0, true);
        }
        return true;
    }

    //
// Method to look up a pattern guide and return formatted answer
//
    private boolean processPattern(COptimaContact contact, Vector ipe,
                                   int startPoint, boolean log) {
//
//
// Now lookup modPattern - first using country iso code and if this fails chain
// lookup code until exhausted - then use ROW code
//
        patcode = "";
        String patres = null;
        patternIso = isoList[0];
        retPattern = "";
        int ngoes = 0;
        String nqs;
        int i;

//
// Swap vocational title for normal title for standard pattern resolution
//
        if (modPattern.length() > 1) {
            for (i = 0; i < isoList.length && patres == null; i++) {
                patternIso = isoList[i];
                //System.err.println("Getting >"+patternIso+"<+>"+
                //modPattern+ "<");
                patres = (String) patternStrings.get(patternIso + modPattern);
                //System.err.println("Returned patres "+patres);
            }
        }


        if (patres == null) {
            if (log == false) {
                //System.err.println("Missing Pattern: "+modPattern+
                //" for name "+ orgName+"\n");
                return false;
            } else if (logWriter != null) {
                debugText.append("Missing Pattern: " + modPattern +
                        " for name " + orgName + "\n");
            }
//
// Calculate whether pattern infers Hold status - if so, don't record it
//
            if (modPattern.indexOf(" H") >= 0) {
                nqs = "9 ";
            } else if (modPattern.indexOf(" Z") >= 0) {
                nqs = "8 ";
            } else {
                nqs = "U ";

                Hashtable v = (Hashtable) missedPatterns.get(modPattern);
                if (v == null) {
                    v = new Hashtable();
                    v.put("PTNCOUNT", new Integer(0));
                    missedPatterns.put(modPattern, v);
                }
                Integer ptnCnt = (Integer) v.get("PTNCOUNT");

                v.put("PTNCOUNT", new Integer(ptnCnt.intValue() + 1));

                String m = (String) v.get(isoList[0]);
                if (m == null) {
                    v.put(isoList[0], orgName);
                }
            }

//
// Always record missed words
//
            for (i = 0; i < missedWordVector.size(); i++) {
                String phrase = (String) missedWordVector.elementAt(i);
                phrase = isoList[0] + " " + phrase;
                Integer mw = (Integer) missedWords.get(phrase.toUpperCase());
                if (mw == null) {
                    mw = new Integer(0);
                }
                missedWords.put(phrase.toUpperCase(),
                        new Integer(mw.intValue() + 1));
            }

            patcode = (nqs.charAt(0) == '9') ? "P-Hold" : "";
            patcode = (nqs.charAt(0) == '8') ? "P-Void" : patcode;
            setParseValues(contact, nqs + modPattern,
                    dummyTitleGroup, "", "", "", "", "", "", "", patcode);
            //System.err.println("Missing Pattern 2: "+modPattern+
            //" for name "+ orgName+"\n");
            return false;
        }
        if (logWriter != null) {
            debugText.append("Pattern found for " + orgName +
                    " - outcode is " + patres + "\n");
        }

        int inPos = 0;
        //int out;

        for (int out = startPoint; out < newParsedEntities.length; out++) {
            String thisPat = patres.substring(inPos, inPos + 3);
            if (patres.charAt(inPos) == '[') {
                int indclo = patres.indexOf("]", inPos);
                thisPat = patres.substring(inPos + 1, indclo);
                inPos = indclo + 1;
            } else {
                inPos += 3;
            }
            retPattern += " " + thisPat;

            int nameCode = (thisPat.length() > 3) ? 5 : 0;

            while (thisPat.length() > 0) {
                if (logWriter != null) {
                    debugText.append("Processing outcode " + thisPat + "\n");
                }
                int sourceItem = 1;
                try {
                    sourceItem =
                            Integer.parseInt(thisPat.substring(1, 2));
                } catch (NumberFormatException nfe) {
                    System.err.println("NE with " + thisPat + " at char 1 in " +
                            patres);
                    System.err.println("OrgName is " + orgName);
                    System.err.println("startPoint is " + startPoint);
                    System.err.println("endPoint is " +
                            newParsedEntities.length);
                    System.err.println("currPoint is " + out);
                    Thread.dumpStack();
                    sourceItem = 1;
                }
                String outString =
                        newParsedEntities[out].tgnTokens[sourceItem - 1];
                if (thisPat.charAt(2) != '*') {
                    int wordNo = Integer.parseInt(thisPat.substring(2, 3));

                    SuperStringTokenizer wrd = new SuperStringTokenizer(
                            outString, " ", false);
                    String[] wrds = wrd.getStringList();
                    outString = wrds[wordNo - 1];
                    if (logWriter != null) {
                        debugText.append("Split outstring is now " +
                                outString + "\n");
                    }
                    nameCode = (nameCode == 2) ? 4 : nameCode; // partial
                }
//
// not partial - so if namecode is unset then work out what it is to be
//
                else if (nameCode == 0) {
                    if (newParsedEntities[out].tgnGroup.startsWith("#")) {
                        nameCode = 1;
                    } else if (outString.equalsIgnoreCase(
                            newParsedEntities[out].orgToken)) {
                        nameCode = 2;
                    } else {
                        nameCode = 3;
                    }
                }

                switch (thisPat.charAt(0)) {
                    case 'V':  // vocational title
                        vtfmlsCode[0] = (vtfmlsCode[0] == 0) ?
                                nameCode : vtfmlsCode[0];
                        resDecomp[0][0] = "voc_title";
                        resDecomp[0][1] += " " + outString;
                        resDecomp[0][1] = resDecomp[0][1].trim();
                        resDecomp[0][2] += thisPat + " ";

                    case 'T':  // single title
                    case 'U':  // plural title
                        vtfmlsCode[1] = (vtfmlsCode[1] == 0) ?
                                nameCode : vtfmlsCode[1];
                        resDecomp[1][0] = "title";
                        resDecomp[1][1] += " " + outString;
                        resDecomp[1][1] = resDecomp[1][1].trim();
                        resDecomp[1][2] += thisPat + " ";
                        break;

                    case 'F':
                        vtfmlsCode[2] = (vtfmlsCode[2] == 0) ?
                                nameCode : vtfmlsCode[2];
                        resDecomp[2][0] = "first";
                        resDecomp[2][1] += " " + outString;
                        resDecomp[2][1] = resDecomp[2][1].trim();
                        resDecomp[2][2] += thisPat + " ";
                        break;

                    case 'M':
                        vtfmlsCode[3] = (vtfmlsCode[3] == 0) ?
                                nameCode : vtfmlsCode[3];
                        resDecomp[3][0] = "middle";
                        resDecomp[3][1] += " " + outString;
                        resDecomp[3][1] = resDecomp[3][1].trim();
                        resDecomp[3][2] += thisPat + " ";
                        break;

                    case 'L':
                        vtfmlsCode[4] = (vtfmlsCode[4] == 0) ?
                                nameCode : vtfmlsCode[4];
                        resDecomp[4][0] = "last";
                        resDecomp[4][1] += " " + outString;
                        resDecomp[4][1] = resDecomp[4][1].trim();
                        resDecomp[4][2] += thisPat + " ";
                        break;

                    case 'S':
                        vtfmlsCode[5] = (vtfmlsCode[5] == 0) ?
                                nameCode : vtfmlsCode[5];
                        resDecomp[5][0] = "suffix";
                        resDecomp[5][1] += " " + outString;
                        resDecomp[5][1] = resDecomp[5][1].trim();
                        resDecomp[5][2] += thisPat + " ";
                        break;

                    case 'D': // Drop
                        resDecomp[6][0] = "drop";
                        resDecomp[6][1] += " " + outString;
                        resDecomp[6][1] = resDecomp[6][1].trim();
                        resDecomp[6][2] += thisPat + " ";
                        break;

                    case 'E': // Email
                        resDecomp[7][0] = "email";
                        resDecomp[7][1] += " " + outString;
                        resDecomp[7][1] = resDecomp[7][1].trim();
                        resDecomp[7][2] += thisPat + " ";
                        break;

                    case 'J': // Job Title
                        resDecomp[8][0] = "jobtitle";
                        resDecomp[8][1] += " " + outString;
                        resDecomp[8][1] = resDecomp[8][1].trim();
                        resDecomp[8][2] += thisPat + " ";
                        break;

                    default:
                        System.err.println("Don't know where to put " +
                                "output string data for pattern code " +
                                thisPat.substring(0, 3));
                        System.exit(1);
                }
                thisPat = thisPat.substring(3);
            }
        }
//
// deal with anything in ipe (ignored parsed entities)
//
        for (i = 0; i < ipe.size(); i++) {
            ParsedEntity ph = (ParsedEntity) ipe.elementAt(i);
            switch (ph.tgnGroup.charAt(0)) {
                case 'D':
                    resDecomp[6][0] = "drop";
                    resDecomp[6][1] += " " + ph.orgToken.trim();
                    resDecomp[6][1] = resDecomp[6][1].trim();
                    resDecomp[6][2] += ph.tgnGroup + " ";
                    break;

                case 'E': // Email
                    resDecomp[7][0] = "email";
                    resDecomp[7][1] += " " + ph.tgnTokens[0].trim();
                    resDecomp[7][1] = resDecomp[7][1].trim();
                    resDecomp[7][2] += ph.tgnGroup + " ";
                    break;

                case 'J': // Job Title
                    resDecomp[8][0] = "jobtitle";
                    resDecomp[8][1] += " " + ph.tgnTokens[0].trim();
                    resDecomp[8][1] = resDecomp[8][1].trim();
                    resDecomp[8][2] += ph.tgnGroup + " ";
                    break;

                default:
                    System.err.println("Don't know where to put " +
                            "output string data for ignored pattern " +
                            ph.tgnGroup);
                    System.exit(1);
            }
        }
        resDecomp[1][1] = repairOutput(resDecomp[1][1], false);
        resDecomp[2][1] = repairOutput(resDecomp[2][1], true);
        resDecomp[3][1] = repairOutput(resDecomp[3][1], false);
        resDecomp[4][1] = repairOutput(resDecomp[4][1], false);
        resDecomp[5][1] = repairOutput(resDecomp[5][1], false);
        String gender = patres.substring(inPos + 1, inPos + 2);

        for (i = 0; i < resDecomp.length; i++) {
            finNspos += (resDecomp[i][0].length() > 0) ? 1 : 0;
        }

        String titleGender = patres.substring(inPos + 2, inPos + 3);
        nqs = patres.substring(inPos + 3, inPos + 4);
        patcode = ((startPoint > 0) ? "M" : "") +
                patres.substring(inPos + 4) + "-" + patternIso;
//
// titleGroup is array of title (defaulted), salutation and fullname recons
//
        String[] titleGroup = setupTitleGroup(nqs, resDecomp[1][1],
                resDecomp[2][1], resDecomp[3][1], resDecomp[4][1],
                resDecomp[5][1], gender, titleGender, isoList[0]);

        return setParseValues(contact, nqs, titleGroup, resDecomp[2][1],
                resDecomp[3][1], resDecomp[4][1], resDecomp[5][1],
                gender, resDecomp[7][1], resDecomp[8][1], patcode);
    }

    //
//***************************************************************************
//
    private String reducePattern(String pattern) {
        pattern = pattern.replace('V', 'T');
        if (pattern.length() == 3 && pattern.charAt(0) != '#') {
            int typeInd = typeCodes.indexOf(pattern.substring(0, 1));
            if (typeInd >= 0) {
                int typeLen = Integer.parseInt(pattern.substring(2));
                if (typeLen > patternTgnMax[typeInd]) {
                    pattern = pattern.substring(0, 2) +
                            String.valueOf(patternTgnMax[typeInd]);
                }
            }
        }
        return pattern;
    }

    //
//***************************************************************************
//
// This method fixes a number of common problems with parsed input data:
//
// 1) Removes duplicated title data (leaves first asis and removes remainder)
//
    private void repairInput(Vector pe, Vector ipe) {
        for (int i = 0; i < pe.size() - 1; i++) {
            ParsedEntity pi = (ParsedEntity) pe.elementAt(i);

            if (pi.tgnGroup.length() == 3 &&
                    (pi.tgnGroup.charAt(0) == 'T' ||
                            pi.tgnGroup.charAt(0) == 'V')) {
                for (int j = i + 1; j < pe.size(); j++) {
                    ParsedEntity pj = (ParsedEntity) pe.elementAt(j);
                    if (pj.tgnGroup.charAt(0) == pi.tgnGroup.charAt(0) &&
                            pj.getFormattedOutput().equals(
                                    pi.getFormattedOutput())) {
                        pj.tgnGroup = "D" + pj.tgnGroup.substring(1);
                        ipe.addElement(pj);
                        pe.removeElementAt(j);
                        j--;

                    }
                }
            }
        }
        parsedEntities = new ParsedEntity[pe.size()];
        pe.copyInto(parsedEntities);
    }

    //
//***************************************************************************
//
    private String repairOutput(String token, boolean doIfCJK) {
        int ind;
        if (token.indexOf("<") >= 0) {
            while ((ind = token.indexOf("< ")) >= 0) {
                token = token.substring(0, ind) + token.substring(ind + 2);
            }
            if ((ind = token.indexOf("<")) >= 0) {
                token = token.substring(0, ind) + token.substring(ind + 1);
            }
        }
        while (token.endsWith("-")) {
            token = token.substring(0, token.length() - 1);
        }
        while (token.startsWith("-")) {
            token = token.substring(1);
        }
        while ((ind = token.indexOf("::")) >= 0) {
            token = token.substring(0, ind) + token.substring(ind + 2);
        }
        while ((ind = token.indexOf("--")) >= 0) {
            token = token.substring(0, ind) + token.substring(ind + 1);
        }

        if (doIfCJK) {
            int i = 0;
            while (i < token.length() - 2) {
                if (token.charAt(i) >= 4096 && token.charAt(i + 2) >= 4096 &&
                        Character.isWhitespace(token.charAt(i + 1))) {
                    token = token.substring(0, i + 1) + token.substring(i + 2);
                }
                i++;
            }
        }
        return token;
    }

    //
//***************************************************************************
//
    private String[] buildIsoList(String isoCode) {
        Vector countryList = new Vector(4, 2);
        String cjkCode = (String) countrySpecials.get(isoCode +
                ((isCJK) ? "_TRUE" : "_FALSE"));
//
// Locate the patternHolder chain for this country
//
        while (isoCode != null && isoCode.equals("ROW") == false) {
            countryList.add(isoCode);
            isoCode = (String) countryStrings.get(isoCode);
        }

        countryList.add("ROW");

        if (cjkCode != null) {
            //System.err.println("Interpreting as "+cjkCode);
            countryList.insertElementAt(cjkCode, 1);
        }

        if (isCJK) {
            countryList.add("R2L");
        } else {
            countryList.add("L2R");
        }

        String[] ret = new String[countryList.size()];
        countryList.copyInto(ret);
        return ret;
    }

    //
//***************************************************************************
//
    private String[] getWordList(String phrase) {
        String[] wordList = _getWordList(phrase);
//
// test for dot - and try without
//
        if (wordList == null) {
            if (phrase.endsWith(".")) {
                wordList = _getWordList(phrase.substring(0,
                        phrase.length() - 1));
            } else {
                wordList = _getWordList(phrase + ".");
            }
        }
//
// test for hyphenation and try either side
//
        if (wordList == null) {
            int hyp = phrase.indexOf("-");
            if (hyp > 0 && hyp < phrase.length() - 1 &&
                    hyp == phrase.lastIndexOf("-") &&
                    isLetter(phrase.charAt(hyp - 1)) &&
                    isLetter(phrase.charAt(hyp + 1))) {
                String[] side1 = _getWordList(phrase.substring(0, hyp));
                side1 = modifySide(side1);
                if (side1 != null && side1.length == 1) {
                    String[] side2 = _getWordList(phrase.substring(hyp + 1));
                    side2 = modifySide(side2);
                    if (side2 != null && side2.length == 1) {
                        if (side1[0].substring(0, 5).equals(
                                side2[0].substring(0, 5))) {
                            wordList = catHyphenatedPhrases(side1, side2,
                                    side1[0].substring(0, 4));
                        } else if (side1[0].substring(0, 3).equals(
                                side2[0].substring(0, 3)) &&
                                (side1[0].charAt(3) == 'U' ||
                                        side2[0].charAt(3) == 'U')) {
                            wordList = catHyphenatedPhrases(side1, side2,
                                    side1[0].substring(0, 4));
                        } else if (side1[0].startsWith("D IU1")) {
                            wordList = catHyphenatedPhrases(side1, side2,
                                    side2[0].substring(0, 3) +
                                            side1[0].substring(3, 4));
                        } else if (side2[0].startsWith("D IU1")) {
                            wordList = catHyphenatedPhrases(side1, side2,
                                    side1[0].substring(0, 4));
                        }
                    }
                }
            }
//
// try removing lead/trail punctuation
//
            if (wordList == null) {
                while (phrase.length() > 0 &&
                        "/,.?@".indexOf(phrase.substring(0, 1)) >= 0) {
                    phrase = phrase.substring(1);
                }
                while (phrase.length() > 0 && "-/,.?@".indexOf(
                        phrase.substring(phrase.length() - 1)) >= 0) {
                    phrase = phrase.substring(0, phrase.length() - 1);
                }
                wordList = _getWordList(phrase);
                if (wordList == null) {
                    wordList = _getWordList(phrase + ".");
                }
            }
        }
        return wordList;
    }

    //
// Method to test/modify a return 'side' entity to the simple IU1 form if it
// is an IU1 type critter
//
    private String[] modifySide(String[] side) {
        if (side != null && side.length > 1) {
            for (int i = 0; i < side.length; i++) {
                if (side[i].startsWith("D IU1")) {
                    String keep = side[i];
                    side = new String[1];
                    side[0] = keep;
                    break;
                }
            }
        }
        return side;
    }

    //
//***************************************************************************
//
    private String[] catHyphenatedPhrases(String[] side1, String[] side2,
                                          String composeKey) {
        String[] wordList = new String[1];

        int len = Integer.parseInt(side1[0].substring(4, 5)) +
                Integer.parseInt(side2[0].substring(4, 5));

        if (side1[0].charAt(2) == 'I' && side1[0].charAt(7) == '.') {
            wordList[0] = composeKey + String.valueOf(len) + " " +
                    side1[0].substring(6, 7) + "-" + side2[0].substring(6);
        } else {
            wordList[0] = composeKey + String.valueOf(len) + " " +
                    side1[0].substring(6) + "-" + side2[0].substring(6);
        }

        return wordList;
    }

    //
//***************************************************************************
//
    private String[] _getWordList(String phrase) {
        recurseLevel = 0;
        return __getWordList(phrase, false);
    }

    private String[] __getWordList(String phrase, boolean firstHitStop) {
        recurseLevel++;
        Hashtable wsStrings = new Hashtable();

        //for (int i = isoList.length - 1; i >= 0; i--)
        boolean quickStop = false;

        for (int i = 0; i < isoList.length; i++) {
            String[] ws = (String[]) wordStrings.get(isoList[i] + "  " +
                    phrase.toUpperCase());
            if (ws != null) {
//
// If redef then invoke redef lookup
//
                //System.err.println("WS 0: "+ws[0]);
                if (ws.length == 1 && ws[0].charAt(2) == 'R') {
                    if (recurseLevel < 4 && firstHitStop == false) {
                        return __getWordList(ws[0].substring(6),
                                (ws[0].charAt(3) != 'U'));
                    }
                    wsStrings.clear();
                    break;
                }

                for (int j = 0; j < ws.length; j++) {
                    if (ws[j].endsWith("::")) {
                        quickStop = true;
                    }
                    wsStrings.put(ws[j].substring(0, 5), ws[j]);
                }
                if (firstHitStop || quickStop) {
                    break;
                }

                if (ws.length > 0 && ws[0].endsWith("::")) {
                    break;
                }

            }
        }

        if (wsStrings.size() > 0) {
            String[] ret = new String[wsStrings.size()];
            int pos = 0;
            Enumeration enumer = wsStrings.keys();
            while (enumer.hasMoreElements()) {
                String key = (String) enumer.nextElement();
                ret[pos++] = (String) wsStrings.get(key);
            }
            new StringSort(ret);
            return ret;
        }
//
// Deal with scenario where value is simple character or character-dot
//
        else if (phrase.length() == 1 ||
                (phrase.length() == 2 && phrase.charAt(1) == '.')) {
            if (isLetter(phrase.charAt(0))) {
                if ((int) phrase.charAt(0) < 4096) {
                    String[] ret = new String[1];
                    ret[0] = "D IU1 " + phrase.toUpperCase().charAt(0) + ".";
                    return ret;
                }
            }
        } else if (recurseLevel > 1) {
            String[] ret = new String[1];
            ret[0] = "D #A  " + phrase;
            return ret;
        } else if (testForEmail(phrase)) {
            String[] ret = new String[1];
            ret[0] = "D EU1 " + phrase;
            return ret;
        }

        return null;
    }

    //
//***************************************************************************
//
    private boolean getLeadStrings(String phrase) {
        for (int i = isoList.length - 1; i >= 0; i--) {
            String ws = (String) leadStrings.get(phrase);
            if (ws != null) {
                return true;
            }
        }

        return false;
    }

    //
//***************************************************************************
//
    private boolean testForEmail(String str) {
        if (str.indexOf(' ') < 0 && str.indexOf('@') >= 1 &&
                str.indexOf('@') == str.lastIndexOf('@') &&
                str.lastIndexOf('.') > str.indexOf('@') + 1) {
            return true;
        }
        return false;
    }

    //
//***************************************************************************
//
//
// Removes miscellaneous characters (or rather replace them with blanks)
//
//
    private String removeMiscChars(String orgName) {
//
// If orgname ends with blank plus something that's not alpha-numeric then
// leave it as it may be a postional indicator.
//
        if (orgName.length() > 2 &&
                Character.isWhitespace(orgName.charAt(orgName.length() - 2)) &&
                !isLetterOrDigit(orgName.charAt(orgName.length() - 1))) {
            orgName = orgName.substring(0, orgName.length() - 2) + "-";
        }

        int i;
        for (i = 0; i < miscChars.length(); i++) {
            if (orgName.indexOf(miscChars.substring(i, i + 1)) >= 0) {
                orgName = orgName.replace(miscChars.charAt(i), ' ');
            }
        }
//
// Now change double and backquotes to single quotes
//
        if (orgName.indexOf("\"") >= 0) {
            orgName = orgName.replace('"', '\'');
        }
        if (orgName.indexOf("`") >= 0) {
            orgName = orgName.replace('`', '\'');
        }
//
// cleanup examples of A- B  and A -B to A-B
//
        if (orgName.indexOf("- ") > 0) {
            for (i = 1; i < orgName.length() - 2; i++) {
                if (orgName.charAt(i) == '-') {
                    if (isLetter(orgName.charAt(i - 1)) &&
                            Character.isWhitespace(orgName.charAt(i + 1)) &&
                            isLetter(orgName.charAt(i + 2))) {
                        orgName = orgName.substring(0, i + 1) +
                                orgName.substring(i + 2);
                    }
                }
            }
        }
        return trimLeadTrailPunct(orgName.trim());
    }

    //
//***************************************************************************
//
    private String trimLeadTrailPunct(String orgName) {
        if (orgName.length() == 1 && ",-/".indexOf(orgName) >= 0) {
            return orgName;
        }
//
// Skip over all leading junk and whitespace
//
        int i;
        for (i = 0; i < orgName.length(); i++) {
            if (Character.isWhitespace(orgName.charAt(i)) == false) {
                if (alphaGroupPunct.indexOf(orgName.substring(i, i + 1)) < 0
                        || leadKeepGroup.indexOf(orgName.substring(i, i + 1)) >= 0) {
                    break;
                }
            }
        }

        if (i >= orgName.length()) {
            orgName = "";
        } else if (i > 0) {
//
// Don't remove leading apostrophe
//
            i -= (leadKeepGroup.indexOf(
                    orgName.substring(i - 1, i)) >= 0) ? 1 : 0;
            if (i > 0) {
                orgName = orgName.substring(i);
            }
        }
//
// If orgname ends with blank plus something that's not alpha-numeric then
// leave it as it may be a postional indicator.
//
/*
	    if (orgName.length() > 2 &&
	       Character.isWhitespace(orgName.charAt(orgName.length() -2)) &&
	       !isLetterOrDigit(orgName.charAt(orgName.length()-1)))
	    {
		return orgName;
	    }
*/
//
// remove any trailing junk in the same way as leading - but not any dots

        for (i = orgName.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(orgName.charAt(i)) == false) {
                if (alphaGroupPunct.indexOf(orgName.substring(i, i + 1)) < 0
                        || trailKeepGroup.indexOf(orgName.substring(i, i + 1)) >= 0) {
                    break;
                }
            }
        }
//
// truncate all trailing junk - with special exception of a dot or apostrophe
// which are allowed to remain
//
        if (i < orgName.length() - 1) {
            i += (trailKeepGroup.indexOf(
                    orgName.substring(i + 1, i + 2)) >= 0) ? 1 : 0;
            if (i < orgName.length() - 1) {
                orgName = orgName.substring(0, i + 1);
            }
        }
        return orgName;
    }

    //
// Method to blank pad adjacent 3+ byte unicode characters
//
    private String spaceLongUnicodeChars(String orgName) {
        isCJK = false;
        direction = "L";
        unicodeSpacer.setLength(0);
        unicodeSpacer.append(orgName);

        for (int i = 0; i < unicodeSpacer.length(); i++) {
            if (Character.isWhitespace(unicodeSpacer.charAt(i))) {
                unicodeSpacer.setCharAt(i, ' ');
                while (i < unicodeSpacer.length() - 1 &&
                        Character.isWhitespace(unicodeSpacer.charAt(i + 1))) {
                    unicodeSpacer.deleteCharAt(i + 1);
                }
                if (i > 0 && i < unicodeSpacer.length() - 1 &&
                        (int) unicodeSpacer.charAt(i - 1) >= 4096 &&
                        (int) unicodeSpacer.charAt(i + 1) >= 4096) {
                    unicodeSpacer.replace(i, i + 1, " ~ ");
                }
            } else if (unicodeSpacer.charAt(i) == '.') {
                unicodeSpacer.setCharAt(i, '.');
                while (i < unicodeSpacer.length() - 1 &&
                        unicodeSpacer.charAt(i + 1) == '.') {
                    unicodeSpacer.deleteCharAt(i + 1);
                }
                if (i > 0 && i < unicodeSpacer.length() - 1 &&
                        (int) unicodeSpacer.charAt(i - 1) >= 4096) {
                    unicodeSpacer.replace(i, i + 1, ". ~ ");
                }
            }
        }

        orgName = unicodeSpacer.toString();
        int len = orgName.length() - 1;
        unicodeSpacer.setLength(0);

        for (int i = 0; i <= len; i++) {
            if ((int) orgName.charAt(i) >= 4096) {
                if (i > 0 &&
                        Character.isWhitespace(orgName.charAt(i - 1)) == false) {
                    unicodeSpacer.append(' ');
                }
                unicodeSpacer.append(orgName.charAt(i));
                if (i < len &&
                        Character.isWhitespace(orgName.charAt(i + 1)) == false) {
                    unicodeSpacer.append(' ');
                }

                isCJK = true;
                direction = "R";
            } else {
                unicodeSpacer.append(orgName.charAt(i));
            }
        }
        return unicodeSpacer.toString();
    }

    //
//***************************************************************************
//
    private String[] setupTitleGroup(String nqs, String title, String first,
                                     String middle, String last, String suffix,
                                     String gender, String titleGender, String iso) {
        String[] titleGroup = new String[3];
        titleGroup[0] = title;
        titleGroup[1] = "";
        titleGroup[2] = "";
//
// If this data is faulty - conflict etc. then don't generate title,sal or full
//
        if (titleGender.equals("C")) {
            titleGroup[0] = "";
        }
//
// First, sort out title if it isn't known but the gender is available
//
        else if (title.length() == 0) {
            String ctryGrp = (String) countryStrings.get(iso);
            if (ctryGrp != null) {
                String defTitle = (String) defaultTitles.get(ctryGrp + gender);
                titleGroup[0] = (defTitle != null) ? defTitle : title;
            }
        }
//
// Now lookup slutation
//
//
// Finally reconstruct the full name from the individual bits (simple version)
//
        String newDirection = (String) lastFirstHash.get(iso + direction);
        newDirection = (newDirection == null) ? direction : newDirection;

        if (newDirection.equals("L")) {
            titleGroup[1] = titleGroup[0] +
                    ((titleGroup[0].length() > 0 && first.length() > 0) ?
                            " " : "") + first;
            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && middle.length() > 0) ?
                            " " : "") + middle;
            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && last.length() > 0) ?
                            " " : "") + last;
            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && suffix.length() > 0) ?
                            " " : "") + suffix;
        } else {
            titleGroup[1] = suffix;

            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && last.length() > 0) ?
                            " " : "") + last;
            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && middle.length() > 0) ?
                            " " : "") + middle;
            titleGroup[1] +=
                    ((titleGroup[1].length() > 0 && first.length() > 0) ?
                            " " : "") + first;
            titleGroup[1] +=
                    ((titleGroup[0].length() > 0) ? " " : "") +
                            titleGroup[0];
        }
        return titleGroup;
    }

    //
//***************************************************************************
//
    private String[] lookupPhrase(String phrase) {
        //System.err.println("Lookup phrase entered with >"+phrase+"<");
        //Thread.dumpStack();
        String[] ret = new String[2];
        phrase = trimLeadTrailPunct(phrase);
        String[] lookupStr = getWordList(phrase);
//
// If didn't lookup then create desc pattern
//
        if (lookupStr == null) {
/*
		if (phrase.length()==1 && isLetter(phrase.charAt(0)))
		{
		    ret[0] = "IU1";
		    ret[1] = phrase.toUpperCase() + ".";
		    return ret;
		}
		else if (phrase.length() == 2 && phrase.charAt(1) == '.' &&
					isLetter(phrase.charAt(0)))
		{
		    ret[0] = "IU1";
		    ret[1] = phrase.toUpperCase();
		    return ret;
		}
		else if (testForEmail(phrase))
		{
		    ret[0] = "EU1";
		    ret[1] = new String(phrase);
		    return ret;
		}
*/

            if (logWriter != null) {
                debugText.append("Failed to lookup word " + phrase + "\n");
            }
            boolean recordIt = false;

            ret[0] = "#";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < phrase.length(); i++) {
                if (isLetter(phrase.charAt(i))) {
                    if (ret[0].endsWith("C") || ret[0].endsWith("U")) {
                        ret[0] = ret[0].substring(0, ret[0].length() - 1) +
                                "A";
                        recordIt = true;
                    } else if (ret[0].endsWith("A") == false) {
                        if ((int) phrase.charAt(i) < 4096) {
                            ret[0] += "C";
                        } else {
                            ret[0] += "U";
                        }
                        sb.append(phrase.substring(i, i + 1).toUpperCase());
                        continue;
                    }
                } else if (Character.isDigit(phrase.charAt(i))) {
                    ret[0] += (ret[0].endsWith("N")) ? "" : "N";
                    recordIt = true;
                } else if (phrase.charAt(i) == ',') {
                    ret[0] += "K";
                } else if (phrase.charAt(i) == ':') {
                    ret[0] += "S";
                } else if (phrase.charAt(i) == '~') {
                    ret[0] += "T";
                } else if (alphaGroupPunct.indexOf(
                        phrase.substring(i, i + 1)) >= 0) {
                    ret[0] += phrase.substring(i, i + 1);
                } else {
                    //System.err.println("Character is type "+
                    //Character.getType(phrase.charAt(i)));
                    ret[0] += "?";
                    recordIt = true;
                }
                sb.append(phrase.substring(i, i + 1).toLowerCase());
            }
            ret[1] = sb.toString();

            if (recordIt) {
                missedWordVector.addElement(ret[1]);
            }
            return ret;
        } else if (lookupStr.length == 1 && lookupStr[0].charAt(2) == '#') {
            ret[0] = lookupStr[0].substring(2, 6).trim();
            ret[1] = lookupStr[0].substring(6);
            return ret;
        }
//
// Else look to see what's found.
//
        return constructDefines(lookupStr);
    }

    //
//***************************************************************************
//
    private String[] constructDefines(String[] lookupStr) {
        String[] ret = new String[lookupStr.length + 1];
        ret[0] = "";
        for (int i = 0; i < lookupStr.length; i++) {
            ret[0] += "&" + lookupStr[i].substring(2, 5);
            ret[i + 1] = lookupStr[i].substring(6);
        }
        ret[0] = ret[0].substring(1);
        return ret;
    }

    //
//***************************************************************************
//
    private boolean setParseValues(COptimaContact contact, String nqs,
                                   String[] titleGroup, String first, String middle, String last,
                                   String suffix, String gender, String email, String jobtitle,
                                   String patt) {
        contact.setField(OFT.Title, titleGroup[0]);
        contact.setField(OFT.FirstName, first);
        contact.setField(OFT.MiddleInitials, middle);
        contact.setField(OFT.LastName, last);
        contact.setField(OFT.NameSuffix, suffix);
        contact.setField(OFT.Salutation, titleGroup[2]);
        contact.setField(OFT.FullName, titleGroup[1]);
        contact.setField(OFT.Gender, gender);
        contact.setField(OFT.Email1, email);
        contact.setField(OFT.JobTitle, jobtitle);
        String code = nqs;

        if ("U89".indexOf(nqs.substring(0, 1)) < 0) {
            if (vtfmlsCode[1] > 0 && titleGroup[0].length() == 0) {
                vtfmlsCode[1] = 6;
            }
            code = nqs +
                    " T" + String.valueOf(vtfmlsCode[1]) +
                    "V" + String.valueOf(vtfmlsCode[0]) +
                    "F" + String.valueOf(vtfmlsCode[2]) +
                    "M" + String.valueOf(vtfmlsCode[3]) +
                    "L" + String.valueOf(vtfmlsCode[4]) +
                    "S" + String.valueOf(vtfmlsCode[5]) +
                    "G" + ((gender.length() != 1) ? "0" : gender +
                    "E" + ((email.length() > 0) ? "2" : "0") +
                    "J" + ((jobtitle.length() > 0) ? "2" : "0") +
                    " " + patt);

        }
        contact.setField(OFT.NCR, code);
        if (logWriter != null &&
                code.substring(2).equals("T0F0M0L0S0G0") == false) {
            debugText.append("NAME PARSED!!\nNQS        " + code +
                    "\nTitle      " + titleGroup[0] +
                    "\nFirst      " + first +
                    "\nMiddle     " + middle +
                    "\nLast       " + last +
                    "\nSuffix     " + suffix +
                    "\nSalutation " + titleGroup[2] +
                    "\nFullName   " + titleGroup[1] +
                    "\nGender     " + gender +
                    "\nEmail      " + email +
                    "\nJob Title  " + jobtitle +
                    "\nPattern    " + patt);
        }
        if (logWriter != null) {
            logWriter.println(debugText.toString());
        }
        return true;
    }

    //
//***************************************************************************
//
    private final void modifyPattern(Vector ipe) {
        Vector countryChain = new Vector(10, 2);
//
// Locate the patternHolder chain for this country
//
        for (int i = 0; i < isoList.length; i++) {
            //System.err.println("Looking for isoCode PH "+isoList[i]);
            PatternHolder ph = (PatternHolder) modifyHash.get(isoList[i]);
            if (ph != null) {
                countryChain.addElement(ph);
                //System.err.println("Adding mp "+ph.countryIso);
            }
        }
//
// country/ROW patternHolder found (or not) - now use the recursion process
// for each source entry and see what comes back
//
        for (int i = 0; i < countryChain.size(); i++) {
            PatternHolder topPh = (PatternHolder) countryChain.elementAt(i);

            for (int j = 0; j < parsedEntities.length; j++) {
                //System.err.println("\n\nStarting search at "+i);
                PatternHolder deepPh = null;

                if (topPh != null) {
                    deepPh = getPatternHolder(topPh,
                            new PatternHolder(null, null, null, null, null, -99),
                            parsedEntities, j, j);

                    if (deepPh != null && deepPh.sources != null) {
                        parsedEntities[j].tokenModifier = deepPh;
                        for (int k = 0; k < deepPh.sources.length; k++) {
                            parsedEntities[j + k].modifiable = false;
                        }
                        j += deepPh.sources.length - 1;
                    }
                }
            }
        }

//
// Each parsedEntry now has the potential of owning a PatternHolder pointer
// which can be applied. No country-based patternholder is usurped over
// partially overlaid by a country group or ROW
//
        Vector npe = new Vector(10, 10);

        for (int i = 0; i < parsedEntities.length; i++) {
            if (parsedEntities[i].tokenModifier != null) {
                modifyPattern(parsedEntities[i].tokenModifier, i, npe, ipe);
            } else if (parsedEntities[i].modifiable == true) {
                npe.addElement(parsedEntities[i]);
            }
        }

        newParsedEntities = new ParsedEntity[npe.size()];
        npe.copyInto(newParsedEntities);
    }

    //
//***************************************************************************
//
//
// This method will replace the tokens in resBit[0] with those in
// newPh.replaces, the output strings of resBits[1] will be replaced using the
// formatting of newPh.formats and the text of resBits[1]
// NameStrings will be re-concatenated according to same formats rule as above
//
    private void modifyPattern(PatternHolder newPh, int start,
                               Vector npe, Vector ipe) {
/*
	    System.err.println("DeepPh for pattern modification for "+
		newPh.toString(newPh.sources)+" replace="+
		newPh.toString(newPh.replaces)+" and format is "+
		newPh.toString(newPh.formats));
	    System.err.println("Start="+start+"  intNewNsPos="+intNewNsPos);
	    System.err.println("Newph.replaces.length is "+
							newPh.replaces.length);
*/

//
// First, create new output pattern list
//
        ParsedEntity[] tempParsedEntities =
                new ParsedEntity[newPh.replaces.length];
        for (int i = 0; i < newPh.replaces.length; i++) {
            tempParsedEntities[i] = new ParsedEntity("PATMOD",
                    newPh.replaces[i]);
            //newResBits[i + intNewNsPos] = new String[2];
            //newResBits[i + intNewNsPos][0] = newPh.replaces[i];
            //newResBits[i + intNewNsPos][1] = "";
            //newNameStrings[i + intNewNsPos] = "PATMOD";
        }
//
// collect all input format data from both resBits[x][y] and nameStrings[x]
// Then tokenize each so that extract, formatting and reconstruct can take
// place using the replaces and formats tokens
//
        String resWords = "";

        int pullPos = 0;
/*
	    System.err.println("Dumping newPh");
	    newPh.dump();
*/
        for (int i = start; i < start + newPh.sources.length;
             i++, pullPos++) {
            if (parsedEntities[i].tgnGroup.startsWith("#")) {
                String[] toks = tokenizeAlphaGroup(
                        parsedEntities[i].tgnTokens[
                                newPh.sourcesPull[pullPos]],
                        parsedEntities[i].tgnGroup.length() - 1);
                for (int j = 0; j < toks.length; j++) {
                    resWords += toks[j] + " ";
                }
            } else {
/*
		    System.err.println("newPh.sourcesPull length is "+
						newPh.sourcesPull.length);
		    System.err.println("newPh.sourcesPull[i]: pP="+pullPos);
		    System.err.println("newPh.sourcesPull[i]: value="+
						newPh.sourcesPull[pullPos]);
		    System.err.println("parsedEntity orgToken is "+
						parsedEntities[i].orgToken);
		    System.err.println("parsedEntity tgnGroup is "+
						parsedEntities[i].tgnGroup);
		    System.err.println("parsedEntity tgnTokens size is "+
					parsedEntities[i].tgnTokens.length);
*/

//
// This code breaks hyphenated and dotted words so that the modify pattern
// routine has the right number of elements to play around with
//
                String rw = parsedEntities[i].tgnTokens[
                        newPh.sourcesPull[pullPos]];
                if (rw.indexOf("-") > 0 || rw.indexOf(".") > 0) {
                    //rw = fragmentToken("-", rw);
                    resWords += fragmentToken(rw) + " ";
                } else {
                    resWords += rw + " ";
                }
            }
        }
        SuperStringTokenizer wrd = new SuperStringTokenizer(
                resWords, " ", false);
        String[] res = wrd.getStringList();

//
// Now use the newPh.formats array to recompose the org to newNameStrings and
// the res to newResBits
//
        int resTok = 0;
        ArrayList structList = new ArrayList(10);
        for (int i = 0; i < newPh.formats.length; i++) {
            structList.clear();

            int lenToGo = newPh.formats[i].indexOf(",");
            String rebuild = null;
            if (lenToGo > 0) {
                rebuild = newPh.formats[i].substring(lenToGo + 1);
            } else {
                lenToGo = newPh.formats[i].length();
            }

            for (int j = 0; j < lenToGo; j++) {
                if (tempParsedEntities[i].tgnTokens[0].length() == 0) {
                    while (res[resTok].startsWith(".")) {
                        res[resTok] = res[resTok].substring(1);
                    }
                }

                switch (newPh.formats[i].charAt(j)) {
                    case 'U':
/*
			    tempParsedEntities[i].tgnTokens[0] +=
						res[resTok++].toUpperCase();
*/
                        structList.add(res[resTok++].toUpperCase());
                        break;

                    case 'L':
/*
			    tempParsedEntities[i].tgnTokens[0] +=
						res[resTok++].toLowerCase();
*/
                        structList.add(res[resTok++].toLowerCase());
                        break;

                    case 'P':
                        String comp = "";
                        if (res[resTok].length() == 1) {
                            comp +=
                                    res[resTok].substring(0, 1).toUpperCase();
                        } else {
                            comp +=
                                    res[resTok].substring(0, 1).toUpperCase() +
                                            res[resTok].substring(1).toLowerCase();
                        }
/*
			    tempParsedEntities[i].tgnTokens[0] += comp;
*/
                        structList.add(comp);
                        resTok++;
                        break;

                    case 'A':
/*
			    tempParsedEntities[i].tgnTokens[0] +=res[resTok++];
*/
                        structList.add(res[resTok++]);
                        break;

                    case 'D':
                        resTok++; // skip value - drop
                        break;

                    case 'S':
                        //tempParsedEntities[i].tgnTokens[0] += " ";
                        structList.add(" ");
                        break;

                    default:
                        System.err.println("Critical error processing " +
                                "formats reconstruct for " + newPh.formats[i] +
                                " at position " + j);
                        System.exit(1);
                }
            }
            if (rebuild == null) {
                rebuild = "123456789".substring(0, structList.size());
            }
            for (int j = 0; j < rebuild.length(); j++) {
                int off = rebuild.charAt(j) - '1';
                tempParsedEntities[i].tgnTokens[0] += structList.get(off);
            }
        }
        if (resTok + 1 < res.length) {
            System.err.println("Only " + (resTok + 1) + " tokens used from " +
                    res.length + " values processing " + resWords +
                    " with format " + newPh.toString(newPh.formats));
            System.exit(1);
        }

//
// Tidy up any hanging '- -' sets to just '-'
//
        for (int i = 0; i < tempParsedEntities.length; i++) {
            int ind;
            while ((ind = tempParsedEntities[i].tgnTokens[0].indexOf("- -"))
                    > 0) {
                tempParsedEntities[i].tgnTokens[0] =
                        tempParsedEntities[i].tgnTokens[0].substring(0, ind) +
                                tempParsedEntities[i].tgnTokens[0].substring(ind + 2);
            }
            while ((ind = tempParsedEntities[i].tgnTokens[0].indexOf(". ."))
                    > 0) {
                tempParsedEntities[i].tgnTokens[0] =
                        tempParsedEntities[i].tgnTokens[0].substring(0, ind) +
                                tempParsedEntities[i].tgnTokens[0].substring(ind + 2);
            }
        }
/*
	    System.err.println("Final intNewNsPos="+(intNewNsPos +
						newPh.replaces.length));
*/

        if (tempParsedEntities.length == 1 &&
                tempParsedEntities[0].tgnGroup.length() == 3 &&
                tempParsedEntities[0].tgnGroup.charAt(0) == 'D') {
            ipe.addElement(tempParsedEntities[0]);
        } else {
            for (int i = 0; i < tempParsedEntities.length; i++) {
//
// Check for re-word check and perform exchange to request
//
                if (tempParsedEntities[i].tgnGroup.startsWith("W")) {
                    tempParsedEntities[i] = new ParsedEntity(
                            tempParsedEntities[i].tgnTokens[0]);
                }
                npe.addElement(tempParsedEntities[i]);
            }
        }
    }

    //
//***************************************************************************
//
    private String fragmentToken(String rw) {
        for (int i = 1; i < rw.length() - 1; i++) {
            if ((rw.charAt(i) == '-' || rw.charAt(i) == '.') &&
                    Character.isWhitespace(rw.charAt(i - 1)) == false &&
                    Character.isWhitespace(rw.charAt(i + 1)) == false) {
                rw = rw.substring(0, i + 1) + " " + rw.substring(i);
                for (i += 2; i < rw.length() &&
                        (rw.charAt(i) == '.' || rw.charAt(i) == '-'); i++)
                    ;
            }
        }
        return rw;
    }
//
//***************************************************************************
//

    private String[] tokenizeAlphaGroup(String phrase, int numTokens) {
        String[] tokens = new String[numTokens];

        for (int i = 0; i < numTokens; i++) {
            tokens[i] = "";
        }

        //System.err.println("Phrase: >"+phrase+"<");
        //System.err.println("Token count: "+numTokens);

        int tokenPtr = -1;
        String ret = "";

        for (int i = 0; i < phrase.length(); i++) {
            if (isLetter(phrase.charAt(i))) {
                if (ret.endsWith("C")) {
                    ret = ret.substring(0, ret.length() - 1) + "A";
                    tokens[tokenPtr] += phrase.substring(i, i + 1);
                } else if (ret.endsWith("A") == false) {
                    ret += "C";
                    tokenPtr += (tokenPtr < numTokens - 1) ? 1 : 0;
                    tokens[tokenPtr] += phrase.substring(i, i + 1);
                } else {
                    tokens[tokenPtr] += phrase.substring(i, i + 1);
                }
            } else if (Character.isDigit(phrase.charAt(i))) {
                if (ret.endsWith("N") == false) {
                    ret += "N";
                    tokenPtr += (tokenPtr < numTokens - 1) ? 1 : 0;
                    tokens[tokenPtr] += phrase.substring(i, i + 1);
                } else {
                    tokens[tokenPtr] += phrase.substring(i, i + 1);
                }
            } else {
                ret += phrase.substring(i, i + 1);
                tokenPtr += (tokenPtr < numTokens - 1) ? 1 : 0;
                tokens[tokenPtr] += phrase.substring(i, i + 1);
            }
        }

        if (tokenPtr + 1 != numTokens) {
            System.err.println("Tokenization of AlphaGroup failed. Input " +
                    "was " + phrase + " - number of expected tokens was " +
                    numTokens + " - number found was " + tokenPtr + 1);
            System.exit(1);
        }
        return tokens;
    }

    //
//***************************************************************************
//
    private String unpackSource(String[][] source, int depth) {
        String str = new String(source[0][0]);

        for (int i = 1; i < depth; i++) {
            str += " " + source[i][0];
        }
        return str;
    }

    //
//***************************************************************************
//
    private PatternHolder getPatternHolder(PatternHolder ph,
                                           PatternHolder bestPh, ParsedEntity[] source,
                                           int trueStart, int thisStart) {
/*
	    System.err.println("Entering with trueStart="+trueStart+
			" thisStart="+
			thisStart+" stop="+stop+"  ph-="+ph+
			"ph.source="+ph.sources);
*/

        if (ph == null || thisStart >= source.length ||
                source[thisStart].modifiable == false || ph.nextHash == null) {
            //System.err.println("Exit 1");
            return bestPh;
        }

        //System.err.println("Looking for key "+ source[thisStart][0]);
        //System.err.println("Hash holds "+ph.nextHash.toString());
        PatternHolder ourPh = (PatternHolder) ph.nextHash.get(
                source[thisStart].tgnGroup);

        if (ourPh == null) {
            return bestPh;
        }

        int ourPosition = 0;
        if (trueStart == 0) {
            if (thisStart >= source.length - 1) {
                ourPosition = PatternHolder.BEGIN_TO_END;
            } else {
                ourPosition = PatternHolder.BEGIN_TO_MIDDLE;
            }
        } else {
            if (thisStart >= source.length - 1) {
                ourPosition = PatternHolder.MIDDLE_TO_END;
            } else {
                ourPosition = PatternHolder.MIDDLE_TO_MIDDLE;
            }
        }
        //System.err.println("Our calculated bit pattern is "+ourPosition);

        //System.err.println("Our PH data is "+ourPh.sources+"  ourpos="+
        //ourPh.patternPos);
        if (ourPh.sources != null) {
            //System.err.println("Source string is "+
            //ourPh.toString(ourPh.sources));
            if (ourPh.patternPos == 0) {
                for (int i = 0; i < modifyPatternHolder.length; i++) {
                    modifyPatternHolder[i].dump("PPos is zero?");
                }
            }

            PatternHolder chainPh = ourPh;

            do {
                if ((chainPh.patternPos & ourPosition) == ourPosition) {
                    bestPh = chainPh;
                    break;
                    //System.err.println("Best Ph is us at "+bestPh);
                }
                chainPh = chainPh.nextAtLevel;
            }
            while (chainPh != null);
        }
        //System.err.println("GOING DOWN");
        return getPatternHolder(ourPh, bestPh, source,
                trueStart, thisStart + 1);
    }

    //
//***************************************************************************
//
// SDN
//
    JTabbedPane w_tab_PNE;
    STextField inputText;
    STextField isoText;
    JButton processName;
    JButton processNext;
    JComboBox processMode;
    JLabel initialLabel;
    STextField reconsName;
    STextField saluName;
    STextField patternText;
    STextField gender;
    STextField pattern;

    JComboBox defineIso;
    STextField defineText;
    STextField redefineText;
    JComboBox defineType;
    JComboBox redefineType;
    JButton nameUpdate;
    JButton nameCreate;
    JButton nameRemove;
    STextField defineFilter;
    STextField redefineFilter;
    JList nameDataList;

    STextField defineModifyText;
    STextField redefineModifyText;
    STextField formatModifyText;
    JComboBox defineModifyIso;
    JComboBox defineModifyPosition;
    JButton modifyUpdate;
    JButton modifyCreate;
    JButton modifyRemove;
    STextField modifyCountryFilter;
    STextField modifyDefineFilter;
    STextField modifyRedefineFilter;
    JList modifyPatternList;

    JTextArea missedWordList;
    JButton missedWordRefresh;
    JButton missedWordClear;

    JList missedPatternList;
    JButton missedPatternRefresh;
    JButton missedPatternClear;

    STextField statusBar;
    JLabel[] changeLabels;
    NameRecord lastNameRecord = null;
    NameRecord orgNameRecord = null;
    String[] lastNameSel = {"", "", "", "", ""};
    String[] lastModifySel = {"", "", "", "", ""};
    String[] processModes = {"Select", "Step", "To Fail",
            "Any Diff", "Out Diff", "To End", "Next Ctry"};
    static String[] modifyPositions =
            {
                    "Select", "A", "BM", "BMBE", "BMBEMM", "BMMM", "BMME", "BE", "BEMM",
                    "BEMMME", "BEME", "MM", "MMME", "ME", "BMMMME",
            };
    static int[] modifyPositionCodes =
            {
                    0,
                    PatternHolder.ANYWHERE,
                    PatternHolder.BEGIN_TO_MIDDLE,
                    PatternHolder.BEGIN_TO_MIDDLE | PatternHolder.BEGIN_TO_END,
                    PatternHolder.BEGIN_TO_MIDDLE | PatternHolder.BEGIN_TO_END |
                            PatternHolder.MIDDLE_TO_MIDDLE,
                    PatternHolder.BEGIN_TO_MIDDLE | PatternHolder.MIDDLE_TO_MIDDLE,
                    PatternHolder.BEGIN_TO_MIDDLE | PatternHolder.MIDDLE_TO_END,
                    PatternHolder.BEGIN_TO_END,
                    PatternHolder.BEGIN_TO_END | PatternHolder.MIDDLE_TO_MIDDLE,
                    PatternHolder.BEGIN_TO_END | PatternHolder.MIDDLE_TO_MIDDLE |
                            PatternHolder.MIDDLE_TO_END,
                    PatternHolder.BEGIN_TO_END | PatternHolder.MIDDLE_TO_END,
                    PatternHolder.MIDDLE_TO_MIDDLE,
                    PatternHolder.MIDDLE_TO_MIDDLE | PatternHolder.MIDDLE_TO_END,
                    PatternHolder.MIDDLE_TO_END,
                    PatternHolder.BEGIN_TO_MIDDLE | PatternHolder.MIDDLE_TO_MIDDLE |
                            PatternHolder.MIDDLE_TO_END,
            };

    int recordProcessMode = 0;
    COptimaContact iContact;
    JGridBagPanel resPanel1;
    JGridBagPanel resPanel2;
    JGridBagPanel resPanel3;
    JGridBagFrame faFrame;

    boolean nameDataModified = false;
    boolean modifyDataModified = false;

    BufferedReader inputDataFile = null;
    BufferedWriter outputDataFile = null;
    long inputDataRecno = 0;
    long numPasses = 0;

    Font uniFont;


    public void doGui(String[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                System.err.println("Args " + i + " is " + args[i]);
            }
        }

        uniFont = new Font("Dialog", Font.BOLD, 12);

        faFrame = new JGridBagFrame("FormatNames V6N  " +
                "Copyright HarteHanks 2004 - 2008", 600, 500);

        JMenuManager jMenuManager = new JMenuManager();
        jMenuManager.addTitle("File");
        jMenuManager.addButton("Save Tables", new SaveTable());
        jMenuManager.addButton("Reload Base Pats", new PatternLoad(false));
        jMenuManager.addButton("Reload All Pats", new PatternLoad(true));
        jMenuManager.addButton("Exit", new ExitAction());
        jMenuManager.addTitle("Job");
        jMenuManager.addButton("Read Data", new LoadData(false));
        jMenuManager.addButton("Read/Write Data", new LoadData(true));

        faFrame.setJMenuBar(jMenuManager);

        w_tab_PNE = new JTabbedPane();
        w_tab_PNE.setBorder(BorderFactory.createRaisedBevelBorder());
        w_tab_PNE.setForeground(Color.lightGray);
        faFrame.addWidget(w_tab_PNE, 0, 0, 100, 93,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1.0, 1.0);

        statusBar = (STextField) faFrame.addWidget(new STextField(""),
                0, 93, 100, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        resetStatusBar();


        w_tab_PNE.addTab("Formatting", makeFormatPanel());
        w_tab_PNE.addTab("Name Data", makeNameDataPanel());
        w_tab_PNE.addTab("Modify Pattern", makeModifyPatternPanel());
        w_tab_PNE.addTab("Missed Words", makeMissedWordPanel());
        w_tab_PNE.addTab("Missed Pattern", makeMissedPatternPanel());
        faFrame.pack();
        faFrame.setVisible(true);
    }

    private JPanel makeFormatPanel() {
        JGridBagPanel me = new JGridBagPanel(600, 500);

        changeLabels = new JLabel[14];
        for (int i = 0; i < changeLabels.length; i++) {
            changeLabels[i] = new JLabel();
        }

        me.addWidget(new JLabel("Input Name "),
                0, 0, 20, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        inputText = (STextField) me.addWidget(new STextField(),
                20, 0, 80, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        inputText.setFont(uniFont);

        me.addWidget(new JLabel("Country Iso "),
                0, 7, 20, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        isoText = (STextField) me.addWidget(new STextField(),
                20, 7, 15, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        isoText.setFont(uniFont);

        processName = (JButton) me.addWidget(new JButton("Process"),
                40, 7, 15, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);
        processName.addActionListener(new ProcAction());

        processNext = (JButton) me.addWidget(new JButton("Process Next(?)"),
                55, 7, 25, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);
        processNext.addActionListener(new ProcNextAction());
        processNext.setEnabled(false);

        processMode = (JComboBox) me.addWidget(new JComboBox(processModes),
                80, 7, 20, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);
        processMode.setEnabled(false);
        processMode.addItemListener(new ProcessModeOption());
//
// Initial breakdown
//
        initialLabel = (JLabel) me.addWidget(
                new JLabel("Initial Data Analysis"),
                0, 14, 50, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        resPanel1 = (JGridBagPanel) me.addWidget(new JGridBagPanel(125, 125),
                0, 21, 100, 15,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.20);
        resPanel1.setBackground(Color.white);

//
// Post Pattern Modification
//
        me.addWidget(new JLabel("Breakdown Modification"),
                0, 36, 35, 6,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        resPanel2 = (JGridBagPanel) me.addWidget(new JGridBagPanel(125, 125),
                0, 42, 100, 15,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.20);
        resPanel2.setBackground(Color.white);

//
// Final Resolve Output
//
        me.addWidget(new JLabel("Pattern Resolution"),
                0, 57, 35, 6,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        resPanel3 = (JGridBagPanel) me.addWidget(new JGridBagPanel(125, 125),
                0, 63, 100, 15,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.20);
        resPanel3.setBackground(Color.white);

        changeLabels[9] = (JLabel) me.addWidget(new JLabel("Recons Name "),
                0, 80, 20, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        reconsName = (STextField) me.addWidget(new STextField(),
                20, 80, 80, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        reconsName.setFont(uniFont);

        changeLabels[10] = (JLabel) me.addWidget(new JLabel("Salutation "),
                0, 87, 20, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        saluName = (STextField) me.addWidget(new STextField(),
                20, 87, 80, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        saluName.setFont(uniFont);

        changeLabels[11] = (JLabel) me.addWidget(new JLabel("Pattern ISO "),
                0, 94, 20, 6,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        patternText = (STextField) me.addWidget(new STextField(),
                20, 94, 10, 6,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        patternText.setFont(uniFont);

        changeLabels[12] = (JLabel) me.addWidget(new JLabel("Gender"),
                30, 94, 15, 6,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        gender = (STextField) me.addWidget(new STextField(),
                45, 94, 10, 6,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        gender.setFont(uniFont);

        changeLabels[13] = (JLabel) me.addWidget(new JLabel("Pattern"),
                55, 94, 15, 6,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        pattern = (STextField) me.addWidget(new STextField(),
                70, 94, 20, 6,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 0.0);
        pattern.setFont(uniFont);


        return me;
    }

    private class ProcessModeOption implements ItemListener {
        public void itemStateChanged(ItemEvent ie) {
            recordProcessMode = processMode.getSelectedIndex();
            processNext.setEnabled(false);
            if (inputDataFile != null) {
                processNext.setEnabled(true);
            }
            switch (recordProcessMode) {
                case 0:
                    break;

                case 1:
                    processNext.setText("Process Next (" +
                            (inputDataRecno + 1) + ")");
                    break;

                case 2:
                    processNext.setText("Process To Next Fail");
                    break;

                case 3:
                    processNext.setText("Process To Any Diff");
                    break;

                case 4:
                    processNext.setText("Process To Out Diff");
                    break;

                case 5:
                    processNext.setText("Process To File End");
                    break;

                case 6:
                    processNext.setText("Process To Next Country");
                    break;

                default:
                    processNext.setEnabled(false);
                    break;
            }
        }
    }

    private class ProcNextAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            NameRecord nr;
            writeNameRecord();

            switch (recordProcessMode) {
                case 1: // single step action
                    if ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, true);
                        newNameRecord.needsWriting = true;
                        compareRecords(newNameRecord, true);
                        processNext.setText("Process Next (" +
                                (inputDataRecno + 1) + ")");
                        numPasses += (patcode.length() > 0) ? 1 : 0;
                    }
                    break;

                case 2: // to next fault
                    while ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, false);
                        newNameRecord.needsWriting = true;
                        compareRecords(newNameRecord, false);
                        numPasses += (patcode.length() > 0) ? 1 : 0;
                        if (patcode.length() == 0) {
                            lastNameRecord = orgNameRecord;
                            nr = orgNameRecord;
                            newNameRecord = processRecord(nr, true);
                            newNameRecord.needsWriting = true;
                            compareRecords(newNameRecord, true);
                            setStatusBar("Pass rate: " + numPasses + "/" +
                                    inputDataRecno, Color.yellow);
                            return;
                        }
                    }
                    buildDisplay(lastNameRecord);
                    break;

                case 3: // to next any diff
                    while ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, false);
                        newNameRecord.needsWriting = true;
                        numPasses += (patcode.length() > 0) ? 1 : 0;
                        if (compareRecords(newNameRecord, false)) {
                            lastNameRecord = orgNameRecord;
                            nr = orgNameRecord;
                            newNameRecord = processRecord(nr, true);
                            newNameRecord.needsWriting = true;
                            compareRecords(newNameRecord, true);
                            setStatusBar("Pass rate: " + numPasses + "/" +
                                    inputDataRecno, Color.yellow);
                            return;
                        }
                    }
                    buildDisplay(lastNameRecord);
                    break;

                case 4: // to next out diff
                    while ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, false);
                        newNameRecord.needsWriting = true;
                        numPasses += (patcode.length() > 0) ? 1 : 0;
                        if (compareRecords(newNameRecord, false, 9, 10)) {
                            lastNameRecord = orgNameRecord;
                            nr = orgNameRecord;
                            newNameRecord = processRecord(nr, true);
                            newNameRecord.needsWriting = true;
                            compareRecords(newNameRecord, true);
                            setStatusBar("Pass rate: " + numPasses + "/" +
                                    inputDataRecno, Color.yellow);
                            return;
                        }
                    }
                    buildDisplay(lastNameRecord);
                    break;

                case 5: // to end of file
                    while ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, false);
                        newNameRecord.needsWriting = true;
                        lastNameRecord = newNameRecord;
                        numPasses += (patcode.length() > 0) ? 1 : 0;
                    }
                    buildDisplay(lastNameRecord);
                    setStatusBar("Pass rate: " + numPasses + "/" +
                            inputDataRecno, Color.green);
                    break;

                case 6: // to next country
                    String lastCountry = isoText.getText();
                    while ((nr = nextRecord()) != null) {
                        NameRecord newNameRecord = processRecord(nr, false);
                        newNameRecord.needsWriting = true;
                        numPasses += (patcode.length() > 0) ? 1 : 0;

                        if (lastCountry.equals(newNameRecord.inputIso) ==
                                false) {
                            lastNameRecord = orgNameRecord;
                            nr = orgNameRecord;
                            newNameRecord = processRecord(nr, true);
                            newNameRecord.needsWriting = true;
                            compareRecords(newNameRecord, true);
                            setStatusBar("Pass rate: " + numPasses + "/" +
                                    inputDataRecno, Color.yellow);
                            return;
                        }
                    }
                    buildDisplay(lastNameRecord);
                    break;

                default:
                    break;
            }
        }
    }

    private void writeNameRecord() {
        if (lastNameRecord != null) {
            if (lastNameRecord.writeNameRecord(outputDataFile) == false) {
                setStatusBar("Write of output record failed", Color.red);
            }
        }
    }

    protected NameRecord nextRecord() {
        writeNameRecord();
        String line = null;
        try {
            line = inputDataFile.readLine();
        } catch (IOException ioe) {
        }

        if (line == null) {
            processNext.setEnabled(false);
            statusBar.setBackground(Color.yellow);
            statusBar.setText("End of file reached on data input");
            writeNameRecord();
            inputDataFile = closeInputFile(inputDataFile);
            outputDataFile = closeOutputFile(outputDataFile);
            return null;
        }
        inputDataRecno++;
        if ((inputDataRecno % 1000) == 0) {
            setStatusBar("Processed Record " +
                    inputDataRecno, Color.lightGray);
        }
        orgNameRecord = new NameRecord(line);
        lastNameRecord = new NameRecord(line);
        return lastNameRecord;
    }

    private class ProcAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            NameRecord nr = new NameRecord(inputText.getText(),
                    isoText.getText());
            NameRecord newNameRecord = processRecord(nr, true);

            compareRecords(newNameRecord, true);
        }
    }

    protected boolean compareRecords(NameRecord newName, boolean setStatus) {
        return compareRecords(newName, setStatus, 0, 13);
    }

    protected boolean compareRecords(NameRecord newName, boolean setStatus,
                                     int start, int end) {
        if (lastNameRecord == newName) {
            System.err.println("BUG");
        }
        if (lastNameRecord == null || newName == null) {
            lastNameRecord = newName;
            if (setStatus) {
                statusBar.setBackground(Color.lightGray);
            }
            return false;
        }

        if (lastNameRecord.inputName.equals(newName.inputName) == false) {
            lastNameRecord = newName;
            statusBar.setBackground(Color.lightGray);
            return false;
        }

        if (lastNameRecord.resultData.length != 14 ||
                newName.resultData.length != 14) {
            setStatusBar("Unable to compare old and new name parsing " +
                            "results due to field count incompatibility",
                    Color.yellow);
            lastNameRecord = newName;
            return true;
        }
        int numChanges = 0;
        for (int i = start; i < end; i++) // don't compare pattern
        {
            if (lastNameRecord.resultData[i] == null &&
                    newName.resultData[i] != null) {
/*
		    System.err.println("Old data at "+i+" is "+
						lastNameRecord.resultData[i]);
		    System.err.println("New data at "+i+" is "+
						newName.resultData[i]);
*/
                changeLabels[i].setBackground(Color.yellow);
                numChanges++;
            } else if (lastNameRecord.resultData[i] != null &&
                    newName.resultData[i] == null) {
/*
		    System.err.println("Old data at "+i+" is "+
						lastNameRecord.resultData[i]);
		    System.err.println("New data at "+i+" is "+
						newName.resultData[i]);
*/
                changeLabels[i].setBackground(Color.yellow);
                numChanges++;
            } else if (lastNameRecord.expandArray(
                    lastNameRecord.resultData[i]).equals(
                    newName.expandArray(newName.resultData[i])) == false) {
/*
		    String old = lastNameRecord.expandArray(
					lastNameRecord.resultData[i]);
		    String new = newName.expandArray(
					lastNameRecord.resultData[i]);
		    System.err.println("Old data at "+i+" is >"+
			lastNameRecord.expandArray(lastNameRecord.resultData[i]) +"<");
		    System.err.println("New data at "+i+" is >"+
			newName.expandArray(newName.resultData[i])+"<");
*/
                changeLabels[i].setBackground(Color.yellow);
                numChanges++;
            }
        }
        if (setStatus) {
            setStatusBar("There were " + numChanges + " changes since last " +
                    "parse", ((numChanges > 0) ? Color.yellow : Color.green));
        }
        lastNameRecord = newName;
        return (numChanges > 0) ? true : false;
    }

    protected void setStatusBar(String text, Color colour) {
        statusBar.setBackground(colour);
        statusBar.setText(text);
        statusBar.paint(statusBar.getGraphics());
    }

    protected void resetStatusBar() {
        setStatusBar("", Color.lightGray);
    }

    protected NameRecord processRecord(NameRecord nr, boolean setDisplay) {
        iContact = new COptimaContact();
        iContact.setField(OFT.FullName, nr.inputName);
        iContact.setField(OFT.CountryISO, nr.inputIso);
        formatName(iContact);

        if (setDisplay) {
            buildDisplay(nr);
        }
        return buildNameRecord(nr);
    }

    private NameRecord buildNameRecord(NameRecord nr) {
        StringBuffer sb = new StringBuffer();
        sb.append(nr.inputName + "|" + nr.inputIso + "|");
        for (int i = 0; i < parsedEntities.length; i++) {
            sb.append(parsedEntities[i].orgToken + "_");
        }
        sb.append("_!");
        for (int i = 0; i < parsedEntities.length; i++) {
            sb.append(parsedEntities[i].tgnTokens[0] + "_");
        }
        sb.append("_!");
        for (int i = 0; i < parsedEntities.length; i++) {
            sb.append(parsedEntities[i].tgnGroup + "_");
        }
        sb.append("_!");

        for (int i = 0; i < newParsedEntities.length; i++) {
            sb.append(newParsedEntities[i].orgToken + "_");
        }
        sb.append("_!");
        for (int i = 0; i < newParsedEntities.length; i++) {
            sb.append(newParsedEntities[i].tgnTokens[0] + "_");
        }
        sb.append("_!");
        for (int i = 0; i < newParsedEntities.length; i++) {
            sb.append(newParsedEntities[i].tgnGroup + "_");
        }
        sb.append("_!");

        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                sb.append(resDecomp[i][0] + "_");
            }
        }
        sb.append("_!");
        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                sb.append(resDecomp[i][1] + "_");
            }
        }
        sb.append("_!");
        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                sb.append(resDecomp[i][2] + "_");
            }
        }
        sb.append("_!");
        sb.append(iContact.getField(OFT.FullName).trim() + " !");
        sb.append(iContact.getField(OFT.Salutation).trim() + " !");
        sb.append(patternIso.trim() + " !");
        sb.append(iContact.getField(OFT.Gender).trim() + " !");
        sb.append(patcode.trim() + " ");
        return new NameRecord(sb.toString());
    }

    private void buildDisplay(NameRecord thisNameRecord) {
        if (thisNameRecord != null) {
            inputText.setText(thisNameRecord.inputName);
            isoText.setText(thisNameRecord.inputIso);
        }
        if (inputDataRecno > 0) {
            initialLabel.setText(
                    "Initial Data Analysis for record " + inputDataRecno);
        } else {
            initialLabel.setText("Initial Data Analysis for data entry");
        }
        resPanel1.removeAll();
        resPanel2.removeAll();
        resPanel3.removeAll();


        int horzSize = 100 / (parsedEntities.length + 1);
        changeLabels[0] = (JLabel) resPanel1.addWidget(new JLabel("Origin"),
                0, 0, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);

        for (int i = 0; i < parsedEntities.length; i++) {
            JButton jb = (JButton) resPanel1.addWidget(
                    new JButton(parsedEntities[i].orgToken),
                    horzSize * (1 + i), 0, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
            jb.addActionListener(new NameDataOrigin(
                    parsedEntities[i].orgToken,
                    parsedEntities[i].tgnTokens[0]));
        }

        changeLabels[1] = (JLabel) resPanel1.addWidget(new JLabel("Recode"),
                0, 33, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        for (int i = 0; i < parsedEntities.length; i++) {
            JButton jb = (JButton) resPanel1.addWidget(
                    new JButton(parsedEntities[i].tgnTokens[0]),
                    horzSize * (1 + i), 33, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
/*
		jb.addActionListener(new NameDataRedefine(
					parsedEntities[i].orgToken,
					parsedEntities[i].tgnTokens[0]));
*/
        }

        changeLabels[2] = (JLabel) resPanel1.addWidget(new JLabel("Pattern"),
                0, 66, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        for (int i = 0; i < parsedEntities.length; i++) {
            JButton jb = (JButton) resPanel1.addWidget(
                    new JButton(parsedEntities[i].tgnGroup),
                    horzSize * (1 + i), 66, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
        }

//
// Intermediate breakdown ofter modifyPattern
//
        horzSize = 100 / (newParsedEntities.length + 1);
        changeLabels[3] = (JLabel) resPanel2.addWidget(new JLabel("Origin"),
                0, 0, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        for (int i = 0; i < newParsedEntities.length; i++) {
            JButton jb = (JButton) resPanel2.addWidget(
                    new JButton(newParsedEntities[i].orgToken),
                    horzSize * (1 + i), 0, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
        }

        changeLabels[4] = (JLabel) resPanel2.addWidget(new JLabel("Recode"),
                0, 33, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        for (int i = 0; i < newParsedEntities.length; i++) {
            JButton jb = (JButton) resPanel2.addWidget(
                    new JButton(newParsedEntities[i].tgnTokens[0]),
                    horzSize * (1 + i), 33, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
        }

        changeLabels[5] = (JLabel) resPanel2.addWidget(new JLabel("Pattern"),
                0, 66, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        for (int i = 0; i < newParsedEntities.length; i++) {
            JButton jb = (JButton) resPanel2.addWidget(
                    new JButton(newParsedEntities[i].tgnGroup),
                    horzSize * (1 + i), 66, horzSize, 33,
                    GridBagConstraints.BOTH,
                    GridBagConstraints.CENTER,
                    1.0, 0.0);
            jb.setBackground(Color.white);
            jb.setHorizontalAlignment(SwingConstants.LEFT);
        }

//
// final breakdown ofter pattern resolve
//
        horzSize = 100 / (finNspos + 1);
        changeLabels[6] = (JLabel) resPanel3.addWidget(new JLabel("Target"),
                0, 0, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        int b = 0;
        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                JButton jb = (JButton) resPanel3.addWidget(
                        new JButton(resDecomp[i][0]),
                        horzSize * (1 + b), 0, horzSize, 33,
                        GridBagConstraints.BOTH,
                        GridBagConstraints.CENTER,
                        1.0, 0.0);
                jb.setBackground(Color.white);
                jb.setHorizontalAlignment(SwingConstants.LEFT);
                b++;
            }
        }

        changeLabels[7] = (JLabel) resPanel3.addWidget(new JLabel("Value"),
                0, 33, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        b = 0;
        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                JButton jb = (JButton) resPanel3.addWidget(
                        new JButton(resDecomp[i][1]),
                        horzSize * (1 + b), 33, horzSize, 33,
                        GridBagConstraints.BOTH,
                        GridBagConstraints.CENTER,
                        1.0, 0.0);
                jb.setBackground(Color.white);
                jb.setHorizontalAlignment(SwingConstants.LEFT);
                b++;
            }
        }

        changeLabels[8] = (JLabel) resPanel3.addWidget(new JLabel("Pattern"),
                0, 66, horzSize, 33,
                GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,
                1.0, 0.0);
        b = 0;
        for (int i = 0; i < resDecomp.length; i++) {
            if (resDecomp[i][0].length() > 0) {
                JButton jb = (JButton) resPanel3.addWidget(
                        new JButton(resDecomp[i][2]),
                        horzSize * (1 + b), 66, horzSize, 33,
                        GridBagConstraints.BOTH,
                        GridBagConstraints.CENTER,
                        1.0, 0.0);
                jb.setBackground(Color.white);
                jb.setHorizontalAlignment(SwingConstants.LEFT);
                b++;
            }
        }

        reconsName.setText(iContact.getField(OFT.FullName));

        saluName.setText(iContact.getField(OFT.Salutation));

        patternText.setText(patternIso);

        gender.setText(iContact.getField(OFT.Gender));

        pattern.setText(patcode);

        for (int i = 0; i < changeLabels.length; i++) {
            changeLabels[i].setBackground(Color.white);
            changeLabels[i].setOpaque(true);
        }
        faFrame.invalidate();
        faFrame.validate();
    }

    private class NameDataOrigin implements ActionListener {
        String origText = "";
        String properText = "";

        public NameDataOrigin(String origText, String properText) {
            this.origText = origText;
            this.properText = properText;
        }

        public void actionPerformed(ActionEvent ae) {
            w_tab_PNE.setSelectedIndex(1);
            defineFilter.setText(origText);
            redefineFilter.setText(properText.toLowerCase());
            defineText.setText(origText);
            redefineText.setText(properText);
            defineFilterChanged();
        }
    }

    private class NameDataRedefine implements ActionListener {
        String origText = "";
        String properText = "";

        public NameDataRedefine(String origText, String properText) {
            this.origText = origText;
            this.properText = properText;
        }

        public void actionPerformed(ActionEvent ae) {
            w_tab_PNE.setSelectedIndex(1);
            defineFilter.setText(origText);
            redefineFilter.setText(properText.toLowerCase());
            defineText.setText(origText);
            redefineText.setText(properText);
            redefineFilterChanged();
        }
    }

    private JPanel makeNameDataPanel() {
        JGridBagPanel me = new JGridBagPanel(500, 500);

        defineText = (STextField) me.addWidget(new STextField(""),
                0, 0, 50, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        defineText.setFont(uniFont);

        redefineText = (STextField) me.addWidget(new STextField(""),
                50, 0, 50, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        redefineText.setFont(uniFont);

        defineIso = (JComboBox) me.addWidget(new JComboBox(countryCodes),
                0, 7, 15, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        defineIso.setSelectedItem("ROW");

        defineType = (JComboBox) me.addWidget(new JComboBox(typeCodeWords),
                15, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);

        redefineType = (JComboBox) me.addWidget(
                new JComboBox(standardGenderCodeWords),
                35, 7, 15, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);

        nameUpdate = (JButton) me.addWidget(new JButton("Update"),
                50, 7, 17, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        nameUpdate.setEnabled(false);
        nameUpdate.addActionListener(new NameUpdate(false, false));

        nameCreate = (JButton) me.addWidget(new JButton("Create"),
                67, 7, 16, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        nameCreate.addActionListener(new NameUpdate(true, false));

        nameRemove = (JButton) me.addWidget(new JButton("Remove"),
                83, 7, 17, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        nameRemove.setEnabled(false);
        nameRemove.addActionListener(new NameUpdate(false, true));

        me.addWidget(new JLabel("Filters:"),
                0, 16, 15, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        defineFilter = (STextField) me.addWidget(new STextField(""),
                20, 16, 45, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        defineFilter.setFont(uniFont);
        defineFilter.addKeyListener(new DefineFilterListener());

        redefineFilter = (STextField) me.addWidget(new STextField(""),
                65, 16, 35, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        redefineFilter.setFont(uniFont);

        me.addWidget(new JLabel(),
                0, 23, 2, 70,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);


        nameDataList = new JList();
        nameDataList.setFont(new Font("Lucida Sans Typewriter",
                Font.PLAIN, 9));
        nameDataList.addListSelectionListener(new NameListSelection());

        me.addWidget(new JScrollPane(nameDataList,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                2, 23, 98, 77,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 1.0);

        return me;
    }

    private class DefineFilterListener extends KeyAdapter {
        public void keyReleased(KeyEvent ke) {
            defineFilterChanged();
        }
    }

    protected void defineFilterChanged() {
        String filterValue = defineFilter.getText();
        filterNameList(filterValue, true);
    }

    protected void redefineFilterChanged() {
        String filterValue = redefineFilter.getText();
        filterNameList(filterValue, false);
    }

    private void filterNameList(String filterValue, boolean keyRequest) {
        if (filterValue.length() < 1) {
            String[] itemCopy2 = {"Enter filter value"};
            nameDataList.setListData(itemCopy2);
            return;
        }
        filterValue = filterValue.toUpperCase();

        Vector itemCopy = new Vector(50000, 50000);

        StringBuffer sb = new StringBuffer(100);

        Enumeration enumer = wordStrings.keys();
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            if (keyRequest == false ||
                    key.substring(5).startsWith(filterValue)) {
                String[] vals = (String[]) wordStrings.get(key);

                for (int i = 0; i < vals.length; i++) {
                    sb.setLength(0);
                    if (keyRequest == false &&
                            vals[i].substring(6).toUpperCase().startsWith(
                                    filterValue)) {
                        continue;
                    }
                    if (key.length() > 25 || vals[i].length() > 31) {
                        sb.append(key + " ! " + vals[i].substring(6) + " ! ");
                        //System.err.println("Key length overrun at "+key);
                    } else {
                        sb.append(key + "                          ");
                        sb.setLength(27);
                        sb.append(vals[i].substring(6) +
                                "                           ");

                        sb.setLength(54);
                    }
                    sb.append(vals[i].substring(2, 3) +
                            "  " + vals[i].substring(3, 4));
                    itemCopy.add(sb.toString());
                }
            }
        }
        String[] itemCopy2 = new String[itemCopy.size()];
        itemCopy.copyInto(itemCopy2);
        new StringSort(itemCopy2);
        nameDataList.setListData(itemCopy2);
        nameUpdate.setEnabled(false);
        nameRemove.setEnabled(false);
    }

    private class NameListSelection implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            String sel = (String) nameDataList.getSelectedValue();
            if (sel != null) {
                if (sel.indexOf("!") > 0) {
                    SuperStringTokenizer str = new SuperStringTokenizer(
                            sel, "!", false);
                    String[] stx = str.getStringList();
                    lastNameSel[0] = stx[0].substring(0, 3);
                    lastNameSel[1] = stx[0].substring(5).trim();
                    lastNameSel[2] = stx[1].trim();
                    stx[2] = stx[2].trim();
                    lastNameSel[3] = stx[2].substring(0, 1);
                    lastNameSel[4] = stx[2].substring(3, 4);
                } else {
                    lastNameSel[0] = sel.substring(0, 3).trim();
                    lastNameSel[1] = sel.substring(5, 25).trim();
                    lastNameSel[2] = sel.substring(27, 52).trim();
                    lastNameSel[3] = sel.substring(54, 55);
                    lastNameSel[4] = sel.substring(57, 58);
                }
            }
            defineText.setText(lastNameSel[1].trim());
            redefineText.setText(lastNameSel[2].trim());

            defineIso.setSelectedItem(lastNameSel[0]);

            int ind = typeCodes.indexOf(lastNameSel[3].trim());
            defineType.setSelectedIndex(ind + 1);

            ind = standardGenderCodes.indexOf(lastNameSel[4].trim());
            redefineType.setSelectedIndex(ind + 1);

            nameUpdate.setEnabled(true);
            nameRemove.setEnabled(true);
        }
    }

    private class NameUpdate implements ActionListener {
        boolean create = false;
        boolean delete = false;

        public NameUpdate(boolean create, boolean delete) {
            this.create = create;
            this.delete = delete;
        }

        public void actionPerformed(ActionEvent ae) {
            String defText = defineText.getText().trim();
            if (defText.length() == 0) {
                return;
            }
            String redefText = redefineText.getText().trim();
            redefText = (redefText.length() == 0) ? " " : redefText;

            String defIso = (String) defineIso.getSelectedItem();
            int defType = defineType.getSelectedIndex();
            int redefType = redefineType.getSelectedIndex();

            if (defType < 1 || redefType < 1) {
                return;
            }

            String[] stt = {"D", defIso, defText, redefText,
                    typeCodes.substring(defType - 1, defType) +
                            standardGenderCodes.substring(redefType - 1, redefType)};

            if (create == false || delete == true) {
                String[] remSel = {"D", lastNameSel[0], lastNameSel[1],
                        lastNameSel[2], lastNameSel[3] + lastNameSel[4]};

                System.err.println("Removing " + remSel[1] + "  " + remSel[2] +
                        "(" + remSel[3] + "), type " + remSel[4]);
                removeWordEntry(remSel);
                nameDataModified = true;
            }
            if (delete == false) {
                if (insertWordEntry(stt) == false) {
                    statusBar.setBackground(Color.red);
                    statusBar.setText("Cannot create entry - already " +
                            "exists");
                } else {
                    statusBar.setBackground(Color.green);
                    statusBar.setText("New entry created");
                    nameDataModified = true;
                }
            } else {
                statusBar.setBackground(Color.yellow);
                statusBar.setText("Entry deleted");
            }
            filterNameList(defText, true);
        }
    }

    private class SaveTable implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            writeNameData();
            writeModifyData();
        }
    }

    protected void writeNameData() {
        if (nameDataModified == false) {
            return;
        }
        BufferedWriter bw = openOutputFile("namedata.rep");

        Vector itemCopy = new Vector(50000, 50000);

        Enumeration enumer = wordStrings.keys();
        while (enumer.hasMoreElements()) {
            String key = (String) enumer.nextElement();
            String[] data = (String[]) wordStrings.get(key);

            for (int i = 0; i < data.length; i++) {
                itemCopy.add("D\t" + key.substring(0, 3) + "\t" +
                        key.substring(5) + "\t" + data[i].substring(6) +
                        "\t" + data[i].substring(2, 4) + "\n");
            }
        }
        String[] itemCopy2 = new String[itemCopy.size()];
        itemCopy.copyInto(itemCopy2);
        new StringSort(itemCopy2);
        boolean success = true;
        for (int i = 0; i < itemCopy2.length; i++) {
            try {
                bw.write(itemCopy2[i]);
            } catch (IOException ioe) {
                System.err.println("WRITE FAILED");
                success = false;
            }
        }
        closeOutputFile(bw);
        if (success) {
            File f1 = new File("namedata.txt");
            File f2 = new File("namedata.txt.old");
            f1.renameTo(f2);

            File f3 = new File("namedata.rep");
            File f4 = new File("namedata.txt");
            f3.renameTo(f4);

            nameDataModified = false;
        }
    }

    private class PatternLoad implements ActionListener {
        boolean loadAll = false;

        public PatternLoad(boolean loadAll) {
            this.loadAll = loadAll;
        }

        public void actionPerformed(ActionEvent ae) {
            String path = getTablePath();
            try {
                loadingDefs = false;
                boolean loaded = loadPatterns(
                        path + File.separator + "patterndata.txt", false, true);
                if (loaded == false) {
                    setStatusBar("Patterns failed to reload", Color.red);
                } else {
                    setStatusBar("Patterns successfully reloaded",
                            Color.green);
                }
                if (loadAll) {
                    loadingDefs = true;
                    loaded = loadPatterns(
                            path + File.separator + "patterndefs.txt", false, false);
                    if (loaded == false) {
                        setStatusBar("Default Patterns failed to reload",
                                Color.red);
                    } else {
                        setStatusBar("Default Patterns successfully " +
                                "reloaded", Color.green);
                    }
                }
            } catch (IOException ioe) {
                setStatusBar("IOException reloading pattern data",
                        Color.red);
            }
        }
    }

    protected void writeModifyData() {
        if (modifyDataModified == false) {
            return;
        }
        BufferedWriter bw = openOutputFile("patternmods.rep");

        boolean success = true;
        for (int i = 0; i < modifyPatternHolder.length; i++) {
            try {
                bw.write(modifyPatternHolder[i].toString() + "\n");
            } catch (IOException ioe) {
                System.err.println("WRITE PATTERN MOD FAILED");
                success = false;
            }
        }

        closeOutputFile(bw);

        if (success) {
            File f1 = new File("patternmods.txt");
            File f2 = new File("patternmods.txt.old");
            f1.renameTo(f2);

            File f3 = new File("patternmods.rep");
            File f4 = new File("patternmods.txt");
            f3.renameTo(f4);
            modifyDataModified = false;
        }
    }

    private class ExitAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (nameDataModified || modifyDataModified) {
                setStatusBar("Modified name or pattern-mod data exists",
                        Color.red);
            } else {
                System.exit(0);
            }
        }
    }

    private class LoadData implements ActionListener {
        boolean writeOut = false;

        public LoadData(boolean writeOut) {
            this.writeOut = writeOut;
        }

        public void actionPerformed(ActionEvent ae) {
            processNext.setEnabled(false);
            processMode.setEnabled(false);
            JFileChooser fc = new JFileChooser(
                    System.getProperties().getProperty("user.home"));
            fc.setDialogTitle("Load Name Data for processing");
            int option = fc.showOpenDialog(faFrame);

            inputDataFile = closeInputFile(inputDataFile);
            outputDataFile = closeOutputFile(outputDataFile);
            String fileName = "";

            if (option == JFileChooser.APPROVE_OPTION) {
                File selFile = fc.getSelectedFile();
                fileName = selFile.getAbsolutePath();
                inputDataFile = openInputFile(fileName);
                inputDataRecno = 0;
                numPasses = 0;
            }
            if (inputDataFile != null) {
                statusBar.setBackground(Color.green);
                statusBar.setText("File " + fileName + " opened");
                processMode.setEnabled(true);
                if (processMode.getSelectedIndex() > 0) {
                    System.err.println("Setting active " +
                            processMode.getSelectedIndex());
                    processNext.setEnabled(true);
                }
                if (writeOut) {
                    String outFileName = fileName + ".out.1";
                    int i = fileName.length() - 1;
                    for (; i >= 0 && Character.isDigit(fileName.charAt(i));
                         i--)
                        ;
                    if (i < fileName.length() - 1) {
                        int fnum = Integer.parseInt(fileName.substring(i + 1));
                        outFileName = fileName.substring(0, i + 1) +
                                String.valueOf(fnum + 1);
                    }
                    outputDataFile = openOutputFile(outFileName);
                }
            } else {
                statusBar.setBackground(Color.red);
                statusBar.setText("File " + fileName + " not opened");
            }
        }
    }

    // SDN
    private JPanel makeModifyPatternPanel() {
        JGridBagPanel me = new JGridBagPanel(500, 500);

        defineModifyText = (STextField) me.addWidget(new STextField(""),
                0, 0, 40, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        defineModifyText.setFont(uniFont);

        redefineModifyText = (STextField) me.addWidget(new STextField(""),
                40, 0, 40, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        redefineModifyText.setFont(uniFont);

        formatModifyText = (STextField) me.addWidget(new STextField(""),
                80, 0, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        formatModifyText.setFont(uniFont);

        defineModifyIso = (JComboBox) me.addWidget(
                new JComboBox(countryCodes),
                0, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        defineModifyIso.setSelectedItem("ROW");

        defineModifyPosition = (JComboBox) me.addWidget(
                new JComboBox(modifyPositions),
                20, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);

        modifyUpdate = (JButton) me.addWidget(new JButton("Update"),
                40, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        modifyUpdate.setEnabled(false);
        modifyUpdate.addActionListener(new ModifyUpdate(false, false));

        modifyCreate = (JButton) me.addWidget(new JButton("Create"),
                60, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        modifyCreate.addActionListener(new ModifyUpdate(true, false));

        modifyRemove = (JButton) me.addWidget(new JButton("Remove"),
                80, 7, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        modifyRemove.setEnabled(false);
        modifyRemove.addActionListener(new ModifyUpdate(false, true));

        me.addWidget(new JLabel("Filters:"),
                0, 16, 15, 7,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);

        modifyCountryFilter = (STextField) me.addWidget(new STextField(""),
                15, 16, 15, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        //defineFilter.addKeyListener(new DefineFilterListener());
        modifyCountryFilter.setFont(uniFont);

        modifyDefineFilter = (STextField) me.addWidget(new STextField(""),
                30, 16, 40, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        modifyDefineFilter.setFont(uniFont);
        modifyDefineFilter.addKeyListener(new ModifyDefineFilter());

        modifyRedefineFilter = (STextField) me.addWidget(new STextField(""),
                70, 16, 30, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        modifyRedefineFilter.setFont(uniFont);

        me.addWidget(new JLabel(),
                0, 23, 2, 70,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0.0, 0.0);


        modifyPatternList = new JList();
        modifyPatternList.setFont(new Font("Lucida Sans Typewriter",
                Font.PLAIN, 9));
        modifyPatternList.addListSelectionListener(
                new ModifyPatternListSelection());
        me.addWidget(new JScrollPane(modifyPatternList,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                2, 23, 98, 77,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 1.0);

        filterModifyList("", true);

        return me;
    }

    private class ModifyPatternListSelection
            implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            String sel = (String) modifyPatternList.getSelectedValue();
            if (sel != null) {
                if (sel.indexOf("!") > 0) {
                    SuperStringTokenizer str = new SuperStringTokenizer(
                            sel, "!", false);
                    String[] stx = str.getStringList();
                    lastModifySel[0] = stx[0].substring(0, 3);
                    lastModifySel[1] = stx[0].substring(5).trim();
                    lastModifySel[2] = stx[1].trim();
                    lastModifySel[3] = stx[2].trim();
                    lastModifySel[4] = stx[3].trim();
                } else {
                    lastModifySel[0] = sel.substring(0, 3);
                    lastModifySel[1] = sel.substring(5, 20).trim();
                    lastModifySel[2] = sel.substring(22, 37).trim();
                    lastModifySel[3] = sel.substring(39, 54).trim();
                    lastModifySel[4] = sel.substring(56);
                }
            }
            defineModifyText.setText(lastModifySel[1]);
            redefineModifyText.setText(lastModifySel[2]);
            formatModifyText.setText(lastModifySel[3]);

            defineModifyIso.setSelectedItem(lastModifySel[0]);
            defineModifyPosition.setSelectedItem(lastModifySel[4]);

            modifyUpdate.setEnabled(true);
            modifyRemove.setEnabled(true);
        }
    }

    private class ModifyUpdate implements ActionListener {
        boolean create = false;
        boolean delete = false;

        public ModifyUpdate(boolean create, boolean delete) {
            this.create = create;
            this.delete = delete;
        }

        public void actionPerformed(ActionEvent ae) {
            String defText = defineModifyText.getText();
            String redefText = redefineModifyText.getText();
            String formatText = formatModifyText.getText();
            if (defText.length() == 0 || redefText.length() == 0 ||
                    formatText.length() == 0) {
                return;
            }

            String defIso = (String) defineModifyIso.getSelectedItem();
            String redefPos = (String) defineModifyPosition.getSelectedItem();

            if (defineModifyPosition.getSelectedIndex() < 1) {
                return;
            }

            String[] stt = {defIso, defText, redefText, formatText, redefPos};

            if (create == false || delete == true) {
                System.err.println("Removing " + lastModifySel[1] + ", redef " +
                        lastModifySel[2] + ", format " + lastModifySel[3]);

                removeModifyEntry(lastModifySel);
                modifyDataModified = true;
                if (defText.lastIndexOf(" ") > 0) {
                    filterModifyList(defText.substring(0,
                            defText.lastIndexOf(" ")), true);
                } else {
                    filterModifyList(defText, true);
                }
            }
            if (delete == false) {
                try {
                    PatternHolder newPh = new PatternHolder(
                            stt[0], stt[1], stt[2],
                            stt[3], stt[4], modifyPatternHolder.length + 1000);
                    validatePatternHolder(newPh, "Test Action");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    setStatusBar(e.getMessage(), Color.red);
                    return;
                }

                if (insertModifyEntry(stt) == false) {
                    statusBar.setBackground(Color.red);
                    statusBar.setText("Cannot create entry - already " +
                            "exists or pattern definition error");
                } else {
                    statusBar.setBackground(Color.green);
                    statusBar.setText("New Pattern Modify entry created");
                    modifyDataModified = true;
                    if (defText.lastIndexOf(" ") > 0) {
                        filterModifyList(defText.substring(0,
                                defText.lastIndexOf(" ")), true);
                    } else {
                        filterModifyList(defText, true);
                    }
                }
            } else {
                statusBar.setBackground(Color.yellow);
                statusBar.setText("Pattern Modify Entry deleted");
            }
        }
    }

    private class ModifyDefineFilter extends KeyAdapter {
        public void keyReleased(KeyEvent ke) {
            modifyDefineFilterChanged();
        }
    }

    protected void modifyDefineFilterChanged() {
        String filterValue = modifyDefineFilter.getText();
        filterModifyList(filterValue, true);
    }

    private void filterModifyList(String filterValue, boolean keyRequest) {
        filterValue = filterValue.toUpperCase();

        String[] itemCopy = new String[modifyPatternHolder.length];
        int numStr = 0;

        StringBuffer sb = new StringBuffer(100);

        for (int i = 0; i < modifyPatternHolder.length; i++) {
            PatternHolder ph = modifyPatternHolder[i];

            String key = ph.toString(ph.sourcesOrg).toUpperCase();
            String replace = ph.toString(ph.replaces).toUpperCase();

            if ((keyRequest == true && key.indexOf(filterValue) >= 0) ||
                    (keyRequest == false && replace.indexOf(filterValue) >= 0)) {
                sb.setLength(0);
                sb.append(ph.countryIso + "  ");

                String format = ph.toString(ph.formats).toUpperCase();
                if (key.length() > 15 || replace.length() > 15 ||
                        format.length() > 15) {
                    sb.append(key + " ! " + replace + " ! " + format + " ! ");
                    //System.err.println("Key length overrun at "+ key);
                } else {
                    sb.append(key + "                          ");
                    sb.setLength(22);
                    sb.append(replace + "                           ");
                    sb.setLength(39);
                    sb.append(format + "                           ");
                    sb.setLength(56);
                }
                sb.append(ph.getPatternPos());
                itemCopy[numStr++] = sb.toString();
            }
        }
        String[] itemCopy2 = new String[numStr];
        System.arraycopy(itemCopy, 0, itemCopy2, 0, numStr);
        new StringSort(itemCopy2);
        modifyPatternList.setListData(itemCopy2);
        modifyUpdate.setEnabled(false);
        modifyRemove.setEnabled(false);
    }

    // SDN
    private JPanel makeMissedPatternPanel() {
        JGridBagPanel me = new JGridBagPanel(500, 500);

        missedPatternList = new JList();
        missedPatternList.setFont(new Font("Lucida Sans Typewriter",
                Font.PLAIN, 9));
        me.addWidget(new JScrollPane(missedPatternList,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                0, 0, 100, 93,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 1.0);

        missedPatternRefresh = (JButton) me.addWidget(new JButton("Refresh"),
                0, 93, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        missedPatternRefresh.addActionListener(new MissedPatternRefresh());

        missedPatternClear = (JButton) me.addWidget(new JButton("Clear"),
                20, 93, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        missedPatternClear.addActionListener(new MissedPatternClear());

        return me;
    }

    private class MissedPatternRefresh implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            VectorPrintWriter vp = new VectorPrintWriter();
            printPatternStats(vp);
            missedPatternList.setListData(vp);
        }
    }

    private class MissedPatternClear implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            missedPatterns = new Hashtable();
            missedPatternList.setListData(new Vector());
        }
    }

    // SDN
    private JPanel makeMissedWordPanel() {
        JGridBagPanel me = new JGridBagPanel(500, 500);

        missedWordList = new JTextArea();
        missedWordList.setFont(uniFont);
        me.addWidget(new JScrollPane(missedWordList,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                0, 0, 100, 93,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                1.0, 1.0);

        missedWordRefresh = (JButton) me.addWidget(new JButton("Refresh"),
                0, 93, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        missedWordRefresh.addActionListener(new MissedWordRefresh());

        missedWordClear = (JButton) me.addWidget(new JButton("Clear"),
                20, 93, 20, 7,
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0.0, 0.0);
        missedWordClear.addActionListener(new MissedWordClear());

        return me;
    }

    private class MissedWordRefresh implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            VectorPrintWriter vp = new VectorPrintWriter();
            printWordStats(vp);
            StringBuffer sb = new StringBuffer(50000);
            for (int i = 0; i < vp.size(); i++) {
                sb.append((String) vp.elementAt(i) + "\n");
            }
            missedWordList.setText(sb.toString());
            missedWordList.moveCaretPosition(0);
        }
    }

    private class MissedWordClear implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            missedWords = new Hashtable();
            missedWordList.setText("");
        }
    }

    //
// Method to snoop into the system event queue stroke sequencers looking for
// the use of the escape key
//
    public boolean testEscapeKey() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        EventQueue event = tk.getSystemEventQueue();
        AWTEvent ev;

        int goes = 0;
        try {
            Thread.yield();
            //System.out.println("In test event");

            while (event.peekEvent() != null) {
                ev = event.getNextEvent();
                String sev = ev.toString();

                if (sev.indexOf("KEY_RELEASED") >= 0 &&
                        sev.indexOf("Escape") >= 0) {
                    return true;
                }
                goes++;
                if (goes > 100) {
                    return false;
                }
            }
        } catch (InterruptedException eint) {
        }

        Thread.yield();

        return false;
    }


    private class ParsedEntity {
        String orgToken = null;
        String[] tgnTokens = null;
        String tgnGroup = null;

        PatternHolder tokenModifier;
        boolean modifiable = true;

        //
// Standard new token constructor with token resolve
//
        public ParsedEntity(String token) {
            orgToken = new String(token);
            String ident[] = lookupPhrase(orgToken);
            tgnGroup = ident[0];
            tgnTokens = new String[ident.length - 1];
            System.arraycopy(ident, 1, tgnTokens, 0, tgnTokens.length);
        }

        //
// new pe with prevalued token and tgn - no lookup
//
        public ParsedEntity(String token, String tgnGroup) {
            orgToken = new String(token);
            this.tgnGroup = tgnGroup;
            tgnTokens = new String[1];
            tgnTokens[0] = "";
        }

        public String getFormattedOutput() {
            String str = "";
            if (tgnTokens != null && tgnTokens.length > 0) {
                for (int i = 0; i < tgnTokens.length; i++) {
                    str += " " + tgnTokens[i];
                }
                return str.substring(1);
            }
            return str;
        }
    }

    private final boolean isLetter(char chr) {
        if (Character.isLetter(chr) || (chr >= 3585 && chr <= 3662)) {
            return true;
        }
        return false;
    }

    private final boolean isLetterOrDigit(char chr) {
        if (Character.isLetterOrDigit(chr) || (chr >= 3585 && chr <= 3662)) {
            return true;
        }
        return false;
    }
}
