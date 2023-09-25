package com.hartehanks;

public class GlobalQueueHeader
{
	public final static int	NEEDS_FINISHING = 0;
	public final static int	IS_FINISHING = 1;
	public final static int	IS_FINISHED = 2;

	GlobalQueueEntity[]	globalQueueEntity;
	int			finishState = NEEDS_FINISHING;

	public GlobalQueueHeader(GlobalQueueEntity[] globalQueueEntity)
	{
	    this.globalQueueEntity = globalQueueEntity;
	}
}
