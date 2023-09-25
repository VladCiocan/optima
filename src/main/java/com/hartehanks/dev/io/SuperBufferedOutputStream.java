package com.hartehanks.dev.io;

import java.util.*;
import java.io.*;

public class SuperBufferedOutputStream extends BufferedOutputStream
{
        public SuperBufferedOutputStream(String file)
				throws FileNotFoundException
        {
	    super(new FileOutputStream(file));
        }
        public SuperBufferedOutputStream(String file, int size)
				throws FileNotFoundException
        {
            super(new FileOutputStream(file), size);
        }

        public SuperBufferedOutputStream(String file, boolean append)
                                throws FileNotFoundException
        {
            super(new FileOutputStream(file, append));
        }


	public SuperBufferedOutputStream(String file, boolean append, int size)
                                throws FileNotFoundException
        {
            super(new FileOutputStream(file, append), size);
        }
}





