package com.hartehanks.dev.misc;

import java.util.*;
import java.io.*;
import java.math.*;

public class Conversion
{
        private static int st_res1;
        private static int st_res2;
        private static int st_res3;
        private static int st_res4;
        private static int st_i;
        private static String[] hex_top = new String[256];
        private static String[] hex_bot = new String[256];
	private static char[] c_top;
	private static char[] c_bot;
	private static char[] c_all;
	private static String hexValues = "0123456789ABCDEF";
	private static boolean inUTF8 = false;

	private static int[] ebcasc =
	{
	    0xD8, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67,  /* 80 - 87 */
	    0x68, 0x69, 0xAB, 0xBB, 0xF0, 0xFD, 0xFE, 0xB1,  /* 88 - 8F */
	    0xB0, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70,  /* 90 - 97 */
	    0x71, 0x72, 0xAA, 0xBA, 0xE6, 0xB8, 0xC6, 0xA4,  /* 98 - 9F */
	    0xB5, 0x7E, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,  /* A0 - A7 */
	    0x79, 0x7A, 0xA1, 0xBF, 0xD0, 0x5B, 0xDE, 0xAE,  /* A8 - AF */
	    0xAC, 0xA3, 0xA5, 0xB7, 0xA9, 0xA7, 0xB6, 0xBC,  /* B0 - B7 */
	    0xBD, 0xBE, 0xDD, 0xA8, 0xAF, 0x5D, 0xB4, 0xD7,  /* B8 - BF */
	    0x7B, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47,  /* C0 - C7 */
	    0x48, 0x49, 0xAD, 0xF4, 0xF6, 0xF2, 0xF3, 0xF5,  /* C8 - CF */
	    0x7D, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50,  /* D0 - D7 */
	    0x51, 0x52, 0xB9, 0xFB, 0xFC, 0xF9, 0xFA, 0xFF,  /* D8 - DF */
	    0x5C, 0xF7, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,  /* E0 - E7 */
	    0x59, 0x5A, 0xB2, 0xD4, 0xD6, 0xD2, 0xD3, 0xD5,  /* E8 - EF */
	    0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,  /* F0 - F7 */
	    0x38, 0x39, 0xB3, 0xDB, 0xDC, 0xD9, 0xDA, 0x9F,  /* F8 - FF */
	    0x00, 0x01, 0x02, 0x03, 0x9C, 0x09, 0x86, 0x7F,  /* 00 - 07 */
	    0x97, 0x8D, 0x8E, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,  /* 08 - 0F */
	    0x10, 0x11, 0x12, 0x13, 0x9D, 0x85, 0x08, 0x87,  /* 10 - 17 */
	    0x18, 0x19, 0x92, 0x8F, 0x1C, 0x1D, 0x1E, 0x1F,  /* 18 - 1F */
	    0x80, 0x81, 0x82, 0x83, 0x84, 0x0A, 0x17, 0x1B,  /* 20 - 27 */
	    0x88, 0x89, 0x8A, 0x8B, 0x8C, 0x05, 0x06, 0x07,  /* 28 - 2F */
	    0x90, 0x91, 0x16, 0x93, 0x94, 0x95, 0x96, 0x04,  /* 30 - 37 */
	    0x98, 0x99, 0x9A, 0x9B, 0x14, 0x15, 0x9E, 0x1A,  /* 38 - 3F */
	    0x20, 0xA0, 0xE2, 0xE4, 0xE0, 0xE1, 0xE3, 0xE5,  /* 40 - 47 */
	    0xE7, 0xF1, 0xA2, 0x2E, 0x3C, 0x28, 0x2B, 0x7C,  /* 48 - 4F */
	    0x26, 0xE9, 0xEA, 0xEB, 0xE8, 0xED, 0xEE, 0xEF,  /* 50 - 57 */
	    0xEC, 0xDF, 0x21, 0x24, 0x2A, 0x29, 0x3B, 0x5E,  /* 58 - 5F */
	    0x2D, 0x2F, 0xC2, 0xC4, 0xC0, 0xC1, 0xC3, 0xC5,  /* 60 - 67 */
	    0xC7, 0xD1, 0xA6, 0x2C, 0x25, 0x5F, 0x3E, 0x3F,  /* 68 - 6F */
	    0xF8, 0xC9, 0xCA, 0xCB, 0xC8, 0xCD, 0xCE, 0xCF,  /* 70 - 77 */
	    0xCC, 0x60, 0x3A, 0x23, 0x40, 0x27, 0x3D, 0x22   /* 78 - 7F */
	};
	private static int[] ascebc =
	{
	    0x20, 0x21, 0x22, 0x23, 0x24, 0x15, 0x06, 0x17,  /* 80 - 87 */
	    0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x09, 0x0A, 0x1B,  /* 88 - 8F */
	    0x30, 0x31, 0x1A, 0x33, 0x34, 0x35, 0x36, 0x08,  /* 90 - 97 */
	    0x38, 0x39, 0x3A, 0x3B, 0x04, 0x14, 0x3E, 0xFF,  /* 98 - 9F */
	    0x41, 0xAA, 0x4A, 0xB1, 0x9F, 0xB2, 0x6A, 0xB5,  /* A0 - A7 */
	    0xBB, 0xB4, 0x9A, 0x8A, 0xB0, 0xCA, 0xAF, 0xBC,  /* A8 - AF */
	    0x90, 0x8F, 0xEA, 0xFA, 0xBE, 0xA0, 0xB6, 0xB3,  /* B0 - B7 */
	    0x9D, 0xDA, 0x9B, 0x8B, 0xB7, 0xB8, 0xB9, 0xAB,  /* B8 - BF */
	    0x64, 0x65, 0x62, 0x66, 0x63, 0x67, 0x9E, 0x68,  /* C0 - C7 */
	    0x74, 0x71, 0x72, 0x73, 0x78, 0x75, 0x76, 0x77,  /* C8 - CF */
	    0xAC, 0x69, 0xED, 0xEE, 0xEB, 0xEF, 0xEC, 0xBF,  /* D0 - D7 */
	    0x80, 0xFD, 0xFE, 0xFB, 0xFC, 0xBA, 0xAE, 0x59,  /* D8 - DF */
	    0x44, 0x45, 0x42, 0x46, 0x43, 0x47, 0x9C, 0x48,  /* E0 - E7 */
	    0x54, 0x51, 0x52, 0x53, 0x58, 0x55, 0x56, 0x57,  /* E8 - EF */
	    0x8C, 0x49, 0xCD, 0xCE, 0xCB, 0xCF, 0xCC, 0xE1,  /* F0 - F7 */
	    0x70, 0xDD, 0xDE, 0xDB, 0xDC, 0x8D, 0x8E, 0xDF,  /* F8 - FF */
	    0x00, 0x01, 0x02, 0x03, 0x37, 0x2D, 0x2E, 0x2F,  /* 00 - 07 */
	    0x16, 0x05, 0x25, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,  /* 08 - 0F */
	    0x10, 0x11, 0x12, 0x13, 0x3C, 0x3D, 0x32, 0x26,  /* 10 - 17 */
	    0x18, 0x19, 0x3F, 0x27, 0x1C, 0x1D, 0x1E, 0x1F,  /* 18 - 1F */
	    0x40, 0x5A, 0x7F, 0x7B, 0x5B, 0x6C, 0x50, 0x7D,  /* 20 - 27 */
	    0x4D, 0x5D, 0x5C, 0x4E, 0x6B, 0x60, 0x4B, 0x61,  /* 28 - 2F */
	    0xF0, 0xF1, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7,  /* 30 - 37 */
	    0xF8, 0xF9, 0x7A, 0x5E, 0x4C, 0x7E, 0x6E, 0x6F,  /* 38 - 3F */
	    0x7C, 0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7,  /* 40 - 47 */
	    0xC8, 0xC9, 0xD1, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6,  /* 48 - 4F */
	    0xD7, 0xD8, 0xD9, 0xE2, 0xE3, 0xE4, 0xE5, 0xE6,  /* 50 - 57 */
	    0xE7, 0xE8, 0xE9, 0xAD, 0xE0, 0xBD, 0x5F, 0x6D,  /* 58 - 5F */
	    0x79, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87,  /* 60 - 67 */
	    0x88, 0x89, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96,  /* 68 - 6F */
	    0x97, 0x98, 0x99, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6,  /* 70 - 77 */
	    0xA7, 0xA8, 0xA9, 0xC0, 0x4F, 0xD0, 0xA1, 0x07   /* 78 - 7F */
	};

