package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import java.util.Map;

/**
 * <p>Title: ISMA</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Marco Tusa Copyright (c) 2008</p>
 *
 * <p>Company: MySQL</p>
 *
 * @author Marco Tusa
 * @version 1.0
 */
public class OrderByTimeProperty implements Comparator, Serializable {
    public OrderByTimeProperty() {
    }

    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;


    public int compare(Object o1, Object o2)
    {
                if(o1 != null && o2 != null)
                {
                    if(o1 instanceof SortableByTime && o2 instanceof SortableByTime)
                    {
                        long a = 0;
                        long b = 0;

                        try {
                            a = ((SortableByTime)o1).getSystemTime();
                            b = ((SortableByTime)o2).getSystemTime();
                            return new Long(a - b).intValue();

                        } catch (NumberFormatException ex) {
                            String as = (String) o1;
                            String bs = (String) o2;
                            return as.compareTo(bs);
                        }
                    }
                    return 0;
                }
                else
                        return 0;
    }



}
