package net.tc.utils;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Utility class for working with HTML.  Methods that take Object as a parameter
 * will extract the String representation of the object by either casting to
 * String or by calling the Object's toString() method.
 *
 * <p>Title: ISMA</p>
 * <p>Description: </p>
 * <p>Copyright: Marco Tusa Copyright (c) 2012 Copyright (c) 2003</p>
 * <p>Company: TusaCentral</p>
 * @author  Tusa
 * @version 1.0
 */
public class HTML
{
    /**
     * Escapes HTML special characters
     * @param o text to escape
     * @return an properly HTML encoded String
     */
    public static String escapeNotAscii(String o)
    {
        if ( o == null ) return null;

        o.replace('\234','\'');
        o.replace('\221','\'');
        o.replace('\222','\'');
        o.replace('\301','\'');

        String s = Text.toString(o);
        StringBuffer sb = new StringBuffer();
        int n = s.length();

        for (int i = 0; i < n; i++)
        {
            Character c = new Character(s.charAt(i));
            if(c.isIdentifierIgnorable(s.charAt(i)))
            {       sb.append(" ");
            }
            else
            {
                sb.append(s.charAt(i));
            }
        }
        o = sb.toString();
        return (String)o;
    }

    public static String escape(Object o)
    {
        if ( o == null ) return null;

        String s = Text.toString(o);
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++)
        {
          char c = s.charAt(i);
          switch (c)
          {

             case '<': sb.append("&lt;"); break;
             case '>': sb.append("&gt;"); break;
             case '&': sb.append("&amp;"); break;
             case '"': sb.append("&quot;"); break;
             case '\'': sb.append("&#39;"); break;
 /*            case '�': sb.append("&agrave;");break;
             case '�': sb.append("&amp;aacute;");break;
             case '�': sb.append("&Agrave;");break;
             case '�': sb.append("&acirc;");break;
             case '�': sb.append("&Acirc;");break;
             case '�': sb.append("&auml;");break;
             case '�': sb.append("&Auml;");break;
             case '�': sb.append("&aring;");break;
             case '�': sb.append("&Aring;");break;
             case '�': sb.append("&aelig;");break;
             case '�': sb.append("&AElig;");break;
             case '�': sb.append("&ccedil;");break;
             case '�': sb.append("&Ccedil;");break;
             case '�': sb.append("&eacute;");break;
             case '�': sb.append("&Eacute;");break;
             case '�': sb.append("&egrave;");break;
             case '�': sb.append("&Egrave;");break;
             case '�': sb.append("&ecirc;");break;
             case '�': sb.append("&Ecirc;");break;
             case '�': sb.append("&euml;");break;
             case '�': sb.append("&Euml;");break;
             case '�': sb.append("&iuml;");break;
             case '�': sb.append("&Iuml;");break;
             case '�': sb.append("&ocirc;");break;
             case '�': sb.append("&Ocirc;");break;
             case '�': sb.append("&ouml;");break;
             case '�': sb.append("&Ouml;");break;
             case '�': sb.append("&oslash;");break;
             case '�': sb.append("&Oslash;");break;
             case '�': sb.append("&szlig;");break;
             case '�': sb.append("&ugrave;");break;
             case '�': sb.append("&Ugrave;");break;
             case '�': sb.append("&ucirc;");break;
             case '�': sb.append("&Ucirc;");break;
             case '�': sb.append("&uuml;");break;
             case '�': sb.append("&Uuml;");break;
             case '�': sb.append("&reg;");break;
             case '�': sb.append("&copy;");break;
             case '�': sb.append("&euro;"); break;*/
             // be carefull with this one (non-breaking white space)
//             case ' ': sb.append("&nbsp;");break;

             default:  sb.append(c); break;
          }
        }

