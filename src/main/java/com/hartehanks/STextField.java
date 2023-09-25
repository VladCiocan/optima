package com.hartehanks;

import javax.swing.*;
import javax.swing.text.*;
import com.hartehanks.dev.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public final class STextField extends JTextField
{
	protected boolean		escaped = false;

	public STextField()
	{
	    super();
	    setupKeys();
	}
	public STextField(String text)
	{
	    super(text);
	    setupKeys();
	}
	public STextField(int cols)
	{
	    super(cols);
	    setupKeys();
	}
	public STextField(String text, int cols)
	{
	    super(text, cols);
	    setupKeys();
	}
	public STextField(Document doc, String text,
                      int cols)
	{
	    super(doc, text, cols);
	    setupKeys();
	}

	private final void setupKeys()
	{
	    addKeyListener(new KeyListener()
	    {
		public final void keyPressed(KeyEvent ke)
		{
		    //System.err.println("KP "+ke);
		    char kc = ke.getKeyChar();
		    switch (kc)
		    {
			case KeyEvent.VK_ESCAPE:
			    escaped = true;
			    ke.consume();
			    break;

			case 'h':
			    if (escaped)
			    {
				ke.setKeyCode(37);
				ke.setKeyChar((char)KeyEvent.VK_LEFT);
			    }
		    	    //System.err.println("KP EXIT "+ke);
			    break;

			case 'l':
			    if (escaped)
			    {
				ke.setKeyCode(39);
				ke.setKeyChar((char)KeyEvent.VK_RIGHT);
			    }
		    	    //System.err.println("KP EXIT "+ke);
			    break;

			case 'k':
			    if (escaped)
			    {
				ke.setKeyCode(38);
				ke.setKeyChar((char)KeyEvent.VK_UP);
			    }
		    	    //System.err.println("KP EXIT "+ke);
			    break;

			case 'j':
			    if (escaped)
			    {
				ke.setKeyCode(40);
				ke.setKeyChar((char)KeyEvent.VK_DOWN);
			    }
		    	    //System.err.println("KP EXIT "+ke);
			    break;
		    }
		}
		public final void keyReleased(KeyEvent ke)
		{
		    //System.err.println("KR "+ke);
		    char kc = ke.getKeyChar();
		    switch (kc)
		    {
			case KeyEvent.VK_ESCAPE:
			    ke.consume();
			    escaped = false;
			    break;

			case 'h':
			    if (escaped)
			    {
				ke.setKeyCode(37);
				ke.setKeyChar((char)KeyEvent.VK_LEFT);
			    }
		    	    //System.err.println("KR EXIT "+ke);
			    break;

			case 'l':
			    if (escaped)
			    {
				ke.setKeyCode(39);
				ke.setKeyChar((char)KeyEvent.VK_RIGHT);
			    }
		    	    //System.err.println("KR EXIT "+ke);
			    break;

			case 'k':
			    if (escaped)
			    {
				ke.setKeyCode(38);
				ke.setKeyChar((char)KeyEvent.VK_UP);
			    }
		    	    //System.err.println("KR EXIT "+ke);
			    break;

			case 'j':
			    if (escaped)
			    {
				ke.setKeyCode(40);
				ke.setKeyChar((char)KeyEvent.VK_DOWN);
			    }
		    	    //System.err.println("KR EXIT "+ke);
			    break;
		    }
		}
		public final void keyTyped(KeyEvent ke)
		{
		    //System.err.println("KT "+ke);
		    char kc = ke.getKeyChar();
		    switch(kc)
		    {
			case KeyEvent.VK_ESCAPE:
			    ke.consume();
			    break;

			case 'h':
			case 'j':
			case 'k':
			case 'l':
			    if (escaped)
			    {
				ke.consume();
			    }
			    break;
		    }
		}
	    });
	}
}
