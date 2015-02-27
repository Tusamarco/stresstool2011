package net.tc.comparators;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.beanutils.MethodUtils;
import java.lang.reflect.*;
import net.tc.lang.LanguageSelector;
import net.tc.data.generic.baseObject;

/**
 * <p>Title: ISMA</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author  Tusa
 * @version 1.0
 */

public class ObjectMultilingualComparator
    implements Comparator, Serializable
{
    private String property;
    private Comparator comparator;
    private LanguageSelector languageSelector;

    public ObjectMultilingualComparator(String property, LanguageSelector languageSelector)
    {
        this(property, languageSelector, ComparableComparator.getInstance());
    }

    public ObjectMultilingualComparator(String property, LanguageSelector languageSelector, Comparator comparator)
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
		if(o1 == null || o2 == null)
			return 0;
        try
        {
			Object value1 = ((baseObject)o1).get(property + languageSelector.getCurrentLanguageCap());
			if(value1 != null && value1.equals(""))
				value1 = ((baseObject)o1).get(property + "En");

            Object value2 = ((baseObject)o2).get(property + languageSelector.getCurrentLanguageCap());
			if(value2 != null && value2.equals(""))
				value2 = ((baseObject)o2).get(property + "En");


			if(value1 == null || value2 == null)
				return 0;

            return comparator.compare(value1, value2);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
		return 0;
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