        return sb.toString();
    }

    public static String escapeXML(Object o)
    {
        if ( o == null ) return null;

        String s = Text.toString(o);
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++)
        {
          char c = s.charAt(i);
          switch (c)
          {

             case '<': sb.append("&amp;lt;"); break;
             case '>': sb.append("&amp;gt;"); break;
             case '&': sb.append("&amp;amp;"); break;
             case '"': sb.append("&amp;quot;"); break;
             case '\'': sb.append("&amp;#39;"); break;
             /*case '�': sb.append("&amp;agrave;");break;
             case '�': sb.append("&amp;aacute;");break;
             case '�': sb.append("&amp;Agrave;");break;
             case '�': sb.append("&amp;acirc;");break;
             case '�': sb.append("&amp;Acirc;");break;
             case '�': sb.append("&amp;auml;");break;
             case '�': sb.append("&amp;Auml;");break;
             case '�': sb.append("&amp;aring;");break;
             case '�': sb.append("&amp;Aring;");break;
             case '�': sb.append("&amp;aelig;");break;
             case '�': sb.append("&amp;AElig;");break;
             case '�': sb.append("&amp;ccedil;");break;
             case '�': sb.append("&amp;Ccedil;");break;
             case '�': sb.append("&amp;eacute;");break;
             case '�': sb.append("&amp;Eacute;");break;
             case '�': sb.append("&amp;egrave;");break;
             case '�': sb.append("&amp;Egrave;");break;
             case '�': sb.append("&amp;ecirc;");break;
             case '�': sb.append("&amp;Ecirc;");break;
             case '�': sb.append("&amp;euml;");break;
             case '�': sb.append("&amp;Euml;");break;
             case '�': sb.append("&amp;iuml;");break;
             case '�': sb.append("&amp;Iuml;");break;
             case '�': sb.append("&amp;ocirc;");break;
             case '�': sb.append("&amp;Ocirc;");break;
             case '�': sb.append("&amp;ouml;");break;
             case '�': sb.append("&amp;Ouml;");break;
             case '�': sb.append("&amp;oslash;");break;
             case '�': sb.append("&amp;Oslash;");break;
             case '�': sb.append("&amp;szlig;");break;
             case '�': sb.append("&amp;ugrave;");break;
             case '�': sb.append("&amp;Ugrave;");break;
             case '�': sb.append("&amp;ucirc;");break;
             case '�': sb.append("&amp;Ucirc;");break;
             case '�': sb.append("&amp;uuml;");break;
             case '�': sb.append("&amp;Uuml;");break;
             case '�': sb.append("&amp;reg;");break;
             case '�': sb.append("&amp;copy;");break;
             case '�': sb.append("&amp;euro;"); break;*/
             // be carefull with this one (non-breaking white space)
//             case ' ': sb.append("&nbsp;");break;

             default:  sb.append(c); break;
          }
        }

        return sb.toString();
    }

    /**
     * Replaces ASCII CRLF's with HTML &lt;br;&gt;'s, first removing
     * &lt;br&gt;'s and &lt;p&gt;'s.
     * @param o text to format
     * @return formatted text or &amp;nbsp; if text is <code>null</code> or empty
     */
    public static String formatLineBreaks(Object o)
    {
        String s = format(o);

        s = Text.replace(s, "<br>", "");
        s = Text.replace(s, "<p>", "");
        s = Text.replace(s, "\r\n", "<br />");

        return s;
    }

    /**
     * @param o text to format
     * @return formatted text or &amp;nbsp; if text is <code>null</code> or empty
     */
    public static String format(Object o)
    {
        return format(o, "&nbsp;");
    }

    public static String format(Object o, String defaultHTML)
    {
        String s = Text.toString(o, null);

        if (Text.isEmpty(s))
        {
            s = defaultHTML;
        }
        else
        {
            s = s.trim();
            s = HTML.escape(s);
            s = escapeNotAscii(s);
        }

        return s;
    }

    public static String format(Object arr[], String delimiter)
    {
        return format(arr, "&nbsp;", delimiter);
    }

    public static String format(Object arr[], String defaultHTML, String delimiter)
    {
        StringBuffer sb = new StringBuffer();

        if ( arr==null || arr.length == 0 )
        {
            sb.append(defaultHTML);
            return sb.toString();
        }
        for (int i = 0; i < arr.length; i++)
        {
            if ( i > 0 )
            {
                sb.append(delimiter);
            }

            sb.append( format(arr[i], defaultHTML) );
        }

        return sb.toString();
    }

    /**
     * Creates attributes for use in an HTML &lt;option&gt; tag.
     * @param optionValue value attribute of &lt;option&gt;
     * @param requestValue current value of &lt;select&gt; box, usually from HTTP request.
     * @return value and selected attribute declarations, where appropriate
     */
    public static String optionAttribs(String optionValue, String requestValue)
    {
        return HTML.optionAttribs(optionValue, new String[] {requestValue});
    }

    /**
     * Creates attributes for use in an HTML &lt;option&gt; tag.
     * @param optionValue value attribute of &lt;option&gt;
     * @param requestValues current value(s) of &lt;select&gt; box, usually from HTTP request.
     * @return value and selected attribute declarations, where appropriate
     */
    public static String optionAttribs(String optionValue, String[] requestValues)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("value=\""+optionValue+"\"");
        Set valset = new HashSet();

        if ( requestValues != null && requestValues.length > 0 && requestValues[0] != null )
            valset.addAll(Arrays.asList(requestValues));

        if ( valset.contains(optionValue) )
        {
            sb.append(" selected=\"true\"");
        }

        return sb.toString();
    }

    public static String printSelectedAttribs(Long optionValue, String[] requestValue, String toPrint)
    {
        return HTML.printSelectedAttribs(optionValue.toString(), requestValue, toPrint);
    }

    public static String printSelectedAttribs(Long optionValue, String requestValue, String toPrint)
    {
        return HTML.printSelectedAttribs(optionValue.toString(), new String[] {requestValue}, toPrint);
    }

    public static String printSelectedAttribs(String optionValue, String requestValue, String toPrint)
    {
        return HTML.printSelectedAttribs(optionValue, new String[] {requestValue}, toPrint);
    }

    public static String printSelectedAttribs(String optionValue, String[] requestValues, String toPrint)
    {
        StringBuffer sb = new StringBuffer();
        Set valset = new HashSet();

        if ( requestValues != null && requestValues.length > 0 && requestValues[0] != null )
            valset.addAll(Arrays.asList(requestValues));

        if ( valset.contains(optionValue) )
        {
            return toPrint;
        }

        return "";
    }

    /**
     * Creates attributes for use in an HTML &lt;input&gt; checkbox tag.
     * @param optionValue value attribute of &lt;input&gt;
     * @param requestValue current value of &lt;select&gt; box, usually from HTTP request.
     * @return value and selected attribute declarations, where appropriate
     */
    public static String checkboxAttribs(String optionValue, String requestValue)
    {
        return HTML.checkboxAttribs(optionValue, new String[] {requestValue});
    }
    public static String checkboxAttribs(Long optionValue, String requestValue)
    {
        return HTML.checkboxAttribs(optionValue.toString(), new String[] {requestValue});
    }
    public static String checkboxAttribs(Long optionValue, String[] requestValues)
    {
        return HTML.checkboxAttribs(optionValue.toString(), requestValues);
    }

    /**
     * Creates attributes for use in an HTML &lt;input&gt; checkbox tag.
     * @param optionValue value attribute of &lt;input&gt;
     * @param requestValues current value(s) of &lt;select&gt; box, usually from HTTP request.
     * @return value and selected attribute declarations, where appropriate
     */
    public static String checkboxAttribs(String optionValue, String[] requestValues)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("value=\""+optionValue+"\"");
        Set valset = new HashSet();

        if ( requestValues != null && requestValues.length > 0 && requestValues[0] != null )
            valset.addAll(Arrays.asList(requestValues));

        if ( valset.contains(optionValue) )
        {
            sb.append(" checked=\"true\"");
        }

        return sb.toString();
    }

    /**
     * Create a hidden form field
     * @param name
     * @param value
     * @return &lt;input type="hidden" name="..." value="..."  /&gt;
     */
    public static String hiddenFormField(String name, String value)
    {
        return "<input type=\"hidden\" "+nameValueAttribs(name,value)+" />";
    }

    /**
     * Create a hidden form fields from the parameterMap
     * @param parameterMap String[] values, keyed parameter names
     * @return a series of hidden form fields like
     * &lt;input type="hidden" name="..." value="..."  /&gt;
     */
    public static String hiddenFormFields(Map parameterMap)
    {
        StringBuffer sb = new StringBuffer();
        Iterator iter = parameterMap.keySet().iterator();

        while (iter.hasNext())
        {
            String param = (String) iter.next();
            String[] values = (String[]) parameterMap.get(param);
            for (int i = 0; i < values.length; i++)
            {
                sb.append( HTML.hiddenFormField(param, values[i]) );
                sb.append( "\r\n" );
            }
        }

        return sb.toString();
    }

    /**
     * Name/value attributes for HTML elements ( HTML escaped )
     * @param name
     * @param value
     * @return name="..." value="..."
     */
    public static String nameValueAttribs(String name, String value)
    {
        return "name=\""+escape(name)+"\" value=\""+escape(value)+"\"";
    }
    public static String optionAttribs(Long optionValue, String requestValue)
{
    return HTML.optionAttribs(optionValue.toString(), new String[] {requestValue});
}

public static String optionAttribs(Long optionValue, String[] requestValue)
{
    return HTML.optionAttribs(optionValue.toString(), requestValue);
}


}
