package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import net.tc.utils.*;

/**
 * Compares two Strings or String arrays
 *
 * <p>Title: ISMA</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author  Tusa
 * @version 1.0
 */

public class StringComparator
    implements Comparator, Serializable
{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public StringComparator()
    {
        this(ComparableComparator.getInstance(), true, true);
    }

    public StringComparator(Comparator comparator)
    {
        this(comparator, true, true);
    }

    public StringComparator(boolean ignoreCase, boolean ignoreAccents)
    {
        this(ComparableComparator.getInstance(), ignoreCase, ignoreAccents);
    }

    public StringComparator(Comparator comparator, boolean ignoreCase, boolean ignoreAccents)
    {
        this.comparator = comparator;
        this.ignoreCase = ignoreCase;
        this.ignoreAccents = ignoreAccents;
    }

    protected String formatForCompare(String value)
    {
        if ( value == null )
            return null;

        if ( ignoreAccents )
            value = Text.removeAccents(value);

        if ( ignoreCase )
            value = value.toLowerCase();

        return value;
    }

    public int compare(Object o1, Object o2)
    {
        String value1;
        String value2;

        if ( o1!= null && o1.getClass().isArray() )
        {
            value1 = Text.delimit( (String[]) o1, "" );
        }
        else
        {
            value1 = (String) o1;
        }

        if ( o2!= null && o2.getClass().isArray() )
        {
            value2 = Text.delimit( (String[]) o2, "");
        }
        else
        {
            value2 = (String) o2;
        }

        value1 = formatForCompare( value1 );
        value2 = formatForCompare( value2 );

        return comparator.compare(value1, value2);
    }

    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase)
    {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoreAccents()
    {
        return ignoreAccents;
    }

    public void setIgnoreAccents(boolean ignoreAccents)
    {
        this.ignoreAccents = ignoreAccents;
    }
}
