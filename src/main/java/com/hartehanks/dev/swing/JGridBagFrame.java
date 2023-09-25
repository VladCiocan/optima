package com.hartehanks.dev.swing;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class JGridBagFrame extends JFrame
{
	private	 FrameResize	fr = null;

	public JGridBagFrame()
	{
	    this("", 200, 200);
	}
	public JGridBagFrame(String title)
	{
	    this(title, 200, 200);
	}
	public JGridBagFrame(int width, int height)
	{
	    this("", width, height);
	}
	public JGridBagFrame(Frame parent, String title, int width, int height)
	{
	    this(title, width, height);
	}
	public JGridBagFrame(String title, int width, int height)
	{
	    super(title);

	    setSize(width, height);

	    GridBagLayout gbl = new GridBagLayout();

	    gbl.columnWidths = new int[100];
	    gbl.rowHeights = new int[100];
	    gbl.columnWeights = new double[100];
	    gbl.rowWeights = new double[100];

	    //enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	    for (int i = 0; i < 100; i++)
	    {
		gbl.columnWidths[i] = width/100;
		gbl.columnWeights[i] = 0.0;
		gbl.rowHeights[i] = height/100;
		gbl.rowWeights[i] = 0.0;
	    }

	    getContentPane().setLayout(gbl);
	}

	private void removeResizeListener()
	{
	    if (fr != null)
	    {
		removeComponentListener(fr);
		fr = null;
	    }
	}
	private void addResizeListener()
	{
	    fr = new FrameResize();
	    addComponentListener(fr);
	}

	public final JComponent addWidget(JComponent w, int x, int y,
		int xw, int yh, int fill, int anchor, double wx, double wy)
			throws IllegalArgumentException
	{
	    if (x < 0 || x > 99)
	    {
		throw new IllegalArgumentException(
				"X co-ordinate not in range 0-99 ("+x+")");
	    }
	    if (y < 0 || y > 99)
	    {
		throw new IllegalArgumentException(
				"Y co-ordinate not in range 0-99 ("+y+")");
	    }
	    if (x+xw < 1 || x+xw > 100)
	    {
		throw new IllegalArgumentException(
			"X+Width dimension not in range 1-100 ("+x+xw+")");
	    }
	    if (y+yh < 1 || y+yh > 100)
	    {
		throw new IllegalArgumentException(
			"Y+Height dimension not in range 1-100 ("+y+yh+")");
	    }
	    GridBagConstraints gbs = new GridBagConstraints();

	    gbs.gridx = x;
	    gbs.gridy = y;
	    gbs.insets = new Insets(0, 0, 0, 0);
	    gbs.gridwidth = xw;
	    gbs.gridheight= yh;
	    gbs.fill = fill;
	    gbs.ipadx = 0;
	    gbs.ipady = 0;
	    gbs.anchor = anchor;
	    gbs.weightx = wx;
	    gbs.weighty = wy;

	    getContentPane().add(w, gbs);
	    return w;
	}

	public final Component addWidget(Component w, int x, int y, int xw,
			int yh, int fill, int anchor, double wx, double wy)
			throws IllegalArgumentException
	{
	    if (x < 0 || x > 99)
	    {
		throw new IllegalArgumentException(
				"X co-ordinate not in range 0-99 ("+x+")");
	    }
	    if (y < 0 || y > 99)
	    {
		throw new IllegalArgumentException(
				"Y co-ordinate not in range 0-99 ("+y+")");
	    }
	    if (x+xw < 1 || x+xw > 100)
	    {
		throw new IllegalArgumentException(
			"X+Width dimension not in range 1-100 ("+x+xw+")");
	    }
	    if (y+yh < 1 || y+yh > 100)
	    {
		throw new IllegalArgumentException(
			"Y+Height dimension not in range 1-100 ("+y+yh+")");
	    }
	    GridBagConstraints gbs = new GridBagConstraints();

	    gbs.gridx = x;
	    gbs.gridy = y;
	    gbs.gridwidth = xw;
	    gbs.gridheight= yh;
	    gbs.fill = fill;
	    gbs.ipadx = 0;
	    gbs.ipady = 0;
	    gbs.anchor = anchor;
	    gbs.weightx = wx;
	    gbs.weighty = wy;

	    getContentPane().add(w, gbs);
	    return w;
	}

	protected class FrameResize implements ComponentListener
	{
	    private int	base_width  = 0;
	    private int	base_height = 0;

	    private FrameResize()
	    {
		Dimension d = getSize();
		base_width = d.width;
		base_height = d.height;
	    }

	    public final void componentResized(ComponentEvent e)
	    {
		Dimension d = getSize();
		d.width  = d.width < base_width  ? base_width  : d.width;
		d.height = d.height< base_height ? base_height : d.height;
		setSize(d);
	    }
	    public final void componentMoved(ComponentEvent e) {};
	    public final void componentShown(ComponentEvent e) {};
	    public final void componentHidden(ComponentEvent e) {};
	}
}
