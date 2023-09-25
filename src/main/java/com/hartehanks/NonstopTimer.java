package com.hartehanks;

import java.awt.event.*;

//
// Simple Thread instance of a timer much like the javax.swing.timer but with
// limited functionality. it doesn't need to have an AWT display environment
// set so doesn't require a graphics DISPLAY or jdk 1.4 headless mode
// operation to perform.
//
public class NonstopTimer extends Thread
{
	private int		seconds = -1;
	private ActionListener	actionListener = null;
	private boolean		keepRunning = true;

//
// the constructor sets up the basic timer length and stores the action
// callback needed to post a timer pop event.
//
	public NonstopTimer(ActionListener actionListener)
	{
	    this.actionListener = actionListener;
	}

//
// When the user 'start()'s the timer the thread invokes the timers' run
// method which will wait for the specified (whole second interval) and then
// call the action callback.
//
	public void run()
	{
	    while (seconds != 0 && keepRunning)
	    {
		try
		{
		    sleep(1000);
		}
		catch (InterruptedException ie)
		{
		    System.err.println("Interrupted timer");
		}
		seconds--;
	    }

	    if (keepRunning)
	    {
		actionListener.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, ""));
	    }
	}

//
// if the timer needs to be stopped then the 'stop()' method has been
// depracated - so this is a replacement that sets an internal 'stop' flag
// which the main timer loop monitors. if the stop flag is set when the timer
// pops then the action callback is not invoked.
//
	public final void setTimer(int seconds)
	{
	    this.seconds = seconds;
	}
	public final void stopTimer()
	{
	    keepRunning = false;
	}
}
