package com.hartehanks.dev.swing;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class JGridBagPanel extends JPanel
{
	int	 i_width;
	int	 i_height;

	public JGridBagPanel(int width, int height)
	{
	    super();
	    i_width = width;
	    i_height= height;

//	    setSize(width, height);

	    GridBagLayout gbl = new GridBagLayout();

	    gbl.columnWidths = new int[100];
	    gbl.rowHeights = new int[100];
	    gbl.columnWeights = new double[100];
	    gbl.rowWeights = new double[100];

	    //enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	    for (int i = 0; i < 100; i++)
	    {
		gbl.columnWidths[i] = i_width/100;
		gbl.columnWeights[i] = 0.0;
		gbl.rowHeights[i] = i_height/100;
		gbl.rowWeights[i] = 0.0;
	    }

	    setLayout(gbl);

	    //addComponentListener(new FrameResize());
	}

	public JComponent addWidget(JComponent w, int x, int y, int xw, int yh,
				int fill, int anchor, double wx, double wy)
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
	    gbs.insets = new Insets(0,0,0,0);

	    add(w, gbs);
	    return w;
	}

	public Component addWidget(Component w, int x, int y, int xw, int yh,
				int fill, int anchor, double wx, double wy)
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

	    add(w, gbs);
	    return w;
	}

	class FrameResize implements ComponentListener
	{
	    public void componentResized(ComponentEvent e)
	    {
		Dimension d = getSize();
		d.width  = d.width < i_width  ? i_width  : d.width;
		d.height = d.height< i_height ? i_height : d.height;
		setSize(d);
	    }
	    public void componentMoved(ComponentEvent e) {};
	    public void componentShown(ComponentEvent e) {};
	    public void componentHidden(ComponentEvent e) {};
	}
}