        static
        {
            String top = "";
            String bot = "";
            String un = "";
            int k = 0;

            for (int i = 0; i < 16; i++)
            {
                int l = 0;
                for (int j = k; j < (k + 16); j++)
                {
                    if (k == 256)
                    {
                        break;
                    }
//
// Call getHex with a different number upto 16 every time loop is executed
//
                    hex_bot[j] = "";
                    bot = getHex(l);
                    hex_bot[j] = bot;
//
// Set reverse array for converting unsigned values
//
                    hex_top[j] = "";
                    top = getHexSign(i);
                    hex_top[j] = top;

                    l++;
                }
                k += 16;
            }
	    String fe = System.getProperties().getProperty("file.encoding");
	    inUTF8 = (fe.equals("UTF-8")) ? true : false;
        }
//
// public static convertor methods
//
        public static int byteToInteger(byte[] data, int data_ptr)
        {
            st_res1 = data[data_ptr];
            st_res1 = (st_res1 + 256) % 256;
            st_res2 = data[data_ptr + 1];
            st_res2 = (st_res2 + 256) % 256;
            st_res3 = data[data_ptr + 2];
            st_res3 = (st_res3 + 256) % 256;
            st_res4 = data[data_ptr + 3];
            st_res4 = (st_res4 + 256) % 256;
            return (st_res1 * 16777216) + (st_res2 * 65536) +
                                                (st_res3 * 256) + st_res4;
        }

