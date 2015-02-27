package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import net.tc.utils.*;

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

public class CollectionsSortByOrderComparator
    implements Comparator, Serializable
{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public CollectionsSortByOrderComparator()
    {

    }


    public int compare(Object o1, Object o2)
    {
		Object a = null;
		Object b = null;
		if(o1 instanceof SynchronizedMap)
		{
			try
			{
				Long order1 = new Long( ( String ) ( ( SynchronizedMap ) o1 ).get( "order" ) );
				Long order2 = new Long( ( String ) ( ( SynchronizedMap ) o2 ).get( "order" ) );
				if(order1 != null && order2 != null)
					return order1.intValue() - order2.intValue();

				return 0;
			}
			catch(NumberFormatException ex)
			{
				return 0;
			}
		}
		else
			return 0;
    }


}
