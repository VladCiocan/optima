package com.hartehanks.dev.app;
import java.util.*;

public final class StringSort
{
	public StringSort(String list[])
	{
	    this(list, true);
	}

	public StringSort(String list[], boolean increasing)
	{
	    if (list != null && list.length > 1)
	    {
		quickSort(list, 0, list.length -1, 0, increasing);
	    }
	}

	public StringSort(String list[], int offset)
	{
	    if (list != null && list.length > 1)
	    {
		quickSort(list, 0, list.length -1, offset, true);
	    }
	}

	public StringSort(String list[], int start, int count)
	{
	    if (list != null && list.length > 1)
	    {
		quickSort(list, start, count - 1, 0, true);
	    }
	}

	private void quickSort(String list[], int left, int right, int offset,
							boolean increasing)
	{
	    if (right > left)
	    {
		String	temp;

		String s1 = list[right].substring(offset).toLowerCase();
		int i = left -1;
		int j = right;

		while(true)
		{
		    if (increasing)
		    {
			while(list[++i].substring(offset).toLowerCase().
							compareTo(s1) < 0);
			while (j > 0)
			{
			    if (list[--j].substring(offset).toLowerCase().
							compareTo(s1) <= 0)
			    {
				break;
			    }
			}
		    }
		    else
		    {
			while(list[++i].substring(offset).toLowerCase().
							compareTo(s1) > 0);
			while (j > 0)
			{
			    if (list[--j].substring(offset).toLowerCase().
							compareTo(s1) >= 0)
			    {
				break;
			    }
			}
		    }
		    if (i >= j)
		    {
			break;
		    }
		    temp = list[i];
		    list[i] = list[j];
		    list[j] = temp;
		}
		temp = list[i];
		list[i] = list[right];
		list[right] = temp;
		quickSort(list, left, i-1, offset, increasing);
		quickSort(list, i+1, right, offset, increasing);

		s1 = null;
		temp = null;
	    }
	}
}
