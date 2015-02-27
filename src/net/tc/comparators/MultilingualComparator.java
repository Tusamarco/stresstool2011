package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.beanutils.MethodUtils;
import java.lang.reflect.*;
import net.tc.lang.LanguageSelector;

/**
 * <p>Title: ISMA</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author  Tusa
 * @version 1.0
 */

public class MultilingualComparator
    implements Comparator, Serializable
{
    private String property;
    private Comparator comparator;
    private LanguageSelector languageSelector;

    public MultilingualComparator(String property, LanguageSelector languageSelector)
    {
        this(property, languageSelector, ComparableComparator.getInstance());
    }

    public MultilingualComparator(String property, LanguageSelector languageSelector, Comparator comparator)
    {
        setProperty(property);
        this.comparator = comparator;
        this.languageSelector = languageSelector;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public String getProperty()
    {
        return property;
    }

    public int compare(Object o1, Object o2)
    {
        try
        {
            String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
            Object value1 = o1==null?null : MethodUtils.invokeMethod(o1, methodName, languageSelector);
            Object value2 = o2==null?null : MethodUtils.invokeMethod(o2, methodName, languageSelector);
            return comparator.compare(value1, value2);
        }
        catch (InvocationTargetException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (IllegalAccessException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (NoSuchMethodException ex)
        {



            throw new IllegalArgumentException(ex.toString());
        }

    }
    public LanguageSelector getLanguageSelector()
    {
        return languageSelector;
    }
    public void setLanguageSelector(LanguageSelector languageSelector)
    {
        this.languageSelector = languageSelector;
    }
}
