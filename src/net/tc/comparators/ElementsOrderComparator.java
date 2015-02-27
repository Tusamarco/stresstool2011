package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Compares two Strings or String arrays
 *
 * <p>Title: TusaCentral</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author  Tusa
 * @version 1.0
 */

public class ElementsOrderComparator
    implements Comparator, Serializable
{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public ElementsOrderComparator()
    {

    }


    public int compare(Object o1, Object o2)
    {
		if(o1 != null && o2 != null)
		{

			int a = ( (Integer)((Vector)o1).get(0) ).intValue();
			int b = ( (Integer)((Vector)o2).get(0) ).intValue();
			int c = ( (Integer)((Vector)o1).get(1) ).intValue();
			int d = ( (Integer)((Vector)o2).get(1) ).intValue();

			int valOrder = 0;

			if((a-b) > 0 && (c - d) > 0)
				valOrder = 6;
			if((a-b) > 0 && (c - d) == 0)
				valOrder = 5;
			if((a-b) > 0 && (c - d) < 0)
				valOrder = 4;
			if((a-b) == 0 && (c - d) > 0)
				valOrder = 3;
			if((a-b) == 0 && (c - d) == 0)
				valOrder = 2;
			if((a-b) == 0 && (c - d) < 0)
				valOrder = 1;

			if((a-b) < 0 && (c - d) > 0)
				valOrder = -1;
			if((a-b) < 0 && (c - d) == 0)
				valOrder = -2;
			if((a-b) < 0 && (c - d) < 0)
				valOrder = -3;

/*			if((a-b) >= 0 && (c - d) <= 0)
				valOrder = 1;
			if((a-b) <= 0 && (c - d) >= 0)
				valOrder = -1;
			if((a-b) < 0 && (c - d) < 0)
				valOrder = -2;
*/
//System.out.println( a + "  " + b + " ("+(a-b) + ") " + c + " " + d + " (" +(c-d) + ") = " + valOrder);
			return valOrder;

//			return a.intValue() - b.intValue();
		}
		else
			return 0;
    }


}
