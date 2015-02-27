package net.tc.comparators;
import java.io.*;
import java.util.*;

/**
 * <p>Title: NDBJ / API</p>
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
public class OrderBySystemTime
        implements Comparator, Serializable

{
    private Comparator comparator;
    private boolean ignoreCase;
    private boolean ignoreAccents;

    public OrderBySystemTime() {
    }

    public int compare(Object o1, Object o2)
    {
                if(o1 != null && o2 != null)
                {
                        Long a = null;
                        Long b = null;

                        try{
                                a = (Long) o1 ;
                                b = (Long) o2 ;
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
