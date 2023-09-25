package com.hartehanks.dev.io;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public class SuperBufferedInputStream extends BufferedInputStream
{
        public SuperBufferedInputStream(String file)
				throws FileNotFoundException
        {
            super(new FileInputStream(file));
        }
        public SuperBufferedInputStream(String file, int size)
				throws FileNotFoundException
        {
            super(new FileInputStream(file), size);
        }

        public byte[] readBytes(byte[] buffer)
        {
	    return readBytes(buffer, true);
	}
        public byte[] readBytes(byte[] buffer, boolean failAfter)
        {
            int    nb = 0;
            int ngoes = 0;
            int nbytes = buffer.length;
            try
            {
                do
                {
                    nb += read(buffer, nb, nbytes - nb);
                    if (nb < nbytes && nb >= 0)
                    {
                        ngoes++;
                        if (ngoes > 10 && failAfter)
                        {
                            throw new Exception("Too many tries reading "+
                                        "record from file " + toString());
                        }
                    }
                    else if (nb < 0)
                    {
                        return null;
                    }
                }
                while (nb < nbytes);
            }
            catch (Exception eio)
            {
                System.err.println("Io exception detected reading bytes "+
                                                        eio.toString());
                System.exit(1);
            }
            return buffer;
        }
//
// String compare method comparing 2 strings for a maximum length
//
        public int strncmp(String s1, String s2, int length)
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
}
