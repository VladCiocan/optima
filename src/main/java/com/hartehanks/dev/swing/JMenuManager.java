package com.hartehanks.dev.swing;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public final class JMenuManager extends JMenuBar
{
	private  Vector	 v_Menustack = new Vector(10,10);
        private ButtonGroup[] group = new ButtonGroup[0];
        private int index = -1;

	public JMenuManager()
	{
	    super();
	}

	public final JMenu addTitle(String title)
	{
	    return addTitleMnemonicToo(title, ' ');
	}

	public final JMenu addTitleMnemonicToo(String title, char mnemonic)
	{
	    JMenu item = new JMenu(title);
	    if (mnemonic != ' ')
	    {
	        item.setMnemonic(mnemonic);
	    }
	    add(item);
	    if (title.equalsIgnoreCase("help"))
	    {
		//setHelpMenu(item);
	    }
	    v_Menustack.removeAllElements();
	    v_Menustack.addElement(item);
	    return item;
	}

	public final void addButton(String title, ActionListener al)
	{
	    addButtonMnemonicToo(title, al, ' ');
	}

	public final void addButtonMnemonicToo(String title, ActionListener al,
	    char mnemonic)
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();

		JMenuItem item = new JMenuItem(title);
		if (mnemonic != ' ')
		{
		    item.setMnemonic(mnemonic);
		}
		cv.add(item);
		if (al != null)
		{
		    item.addActionListener(al);
		}
	    }
	}

	public final void removeJMenuItem()
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		cv.remove(cv.getItemCount() - 1);
	    }
	}

	public final void removeAllJMenuItems()
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		cv.removeAll();
	    }
	}

	public final JMenuItem returnAddButtonMnemonicToo
	    (String title, ActionListener al,
	    char mnemonic)
	{
	    addButtonMnemonicToo(title, al, mnemonic);
	    JMenu cv = (JMenu) v_Menustack.lastElement();
	    return cv.getItem(cv.getItemCount() - 1);
	}

	public final int getJMenuItemCount()
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		return cv.getItemCount();
	    }
	    return -1;
	}

	public final JMenuItem addFullButton(String title, ActionListener al,
	    Icon icon, char mnemonic)
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();

		JMenuItem item = new JMenuItem(title, icon);
		if (mnemonic != ' ')
		{
		    item.setMnemonic(mnemonic);
		}
		cv.add(item);
		if (al != null)
		{
		    item.addActionListener(al);
		}
		return item;
	    }
	    return null;
	}

	public final JCheckBoxMenuItem addCheckBox(String title,
						ItemListener al, boolean state)
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(title, state);
		cv.add(item);
		if (al != null)
		{
		    item.addItemListener(al);
		}
		return item;
	    }
	    return null;
	}

//
// Method to add JRadio buttons to the menu bar
//
        public final JRadioButtonMenuItem addRadioButtonJMenuItem(String title,
                                 ItemListener il, boolean state,
                                 boolean newGroup)
        {
	    if (v_Menustack.size() > 0)
	    {
	        JMenu cv = (JMenu)v_Menustack.lastElement();
                JRadioButtonMenuItem rb_item =
                     new JRadioButtonMenuItem(title, state);
		cv.add(rb_item);
                if (il != null)
                {
                    rb_item.addItemListener(il);
                }
                if (newGroup)
                {
                    ButtonGroup[] tempGroup = new ButtonGroup[group.length];
                    System.arraycopy(group, 0, tempGroup, 0, tempGroup.length);
                    group = new ButtonGroup[tempGroup.length + 1];
                    group[++index] = new ButtonGroup();
                }
                group[index].add(rb_item);
                return rb_item;
	    }
	    return null;
        }


	public final void addCascade(String title)
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		JMenu item = new JMenu(title);
		cv.add(item);
		v_Menustack.addElement(item);
	    }
	}

	public final void addSeparator()
	{
	    if (v_Menustack.size() > 0)
	    {
		JMenu cv = (JMenu)v_Menustack.lastElement();
		cv.addSeparator();
	    }
	}

	public final void upLevel()
	{
	    if (v_Menustack.size() > 0)
	    {
		v_Menustack.removeElementAt(v_Menustack.size()-1);
	    }
	}
}