        public static int byteToShort(byte[] data, int data_ptr)
        {

            st_res1 = data[data_ptr];
            st_res1 = (st_res1 + 256) % 256;
            st_res2 = data[data_ptr + 1];
            st_res2 = (st_res2 + 256) % 256;

            return (st_res1 * 256) + st_res2;
        }

        public static String byteToString(byte[] data, int data_ptr)
        {
            for (st_i = data_ptr; data[st_i] != 0; st_i++);
            return new String(data, data_ptr, st_i - data_ptr);
        }
//
// Returns a String[] with two elements to display both bytes in same row
// as the passed string argument
//
        public static String[] stringToHex(String s, int offset)
        {
//
// if the passed string is null go no further
//
            if (s == null)
            {
                return null;
            }
//
// The String may have a prefix which the caller may not wish to be converted
// If not offset will be passed as a 0
//
            s = s.substring(offset);
	    c_top = new char[s.length()];
	    c_bot = new char[s.length()];
//
// get the byte values
//
            byte[] b = s.getBytes();
            int i;
            for (i = 0; i < b.length; i++)
            {
                Byte by = new Byte(b[i]);
//
// Convert bytes to ints
//
                int bi = by.intValue();
//
// Convert ints to hex taking care of any signed values at the same time
//
                c_top[i] =  hex_top[bi + 128].charAt(0);
                c_bot[i] =  hex_bot[bi + 128].charAt(0);
            }
	    String[] hex = new String[2];
//
// copy chars into String array
//
	    hex[0] = hex[0].valueOf(c_top);
	    hex[1] = hex[1].valueOf(c_bot);

	    return hex;
        }
//
// Returns the hex of the passed String argument as a String
//
        public static String stringToHex(int offset, String s)
        {
//
// if the passed string is null go no further
//
            if (s == null)
            {
                return null;
            }
//
// The String may have a prefix which the caller may not wish to be converted
// If not offset will be passed as a 0
//
            s = s.substring(offset);
	    c_all = new char[s.length() + s.length()];
//
// get the byte values
//
            byte[] b = s.getBytes();
            int i;
	    int j = 0;
            for (i = 0; i < b.length; i++)
            {
                Byte by = new Byte(b[i]);
//
// Convert bytes to ints
//
                int bi = by.intValue();
//
// Convert ints to hex taking care of any signed values at the same time
//
                c_all[j] =  hex_top[bi + 128].charAt(0);
                c_all[j + 1] =  hex_bot[bi + 128].charAt(0);
		j += 2;
            }
	    return new String(c_all);
        }
//
// Methods to check if an argument is a valid hex format, then compare it with
// another hex value
//
	public static boolean checkHex(String hex)
	{
	    hex = hex.toUpperCase();
//
// The length of the argument must be divisble by 2
//
	    if ((hex.length() % 2) != 0)
	    {
		return false;
	    }
//
// Check that the characters are valid hex characters
//
	    for (int i = 0; i < hex.length(); i++)
	    {
		if (hexValues.indexOf(hex.charAt(i)) < 0)
		{
		    return false;
		}
	    }
	    return true;
	}

