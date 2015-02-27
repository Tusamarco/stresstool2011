package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;


/**
 * Compares two Strings or String arrays
 *
 * <p>Title: ISMA</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author  Tusa
 * @version 1.0
 */

public class collectionsOrderComparator
    implements Comparator, Serializable
{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public collectionsOrderComparator()
    {

    }


    public int compare(Object o1, Object o2)
    {
		if(o1 != null && o2 != null)
		{
			Integer a = null;
			Integer b = null;

			try{
				a = new Integer( ( String ) o1 );
				b = new Integer( ( String ) o2 );
				return a.intValue() - b.intValue();

			}
			catch(NumberFormatException ex)
			{
				String as = (String)o1;
				String bs = (String)o2;
				return as.compareTo(bs);
			}
		}
		else
			return 0;
    }


}
