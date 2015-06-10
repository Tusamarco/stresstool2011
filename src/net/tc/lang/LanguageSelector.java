package net.tc.lang;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
//import org.apache.commons.beanutils.PropertyUtils;
import net.tc.utils.Text;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Language state for use when getting/setting multi-lingual bean properties.
 *
 * <p>Title: ISMA</p>
 * <p>Description: Information System Modular Architecture</p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author
 * @version 1.0
 */
public class LanguageSelector
{
    private String currentLanguage;
    private String[] languageOrder;

    public LanguageSelector(String currentLanguage)
    {
        this.currentLanguage = currentLanguage;
    }

    public LanguageSelector(String currentLanguage, String[] languageOrder)
    {
        this.currentLanguage = currentLanguage;
        this.languageOrder = languageOrder;
    }

    public String getMultilingualPropertyName(String propertyName)
    {
        try
        {
            return getMultilingualPropertyName(propertyName, currentLanguage);
        }
        catch (IntrospectionException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
    }

    private String getMultilingualPropertyName(String propertyName, String lang)
        throws IntrospectionException
    {
        return propertyName + Text.toProperCase(lang);
    }

    /**
     * Uses reflection to call the approiate accessor method.
     * @param propertyName name of the JavaBean property to retrieve
     * @param lang language desired
     * @return result of calling accessor method with name formed by concatenation
     * of propertyName and lang.  For example, if propertyName is "title" and lang is
     * "en", the result of <code>getTitleEn()</code> will be returned.
     */
    public Object getMultilingualProperty(Object beanInstance, String propertyName, String lang)
    {
        if ( beanInstance == null ) return null;

        try
        {
            String mlPropertyName = getMultilingualPropertyName(propertyName, lang);
			if(beanInstance instanceof Map)
			{
				if(!((Map)beanInstance).containsKey(mlPropertyName))
					return null;
				return ((Map)beanInstance).get(mlPropertyName);
			}

            return BeanUtils.getProperty(beanInstance, mlPropertyName);
        }
        catch (IllegalAccessException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (InvocationTargetException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (NoSuchMethodException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (IntrospectionException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
    }

    /**
     * Uses reflection to call the appropriate accessor method based on the
     * current language selector.  If value for a language is blank or null,
     * value defaults to next language.
     *
     * @return value of property in current language
     */
    public Object getMultilingualProperty(Object beanInstance, String propertyName)
    {
        Object val = getMultilingualProperty(beanInstance, propertyName, currentLanguage);

        if ( languageOrder !=  null )
        for (int i = 0; Text.isEmpty(val) && i < languageOrder.length; i++)
        {
            if ( ! languageOrder[i].equals(currentLanguage) )
                val = getMultilingualProperty(beanInstance, propertyName, languageOrder[i]);

            if ( val != null && val instanceof String )
                val = "["+val+"]";
        }

        return val;
    }
    public void setMultilingualProperty(Object beanInstance, String propertyName, Object value)
    {
        setMultilingualProperty(beanInstance, propertyName, currentLanguage, value);

    }

    public Object getMultilingualUrlProperty(Object beanInstance, String propertyName)
    {
        Object val = getMultilingualProperty(beanInstance, propertyName, currentLanguage);

        if ( languageOrder !=  null )
        for (int i = 0; Text.isEmpty(val) && i < languageOrder.length; i++)
        {
            if ( ! languageOrder[i].equals(currentLanguage) )
                val = getMultilingualProperty(beanInstance, propertyName, languageOrder[i]);

            if ( val != null && val instanceof String )
                val = val;
        }

        return val;
    }

    public String getCurrentLanguage()
    {
        return currentLanguage;
    }
    public String getCurrentLanguageCap()
    {
        String lang = currentLanguage.substring(0,1).toUpperCase()+currentLanguage.substring(1,2);
        return lang;
    }

    public void setCurrentLanguage(String currentLanguage)
    {
        this.currentLanguage = currentLanguage;
    }

    public String[] getLanguageOrder()
    {
        return languageOrder;
    }

    public void setLanguageOrder(String[] languageOrder)
    {
        this.languageOrder = languageOrder;
    }

    public void setMultilingualProperty(Object beanInstance, String propertyName, String lang, Object value)
    {
        if ( beanInstance == null ) return;

        try
        {
            String mlPropertyName = getMultilingualPropertyName(propertyName, lang);
            if(beanInstance instanceof Map)
            {
                    if(!((Map)beanInstance).containsKey(mlPropertyName))
                            return ;
                    else
                    ((Map)beanInstance).put(mlPropertyName,value);
            }

            BeanUtils.setProperty(beanInstance, mlPropertyName, value);
        }
        catch (IllegalAccessException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (InvocationTargetException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
        catch (IntrospectionException ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }
    }


}