	public static int hexCmp(String s1, String s2, int length)
	{
            int len = (length > s1.length()) ? s1.length() : length;
                len = (length > s2.length()) ? s2.length() : length;

	    for (int i = 0; i < len; i++)
            {
                if (s1.charAt(i) != s2.charAt(i))
                {
                    return s1.charAt(i) - s2.charAt(i);
                }
            }
            return 0;
	}

//
// Private methods
//
//
// returns hex equivalent of decimal nmber
//
        public static String getHex(int i)
        {
            String num = "";

            switch (i)
            {
                case 10:
                    {
                     num = "A";
                     break;
                    }
                case 11:
                    {
                        num = "B";
                        break;
                    }
                case 12:
                    {
                        num = "C";
                        break;
                    }
                case 13:
                    {
                        num = "D";
                        break;
                    }
                case 14:
                    {
                        num = "E";
                        break;
                    }
                case 15:
                    {
                        num = "F";
                        break;
                    }
                default:
                    {
                        num =  + i + "";
                    }
            }
            return num;
        }
//
// Method is used to change the decimal number passed to another before
// calling the getHex method.  Used to set up array for signed values
//
        private static String getHexSign(int i)
        {
	    if (i < 8)
	    {
		i += 8;
	    }
	    else
	    {
		i -= 8;
	    }
            return getHex(i);
        }
//
// Method to convert a String value to a boolean and return it as a
// primitives boolean data type
//
	public static boolean getBooleanValue(String state)
	{
	    return getBooleanValue(state, false);
	}

	public static boolean getBooleanValue(String state, boolean defState)
	{
	    if (state != null)
	    {
		return state.equalsIgnoreCase("false") ? false: true;
	    }
	    return defState;
	}
//
// Method to convert a boolean value to a String and return it as a String
//
	public static String getBooleanStringValue(boolean state)
	{
	    return (state == false) ? "false": "true";
	}
//
// Method to convert byte[] array from EBCDIC to ASCII
//
	public static void toAscii(byte[] ebcdic)
	{
	    toAscii(ebcdic, 0, ebcdic.length);
	}
	public static void toAscii(byte[] ebcdic, int start, int length)
	{
	    for (int i = start; i < start + length; i++)
	    {
		ebcdic[i] = (byte)ebcasc[128+ebcdic[i]];
	    }
	}
//
// Method to convert byte[] array from ASCII to EBCDIC
//
	public static void toEbcdic(byte[] ascii)
	{
	    toEbcdic(ascii, 0, ascii.length);
	}
	public static void toEbcdic(byte[] ascii, int start, int length)
	{
	    for (int i = start; i < start + length; i++)
	    {
		ascii[i] = (byte)ascebc[128+ascii[i]];
	    }
	}
//
// Methods for print formatting of integers or longs
//
	public static String toPaddedString(BigDecimal value, int length)
	{
	    String str = "                    "+value.toString();
	    while (str.length() < length)
	    {
		str = "          "+str;
	    }
	    return str.substring(str.length() - length);
	}
	public static String toPaddedString(long value, int length)
	{
	    String str = "                    "+String.valueOf(value);
	    while (str.length() < length)
	    {
		str = "          "+str;
	    }
	    return str.substring(str.length() - length);
	}
	public static String toPaddedString(String value, int length)
	{
	    String str = value+ "                    ";
	    while (str.length() < length)
	    {
		str += "          ";
	    }
	    return str.substring(0, length);
	}

//
// Method to do Streing replace of multiple characters throughout whole string
//
	public static String replaceCharacters(String target,
							String from, String to)
	{
	    int flen = from.length();
	    int ind;
	    while ((ind = target.indexOf(from)) >= 0)
	    {
		target = target.substring(0, ind) + to +
						target.substring(ind + flen);
	    }

	    return target;
	}
//
// Function to check and repair a supposed UTF-8 byte stream sequence
//
// Return value indicates potential corruption occurred if true
//
	public static boolean fixUTF8(byte[] inbyte, int maxLen)
	{
	    if (inUTF8 && maxLen > 0)
	    {
//
// Test for potential unicode sequence in play
//
		maxLen = (maxLen > inbyte.length) ? inbyte.length : maxLen;
		if (maxLen > 0 && (inbyte[maxLen - 1] & 0x80) == 0x80)
		{
//
// Sequence found - back up to sequence start as determined by 0xCx
//
		    int i = maxLen - 1;
		    for (; i >= 0 && (inbyte[i] & 0xC0) == 0x80; i--);

		    if (i >= 0 && (inbyte[i] & 0xC0) == 0xC0)
		    {
			int inbLen = maxLen - i;
			int uniLen = inbyte[i] & 0xF0;
			if (inbLen == 2 &&
			    ((uniLen & 0xF0) ==0xC0 || (uniLen & 0xF0) ==0xD0))
			{
			    return false;
			}
			else if (inbLen == 3 && (uniLen & 0xF0) == 0xE0)
			{
			    return false;
			}
			else if (inbLen == 4 && (uniLen & 0xF0) == 0xF0)
			{
			    return false;
			}
		    }
		    else if (i >= 0)
		    {
			System.err.println("Logic error"+inbyte[i]);
		    }
		    else
		    {
			i = 0;
		    }

		    //System.err.println("Trimmed  "+(maxLen - i)+" bytes");
		    for (; i < maxLen; i++)
		    {
			inbyte[i] = 0x20;
		    }
		    return true;
		}
	    }
	    return false;
	}
//
// Levenstein fast score algorithm
//

