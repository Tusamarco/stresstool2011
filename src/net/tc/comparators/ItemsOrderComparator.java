package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;

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

public class ItemsOrderComparator
    implements Comparator, Serializable
{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public ItemsOrderComparator()
    {

    }


    public int compare(Object o1, Object o2)
    {
		if(o1 != null && o2 != null)
		{
			Long a = new Long( ( String ) o1 );
			Long b = new Long( ( String ) o2 );
			return a.intValue() - b.intValue();
		}
		else
			return 0;
    }


}