	public static int getLevenshteinDistance(String s, String t,
							boolean caseSensitive)
	{
	    if (s == null || t == null)
	    {
		throw new IllegalArgumentException("Strings must not be null");
	    }

/*
  The difference between this impl. and the previous is that, rather than
  creating and retaining a matrix of size s.length()+1 by t.length()+1, we
  maintain two single-dimensional arrays of length s.length()+1.  The first,
  d, is the 'current working' distance array that maintains the newest distance
  cost counts as we iterate through the characters of String s.  Each time we
  increment the index of String t we are comparing, d is copied to p, the
  second int[].  Doing so allows us to retain the previous cost counts as
  required by the algorithm (taking the minimum of the cost count to the left,
  up one, and diagonally up and to the left of the current cost count being
  calculated).  (Note that the arrays aren't really copied anymore, just
  switched...this is clearly much better than cloning an array or doing a
  System.arraycopy() each time through the outer loop.) Effectively, the
  difference between the two implementations is this one does not cause an out
  of memory condition when calculating the LD over two very large strings.
*/
	    int n = s.length(); // length of s
	    int m = t.length(); // length of t

	    if (n + m == 0)
	    {
		return -2;
	    }
	    else if (n == 0 || m == 0)
	    {
		return -1;
	    }

	    if (caseSensitive == false)
	    {
		s = s.toUpperCase();
		t = t.toUpperCase();
	    }

	    int p[] = new int[n+1]; //'previous' cost array, horizontally
	    int d[] = new int[n+1]; // cost array, horizontally
	    int _d[]; //placeholder to assist in swapping p and d

//
// indexes into strings s and t
//
	    int i; // iterates through s
	    int j; // iterates through t

	    char t_j; // jth character of t

	    int cost; // cost

	    for (i = 0; i <= n; i++)
	    {
		p[i] = i;
	    }

	    for (j = 1; j <= m; j++)
	    {
		t_j = t.charAt(j-1);
		d[0] = j;

		for (i = 1; i <= n; i++)
		{
		    cost = s.charAt(i-1) == t_j ? 0 : 1;
//
// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
//
		    d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
		}

//
// copy current distance counts to 'previous row' distance counts
//
		_d = p;
		p = d;
		d = _d;
	    }

//
// our last action in the above loop was to switch d and p, so p now
// actually has the most recent cost counts
//
	    int finalScore = 100;
	    if (p[n] > 0)
	    {
		int minLen = (n < m) ? n : m;
		if (p[n] >= ((3 + minLen) / 4))
		{
		    if (p[n] >= ((1 + minLen) / 2))
		    {
			finalScore -= (p[n] + 25);
		    }
		    else
		    {
			finalScore -= (p[n] + 10);
		    }
		}
		else
		{
		    finalScore -= p[n];
		}
		finalScore += (n >= 9 || m >= 9) ? 1 : 0;
	    }
	    return (finalScore >= 0) ? finalScore : 0;
	}
//
//
// Test Methods
//
// Method to call if wish to see contents of arrays
//
        public static void testMethod()
        {
            System.err.println("");
            System.err.println("");
            System.err.println("");
            System.err.println("");
            System.err.println("Top Hex Row for indexing");
            for (int i = 0; i < hex_bot.length; i++)
            {
                System.err.print(hex_top[i]);
            }
            System.err.println("");
            System.err.println("");
            System.err.println("Bottom Hex Row indexing");
            for (int i = 0; i < hex_bot.length; i++)
            {
                System.err.print(hex_bot[i]);
            }
            System.err.println("");
            System.err.println("");
        }

	public static void main(String[] args)
	{
	    System.err.println("Lev: "+
		getLevenshteinDistance(args[0], args[1], false));
	}
}
